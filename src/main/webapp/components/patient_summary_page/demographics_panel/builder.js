
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The demographics panel builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "patient_summary_page.demographics_panel";

        // The currently-selected patient.
        this.patient = null;

        // For saved state of the grid
        this.savedState = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        this.selectors = {
            edit_registry_status_dialog: "#patient_demographics_edit_registry_status_dialog",
            edit_workflow_status_dialog: "#patient_demographics_edit_workflow_status_dialog",
            /*owner*/
            edit_owner_name_dialog: "#patient_demographics_edit_owner_name_dialog"
        };

        this.urlKeys = {
            get_demographics: "getpatientdemographics",
            update_registry_status: "updateregistrypatientstatushistory"
        };
        
        //Test for Heena Oct 11 git issue; Remove once it's done

        //$("#loadState").jqxButton({ theme: "darkblue" });

         self.savedState = app_.getData("grid_saved_state");

        // console.log("Retrieved Value:"+self.selectedText);

        console.log("Retrieved State Value INSIDE Demographics Panel Builder.js:");
        console.log(self.savedState);


        // Initialize the component.
        this.initialize = function () {

            // Get the selected patient from application data.
            self.patient = app_.getPatient();
            if (!self.patient || !self.patient.mrn) { return app_.displayError("Invalid selected patient", "demographics panel initialize"); }

            // Handle the onclick of the "edit registry status" control.
            $_("#edit_patient_registry_status").click(function () {

                // Populate the dialog's list of registry statuses.
                //app_.populateTermList("PHEDRS Patient Status CV", ".edit-registry-status-dialog .registry-status");


                // Open the dialog.
                $_("#edit_patient_registry_status").popModal({
                    html: $_(self.selectors.edit_registry_status_dialog).html(),
                    onOkBut: function () {

                        // Get the status ID and comment from the dialog.
                        var statusID = $_(".edit-registry-status-dialog .registry-status").val();

                        console.log("Checking Status Value after click:", statusID);

                        var statusComment = $_(".edit-registry-status-dialog .registry-status-comment").val();

                        if (!self.patient.registry_patient_id) { return app_.displayError("Invalid registry patient ID", "edit registry status dialog"); }

                        var data = {
                            registry_id: app_.registry.id,
                            registry_patient_id: self.patient.registry_patient_id,
                            new_status_id: statusID,
                            changer_id: app_.user.id,
                            reg_status_change_comment: statusComment
                        };
                       
                        app_.get(data, self.processUpdatedRegistryStatus, app_.processError, self.urlKeys.update_registry_status);                        
                    }
                });
               
                // If a registry status exists, select it in the dialog list.
                if (self.patient.registry_status.id) {
                	console.log("registry_status_id = " + registry_status.id);
                    $_(".edit-registry-status-dialog .registry-status option[value='" + self.patient.registry_status.id + "']").prop("selected", true);
                } else {
                	console.log("ooops...no registry status id");
                }
            });


            // Handle the onclick of the "edit workflow status" control.
            $_("#edit_patient_workflow_status").click(function () {

                // Populate the dialog's list of workflow statuses.
               // app_.populateTermList("PHEDRS Review Status CV", ".edit-workflow-status-dialog .workflow-status");

                // Open the dialog.
                $_("#edit_patient_workflow_status").popModal({
                    html: $_(self.selectors.edit_workflow_status_dialog).html(),
                    onOkBut: function () {

                        // Get the status ID and comment from the dialog.
                        var statusID = $_(".edit-workflow-status-dialog .workflow-status").val();
                        var statusComment = $_(".edit-workflow-status-dialog .workflow-status-comment").val();

                        if (!self.patient.registry_patient_id) { return app_.displayError("Invalid registry patient ID", "edit workflow status dialog"); }

                        var data = {
                            registry_id: app_.registry.id,
                            registry_patient_id: self.patient.registry_patient_id,
                            new_review_status_id: statusID,
                            changer_id: app_.user.id,
                            workflow_status_change_comment: statusComment
                        };

                        app_.get(data, self.processUpdatedWorkflowStatus, app_.processError, self.urlKeys.update_registry_status);
                    }
                });

                // If a workflow status exists, select it in the dialog list.
                if (self.patient.workflow_status.id) {
                    $_(".edit-workflow-status-dialog .workflow-status option[value='" + self.patient.workflow_status.id + "']").prop("selected", true);
                } 
            });
            // Handle the onclick of the "edit owner name" control.
            $_("#edit_owner_name").click(function () {


                // Open the dialog.
                $_("#edit_owner_name").popModal({
                    html: $_(self.selectors.edit_owner_name_dialog).html(),
                    onOkBut: function () {

                        // Get the status ID and comment from the dialog.
                        var statusID = $_(".edit-owner-name-dialog .owner-name").val();

                        console.log("Checking Status Value after click:", statusID);

                        var statusComment = $_(".edit-owner-name-dialog .registry-status-comment").val();

                        if (!self.patient.registry_patient_id) { return app_.displayError("Invalid registry patient ID", "edit registry status dialog"); }

                        var data = {
                            registry_id: app_.registry.id,
                            registry_patient_id: self.patient.registry_patient_id,
                            new_status_id: statusID,
                            changer_id: app_.user.id,
                            reg_status_change_comment: statusComment
                        };

                        app_.get(data, self.processUpdatedRegistryStatus, app_.processError, self.urlKeys.update_registry_status);
                    }
                });

                // If a owner-name exists, load the text box.
                //if (self.patient.registry_status.id) {
                console.log("owner-name = " + registry_status.id);
                $_(".edit-owner-name-dialog .owner-name").val = "test";
                /* } else {
                     console.log("ooops...no registry status id");
                 }*/
            });
            //end owner
            
            // Handle the click event for the status history link.
            $_("#status_history_link").click(function () {

                // Display the registry status history dialog.
                app_.displayComponent("registry_status_history_dialog", "application_dialog_container");
            });

            // Handle the click event for the review/workflow status history link.
            $_("#reviewstatus_history_link").click(function () {

                // Display the workflow_status history dialog.
                app_.displayComponent("workflow_status_history_dialog", "application_dialog_container");
            });

            var data = {
                mrn: self.patient.mrn,
                registry_id: app_.registry.id
            };

            // Request patient data from the web service.
            app_.get(data, self.processDemographics, app_.processError, self.urlKeys.get_demographics);

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };


        this.processDemographics = function (data_, textStatus_, jqXHR_) {
            
            var patient = data_.patientDemographics;
            if (!patient) { return app_.displayError("Invalid patient", "processDemographics"); }

            var registryPatient = data_.registryPatient;
            if (!registryPatient || !registryPatient.registry_patient_id) { return app_.displayError("Invalid registry patient", "processDemographics"); }

            

            // Update the patient data that will be stored in the application data.
            self.patient.registry_patient_id = registryPatient.registry_patient_id;

            
            //-----------------------------------------------------------------------------------------------------
            // DOB and age
            //-----------------------------------------------------------------------------------------------------
            if (patient.birth_date) {

                // DOB
                $_("#dob_value").html(formatDateString(patient.birth_date));

                // Age
                var age = calculateAge(new Date(patient.birth_date));
                if (!age) { age = ""; }

                $_("#patient_age").html(age);
            }

            //-----------------------------------------------------------------------------------------------------
            // Gender
            //-----------------------------------------------------------------------------------------------------
            if (patient.gender) {
                var gender = formatGender(patient.gender);
                $_("#patient_gender").html(gender);
            }

            //-----------------------------------------------------------------------------------------------------
            // Race
            //-----------------------------------------------------------------------------------------------------
            if (patient.race) {
                var race = formatRace(patient.race);
                $_("#patient_race").html(race);
            }

            //-----------------------------------------------------------------------------------------------------
            // The patient's full name
            //-----------------------------------------------------------------------------------------------------
            var fullName = patient.full_name;
            $_("#patient_full_name").html(fullName);
            
            // Update the patient data (that gets stored in the application data).
            self.patient.name = fullName;

            //-----------------------------------------------------------------------------------------------------
            // MRN
            //-----------------------------------------------------------------------------------------------------
            if (!patient.mrn) {
                app_.displayError("Invalid MRN","processPatient");
            } else {
                $_("#mrn_values").html(patient.mrn);
            }

            //-----------------------------------------------------------------------------------------------------
            // Get the address info and assemble it.
            //-----------------------------------------------------------------------------------------------------
            var addressInfo = "";

            if (patient.address_city) { addressInfo += patient.address_city; }
            if (patient.zipcode && addressInfo) { addressInfo += ", " + patient.zipcode; }

            $_("#patient_address").html(addressInfo);

            //-----------------------------------------------------------------------------------------------------
            // Phone number
            //-----------------------------------------------------------------------------------------------------
            if (patient.phone_number) { jQuery("#patient_phone").html(patient.phone_number); }

            //-----------------------------------------------------------------------------------------------------
            // SSN (TODO: necessary?)
            //-----------------------------------------------------------------------------------------------------
            if (patient.ssn) { $_("#ssn_value").html(patient.ssn); }


            //-----------------------------------------------------------------------------------------------------
            // Registry status and comment
            //-----------------------------------------------------------------------------------------------------
            if (registryPatient.registry_status && registryPatient.registry_status_id) {

                var formattedStatus = formatRegistryStatus(registryPatient.registry_status);

                // Display the status.
                $_("#registry_status").html(formattedStatus);

                // Update the patient data that will be stored in the application data.
                self.patient.registry_status.id = registryPatient.registry_status_id;
                self.patient.registry_status.label = registryPatient.registry_status;

                // Format and display the registry status, assigner, and date.
                var assignmentDate = registryPatient.assignment_date;
                if (assignmentDate) {
                    assignmentDate = "(" + formatDateString(assignmentDate) + ")";
                } else {
                    assignmentDate = "";
                }
                $_("#status_assigned_on").html(assignmentDate);
                
                var assignedBy = registryPatient.assigned_by_name;
                if (assignedBy) {
                    assignedBy = "Assigned by " + assignedBy + " ";
                } else {
                    assignedBy = "";
                }
                $_("#status_assigned_by").html(assignedBy);

                var registrarComment = registryPatient.registrar_comment;
                if (registrarComment) {
                    registrarComment = "\"" + registrarComment + "\"";
                } else {
                    registrarComment = "";
                }
                $_("#status_comment").html(registrarComment);
            }

            //-----------------------------------------------------------------------------------------------------
            // Workflow status and comment
            //-----------------------------------------------------------------------------------------------------
            if (registryPatient.registryWorkflowStatus && registryPatient.registry_workflow_status_id) {

                var formattedStatus = formatWorkflowStatus(registryPatient.registryWorkflowStatus);

                // Display the status.
                $_("#workflow_status").html(formattedStatus);

                // Update the patient data that will be stored in the application data.
                self.patient.workflow_status.id = registryPatient.registry_workflow_status_id;
                self.patient.workflow_status.label = registryPatient.registryWorkflowStatus;

                // Format and display the worklow status, reviewer, and date.
                var lastReviewDate = registryPatient.last_review_date;
                if (lastReviewDate) {
                    lastReviewDate = " (" + formatDateString(lastReviewDate) + ")";
                } else {
                    lastReviewDate = "";
                }
                $_("#reviewstatus_reviewed_on").html(lastReviewDate);
                
                var reviewerName = registryPatient.reviewer_name;
                if (reviewerName) {
                    reviewerName = "Last reviewed by " + reviewerName;
                } else {
                    reviewerName = "";
                }
                $_("#reviewstatus_reviewed_by").html(reviewerName);

                var workflowComment = registryPatient.workflow_comment;

               
                if (workflowComment) {
                    workflowComment = "\"" + workflowComment + "\"";
                } else {
                    workflowComment = "";
                }
               
                $_("#reviewstatus_comment").html(workflowComment);
            }

            // Persist the updated patient object in application data.
            app_.setData("selected_patient", self.patient);
        };


        // Process the call to the patient demographics web service.
        this.processRefreshedStatuses = function (data_, textStatus_, jqXHR_) {

            var registryPatient = data_.registryPatient;
            if (!registryPatient || !registryPatient.registry_patient_id) { return app_.displayError("Invalid registry patient", "processRefreshedStatuses"); }

            //-----------------------------------------------------------------------------------------------------
            // Registry status and comment
            //-----------------------------------------------------------------------------------------------------
            if (registryPatient.registry_status && registryPatient.registry_status_id) {

                var formattedStatus = formatRegistryStatus(registryPatient.registry_status);

                // Display the status.
                $_("#registry_status").html(formattedStatus);

                // Update the patient data that will be stored in the application data.
                self.patient.registry_status.id = registryPatient.registry_status_id;
                self.patient.registry_status.label = registryPatient.registry_status;

                // Format and display the registry status, assigner, and date.
                var assignmentDate = registryPatient.assignment_date;

               

                if (assignmentDate) {
                    assignmentDate = "(" + formatDateString(assignmentDate) + ")";
                } else {
                    assignmentDate = "";
                }
                $_("#status_assigned_on").html(assignmentDate);

                var assignedBy = registryPatient.assigned_by_name;
                if (assignedBy) {
                    assignedBy = "Assigned by " + assignedBy + " ";
                } else {
                    assignedBy = "";
                }
                $_("#status_assigned_by").html(assignedBy);

                var registrarComment = registryPatient.registrar_comment;
                if (registrarComment) {
                    registrarComment = "\"" + registrarComment + "\"";
                } else {
                    registrarComment = "";
                }
                $_("#status_comment").html(registrarComment);
            }

            //-----------------------------------------------------------------------------------------------------
            // Workflow status and comment
            //-----------------------------------------------------------------------------------------------------
            if (registryPatient.registryWorkflowStatus && registryPatient.registry_workflow_status_id) {

                var formattedStatus = formatWorkflowStatus(registryPatient.registryWorkflowStatus);

                // Display the status.
                $_("#workflow_status").html(formattedStatus);

                // Update the patient data that will be stored in the application data.
                self.patient.workflow_status.id = registryPatient.registry_workflow_status_id;
                self.patient.workflow_status.label = registryPatient.registryWorkflowStatus;

                // Format and display the worklow status, reviewer, and date.
                var lastReviewDate = registryPatient.last_review_date;
                if (lastReviewDate) {
                    lastReviewDate = "(" + formatDateString(lastReviewDate) + ")";
                } else {
                    lastReviewDate = "";
                }
                $_("#reviewstatus_reviewed_on").html(lastReviewDate);

                var reviewerName = registryPatient.reviewer_name;
                if (reviewerName) {
                    reviewerName = "Last reviewed by " + reviewerName + " ";
                } else {
                    reviewerName = "";
                }
                $_("#reviewstatus_reviewed_by").html(reviewerName);

                var workflowComment = registryPatient.workflow_comment;
                if (workflowComment) {
                    workflowComment = "\"" + workflowComment + "\"";
                } else {
                    workflowComment = "";
                }
                $_("#reviewstatus_comment").html(workflowComment);
            }

            // Persist the updated patient object in application data.
            app_.setData("selected_patient", self.patient);
        };


        this.processUpdatedRegistryStatus = function (data_, textStatus_, jqXHR_) {

            // Get the updated patient demographics (which includes registry and workflow statuses and comments).
            self.refreshStatuses();

            console.log(data_);
            if (data_.webServiceStatus.status == "SUCCESS") {
                fadeInfoInAndOut("successful-result", "#registry_status_info", "<i class='fa fa-check-circle'></i>");
            } else {
                fadeInfoInAndOut("failed-result", "#registry_status_info", "<i class='fa fa-times-circle'></i>");
            }
        };

        this.processUpdatedWorkflowStatus = function (data_, textStatus_, jqXHR_) {

            // Get the updated patient demographics from the web service (which will include registry and workflow statuses).
            self.refreshStatuses();

            console.log(data_);
            if (data_.webServiceStatus.status == "SUCCESS") {
                fadeInfoInAndOut("successful-result", "#workflow_status_info", "<i class='fa fa-check-circle'></i>");
            } else {
                fadeInfoInAndOut("failed-result", "#workflow_status_info", "<i class='fa fa-times-circle'></i>");
            }
        };
        
        // Get the updated patient demographics from the web service (which will include registry and workflow statuses).
        this.refreshStatuses = function () {

            var data = {
                mrn: self.patient.mrn,
                registry_id: app_.registry.id
            };

            app_.get(data, self.processRefreshedStatuses, app_.processError, self.urlKeys.get_demographics);
        };

    };

})(jQuery, jQuery.phedrs.app));