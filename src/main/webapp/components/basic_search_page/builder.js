
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "advanced search" page builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "basic_search_page";

        this.selectedText = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service URL key.
        this.urlKeys = {
            search: "search"
        };

        
        self.selectedText = app_.getData("entered_search_text");

        console.log("Retrieved Value:"+self.selectedText);


        
        // Initialize the page
        this.initialize = function () {
                
            self.search();



            /*
            $_("#basic_search_page .search-button").click(function () {
                self.search();
            });*/

            /*$_("#advanced_search_page .clear-button").click(function () {
                self.clearSearch();
            });*/
            
                       

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };


        this.processSearchResults = function (data_, textStatus_, jqXHR_) {

            // Make sure data is a JSON object.
            data_ = app_.processResponse(data_, jqXHR_);

            console.log(data_);

            // Validate the data and registry patients. If no registry patients were returned, no need to continue.
            if (!data_ || !data_.registryPatients) {
                $_("#search_results_panel_count").html("(" + 0 + ")");
                return false;
            }

            // Populate the count and display the results container.
            $_("#search_results_panel_count").html("(" + data_.registryPatients.length + ")");
            $_("#search_results_container").show();

            var source = {
                localdata: data_.registryPatients,
                datatype: "array"
            };

            // Initialize the JQX grid object.            
            var grid = $_("#search_results_panel_grid").jqxGrid({

                altrows: true,
                autoshowloadelement: true,
                pageable: true,
                autoheight: true,
                showtoolbar: true,
                theme: 'classic',

                columnsresize: true,
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
                    text: 'Reg status',
                    datafield: 'registry_status',                    
                    filterable: false,
                    width: 100
                },
                {
                    text: 'Detection Events',
                    datafield: 'detectionEventAbbr', 
                    width: 100
                },
                {
                    text: 'Workflow status',
                    datafield: 'registryWorkflowStatus',
                    filterable: false,
                    width: 100
                },
                {
                    text: 'Last contact',
                    cellsalign: "middle",
                    cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                        return "<div class=\"dateColumn\">" + formatDateString(value_) + "</div>";
                    },
                    datafield: 'lastContactDate',
                    filterable: false,
                    width: 100
                },
                {
                    text: 'Last review',
                    cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                        return "<div class=\"dateColumn\">" + formatDateString(value_) + "</div>";
                    },
                    datafield: 'last_review_date',
                    filterable: false,
                    width: 100
                },
                {
                    text: 'Reviewed by',
                    datafield: 'assigned_by_name',
                    filterable: false,
                    width: 100
                }],


                selectionmode: 'singlerow',
                showdefaultloadelement: true,
                sortable: true,
                source: source,
                width: 785
            });

            /* For Showing Loading Icon if needed : $("#search_results_panel_grid").jqxGrid('showloadelement');*/

            // TODO: dynamically calculate the width property by iterating thru columns and summing their widths?
            $_("#search_results_panel_grid").jqxGrid("autoresizecolumns");

            /*$("#excelExport").jqxButton({
                 theme: 'energyblue'
             });*/ 

            $("#excelExport").click(function() {
                 $("#search_results_panel_grid").jqxGrid('exportdata', 'xls', 'Search_Results');
            });

            
            // Handle row selection
            $_("#search_results_panel_grid").on('rowselect', function (event_) {
                console.log("row select");
                // Get the data from the selected row.
                var rowIndex = event_.args.rowindex;
                var rowData = $_("#search_results_panel_grid").jqxGrid('getrowdata', rowIndex);

                // Get the selected patient's MRN and name.
                var mrn = rowData["mrn"];
                var patient_name = rowData["full_name"];

                // Create a patient object and add it to application data. 
                app_.setPatient(mrn, patient_name);

                app_.setPage("patient_summary_page");
            });
        };


       
        this.search = function () {

            //var firstName = $_("#search_first_name").val();
            //var lastName = $_("#search_last_name").val();
            //var mrn = $_("#search_mrn").val();
            var data = {
                registry_id: app_.registry.id,
                //first_name: firstName,
               // last_name: lastName,
                mrn:  self.selectedText
               
            };

            app_.get(data, self.processSearchResults, app_.displayError, self.urlKeys.search);
        };
    };

})(jQuery, jQuery.phedrs.app));