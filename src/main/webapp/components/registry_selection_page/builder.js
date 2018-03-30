
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "registry selection" page builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "registry_selection_page";

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;


        // Initialize the virtual page
        this.initialize = function () {

            if (!app_.user.login_id) { return app_.displayError("Invalid login ID", "initialize"); }

            // Call the authorize web service with the user's login ID.
            var data = {
                username: app_.user.login_id
            };

            app_.get(data, self.processAuthorize, app_.processError, "authorize");

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };


        // Process the results of the authorize web service.
        this.processAuthorize = function (data_, textStatus_, jqXHR_) {

            // Validate the list of registries that were returned.
            if (!data_.registries) {
                // Set the user status to "unable to authorize" and display the error.
                app_.user.state = app_.userStates.unable_to_authorize;
                app_.saveUserData();
                return app_.displayError("Invalid registry list", "processAuthorize");

            } else if (data_.registries.length < 1) {
                // Set the user status to "unable to authorize" and display the error.
                app_.user.state = app_.userStates.unable_to_authorize;
                app_.saveUserData();
                return app_.displayError("No registries in registry list", "processAuthorize");
            }

            // Maintain the collection of available registries.
            var available_registries = data_.registries;

            console.log("Checking Available Registries");
            console.log(available_registries);
           
            
            //Only if there is one registry
            if (available_registries.length == 1) {
                // Automatically select the one registry.

                self.select(available_registries[0].id, available_registries[0].name);
                return true;
            } 

            // Populate the page with the registries that can be selected.
            var html = "<table id='registry_selection_table' class='list-table'>" +
                "<tr class='header-row'><th></th><th>Registry</th><th>Role</th></tr>";
        
            $_.each(available_registries, function (index_, registry_) {

                html += "<tr>" +
                    "<td class='btn-column'><button class='btn btn-success table-button' data-registry-id='" + registry_.id + "' data-registry-name='" + registry_.name + "' data-role-name='" + registry_.role.role_name + "'>SELECT</button></td>" +
                    "<td class='registry-name-column'>" + registry_.name + "</td>" +
                    "<td class='role-name-column'>" + registry_.role.role_name + "</td>" +
                    "</tr>";
            });

            html += "</table>";

            $_(".page-body").html(html);

            // Assign an click handler to all select buttons.
            $_("button[data-registry-id]").each(function () {
                $_(this).click(function () {
                    var id = $_(this).attr("data-registry-id");
                    var name = $_(this).attr("data-registry-name");
                    console.log("Inside Reg selection Page for Registry Name check");
                    console.log(name);
                    //save the name
                    app_.setData("selected_registry_name",name);
                    var role_name = $_(this).attr("data-role-name");
                    self.select(id, name, role_name);
                });
            });
        };


        this.select = function (registryID_, registryName_, role_name) {

            if (!registryID_) { alert("Invalid registry ID in select"); return false; }
            if (!registryName_) { alert("Invalid registry name in select"); return false; }

            // Maintain the selected registry ID in app data.
            app_.setData(app_.keys.selected_registry_id, registryID_);
            app_.setData("selected_registry_roleName", role_name);
            app_.setData("selected_registry_name", registryName_);
            //alert("role_name" + role_name);
            if (role_name == "Manager") {               
                $_("#addRegList").hide();
            } else if (role_name == "Registrar") {
                $_("#adminDropDown").hide();
            }
            var registry_key = null;

            // HUGE HACK!!!
            if (registryName_.indexOf("COPD") > -1) {
                registry_key = "copd";
            } else if (registryName_.indexOf("CRCP") > -1) {
                registry_key = "crcp";
            } else if (registryName_.indexOf("Multiple Myeloma") > -1) {
                registry_key = "multiple_myeloma";
            } else {
                alert("Unrecognized registry " + registryName_);
                return false;
            }

            app_.loadRegistryMetadata("registries");
        };
    };

})(jQuery, jQuery.phedrs.app));