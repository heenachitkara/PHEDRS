

function WebServices(environment_) {

    var self = this;

    // The environment-specific host/path/URL for the web services.
    // NOTE: modify these URLs if the development or production machine changes!
   this.baseURLs = {
        //dev: "https://cashew-dev.hs.uab.edu:8443/RegistryWS/",
        dev: "/RegistryWS/",
        local: "jsonFiles/",
        localws: "/RegistryWS/",
        prod: "/RegistryWS/"
    };

    
   /* $.ajax({
        url: 'proxy.php',
        type: 'GET',
        data: {"https://cashew-dev.hs.uab.edu:8443/RegistryWS/"}, 
    success: function(response) {
        
             }
        });
*/

    this.environment = null;

    // TODO: any new web service URLs should be added here!!!
    this.names = [

        // Authorize the user to see what registries they have access to and what role(s)
        // are available to them.
        "authorize",
        
        // Get registry patients in order to populate a "view" tab.
        "getregistrypatientsintabs",

         
        "getpatientattributes",

         // Get the document content
        "showdocument",

        //Get NLP document list
        "getdocuments",

        // Get Encounters

        "getencounters",

        // Get Diagnoses
        "getpatientdiagnosis",

        // Get Users List
        "getregistryusers",

        //Get Patient Demographics
        "getpatientdemographics",

        //Add registry patient attribute
       "addregistrypatientattribute",

       //Update Registry status
       "updateregistrypatientstatushistory",

       //Get Detection Codes
       "getdetectioncodes",

       // Get Registry Patient History
       "getregistrypatienthistory",

       // For showinfo Webservice
       "showinfo",

       // This webservice gives us the list for adding patient attributes.
       "getcvterms",
        
        // For Remove and Save operation for Patient Attribute
       "updateregistrypatientattribute",

       //For adding Defined attributes
       "addregistryencounterattribute"

    ];

    // A map/dictionary from the web service name to its environment-specific URL.
    this.services = null;

     
   /* // Make an AJAX GET request. Replace it with the below get request
    this.get = function (data_, done_, fail_, webServiceKey_){
    //function get(data_, done_, fail_, webServiceKey_) {

    // Validate the web service key parameter and maintain it in a cookie.
    if (isEmpty(webServiceKey_)) { alert("Invalid webServiceKey in get()"); return false; }
    Cookies.set("web_service", webServiceKey_);

    // The proxy page will be used by default. This corresponds to environment values "dev" and "prod". 
    var url = "proxy.php";

    // If the environment is "local", we will retrieve the local JSON file that is a placeholder for 
    // the results of an actual web service.
    if (environment === "local") {
        url = "jsonFiles/" + webServiceKey_ + ".json?v=" + Common.uniqueID();
    }

    // Create the AJAX request.
    jQuery.ajax({
        data: data_,
        method: "GET",
        url: url
    })
    .done(done_)
    .fail(fail_);
   };
*/





    // Return a web service URL for the requested name.
    this.get = function (name_) {

        if (isEmpty(name_)) { regman.displayError("Invalid web service name (empty)", "get"); return false; }

        var url = self.services[name_];
        if (isEmpty(url)) { regman.displayError("Invalid URL for name '" + name_ + "'", "get"); return false; }

        return url;
    };

    this.initialize = function () {

        self.services = {};

        jQuery.each(self.names, function (index_, name_) {

            var url = self.baseURLs[self.environment] + name_;
            if (self.environment === "local") { url += ".json?v=" + Common.uniqueID(); }

            self.services[name_] = url;

            console.log("Checking URL in WebServices: "+url);
        });
    };



    // Validate the input parameter and assign it to a member variable.
    if (isEmpty(environment_)) { regman.displayError("Invalid environment", "WebServices"); }
    if (environment_ !== "dev" && environment_ !== "local" && environment_ !== "localws" && environment_ !== "prod") { regman.displayError("Environment must be dev, local, localws or prod", "WebServices"); }

    self.environment = environment_;

    self.initialize();
};
