
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "add user" dialog builder
    return new function () {

        var self = this;

        this.dialogSelector = "#add_user_dialog";

        // This object's key in the Application.components collection.
        this.key = "add_user_dialog";

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service keys.
        this.urlKey = "addregistryactor";


        // Initialize the page
        this.initialize = function () {

            // Display this dialog.
            $_(self.dialogSelector).modal();
        	
            $_(".save-button").click(function () {
                self.save();
                 window.location.reload();
            });
            var data1 = {
                cv_id: 22
            };
            app_.getNoPreValidate(data1, self.userRoleList, app_.processError, "get_role_names");

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };

        this.userRoleList = function (data_, textStatus_, jqXHR_) {


            data_ = app_.processResponse(data_, jqXHR_);


            var attributeCount = 0;
            var collection = data_.roleNames;
            if (collection) { attributeCount = collection.length; }


            var source = {
                localdata: collection,
                datatype: "array",
                datafields:
                [
                    { name: 'roleId', type: 'string' },
                    { name: 'roleName', type: 'string' }
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
                html += "<option value='" + term_.roleId + "'>" + term_.roleName + "</option>";
            }); 

            $_(".user_role_List").html(html);
        };

        this.processSave = function (data_, textStatus_, jqXHR_) {


            console.log("I am inside processSave");
            console.log(data_);

            console.log("TODO: processSave");

            // TODO: clear the controls.
            $_("#user_login_id").val("");
            $_("#user_first_name").val("");
            $_("#user_last_name").val("");
            $_(self.dialogSelector).modal("hide");
        };


        this.save = function () {


            var user_login_id = $_("#user_login_id").val();
            if (!user_login_id) { alert("Please enter a login ID"); return false; }

            var user_first_name = $_("#user_first_name").val();
            if (!user_first_name) { alert("Please enter a first name"); return false; }

            var user_last_name = $_("#user_last_name").val();
            if (!user_last_name) { alert("Please enter a last name"); return false; }

            var user_email = $_("#user_email").val();
            if (!user_email) { alert("Please enter an email address"); return false; }

            var user_role = $_("#user_role_List option:selected").attr("value");
            if (!user_role) { alert("Please select a role"); return false; }
            var user_login_id = $_("#user_login_id").val();

            

            var user_is_enabled = $_("#user_is_enabled option:selected").attr("value");
            if (!user_is_enabled) { alert("Please select enabled or disabled"); return false; }
            var isEnabled = true;
            if (user_is_enabled === "false") { isEnabled = false; }
            
            var data = {
                email: user_email,
                full_name: user_first_name + " " + user_last_name,
                is_enabled: isEnabled,
                is_machine: false,
                login_id: user_login_id,
                registry_id: app_.registry.id,
                role_id: user_role
            };

            console.log(data);

            app_.get(data, self.processSave, self.displayError, self.urlKey);
        };

    };

})(jQuery, jQuery.phedrs.app));