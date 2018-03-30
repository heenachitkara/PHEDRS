
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The diagnosis panel builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "patient_summary_page.diagnosis_panel";

        // Selectors for the jqx grid and the count element in the title.
        this.countSelector = "#diagnosis_panel_count";
        this.gridSelector = "#diagnosis_panel_data";

        // The selected patient from application data.
        this.patient = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        this.urlKey = "getpatientdiagnosis";
        

        // Initialize the virtual page
        this.initialize = function () {

            console.log("Loading diagnosis panel");

            var timeStamp = Math.floor(Date.now());

            
            // Get the selected patient from application data.
            self.patient = app_.getPatient();
            if (!self.patient || !self.patient.mrn) { return app_.displayError("Invalid selected patient", "diagnosis panel initialize"); }

            var data = {
                mrn: self.patient.mrn,
                type: "UAB ICD-10-CM Codes,UAB APR DRG Codes,UAB MS DRG Codes,ICD9CM"
            };
            
            // Request diagnosis data from the web service.
            app_.get(data, self.processDiagnoses, app_.processError, self.urlKey);

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };



        this.processDiagnoses = function (data_, textStatus_, jqXHR_) {
            
            var collection = data_.diagnoses;             

            // A helper function used for diagnosis codes below.
            var isUsedKey = function (arrayOfObject, key) {
                for (var i = 0; i < arrayOfObject.length; i += 1) {
                    if (arrayOfObject[i].key == key) {
                        return true;
                    }
                }
                return false;
            };
            
            var diagnosisCodes = [];
            
            for (var i = 0; i < collection.length; i++) {
                if (i == 0) {
                    var newItem = {};
                    newItem.key = collection[i].code_value;
                    newItem.dates = [collection[i].code_assignment_date];
                    newItem.code_description = collection[i].code_description;
                    newItem.detection_criteria = collection[i].detection_criteria;
                    newItem.name = collection[i].name;
                    diagnosisCodes.push(newItem);
                } else {
                    var item = collection[i];
                    var itemName = item.code_value;
                    var itemDate = item.code_assignment_date;
                    var itemDescription = item.code_description;
                    var detectionCriterion = item.detection_criteria;
                    var itemCodeName = item.name;

                    if (isUsedKey(diagnosisCodes, itemName)) {
                        for (var j = 0; j < diagnosisCodes.length; j++) {
                            if (diagnosisCodes[j].key == itemName) {
                                var index = diagnosisCodes[j].dates.length;
                                diagnosisCodes[j].dates[index] = itemDate;
                            }
                        }
                    } else {
                        var nextNewItem = {};
                        nextNewItem.key = itemName;
                        nextNewItem.dates = [itemDate];
                        nextNewItem.code_description = itemDescription;
                        nextNewItem.detection_criteria = detectionCriterion;
                        nextNewItem.name = itemCodeName;
                        diagnosisCodes.push(nextNewItem);
                    }
                }
            }
            
            var newSource = {
                localdata: diagnosisCodes,
                datafields: [{
                    name: 'code_value',
                    type: 'string',
                    map: 'key'
                },
                {
                    name: 'code_assignment_date',
                    type: 'date',
                    map: 'dates>0'
                },
                {
                    name: 'name',
                    type: 'string'

                },
                {
                    name: 'code_description',
                    type: 'string'

                },
                {
                    name: 'detection_criteria',
                    type: 'string'

                }
                ],
                id: 'code_value', // Defining id in the newSource in order to hide the LVR's by their actual code_value rather than using row id  
                datatype: "array"  // Not required as hiding LVR  is taken care by webservice, think about removing it and testing then

            };

            var newAdapter = new $_.jqx.dataAdapter(newSource);

            var cellsrenderer = function (row, column, value) {
                return '<div style="text-align: center; margin-top: 5px;">' + value + '</div>';
            };

            var columnrenderer = function (value) {
                return '<div style="text-align: center; margin-top: 5px;">' + value + '</div>';
            };

            var iconrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var icon = '';
                if (diagnosisCodes[row].dates.length > 1) {
                    icon = '<img src="images/icon-down.png" style="position: absolute; right: 5px;" />';
                }

                return '<span style="position: relative; width: 100%; margin: 4px; float: ' + columnproperties.cellsalign + ';">' + newAdapter.formatDate(value, 'd') + icon + '</span>';
            };
            
            $_(self.gridSelector).jqxGrid({
                source: newAdapter,

                editable: true,
                width: '600',
                pageable: true,
                sortable: true,
                autoheight: true,
                theme: 'classic',
                ready: function () {
                    var diagCount = $_(self.gridSelector).jqxGrid('getrows').length;
                    $_(self.countSelector).html("(" + diagCount + ")");
                },
                height: '170',
                columnsResize: true,
                columns: [
                {
                    text: 'Ontology',
                    datafield: 'name',
                    width: 85,
                    editable: false
                },
                {
                    text: 'Code Value',
                    datafield: 'code_value',
                    width: 75,
                    editable: false,
                    renderer: columnrenderer,
                    cellsrenderer: cellsrenderer
                },
                
                {
                    text: 'Latest Date(s)',
                    datafield: 'code_assignment_date',
                    cellsformat: 'd',
                    columntype: 'combobox',
                    width: 100,
                    cellsrenderer: iconrenderer,

                    createeditor: function (row, column, editor) {
                        var info = $_(self.gridSelector).jqxGrid('getrowdata', row);

                        console.log("length of info: " + info);
                        var groupName = info.code_value; 
                        console.log("Contents of groupName: " + groupName);
                        var dates = [];
                        for (var i = 0; i < diagnosisCodes.length; i++) {
                            if (diagnosisCodes[i].key == groupName) {
                                dates = diagnosisCodes[i].dates;
                            }
                        }
                        editor.jqxComboBox({ autoDropDownHeight: false, source: dates, promptText: "Previous Date(s):", scrollBarSize: 10 });
                        
                    },

                    initeditor: function (row, column, editor) {
                        var info = $_(self.gridSelector).jqxGrid('getrowdata', row);
                        var groupName = info.code_value;
                        var dates = [];
                        for (var i = 0; i < diagnosisCodes.length; i++) {
                            if (diagnosisCodes[i].key == groupName) {
                                dates = diagnosisCodes[i].dates;
                            }
                        }

                       
                        editor.jqxComboBox({
                            autoDropDownHeight: false,
                            source: dates,
                            promptText: "Dates:",
                            width: 100,
                            scrollBarSize: 10,

                            renderer: function (index_, label_, value_) {
                                return formatDateString(value_);
                            },
                            renderSelectedItem: function (index, item) {
                                var records = editor.jqxComboBox('getItems');
                                var currentRecord = records[index].label;
                                return formatDateString(currentRecord);;
                            }
                        });
                    },
                    cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
                        // return the old value, if the new value is empty.
                        if (newvalue == "") return oldvalue;
                    }
                },

                {
                    text: 'Description',
                    datafield: 'code_description'
                   
                },
                {
                    text: 'Detection Criterion',
                    datafield: 'detection_criteria',
                    editable: false,
                    hidden: true
                    
                }

                ],
            });

        }; // End of processDiagnoses
        
    };

})(jQuery, jQuery.phedrs.app));
