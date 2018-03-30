// The Generic Registry ("chronic obstructive pulmonary disease")
jQuery(window).trigger(jQuery.phedrs.app.events.registry_loaded, (function ($_,app_) { 

    return new function () {

        var self = this;

        // TODO: when the registry loads, call a webservice to retrieve metadata including the available role ID's for this registry!
       var data = {
                registry_id: app_.getData(app_.keys.selected_registry_id)
            };
        // Display components (pages and dialogs).
        this.components = {
            add_by_mrn_page: {
                key: "add_by_mrn_page",
                selector: "#add_by_mrn_page",
                type: "page"
            },
            add_user_dialog: {
                key: "add_user_dialog",
                selector: "#add_user_dialog",
                type: "dialog"
            },
            attribute_config_dialog: {
                key: "attribute_config_dialog",
                selector: "#attribute_config_dialog",
                type: "dialog"
            },
            patientAttribute_config_dialog: {
                key: "patientAttribute_config_dialog",
                selector: "#patientAttribute_config_dialog",
                type: "dialog"
            },
            encounterAttribute_config_dialog: {
                key: "encounterAttribute_config_dialog",
                selector: "#encounterAttribute_config_dialog",
                type: "dialog"
            },
            edit_user_dialog: {
                key: "edit_user_dialog",
                selector: "#edit_user_dialog",
                type: "dialog"
            },
            advanced_search_page: {
                key: "advanced_search_page",
                selector: "#advanced_search_page",
                type: "page"
            },
            basic_search_page: {
                key: "basic_search_page",
                selector: "#basic_search_page",
                type: "page"
            },
            registry_home_page: {
                key: "registry_home_page",
                selector: "#registry_home_page",
                type: "page"
            },
            
            crcp_users_page: {
                key: "crcp_users_page",
                selector: "#crcp_users_page",
                type: "page"
            },
            document_details_dialog: {
                key: "document_details_dialog",
                selector: "#document_details_dialog",
                type: "dialog"
            },
            encounter_attributes_dialog: {
                key: "encounter_attributes_dialog",
                selector: "#encounter_attributes_dialog",
                type: "dialog"
            },
            generate_report_page: {
                key: "generate_report_page",
                selector: "#generate_report_page",
                type: "page"
            },
            patient_history_dialog: {
                key: "patient_history_dialog",
                selector: "#patient_history_dialog",
                type: "dialog"
            },
            patient_review_page: {
                key: "patient_review_page",
                selector: "#patient_review_page",
                type: "page"
            },

            patient_summary_page: {
                key: "patient_summary_page",
                selector: "#patient_summary_page",
                type: "page",

                demographics_panel: {
                    key: "patient_summary_page.demographics_panel",
                    selector: "#demographics_panel",
                    type: "panel"
                },

                diagnosis_panel: {
                    key: "patient_summary_page.diagnosis_panel",
                    selector: "#diagnosis_panel",
                    type: "panel"
                },

                encounters_panel: {
                    key: "patient_summary_page.encounters_panel",
                    selector: "#encounters_panel",
                    type: "panel"
                },

                nlp_document_panel: {
                    key: "patient_summary_page.nlp_document_panel",
                    selector: "#nlp_document_panel",
                    type: "panel"
                },

                patient_attributes_panel: {
                    key: "patient_summary_page.patient_attributes_panel",
                    selector: "#patient_attributes_panel",
                    type: "panel"
                }
            },
            registry_status_history_dialog: {
                key: "registry_status_history_dialog",
                selector: "#registry_status_history_dialog",
                type: "dialog"
            },
            user_list_page: {
                key: "user_list_page",
                selector: "#user_list_page",
                type: "page"
            },
            add_new_registry: {
                key: "add_new_registry",
                selector: "#add_new_registry",
                type: "page"
            },
            config_registry: {
                key: "config_registry",
                selector: "#config_registry",
                type: "page"
            },
            workflow_status_history_dialog: {
                key: "workflow_status_history_dialog",
                selector: "#workflow_status_history_dialog",
                type: "dialog"
            }
        };

        // Controlled vocabularies and their terms.
        this.cv = null;

        // The component key of the registry's default (home) page
        this.default_page = "registry_home_page";
        
        

        // This registry's ID
        this.id = null;

        this.key = null;
        var registryCVS = "PHEDRS Patient Attribute Ontology, PHEDRS Patient Status CV, PHEDRS Review Status CV";
        var retrievedRegistryName = app_.getData("selected_registry_name");

        this.title = retrievedRegistryName;

        // Use the title to generate the key.
        (function () { self.key = self.title.toLowerCase().replace(/\s/g, "_"); })();

        this.registry_cvs = new String("PHEDRS Patient Attribute Ontology, PHEDRS Patient Status CV, PHEDRS Review Status CV");
        // Registry-specific CV's.
        // TODO: this should actually be dynamically retrieved by a web service!
        /*this.registry_cvs = new String("PHEDRS Patient Attribute Ontology," +
                "PHEDRS Patient Status CV," +
                "PHEDRS Review Status CV," +
                "UAB COPD Registry Encounter Attribute CV," +
                "UAB COPD Registry Patient Attributes," +
                "Patient Registry Evidence Codes");*/
        this.initialize = function () {
            // Request patient attributes from the web service.
            app_.getNoPreValidateConfigReg(data, self.processRegsitryCVSPatientAttrib, app_.processError, "getPatientAttributeCV");
            // Request encounter attributes from the web service.
            app_.getNoPreValidateConfigReg(data, self.processRegsitryCVSEncounterAttrib, app_.processError, "getEncounterAttributeCV");

            
            //Get the selected registry name saved in registry selection page after user's selection of a particular registry

            
       
    };

    this.processRegsitryCVSPatientAttrib = function (data_, textStatus_, jqXHR_) {
        registryCVS = "PHEDRS Patient Attribute Ontology, PHEDRS Patient Status CV, PHEDRS Review Status CV";
        data_ = app_.processResponse(data_, jqXHR_);
        var attributeCount = 0;
        var collection = data_.patientCVList;
        if (collection) {
            attributeCount = collection.length;
        }

        var cvtermname = "";
        $_.each(collection, function (index_, term_) {
            cvtermname = term_.patientCVName;
        });
        registryCVS = registryCVS + ", " + cvtermname;

    };

    this.processRegsitryCVSEncounterAttrib = function (data_, textStatus_, jqXHR_) {
        data_ = app_.processResponse(data_, jqXHR_);
        var attributeCount = 0;
        var collection = data_.encounterCVList;
        if (collection) {
            attributeCount = collection.length;
        }

        var cvtermname = "";
        $_.each(collection, function (index_, term_) {
            cvtermname = term_.encounterCVName;
        });
        
        registryCVS = registryCVS + ", " + cvtermname + ", Patient Registry Evidence Codes";
        
        this.registry_cvs = new String(registryCVS);

    };
    self.initialize();
    };
})(jQuery, jQuery.phedrs.app));