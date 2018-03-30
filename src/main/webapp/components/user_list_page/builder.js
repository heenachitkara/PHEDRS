
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "user list" page builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "user_list_page";

        
        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service URL keys.
        this.urlKeys = {
            get_users: "getregistryusers"
        };


        // Initialize the page
        this.initialize = function () {

            // Handle the "add user button" click.
            $_("#user_list_page .add-user-button").click(function () {                
                app_.displayComponent("add_user_dialog", app_.selectors.dialog_container);
            });

            // Retrieve the list of users.
            var data = {
                registry_id: app_.registry.id
            };

            app_.get(data, self.processUsers, app_.displayError, self.urlKeys.get_users);

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });          
        };

        this.processUsers = function (data_, textStatus_, jqXHR_) {
            
            var source = {
                localdata: data_.registryUserList,
                datatype: "array"
            };
            var dataAdapter = new $_.jqx.dataAdapter(source, {
                //loadComplete: function (data) { console.log("load is complete"); },
                loadError: function (xhr, status, error) { }
            });
            var html = "";
            $_("#user-list-table").jqxGrid({
                source: dataAdapter,
                width: 700,
                autoheight: true,
                columns: [
                    {
                        text: 'Name',                        
                        datafield: 'full_name',
                        width: 200
                    },
                    {
                        text: 'Login ID',
                        datafield: 'login_id',
                        width: 200
                    }, {
                        text: 'Role',
                        datafield: 'name',
                        width: 200
                    }
                    , {
                        text: 'Edit', datafield: 'Edit', columntype: 'button', cellsrenderer: function () {
                            return "Edit";
                        },
                        buttonclick: function (row) {
                            
                            var dataRecord = $("#user-list-table").jqxGrid('getrowdata', row);
                           
                            app_.displayComponent("edit_user_dialog", app_.selectors.dialog_container, dataRecord);
                            
                        }
                    }
                    

                ]
            });


           /* $_.each(data_.users, function (index_, user_) {

                html += "<tr>" +
                    "<td>" + user_.full_name + "</td>" +
                    "<td>" + user_.login_id + "</td>" +
                    "<td>" + user_.role_name + "</td>" +
                     "<td> <button onclick=\"editButton()\" type=\"button\" class=\"btn btn-success btn-sm edit-user-button\">EDIT</button></td>" +
                    "</tr>";
            });*/


           // $_("#user_list_page .user-list-table").html(html);
        };
    };

})(jQuery, jQuery.phedrs.app));