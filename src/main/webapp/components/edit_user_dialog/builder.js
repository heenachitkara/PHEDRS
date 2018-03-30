
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "add user" dialog builder
    return new function () {

        var self = this;

        this.dialogSelector = "#edit_user_dialog";

        // This object's key in the Application.components collection.
        this.key = "edit_user_dialog";

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service keys.
        this.urlKey = "updateregistryactor";

        var editData = app_.userData;

        // Initialize the page
        this.initialize = function () {

            // Display this dialog.
            $_(self.dialogSelector).modal();

            $_(".edit_save-button").click(function () {
                self.save();
                window.location.reload();
            });
            
           // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
            $_("#edit_login_id").val(editData.login_id);
            $_("#edit_user_role").val(editData.role_id);
            $_("#edit_first_name").val(editData.full_name);
            $_("#edit_email").val(editData.email);
            $_("#edit_actorId").val(editData.userID);

            var data1 = {
                cv_id: 22
            };
            app_.getNoPreValidate(data1, self.userRoleList, app_.processError, "get_role_names");
            
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
            var user_role_id = $_("#edit_user_role").val();
           
            var html = "";
            $_.each(collection, function (index_, term_) {
                if (user_role_id == term_.roleId) {
                    html += "<option selected value='" + term_.roleId + "'>" + term_.roleName + "</option>";
                } else {
                    html += "<option value='" +term_.roleId + "'>" +term_.roleName + "</option>";
                }
               
            }); 

            $_(".edit_user_role_list").html(html);
        };
        this.processSave = function (data_, textStatus_, jqXHR_) {


            console.log("I am inside processSave");
            console.log(data_);

            console.log("TODO: processSave");

            // TODO: clear the controls.
            $_("#edit_login_id").val("");
            $_("#edit_first_name").val("");

            $_(self.dialogSelector).modal("hide");
        };


        this.save = function () {


            var user_login_id = $_("#edit_login_id").val();
            var user_name = $_("#edit_first_name").val();          
            var user_email = $_("#edit_email").val();
            var user_actorID = $_("#edit_actorId").val();
            var user_roleName = $_(".edit_user_role_list").val();           
          
            var data = {               
                name: user_login_id,
                login:user_name,
                email: user_email,
                is_machine:'N',
                actor_id: user_actorID,
                role_id: user_roleName,
                registry_id: app_.registry.id
            };

            console.log(data);

            app_.get(data, self.processSave, self.displayError, self.urlKey);
        };

    };

})(jQuery, jQuery.phedrs.app));