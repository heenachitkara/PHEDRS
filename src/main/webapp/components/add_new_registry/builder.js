
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "user list" page builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "add_new_registry";

        
        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service URL keys.
        this.urlKey = "addRegistry";


        // Initialize the page
        this.initialize = function () {

            // Handle the "add user button" click.
            /*$_("#user_list_page .add-user-button").click(function () {                
                app_.displayComponent("add_user_dialog", app_.selectors.dialog_container);
            });

            // Retrieve the list of users.
            var data = {
                registry_id: app_.registry.id
            };

            app_.get(data, self.processUsers, app_.displayError, self.urlKeys.get_users);

            // Trigger the "builder initialized" event. */
                
            $_(".add-registry-button").click(function () {               
                self.save();
                //window.location.reload(); // Hiding relod for testing Adarsh
            });

            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };

        this.save = function () {

            var reg_Name = $_("#registry_name").val();
            if (!reg_Name) { alert("Please enter a Registry Name"); return false; }
            var reg_Definition = $_("#registry_definition").val();
            if (!reg_Definition) { alert("Please enter a Registry Definition"); return false; }

            var data = {
                name: reg_Name,
                owner_name: app_.user.display_name,
                definition: reg_Definition

            };

            console.log(data);            
            //app_.getNoPreValidate(data, self.processSavedRegistry, self.displayError, self.urlKey); //Hiding this Adarsh
            app_.getNoPreValidateConfigReg(data, self.processSavedRegistry, self.displayError, self.urlKey);
           
        };

        this.processSavedRegistry = function (data_, textStatus_, jqXHR_) {
                  
             // convert data to JSON, if necessary
             data_ = app_.processResponse(data_, jqXHR_);
            
             if (data_.registryID > 0) {
                alert("The Registry was successfully added. Please add a manager for this registrar");
                app_.setData("new_registry_id", data_.registryID);
                app_.displayComponent("config_registry", "");
                // $_(window).trigger(app_.events.builder_initialized, { key: "config_registry" });
             } else {
                 alert("The Registry not added. Please check the data entered.");
             }
        };       
        this.displayError = function (jqXHR_, textStatus_, errorThrown_) {
            return self.displayError(errorThrown_, "addRegistiry");
        };
    };

})(jQuery, jQuery.phedrs.app));