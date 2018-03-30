

// User-related data and functions
function UserManager(environment_, loginID_) {

    var self = this;

    this.environment = null;

    this.loginID = null;

    // Web service URLs
    this.urls = {
        authorize: {
            dev: "https://cashew-dev.hs.uab.edu:8443/RegistryWS/authorize",
            local: "jsonFiles/authorize.json?v=" + Common.uniqueID(),
            prod: null
        }
    };


    // Authorize the user and determine 1) which registries they are able to access, and 
    // 2) what role is available to them for each registry.
    this.authorize = function () {

        var url = self.urls["authorize"][self.environment];
        if (isEmpty(url)) { alert("Invalid URL for authorize"); return false; }

        var ajaxRequest = jQuery.ajax({
            //beforeSend: TODO: show spinner!
            data: {
                login_id: self.login_id
            },
            dataType: "json",
            method: "GET",
            url: url
        })
        .done(function (data_, textStatus_, jqXHR_) {

            console.log(data_);

            // Validate the web service and retrieve the status.
            if (typeof (data_) === "undefined" || data_ === null) { alert("Error in authorize(): Invalid data returned from  web service"); return false; }
            if (isEmpty(data_.webservice_status) || isEmpty(data_.webservice_status.status)) { alert("Error in authorize(): Invalid web service status"); return false; }
            if (data_.webservice_status.status != "SUCCESS") { alert(data_.webservice_status.message); return false; }

            // How many registries are available? If this user has access to multiple registries, we need to allow them 
            // to select which one they would like to access. If they only have access to one registry, set this as their 
            // default. If they don't have access to any registries, complain.
            // TODO
 

        })
        .fail(function (jqXHR_, textStatus_, errorThrown_) {
            alert("Error in authorize(): " + errorThrown_);
            return false;
        });
    };


    // Validate input parameters
    if (isEmpty(environment_)) { alert("Invalid environment"); return null; }
    if (isEmpty(loginID_)) { alert("Invalid login ID"); return null; }

    self.environment = environment_;
    self.loginID = loginID_;
};