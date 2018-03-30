
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The patient attributes panel builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "patient_summary_page.patient_attributes_panel";

        // The currently-selected patient.
        this.patient = null;

        // Updated when a user clicks on a row's edit button.
        this.selectedRowIndex = -1;

        // Selectors
        this.selectors = {
            add_attribute_dialog: ".add-patient-attribute-dialog:visible",
            add_button: "#add_patient_attribute_button",
            count: "#patient_attributes_count",
            edit_dialog: "#patient_attributes_edit_dialog",
            grid: "#patient_attributes_grid"
        };

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        this.urlKeys = {
            add_patient_attribute: "addregistrypatientattribute",
            get_patient_attributes: "getpatientattributes",
            update_patient_attribute: "updateregistrypatientattribute"
        };



        // Add a new patient attribute.
        this.addPatientAttribute = function () {

            // Create the dialog.
            $_(self.selectors.add_button).popModal({

                // Copy the HTML from the dialog template.
                html: $_("#add_patient_attribute_dialog_template").html(),
                onOkBut: function () {

                    var cvterm_id = $_(self.selectors.add_attribute_dialog + " .attribute-name").val();
                    var comment = $_(self.selectors.add_attribute_dialog + " .attribute-comment").val();
                    // TODO: validate?

                    if (!self.patient.mrn) { return app_.displayError("Invalid mrn", "handling ok button of add attribute dialog"); }

                    var data = {
                        cache : false,
                        registry_id: app_.registry.id,
                        mrn: self.patient.mrn,
                        cvterm_id: cvterm_id,
                        annotator_id: app_.user.id,
                        annotator_comment: comment
                        
                    };

                    // Add the patient attribute.
                    app_.getNoPreValidate(data, self.processAddedAttribute, app_.processError, self.urlKeys.add_patient_attribute);
                },
                onCancelBut: function () { },
                onClose: function () { }
            });
            
            
            // Populate the list of terms.
           var data1 = {
                registry_id: app_.registry.id
            };
            app_.getNoPreValidate(data1, self.addPatientAttributesDropDowndata, app_.processError, "getRegistryPatientAttributes");

        };
        this.addPatientAttributesDropDowndata = function (data_, textStatus_, jqXHR_) {

            data_ = app_.processResponse(data_, jqXHR_);

            var attributeCount = 0;
            var collection = data_._reg_config_patient_attributes;
            if (collection) { attributeCount = collection.length; }


            var source = {
                localdata: collection,
                datatype: "array",
                datafields:
                [
                    { name: 'patienrCvTermName', type: 'string' },
                    { name: 'patientCvTermDefinition', type: 'string' },
                    { name: 'cvID', type: 'string' },
                    { name: 'cvTermID', type: 'string' }
                ],
                deleterow: function (rowid, commit) {

                    commit(true);
                },

                updaterow: function (rowid, rowdata, commit) {

                    commit(true);
                }
            };
            var html = "";
            $_.each(collection, function (index_, term_) {
                html += "<option value='" + term_.cvTermID + "'>" + term_.patienrCvTermName + "</option>";
            });

            $_(self.selectors.add_attribute_dialog + " .attribute-name").html(html);
        };

        // Initialize the component.
        this.initialize = function () {

            console.log("          (inside initialize for patient attrs panel)");

            // Get the selected patient from application data.
            self.patient = app_.getPatient();
            if (!self.patient || !self.patient.mrn) { return app_.displayError("Invalid selected patient", "patient attributes initialize"); }

            var data = {
                cache: false,
                mrn: self.patient.mrn,
                registry_id: app_.registry.id
            };

            // Handle the "add patient attribute" button click.
            $_(self.selectors.add_button).click(function () {
                self.addPatientAttribute();
            });

            // Initialize the edit dialog
            $_(self.selectors.edit_dialog).jqxWindow({
                width: /*250*/350, height: 300, resizable: false, isModal: true, autoOpen: false, cancelButton: $_(self.selectors.edit_dialog + " .cancel-button"), modalOpacity: 0.01
            });

            $_(self.selectors.edit_dialog).on('open', function () {

                //$("#attributeName").jqxInput('selectAll');

                $_(self.selectors.edit_dialog + " .assignedOnDate").jqxDateTimeInput({ theme: "arctic", formatString: "d", width: '150px', height: '20px', allowNullDate: true, showFooter: true });
                $_(self.selectors.edit_dialog + " .endAssignedOnDate").jqxDateTimeInput({ theme: "arctic", formatString: "d", width: '150px', height: '20px', allowNullDate: true, showFooter: true });
            });

            // Handle the click event of the edit dialog's save button.
            $_(self.selectors.edit_dialog + " .save-button").click(function () {
                self.savePatientAttribute();             
            });


           // Handle the click event of the edit dialog's remove button.
            $_(self.selectors.edit_dialog + " .remove-button").click(function () {
                self.removePatientAttribute();             
            });


            console.log("before getting patient attributes...");
            // Request patient attributes from the web service.
            app_.getNoPreValidate(data, self.processPatientAttributes, app_.processError, self.urlKeys.get_patient_attributes);

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };


        this.processPatientAttributes = function (data_, textStatus_, jqXHR_) {
            
            // dmd 02/22/17: convert data to JSON, if necessary.
            data_ = app_.processResponse(data_, jqXHR_);

            var attributeCount = 0;
            var collection = data_.registry_all_patient_attribute_list;
            if (collection) { attributeCount = collection.length; }
                        
            // Display the count
            $_(self.selectors.count).html("(" + attributeCount + ")");

            var source = {
                localdata: collection,
                datatype: "array",
                datafields:
                [
                    { name: 'name', type: 'string' },
                    { name: 'start_assignment_date', type: 'string' },
                    { name: 'end_assignment_date', type: 'string' },
                    { name: 'annotator_comment', type: 'string' },
                    { name: 'registry_patient_cvterm_id', type: 'string' }
                ],
                deleterow: function (rowid, commit) {
                    commit(true);
                },

                updaterow: function (rowid, rowdata, commit) {
                    commit(true);
                }
            };

            var dataAdapter = new $_.jqx.dataAdapter(source, {
                loadComplete: function (data) { },
                loadError: function (xhr, status, error) { }
            });

            var editrow = -1;

            

            $_(self.selectors.grid).jqxGrid({
                  source: dataAdapter,
                  width: '550',
                  height: '200',
                  pageable: true,
                  sortable: true,
                  autoheight: true,
                  columnsResize: true,
                  theme: 'classic',
                  columns: [
                     {
                         text: 'Name', datafield: 'name', width: 150
                     },
                     {
                         text: 'Start ',
                         cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                             var formattedDate = formatDateString(value_);
                             return "<div class=\"dateColumn\">" + formattedDate + "</div>";
                         },
                        
                         datafield: 'start_assignment_date', width: 100
                     },
                     {
                         text: 'End',
                         cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                             var formattedDate = formatDateString(value_);
                             return "<div class=\"dateColumn\">" + formattedDate + "</div>";
                         },
                         datafield: 'end_assignment_date', width: 100
                     },
                      { text: 'Comment', datafield: 'annotator_comment', width: 150 },
                      { text: 'CVTermID', datafield: 'registry_patient_cvterm_id', width: 150, hidden: true },
                      {
                          text: 'Edit', datafield: 'Edit', columntype: 'button', cellsrenderer: function () {
                              return "Edit";
                          },
                          buttonclick: function (row_) {

                              console.log(row_);

                              // Maintain the index of the selected row.
                              self.selectedRowIndex = row_;

                              // Open the "edit dialog" when the user clicks the edit button.
                              
                              var offset = $_(self.selectors.grid).offset();
                              $_(self.selectors.edit_dialog).jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });

                              // get the clicked row's data and initialize the input fields.
                              var dataRecord = $_(self.selectors.grid).jqxGrid('getrowdata', row_);
                              $_(self.selectors.edit_dialog + " .attributeName").val(dataRecord.name);

                              // Changing Date format for Start Date on Edit Popup 
                              var formatDateAssignedDate = formatDateString(dataRecord.start_assignment_date);
                              $_(self.selectors.edit_dialog + " .assignedOnDate").val(formatDateAssignedDate);


                              // Changing Date format for End Date on Edit Popup Only if the date value isn't null 
                              if (dataRecord.end_assignment_date !== null) {
                                  var formatDateEndDate = formatDateString(dataRecord.end_assignment_date);
                                  $_(self.selectors.edit_dialog + " .endAssignedOnDate").val(formatDateEndDate);

                              } else {
                                  /*$("#endAssignedOnDate").jqxDateTimeInput({ theme: "arctic", formatString: "d", width: '150px', height: '20px',allowNullDate: true });*/
                                  console.log("Inside Else statement of buttonclick line #274 ","");
                                  //$_(self.selectors.edit_dialog + " .endAssignedOnDate").val(dataRecord.start_assignment_date);
                                    $_(self.selectors.edit_dialog + " .endAssignedOnDate").val("");
                              }


                              $_(self.selectors.edit_dialog + " .userComment").val(dataRecord.annotator_comment);
                              $_(self.selectors.edit_dialog + " .cvTermID").val(dataRecord.registry_patient_cvterm_id);

                              
                              // show the popup window.
                              $_(self.selectors.edit_dialog).jqxWindow('open');
                          }
                      }
                  ]
              });  // END of  $(self.selectors.grid).jqxGrid initialization
        };

        // Process the result of adding a patient attribute.
        this.processAddedAttribute = function (data_, textStatus_, jqXHR_) {

            // dmd 02/22/17: convert data to JSON, if necessary.
            data_ = app_.processResponse(data_, jqXHR_);

            if (!data_ || !data_.status) { return app_.displayError("Invalid response", "processAddedAttribute"); }
            
            var data = {
                cache: false,
                mrn: self.patient.mrn,
                registry_id: app_.registry.id
            };

            // Request updated patient attributes from the web service.
            app_.getNoPreValidate(data, self.processPatientAttributes, app_.processError, self.urlKeys.get_patient_attributes);

        };


        this.processSavedPatientAttribute = function (data_, textStatus_, jqXHR_) {
            //alert("TODO: process saved patient!");
        };
        // I added this for patient removal feature but not sure what processing needs to be done?
        this.processRemovedPatientAttribute = function (data_, textStatus_, jqXHR_) {
            //Reload to make sure the patient attribute count is updated after removal
            window.location.reload();


            
        };


        this.savePatientAttribute = function () {

            if (self.selectedRowIndex < 0) { return app_.displayError("Invalid selected row index", "savePatientAttribute"); }

            // Get the values from the edit dialog.
            // TODO: shouldn't we validate these???
            var attributeName = $_(self.selectors.edit_dialog + " .attributeName").val();
            var assignedOnDate = $_(self.selectors.edit_dialog + " .assignedOnDate").val();
            var cvTermID = $_(self.selectors.edit_dialog + " .cvTermID").val();
            var endAssignedOnDate = $_(self.selectors.edit_dialog + " .endAssignedOnDate").val();
            var userComment = $_(self.selectors.edit_dialog + " .userComment").val();

            // Update the row in the patient attributes grid.
            var row = {
                name: attributeName,
                start_assignment_date: assignedOnDate,
                end_assignment_date: endAssignedOnDate,
                annotator_comment: userComment
            };

            // Use the selected row index to update the displayed row data.
            var rowID = $_(self.selectors.grid).jqxGrid('getrowid', self.selectedRowIndex);
            $_(self.selectors.grid).jqxGrid('updaterow', rowID, row);

            // Hide the dialog
            // TODO: should we clear the contents before it's used again???
            $_(self.selectors.edit_dialog).jqxWindow('hide');


            // Convert dates to the format preferred by Oracle.
            assignedOnDate = convertDateForOracle(assignedOnDate);
            endAssignedOnDate = convertDateForOracle(endAssignedOnDate);

            var data = {
                cache: false,
                registry_id: app_.registry.id,
                registry_patient_cvterm_id: cvTermID,
                start_assignment_date: assignedOnDate,
                end_assignment_date: endAssignedOnDate,
                is_valid: "Y",
                annotator_comment: userComment
            };

            // Call the web service to update the patient attribute.
            app_.getNoPreValidate(data, self.processSavedPatientAttribute, app_.processError, self.urlKeys.update_patient_attribute);
        };


        // For Removing Patient Attribute
         this.removePatientAttribute = function () {

            if (self.selectedRowIndex < 0) { return app_.displayError("Invalid selected row index", "removePatientAttribute"); }

            // Get the values from the edit dialog.
         
            var attributeName = $_(self.selectors.edit_dialog + " .attributeName").val();
            var assignedOnDate = $_(self.selectors.edit_dialog + " .assignedOnDate").val();
            var cvTermID = $_(self.selectors.edit_dialog + " .cvTermID").val();
            var endAssignedOnDate = $_(self.selectors.edit_dialog + " .endAssignedOnDate").val();
            var userComment = $_(self.selectors.edit_dialog + " .userComment").val();

            // Update the row in the patient attributes grid. (I don't think it's needed for Remove)
            var row = {
                name: attributeName,
                start_assignment_date: assignedOnDate,
                end_assignment_date: endAssignedOnDate,
                annotator_comment: userComment
            };

            if (self.selectedRowIndex >= 0 ) {
                 var rowID = $_(self.selectors.grid).jqxGrid('getrowid', self.selectedRowIndex);
                 var commit = $_(self.selectors.grid).jqxGrid('deleterow', rowID);
            }


            // Hide the dialog
            
            $_(self.selectors.edit_dialog).jqxWindow('hide');


            // Convert dates to the format used by Oracle.
            assignedOnDate = convertDateForOracle(assignedOnDate);
            endAssignedOnDate = convertDateForOracle(endAssignedOnDate);

            var data = {
                cache: false,
                registry_id: app_.registry.id,
                registry_patient_cvterm_id: cvTermID,
                start_assignment_date: assignedOnDate,
                end_assignment_date: endAssignedOnDate,
                is_valid: "N",
                annotator_comment: userComment
            };

            // Call the web service to remove the patient attribute.
            app_.getNoPreValidate(data, self.processRemovedPatientAttribute, app_.processError, self.urlKeys.update_patient_attribute);
        };

                
    };

})(jQuery, jQuery.phedrs.app));