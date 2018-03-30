
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "generate report" page builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "generate_report_page";

        this.patient = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service URL and its key.
        this.url = null;
        this.urlKey = "generatereport"; // Not real!!!



        this.generateReport = function () {

            
            var data = {
                
                registry_id: app_.registry.id
                // TODO
                
            };

            app_.getNoPreValidate(data, self.processAddedMRN, app_.displayError, self.url);
        };


        // Initialize the page
        this.initialize = function () {

            self.url = app_.lookupWebServiceURL(self.urlKey);
            if (!self.url) { return app_.displayError("Invalid URL", "initialize"); }

            

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };

        this.processGeneratedReport = function (data_, textStatus_, jqXHR_) {

            // Make sure data is a JSON object.
            data_ = app_.processResponse(data_, jqXHR_);

            console.log("TODO");
        };
    };

})(jQuery, jQuery.phedrs.app));