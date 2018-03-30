
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "encounter attributes" dialog builder
    return new function () {

        var self = this;

        this.dialogSelector = "#encounterAttribute_config_dialog";

        this.encounterKey = null;

        this.finNumKey = null;

        // This object's key in the Application.components collection.
        this.key = "encounterAttribute_config_dialog";

        this.patient = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service key.
        this.urlKeys = {
            add_EncounterAttribute_CVTerm: "addEncounterAttributeCVTerm"
        };


        this.cancelExternalDialog = function () {
            window.location.reload();

        }

        this.registryIdValue = 0;
        // Initialize the page
        this.initialize = function () {

            this.registryIdValue = app_.getData("new_registry_id");
            
            if (!this.registryIdValue) {
                this.registryIdValue = app_.registry.id;
            }
            
            // Display this dialog.
            $_(self.dialogSelector).modal();

            $_(self.dialogSelector).on("hide.bs.modal", function (event_, ui_) {
            });

            $_(".encounterAttrib-save-button").click(function () {               
                self.saveAttribute();
            });

            $_(".encounterAttrib-cancel-button").click(function () {
                self.cancelExternalDialog();

            });

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };



        // Save a new attribute along with an optional comment.
        this.saveAttribute = function () {

            var regName = document.getElementById("configEncounter_attributes_add_registry_name").value; //$_("configPatient_attributes_add_registry_name").val();
            var regDef = document.getElementById("configEncounter_attributes_add_registry_def").value; //$_("configPatient_attributes_add_registry_def").val();
           
            var data = {
                cache: false,
                name: regName,
                registry_id: this.registryIdValue,
                definition: regDef
            };

            console.log(data);
            app_.getNoPreValidateConfigReg(data, self.cancelExternalDialog, app_.processError, self.urlKeys.add_EncounterAttribute_CVTerm);
        };
    };

})(jQuery, jQuery.phedrs.app));