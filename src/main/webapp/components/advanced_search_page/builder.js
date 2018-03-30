
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "advanced search" page builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "advanced_search_page";

        this.patient = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service URL key.
        this.urlKeys = {
            get_registry_users: "getregistryusers",
            search: "search"
        };

        this.users = null;


        // Clear all search controls.
        this.clearSearch = function () {

            $_("#search_first_name").val("");
            $_("#search_last_name").val("");
            $_("#search_mrn").val("");
            //$_("#search_ssn").val("");
            $_("#search_registry_status option:selected").prop("selected", false);
            $_("#search_assigned_by option:selected").prop("selected", false);
            $_("#search_from_assignment_date").val("");
            $_("#search_to_assignment_date").val("");
            $_("#search_workflow_status option:selected").prop("selected", false);
            $_("#search_reviewer option:selected").prop("selected", false);
            $_("#search_from_review_date").val("");
            $_("#search_to_review_date").val("");
            $_("#search_detection_events option:selected").prop("selected", false);
            $_("#search_patient_attributes option:selected").prop("selected", false);
            $_("#search_encounter_attributes option:selected").prop("selected", false);
            $_("#search_from_encounter_date").val("");
            $_("#search_to_encounter_date").val("");


            // Clear the grid.
            $_("#search_results_panel_grid").jqxGrid("clear");
            $_("#search_results_panel_grid").jqxGrid("refresh");

            // Clear the result count and hide the container.
            $_("#search_results_panel_count").html("");
            $_("#search_results_container").hide();
        };

        this.getRegistryUsers = function () {

            var data = {
                registry_id: app_.registry.id
            };

            app_.get(data, self.processRegistryUsers, app_.processError, self.urlKeys.get_registry_users);
        };


        // Initialize the page
        this.initialize = function () {

            $_("#advanced_search_page .search-button").click(function () {
                self.search();
            });

            $_("#advanced_search_page .clear-button").click(function () {
                self.clearSearch();
            });
            
            // Convert date fields into datepickers.
            $_("#search_from_assignment_date").datepicker();
            $_("#search_to_assignment_date").datepicker();
            $_("#search_from_review_date").datepicker();
            $_("#search_to_review_date").datepicker();
            $_("#search_from_encounter_date").datepicker();
            $_("#search_to_encounter_date").datepicker();

            // Populate the list of registry statuses.
            app_.populateTermList("PHEDRS Patient Status CV", "#search_registry_status", true);

            // Populate the list of workflow statuses.
            app_.populateTermList("PHEDRS Review Status CV", "#search_workflow_status", true);

            // Populate the list of patient attributes.
           // app_.populateTermList("UAB COPD Registry Patient Attributes,PHEDRS Patient Attribute Ontology", "#search_patient_attributes", true);
            var data = {
                registry_id: app_.registry.id
            };

            // Request patient attributes from the web service.
            app_.getNoPreValidate(data, self.addPatientAttributesDropDowndata, app_.processError, "getRegistryPatientAttributes");

            // Populate the list of detection events.
           app_.populateTermList("Patient Registry Evidence Codes", "#search_detection_events", true);

            // Populate the list of encounter attributes.
           // app_.populateTermList("UAB COPD Registry Encounter Attribute CV", "#search_encounter_attributes", true);
            app_.getNoPreValidate(data, self.addEncounterAttributesDropDowndata, app_.processError, "getRegistryEncounterAttributes");
            // Get a list of registry users (TODO: do we need to specify roles here?)
            self.getRegistryUsers();

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
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
            var html = "<option value=''></option>";
            $_.each(collection, function (index_, term_) {
                //alert("term" + term_.patienrCvTermName)
                //if (!term_ || !term_.cvTermID || !term_.patienrCvTermName) { return self.displayError("Invalid term at index " + index_, "populateTermList"); }

                html += "<option value='" + term_.cvTermID + "'>" + term_.patienrCvTermName + "</option>";
            });
           
            $_("#search_patient_attributes").html(html);
        };

        this.addEncounterAttributesDropDowndata = function (data_, textStatus_, jqXHR_) {

            
            data_ = app_.processResponse(data_, jqXHR_);


            var attributeCount = 0;
            var collection = data_._reg_config_encounter_attributes;
            if (collection) { attributeCount = collection.length; }


            var source = {
                localdata: collection,
                datatype: "array",
                datafields:
                [
                    { name: 'encounterCvTermName', type: 'string' },
                    { name: 'encounterCvTermDefinition', type: 'string' },
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
            var html = "<option value=''></option>";
            $_.each(collection, function (index_, term_) {
                //alert("term encounterCvTermName" + term_.encounterCvTermName)
                //if (!term_ || !term_.cvTermID || !term_.patienrCvTermName) { return self.displayError("Invalid term at index " + index_, "populateTermList"); }

                html += "<option value='" + term_.cvTermID + "'>" + term_.encounterCvTermName + "</option>";
            });

            $_("#search_encounter_attributes").html(html);
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
            
            //Show Loading Icon related work in progress (Please don't remove !)

           /* $("#jqxLoader").jqxLoader({ width: 100, height: 60, imagePosition: 'top', autoOpen: true });


            $("#search_results_panel_grid").on("bindingcomplete", function (event) {
                $('#jqxLoader').jqxLoader('close');
            }); */

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
                width: 823
            });

            //Show Loading Icon related work in progress (Please don't remove !)
          // $("#search_results_panel_grid").jqxGrid('showloadelement');

            // TODO: dynamically calculate the width property by iterating thru columns and summing their widths?
            $_("#search_results_panel_grid").jqxGrid("autoresizecolumns");

           /* $("#excelExport").jqxButton({
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


        this.processRegistryUsers = function (data_, textStatus_, jqXHR_) {

            // Make sure data is a JSON object.
            data_ = app_.processResponse(data_, jqXHR_);

            // Populate the local collection of users for later use.
            self.users = data_.users;

            var html = "<option value=''></option>";
            $_.each(data_.users, function (index_, user_) {
                html += "<option value='" + user_.userID + "'>" + user_.fullName + "</option>";
            });

            // Populate the "assigned by" and reviewer lists.
            $_("#search_assigned_by").html(html);
            $_("#search_reviewer").html(html);
        };


        this.search = function () {

            /*
            search_first_name
            search_last_name
            search_mrn
            search_ssn
            search_registry_status
            search_assigned_by
            search_from_assignment_date
            search_to_assignment_date
            earch_workflow_status
            search_reviewer
            search_from_review_date
            search_to_review_date
            search_detection_events
            search_patient_attributes
            search_encounter_attributes
            search_from_encounter_date
            search_to_encounter_date
            */

            var firstName = $_("#search_first_name").val();
            var lastName = $_("#search_last_name").val();
            var mrn = $_("#search_mrn").val();
            //var ssn = $_("#search_ssn").val();

            // Registry status
            var registryStatus = "";
            $_("#search_registry_status option:selected").each(function () {
                registryStatus += $_(this).attr("value") + ",";
            });
            var assignedBy = $_("#search_assigned_by").val();
            var fromAssignmentDate = $_("#search_from_assignment_date").val();
            var toAssignmentDate = $_("#search_to_assignment_date").val();

            // Workflow status
            var workflowStatus = "";
            $_("#search_workflow_status option:selected").each(function () {
                workflowStatus += $_(this).attr("value") + ",";
            });
            var reviewer = $_("#search_reviewer").val();
            var fromLastReview = $_("#search_from_review_date").val();
            var toLastReview = $_("#search_to_review_date").val();

            var detectionEvents = "";
            $_("#search_detection_events option:selected").each(function () {
                detectionEvents += $_(this).attr("value") + ",";
            });

            var patientAttributes = "";
            $_("#search_patient_attributes option:selected").each(function () {
                patientAttributes += $_(this).attr("value") + ",";
            });

            var encounterAttributes = "";
            $_("#search_encounter_attributes option:selected").each(function () {
                encounterAttributes += $_(this).attr("value") + ",";
            });

            var fromEncounterDate = $_("#search_from_encounter_date").val();
            var toEncounterDate = $_("#search_to_encounter_date").val();


            // TODO: since there are so many parameters, do we need to make this a POST?
            var data = {
                registry_id: app_.registry.id,

                first_name: firstName,
                last_name: lastName,
                mrn: mrn,
                //ssn: ssn,

                registry_status_id: registryStatus,
                assigned_by: assignedBy,
                assignment_date_from: fromAssignmentDate,
                assignment_date_to: toAssignmentDate,

                workflow_status_id: workflowStatus,
                reviewed_by: reviewer,
                last_review_from: fromLastReview,
                last_review_to: toLastReview,

                detection_events: detectionEvents,
                encounter_attributes: encounterAttributes,
                patient_attributes: patientAttributes,
                
                encounter_date_from: fromEncounterDate,
                encounter_date_to: toEncounterDate
            };

            app_.get(data, self.processSearchResults, app_.displayError, self.urlKeys.search);
        };
    };

})(jQuery, jQuery.phedrs.app));