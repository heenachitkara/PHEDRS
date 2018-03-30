
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The login page builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "login_page";

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service key.
        this.urlKey = "authenticate";


        // Initialize the page
        this.initialize = function () {

            $_("#login_button").click(function () {
                self.login();
            });

            // The user will often press enter after entering their password.
            $_("#user_password").keypress(function (event_) {
                var keycode = (event_.keyCode ? event_.keyCode : event_.which);
                if (keycode == "13") { self.login(); }
            });

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };

        // Provide the user credentials to the "authenticate" web service.
        this.login = function () {

            var loginID = $_("#user_login_id").val();
            if (!loginID) { alert("Please enter a valid login ID"); return false; }

            var password = $_("#user_password").val();
            if (!password) { alert("Please enter a valid password"); return false; }

            var data = {
                username: loginID,
                password: password
            };

            app_.post(data, self.processLogin, app_.processError, self.urlKey);
        };

        // Process the results of the "authenticate" web service.
        this.processLogin = function (data_, textStatus_, jqXHR_) {

            // Update the application's user object.
            app_.user.display_name = data_.user.displayName;
            app_.user.id = data_.user.userID;
            app_.user.login_id = data_.user.login;
            app_.user.state = app_.userStates.not_yet_authorized;
            
            // TODO: Set the date/time the user needs to re-authenticate.
            //phedrs.app.authData.validation.next_authentication = phedrs.app.settings.authentication_interval + currentTime;

            app_.saveUserData();

            // Open the "registry selection" page.
            app_.setPage("registry_selection_page");
        };
    };

})(jQuery, jQuery.phedrs.app));