
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "add by mrn" page builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "add_by_mrn_page";

        // Temporary storage of minimal patient info.
        this.patient = {
            full_name: null,
            mrn: null
        };
        
        this.selectors = {
            error_panel: "#add_by_mrn_page .error-panel",
            existing_patient_panel: "#add_by_mrn_page .existing-patient-panel",
            new_patient_panel: "#add_by_mrn_page .new-patient-panel",
            patient_added_panel: "#add_by_mrn_page .patient-added-panel",
            preview_panel: "#add_by_mrn_page .patient-data-preview"
        };

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service URL keys.
        this.urlKeys = {
            add_mrn: "addmrn",
            lookup_mrn: "lookupmrn"
        };



        this.addMrn = function () {

            if (!self.patient.mrn) { alert("Error: invalid MRN"); return false; }

            var status_id = $_("select.status-id-ctrl option:selected").val();
            var status_comment = $_("textarea.status-comment-ctrl").val();

            var review_status_id = $_("select.review-status-id-ctrl option:selected").val();
            var review_comment = $_("textarea.review-status-comment-ctrl").val();

            var data = {
                mrn: self.patient.mrn,
                registry_id: app_.registry.id,
                status_id: status_id,
                review_status_id: review_status_id,
                changer_id: app_.user.id,
                reg_status_change_comment: status_comment,
                workflow_status_change_comment: review_comment
            };

            // Clear the comment fields in case they're used again, then hide the new patient panel.
            $_("textarea.status-comment-ctrl").val("");
            $_("textarea.review-status-comment-ctrl").val("");
            $_(self.selectors.new_patient_panel).hide();

            app_.getNoPreValidate(data, self.processAddedMRN, app_.displayError, self.urlKeys.add_mrn);
        };


        // Initialize the page
        this.initialize = function () {

            $_("button.add-button").click(function () {
                self.addMrn();
            });

            $_("button.lookup-button").click(function () {
                self.lookupMRN();
            });

            $_("button.view-patient-button").click(function () {

                app_.setPatient(self.patient.mrn, self.patient.full_name);

                app_.setPage("patient_summary_page");
            });

            // Collapse result panels
            $_(self.selectors.error_panel).hide();
            $_(self.selectors.existing_patient_panel).hide();
            $_(self.selectors.new_patient_panel).hide();
            $_(self.selectors.patient_added_panel).hide();
            $_(self.selectors.preview_panel).hide();

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };


        this.lookupMRN = function () {

            var mrn = $_("input.mrn-ctrl").val();
            if (!mrn) { alert("Please enter an MRN to lookup"); return false; }

            mrn = mrn.trim();
            if (mrn.length < 1) { alert("Please enter a valid MRN to lookup"); return false; }

            self.patient.mrn = mrn;

            // Collapse result panels
            $_(self.selectors.error_panel).hide();
            $_(self.selectors.existing_patient_panel).hide();
            $_(self.selectors.new_patient_panel).hide();
            $_(self.selectors.patient_added_panel).hide();
            $_(self.selectors.preview_panel).hide();


            var data = {
                mrn: mrn,
                registry_id: app_.registry.id
            };

            app_.getNoPreValidate(data, self.processLookupMRN, app_.displayError, self.urlKeys.lookup_mrn);
        };


        this.processAddedMRN = function (data_, textStatus_, jqXHR_) {

            // Make sure a JSON object was returned.
            data_ = app_.processResponse(data_, jqXHR_);

            console.log(data_);

            // Validate the contents of the results.
            if (!data_ || !data_.webServiceStatus) {
                return app_.displayError("Invalid response", "processAddedMRN");

            } else if (data_.webServiceStatus.status !== "SUCCESS") {

                var message = data_.webServiceStatus.message;
                if (!message) { message = "An unknown error occurred"; }

                $_(self.selectors.error_panel).html(message).show();

            } else {
                $_(self.selectors.patient_added_panel).show();
            }
        };


        this.processLookupMRN = function (data_, textStatus_, jqXHR_) {

            // Make sure a JSON object was returned.
            data_ = app_.processResponse(data_, jqXHR_);

            console.log(data_);

            // Validate the contents of the results.
            if (!data_ || !data_.webServiceStatus || data_.webServiceStatus.status !== "SUCCESS" || !data_.lookupStatus) { return app_.displayError("Invalid response","processLookupMRN"); }

            if (data_.lookupStatus === "invalid_mrn" || !data_.demographics) {
                // The MRN must've been invalid.
                $_(self.selectors.error_panel).html("This MRN is invalid").show();
                return false;
            }
            
            // Get the patient data that will be displayed in the preview panel.
            var first_name = data_.demographics.first_name;
            if (!first_name) { first_name = "(Invalid first name)"; }

            var last_name = data_.demographics.last_name;
            if (!last_name) { last_name = "(Invalid last name)"; }

            self.patient.full_name = first_name + " " + last_name;
            
            var birth_date = data_.demographics.birth_date;
            if (!birth_date) {
                birth_date = "(Invalid birth date)";
            } else {
                birth_date = formatDateString(birth_date);
            }

            var gender = data_.demographics.gender;
            gender = (!gender ? "(unknown gender)" : formatGender(gender));

            var race = data_.demographics.race;
            race = (!race ? "(unknown race)" : formatRace(race));

            var raceGenderAge = race + " " + gender + ", " + calculateAge(birth_date);


            // Update preview panel
            $_(".patient-data-preview .full-name").html(self.patient.full_name);
            $_(".patient-data-preview .mrn").html("<label for='mrn'>MRN:</label> " + self.patient.mrn);
            $_(".patient-data-preview .race-gender-age").html(raceGenderAge);

            $_(self.selectors.preview_panel).show();


            /*
            "patientInfo" : {
                "webservice_status" : null,
                "registry_patient" : {
                    "registry_patient_id" : 98059,
                    "mrn" : null,
                    "full_name" : null,
                    "registry_status" : "Candidate",
                    "registrar_comment" : "test",
                    "last_review_date" : null,
                    "assigned_by_name" : null,
                    "lastContactDate" : null,
                    "registryWorkflowStatus" : "Patient Never Reviewed",
                    "workflow_comment" : "blah",
                    "detectionEventId" : 0,
                    "detectionEventName" : null,
                    "detectionEventAbbr" : null
                },
                "patient_demographics" : {
                    "numeric_patient_id" : 0,
                    "mrn" : "421083",
                    "full_name" : "LULA ALLISON",
                    "first_name" : "LULA",
                    "last_name" : "ALLISON",
                    "phone_number" : null,
                    "address1" : null,
                    "address2" : null,
                    "city" : "BIRMINGHAM",
                    "state" : null,
                    "zipcode" : "35211",
                    "birth_date" : "1938-06-19",
                    "gender" : "F",
                    "race" : "B",
                    "ethnicity" : null
                },
                "nlp_document_list" : null,
                "registry_encounters_list" : null,
                "registry_patient_diagnosis_codes_list" : null,
                "registry_all_patient_attribute_list" : null
            }*/


            if (data_.lookupStatus === "already_in_registry") {
                $_(self.selectors.existing_patient_panel).show();
            } else {

                // Populate the dialog's list of registry statuses.
                app_.populateTermList("PHEDRS Patient Status CV", ".new-patient-panel .status-id-ctrl");

                // Populate the dialog's list of workflow statuses.
                app_.populateTermList("PHEDRS Review Status CV", ".new-patient-panel .review-status-id-ctrl");

                $_(self.selectors.new_patient_panel).show();
            }

        };
    };

})(jQuery, jQuery.phedrs.app));