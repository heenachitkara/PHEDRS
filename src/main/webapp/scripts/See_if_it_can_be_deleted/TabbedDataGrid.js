function TabbedDataGrid(key_, tabIndex_, urlKey_/*,urlLoginKey_*/) {

    var self = this;

    // For Login Webservice
   // var urlLoginKey = "login";

    this.cellClicked = false;

    // The number of records in the list/grid
    this.count = 0;

    // The jQuery selector for the tab's count label.
    this.countSelector = null;

    // This key string is used to generate unique identifiers for each of this object's components.
    // It can have letters, numbers, an underscore, or a hyphen and SHOULD NOT contain any whitespace!
    this.key = null;

    // The JQX grid object
    this.grid = null;

    // The jQuery selector for the grid.
    this.gridSelector = null;

    // The jQuery selector for the tab's panel.
    this.panelSelector = null;

    // The source will contain data retrieved by an AJAX web service.
    this.source = {
        localdata: new Array(),
        datatype: "array"
    };

    // TODO: this needs to be replaced!!!
    this.tabIndex = -1;

    // The jQuery selector for the tab.
    this.tabSelector = null;

    // The URL key (provided as a parameter) used to lookup the URL of an AJAX web service.
    this.urlKey = null;

    this.urlLoginKey = null;



    


    this.getData = function () {

        // Lookup the AJAX web service URL
        // TODO: why don't we just initialize this object with the URL instead of the key???
        var url = regman.getWebServiceURL(self.urlKey);
        if (isEmpty(url)) { alert("Invalid URL in getData()"); return false; }

        var ajaxRequest = jQuery.ajax({
            //beforeSend: TODO: show spinner!
            data: {
                registry_id: regman.registry.id,
                //tab_number: self.tabIndex
                tab_type: self.tabIndex
            },
            dataType: "json",
            method: "GET",
            url: url
        })
        .done(function (data_, textStatus_, jqXHR_) {

            // Validate the web service and retrieve the status.
            if (typeof (data_) === "undefined" || data_ === null) { alert("Invalid data returned from web service"); return false; }
            if (isEmpty(data_.webservice_status) || isEmpty(data_.webservice_status.status)) { alert("Invalid web service status"); return false; }
            if (data_.webservice_status.status != "SUCCESS") { alert(data_.webservice_status.message); return false; }

            // How many patients are in the list?
            if (typeof (data_.registry_patient_list) !== "undefined" && data_.registry_patient_list !== null) {
                self.count = data_.registry_patient_list.length;
            }

           // console.log("Checking Patient List:"+data_.registry_patient_list);
            // Provide the grid's source
            jQuery(self.gridSelector).jqxGrid("source", {
                localdata: data_.registry_patient_list,
                datatype: "array"
            });

            // Refresh the grid
            jQuery(self.gridSelector).jqxGrid("refresh");

            // Display the tab's record count
            jQuery(self.countSelector).html("(" + self.count + ")");

            // Display the tab's panel
            jQuery(self.tabSelector).tab("show");

        })
        .fail(function (jqXHR_, textStatus_, errorThrown_) {
            alert("Error in getData(): " + errorThrown_);
            return false;
        });
    };

    this.initialize = function () {
         
         // self.getLoginInfo();
        // (Re)initialize the source.
        self.source = {
            localdata: new Array(),
            datatype: "array"
        };
        // Initialize the JQX grid object.
        self.grid = jQuery(self.gridSelector).jqxGrid({

            altrows: true,
            columnsResize: true,
            pageable: true,
            height: 500,
            autoheight: true,
            theme: 'classic',
            columns: [
            {
                text: 'Name',
                datafield: 'full_name',
                filterable: false,
                width: 200
            },
            {
                text: 'MRN',
                datafield: 'mrn',
                filterable: false,
                width: 100
            },
            {
                text: 'Registry Status',
                datafield: 'registry_status',
                filterable: false,
                width: 120
            },
            {
                text: 'Detection Events',
                datafield: 'detectionEventName',
                width: 200
            },
            {
                text: 'Workflow status',
                datafield: 'registryWorkflowStatus',
                filterable: false,
                width: 160
            },
            {
                text: 'Last contact',
                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {

                    var formattedDate = "";
                    if (!isEmpty(value_)) {
                        var dateObject = new Date(value_);
                        formattedDate = (dateObject.getMonth() + 1) + "/" + dateObject.getDate() + "/" + dateObject.getFullYear();
                    }
                    return "<div class=\"dateColumn\">" + formattedDate + "</div>";
                },
                datafield: 'lastContactDate',
                filterable: false,
                width: 100
            },
            {
                text: 'Last review',
                datafield: 'last_review_date',
                filterable: false,
                width: 100
            },
            {
                text: 'Assigned to',
                datafield: 'assigned_by_name',
                filterable: false,
                width: 100
            },
            {
                text: 'History',
                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                    return "<div class=\"historyIcon\" title=\"View History\" id=\"historyIcon\">&nbsp;</div>";
                },
                datafield: 'history',
                filterable: false,
                sortable: false,
                width: 60
            }],

            selectionmode: 'multiplecells',
            sortable: true,
            source: self.source,
            width: 1130
        });

        // TODO: dynamically calculate the width property by iterating thru columns and summing their widths?

        // Handle tab clicks
        jQuery(self.tabSelector).click(function () {
            self.getData();
        });


        // Handle cell selection
        jQuery(self.gridSelector).on('cellselect', function (event_) {



            // Get the data from the selected row.
            var rowIndex = event_.args.rowindex;
            var rowData = jQuery(self.gridSelector).jqxGrid('getrowdata', rowIndex);

            // Get the selected patient's MRN and maintain in the Application Manager.
            var mrn = rowData["mrn"];
            regman.selectData("mrn", mrn);


            var selectedField = event_.args.datafield;

            if (selectedField === "mrn" ||
                selectedField === "full_name" ||
                selectedField === "last_review" ||
                selectedField === "status") {
                if (self.cellClicked) {
                    return false;
                } else {
                    self.cellClicked = true;
                }
                // TODO: should we handle the click event for other columns, as well?

                // Display the patient review page.
                regman.displayComponent("patient_review_page");

            } else if (selectedField === "assign") {

                // TODO: implement this!
                //regman.displayComponent("assign_registrar_dialog");

                // For reference, this is what was called in the original CRCP:
                // self.showUserListDialog(data.mrn, data.patname, data.statusid, data.assigned_to_id, data.id, rowindex);

            } else if (selectedField === "dx") {

                // TODO: implement this!
                //regman.displayComponent("diagnosis_dialog");


                // For reference, this is what was called in the original CRCP:
                // self.showDiagnosisListDialog(data.mrn, data.patname, data.id);

            }  else if (selectedField === "detectionEventName") {

                //regman.displayComponent("detection_event_dialog");

                 
                jQuery("#detectionCode").html(rowData["full_name"] + " (" + rowData["mrn"] + ")");
                self.getDetectionDetails(mrn);
                
                $('#detectionCodeDetails').dialog({
                    position: { my: 'center', at: 'center' },
                    modal: true,
                    width: 400,
                    height: 290,
                    dialogClass: "dialogHistory",
                    buttons: {
                        Close: function () {
                            $(this).dialog("close")
                        }
                    }
                });




                
            }



            else if (selectedField === "history") {
               
                jQuery("#hisotryName").html(rowData["full_name"] + " (" + rowData["mrn"] + ")");
                self.getHistoryDetails(mrn);
                $('#historyDetails').dialog({
                    position: { my: 'center', at: 'center' },
                    //modal: true,
                    width: 700,
                    height: 300,
                    dialogClass: "dialogHistory",
                    buttons: {
                        Close: function () {
                            $(this).dialog("close")
                        }
                    }
                });

                

                // TODO: implement this!
                //regman.displayComponent("patient_registry_history_dialog");

                // For reference, this is what was called in the original CRCP:
                // self.showHistoryListDialog(data.mrn, data.patname, data.id);
            }
        });

    };


    // Validate input parameters
    if (isEmpty(key_)) { alert("Invalid key in TabbedDataGrid"); return null; }
    self.key = key_;

    if (isEmpty(tabIndex_)) { alert("Invalid tabIndex in TabbedDataGrid"); return null; }
    self.tabIndex = tabIndex_;

    if (isEmpty(urlKey_)) { alert("Invalid URL key in TabbedDataGrid"); return null; }
    self.urlKey = urlKey_;

    // Use the key to generate unique jQuery selectors used by this object.
    self.countSelector = "#count_" + key_;
    self.gridSelector = "#jqxgrid_" + key_;
    self.panelSelector = "#panel_" + key_;
    self.tabSelector = "#tab_" + key_;


    // Begin initialization
    self.initialize();

     // Get the Login Details

    this.getLoginInfo =  function () {

       // Lookup the AJAX web service URL
        var url = regman.getWebServiceURL("showinfo");

        console.log("URL for Login in TabbedDataGrid:"+url);

        if (isEmpty(url)) { alert("Invalid URL in getShowInfo()"); return false; }

       

       var ajaxRequest = jQuery.ajax({
            //beforeSend: TODO: show spinner!
            data: {
                registry_id: regman.registry.id
                
            },
            dataType: "json",
            method: "GET",
            url: url
        })
        .done(function (data_, textStatus_, jqXHR_) {

            // Validate the web service and retrieve the status.
            if (typeof (data_) === "undefined" || data_ === null) { alert("Invalid data returned from Encounter Web Service"); return false; }
            if (isEmpty(data_.webservice_status) || isEmpty(data_.webservice_status.status)) { alert("Invalid Web Service Status for Encounters!"); return false; }
            if (data_.webservice_status.status != "SUCCESS") { alert(data_.webservice_status.message); return false; }

            // Process and display Encounters
           self.processLogin(data_.login_info);

        })
        .fail(function (jqXHR_, textStatus_, errorThrown_) {
            alert("Error in getShowInfo(): " + errorThrown_);
            return false;
        });
     

     }  // End of getLoginInfo



     this.processLogin = function (collection_) {

        console.log("Checking Collection for Login Information: " + collection_.length);
        

   }



    this.getHistoryDetails = function (mrn_) {
        var url = regman.getWebServiceURL("getregistrypatienthistory");
         //console.log("URL for Patient History in TabbedDataGrid:"+url);
        if (isEmpty(url)) { alert("Invalid URL in getHistoryDetails()"); return false; }

        var ajaxRequest = jQuery.ajax({
            data: {
                registry_id: regman.registry.id,
                mrn: mrn_
            },
            dataType: "json",
            method: "GET",
            url: url
        })
        .done(function (data_, textStatus_, jqXHR_) {
            if (typeof (data_) === "undefined" || data_ === null) { alert("Invalid data returned from web service"); return false; }
            if (isEmpty(data_.webservice_status) || isEmpty(data_.webservice_status.status)) { alert("Invalid web service status"); return false; }
            if (data_.webservice_status.status != "SUCCESS") { alert(data_.webservice_status.message); return false; }
            self.processHistoryDetails(data_.registry_patient_history_list);
        })
        .fail(function (jqXHR_, textStatus_, errorThrown_) {
            alert("Error in getHistoryDetails(): " + errorThrown_);
            return false;
        });
    };

    this.getDetectionDetails = function (mrn_) {
        var url = regman.getWebServiceURL("getdetectioncodes");
        if (isEmpty(url)) { alert("Invalid URL in getDetectionDetails()"); return false; }

        var ajaxRequest = jQuery.ajax({
            data: {
                registry_id: regman.registry.id,
                mrn: mrn_
            },
            dataType: "json",
            method: "GET",
            url: url
        })
        .done(function (data_, textStatus_, jqXHR_) {
            if (typeof (data_) === "undefined" || data_ === null) { alert("Invalid data returned from Detection Code Web Service"); return false; }
            if (isEmpty(data_.webservice_status) || isEmpty(data_.webservice_status.status)) { alert("Invalid Seb Service Status"); return false; }
            if (data_.webservice_status.status != "SUCCESS") { alert(data_.webservice_status.message); return false; }
            self.processDetectionCodes(data_.registry_detection_code_list);
        })
        .fail(function (jqXHR_, textStatus_, errorThrown_) {
            alert("Error in getDetectionDetails(): " + errorThrown_);
            return false;
        });
    };


     this.processDetectionCodes = function (collection_) {

        var source = {
            localdata: collection_,
            datatype: "array"
        };
        var dataAdapter = new $.jqx.dataAdapter(source, {
            loadComplete: function (data) { },
            loadError: function (xhr, status, error) { }
        });
        $("#detectionDetailsPanel").jqxGrid({
            source: dataAdapter,
            width: 1200,
            height: 150,
            columnsResize: true,

            columns: [{
                text: 'Detection',
                datafield: 'accession',
                width: 150
            } ,
            {
                text: 'Description',
                datafield: 'code_value',
                width: 400,
                hidden: true
            },
            {
                text: 'Detection Date',

                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                    var formattedDate = "";
                    if (!isEmpty(value_)) {
                        var dateObject = new Date(value_);
                        formattedDate = (dateObject.getMonth() + 1) + "/" + dateObject.getDate() + "/" + dateObject.getFullYear();
                    }
                    return formattedDate;
                },
                datafield: 'code_assignment_date',
                width: 200
            }

            ]
        });


    };


    this.processHistoryDetails = function (collection_) {

        var source = {
            localdata: collection_,
            datatype: "array"
        };
        var dataAdapter = new $.jqx.dataAdapter(source, {
            loadComplete: function (data) { },
            loadError: function (xhr, status, error) { }
        });
        $("#historyDetailsPanel").jqxGrid({
            source: dataAdapter,
            width: 800,
            height: 150,
            columnsResize: true,

           

            columns: [/*{
                text: 'Changer ID',
                datafield: 'changer_id',
                width: 150
            },*/ {
                text: 'Previous Registry Status',
                datafield: 'previous_status',
                width: 150
            }, {
                text: 'Current Registry Status',
                datafield: 'current_status',
                width: 150
            }, {
                text: 'Previous Review Status',
                datafield: 'prev_review_status',
                width: 160
            }, {
                text: 'Current Review Status',
                datafield: 'curr_review_status',
                width: 150
            }, {
                text: 'Date',

                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                    var formattedDate = "";
                    if (!isEmpty(value_)) {
                        var dateObject = new Date(value_);
                        formattedDate = (dateObject.getMonth() + 1) + "/" + dateObject.getDate() + "/" + dateObject.getFullYear();
                    }
                    return formattedDate;
                },
                datafield: 'change_date',
                width: 100
            }

            ]
        });


    };
};
