
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "patient summary" page builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "patient_summary_page";

        // Metadata for child panels
        this.panels = {
            demographics_panel: {
                key: "patient_summary_page.demographics_panel",
                selector: "#demographics_panel"
            },
            diagnosis_panel: {
                key: "patient_summary_page.diagnosis_panel",
                selector: "#diagnosis_panel"
            },
            encounters_panel: {
                key: "patient_summary_page.encounters_panel",
                selector: "#encounters_panel"
            },
            nlp_document_panel: {
                key: "patient_summary_page.nlp_document_panel",
                selector: "#nlp_document_panel"
            },
            patient_attributes_panel: {
                key: "patient_summary_page.patient_attributes_panel",
                selector: "#patient_attributes_panel"
            }
        };

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;



        // Initialize the component.
        this.initialize = function () {
            
            // Add the patient demographics panel.
            app_.displayComponent(self.panels.demographics_panel.key, self.panels.demographics_panel.selector);

            // Add the patient attributes panel.
            app_.displayComponent(self.panels.patient_attributes_panel.key, self.panels.patient_attributes_panel.selector);

            // Add the diagnosis panel.
            app_.displayComponent(self.panels.diagnosis_panel.key, self.panels.diagnosis_panel.selector);

            // Add the encounters panel.
            app_.displayComponent(self.panels.encounters_panel.key, self.panels.encounters_panel.selector);

             // Add the nlp documents panel.
            app_.displayComponent(self.panels.nlp_document_panel.key, self.panels.nlp_document_panel.selector);

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };

        
    };

})(jQuery, jQuery.phedrs.app));