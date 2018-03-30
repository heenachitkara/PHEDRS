function TabbedDataGrid($_, key_, tabIndex_, urlKey_) {

    var self = this;

    this.app = $_.phedrs.app;

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
    this.tabIndex = null;

    // The jQuery selector for the tab.
    this.tabSelector = null;

    // The URL key (provided as a parameter) used to lookup the URL of an AJAX web service.
    this.urlKey = null;


    this.getData = function () {

        var data = {
            registry_id: self.app.registry.id,
            tab_key: self.tabIndex
        };
        
        self.app.get(data, self.processTabData, self.app.processError, self.urlKey);
    };

    this.initialize = function () {

        // (Re)initialize the source.
        self.source = {
            localdata: new Array(),
            datatype: "array"
        };

        var dataAdapter = new $.jqx.dataAdapter(self.source, {

            loadComplete: function(data){},
            loadError: function(xhr,status,error) {},
            formatData: function (data) {
                        data.name_startsWith = $("#searchField").val();
                        return data;
                    }

        });

        var centerAlign = function (row, column, value) {
            return '<div style="text-align: left; margin-top: 5px;">' + value + '</div>';
            }

        
        // Initialize the JQX grid object.
        self.grid = $_(self.gridSelector).jqxGrid({

            altrows: true,
            autoshowloadelement: true,
            showemptyrow: false,
            pageable: true,
            pagesizeoptions:['25','50','100','150'],
            pagesize: 50,

            autoheight: true,
            theme: 'classic',
            
            columnsresize: true,
            columns: [
            {
                text: 'Name',
                datafield: 'full_name',
                filterable: false,
                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                     return "<div style=\"margin-left: 10px;\">"+ value_ + "</div>";
                
                },
                width: 200
            },
            {
                text: 'MRN',
                datafield: 'mrn',
                filterable: false,
                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                       return "<div style=\"margin-left: 10px;\">"+ value_ + "</div>";
                
                },
                width: 100
            },
          {
              text: 'Reg status',
              datafield: 'registry_status',
              //cellsalign: 'right',
              cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                  //return value_ + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"https://i.stack.imgur.com/l41qO.png\">";
                  //return value_ + "<span style=\"margin-left: 10px;\"><img src=\"https://i.stack.imgur.com/l41qO.png\" align = \"right\"></span>";
                   //return "<div style=\"margin-left: 10px;\"><span style = \"padding-right : 50px;\">" + value_ + "</span><img src=\"https://i.stack.imgur.com/l41qO.png\" align = \"right\"></div>";
                   return "<div style=\"margin-left: 10px;\"><span style = \"padding-right : 50px;\">" + value_ + "</span><img src=\"https://i.stack.imgur.com/l41qO.png\" margin-right: 20px;\"></div>";
              },
              filterable: false,
              width:140
          },
            {
                text: 'Detection Events',
                datafield: 'detectionEventAbbr', 
                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                    //return "<div class=\"dateColumn\">" + formatDateString(value_) + "</div>";
                   // return "<div style=\"text-align:center;\">"+ value_ + "</div>";
                    return "<div style=\"margin-left: 10px;\">"+ value_ + "</div>";
                
                },
                width: 140
            },
            {
                text: 'Workflow status',
                datafield: 'registryWorkflowStatus',
                //cellsalign: 'right',
                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                    return "<div style=\"margin-left: 10px;\"><span style = \"padding-right : 50px;\">" + value_ + "</span><img src=\"https://i.stack.imgur.com/l41qO.png\" align = \"right\"></div>";
                    //return "<div style=\"margin-left: 10px;\"><span style = \"padding-right : 50px;\">" + value_ + "</span><img src=\"https://i.stack.imgur.com/l41qO.png\" style:\"margin-right:20px;\"></div>";
                },
               // cellsrenderer: centerAlign,
                filterable: false,
                width:220
            },
            {
                text: 'Last contact',
                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                    //return "<div class=\"dateColumn\">" + formatDateString(value_) + "</div>";
                    //return "<div style=\"text-align:center;\"><span class=\"dateColumn\">" + formatDateString(value_) + "</span></div>";
                     return "<div style=\"margin-left: 10px;\"><span class=\"dateColumn\">" + formatDateString(value_) + "</span></div>";
                
                },
                datafield: 'lastContactDate',
                filterable: false
            },
            {
                text: 'Last review',
                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                    return "<div class=\"dateColumn\">" + formatDateString(value_) + "</div>";
                },
                datafield: 'last_review_date',
                filterable: false
            },
            {
                text: 'Reviewed by',
                datafield: 'assigned_by_name',
                filterable: false
            }],

            
            selectionmode: 'multiplecells',
            showdefaultloadelement: true,
            sortable: true,
            source: dataAdapter,
            width: 1130
        });

         $(self.gridSelector).jqxGrid('showloadelement');

        // TODO: dynamically calculate the width property by iterating thru columns and summing their widths?
        $_(self.gridSelector).jqxGrid("autoresizecolumns");

        // Handle tab clicks
        $_(self.tabSelector).click(function () {
            self.getData();
        });

                
        // Handle cell selection
        $_(self.gridSelector).on('cellselect', function (event_) {

                var state = null;

              // save the current state of jqxGrid.
                state = $(self.gridSelector).jqxGrid('savestate');

                jQuery.phedrs.app.setData("grid_saved_state",state);
                  
                
            // Get the data from the selected row.
            var rowIndex = event_.args.rowindex;
            var rowData = $_(self.gridSelector).jqxGrid('getrowdata', rowIndex);

            // Get the selected patient's MRN and name.
            var mrn = rowData["mrn"];
            var patient_name = rowData["full_name"];

            // Create a patient object and add it to application data. 
            self.app.setPatient(mrn, patient_name);
            
            var selectedField = event_.args.datafield;

            if (selectedField === "full_name") {
                
                self.app.setPage("patient_summary_page"); 

            } else if (selectedField === "registry_status") {
            
                // Display the registry status history dialog.
                self.app.displayComponent("registry_status_history_dialog", "application_dialog_container");

            } else if (selectedField === "registryWorkflowStatus" || selectedField === "last_review_date") {

                // Display the workflow_status history dialog.
                self.app.displayComponent("workflow_status_history_dialog", "application_dialog_container");

            } else if (selectedField === "assign") {

               
            } else if (selectedField === "dx") {
                
            } 
        });

    };


    
    this.processTabData = function (data_, textStatus_, jqXHR_) {

        // How many patients are in the list?
        if (data_.registryPatients) {
            self.count = data_.registryPatients.length;
        } else {
            self.count = 0;
        }

        // Provide the grid's source
        $_(self.gridSelector).jqxGrid("source", {
            localdata: data_.registryPatients,
            datatype: "array"
        });

        // Refresh the grid
        $_(self.gridSelector).jqxGrid("refresh");

        // Display the tab's record count
        $_(self.countSelector).html("(" + self.count + ")");

        // Display the tab's panel
        $_(self.tabSelector).tab("show");
    };
    



    // Validate input parameters
    if (!key_) { alert("Invalid key in TabbedDataGrid"); return null; }
    self.key = key_;

    if (!tabIndex_) { alert("Invalid tabIndex in TabbedDataGrid"); return null; }
    self.tabIndex = tabIndex_;

    if (!urlKey_) { alert("Invalid URL key in TabbedDataGrid"); return null; }
    self.urlKey = urlKey_;

    // Use the key to generate unique jQuery selectors used by this object.
    self.countSelector = "#count_" + key_;
    self.gridSelector = "#jqxgrid_" + key_;
    self.panelSelector = "#panel_" + key_;
    self.tabSelector = "#tab_" + key_;


    // Begin initialization   
    self.initialize();
};
