


function RegistryManager(registry_, environment_) {
    var self = this;


    //----------------------------------------------------------------------------------------------------------------------
    // A collection of component metadata keyed by an identifier
    //
    // Example:
    //
    // component = {
    //
    //    // An instance of a JavaScript "builder" object will be created and cached here the first time it is needed.
    //    builder: { /* The JavaScript builder object */},
    //
    //    // The JavaScript builder's "class" name.
    //    builder_class: "home_page",
    //
    //    // The URL where the builder object is defined
    //    builder_url: "scripts/builders/HomePage.js",
    //
    //    // The key of a corresponding fragment from the htmlFragments collection.
    //    fragment_key: "home",
    //
    //    // The component's key in this collection
    //    key: "home",
    //
    //    // The component's type (dialog, page, or panel)
    //    type: "page"
    // }
    //----------------------------------------------------------------------------------------------------------------------
    this.components = null;

    // Environment is either "dev", "local", or "prod".
    this.environment = null;

    //----------------------------------------------------------------------------------------------------------------------
    // A collection of HTML fragment metadata
    //
    // Example:
    //
    // fragment = {
    //
    //    // Does this fragment contain templates that need to be replaced? Templates will
    //    // be represented inside {{ }}.
    //    is_templated: true,
    //
    //    // The key is what identifies the fragment in this collection
    //    key: "home",
    //
    //    // The selector that identifies the fragment on the page. 
    //    selector: "#home_page", 
    //
    //    // If this is a templated fragment, the following will be a list of all templates
    //    // in the fragment that need to be replaced. 
    //    template_identifiers: [
    //      "application_title",
    //      "result_count"
    //    ],
    //
    //    // Is this a dialog, page, or panel component?
    //    type: "page",
    //
    //    // The relative URL of the HTML fragment
    //    url: "fragments/home.html"
    // }
    //----------------------------------------------------------------------------------------------------------------------
    this.htmlFragments = null;

    // The specific registry to be handled by the Registry Manager. For instance, the COPD Registry Control Panel.
    this.registry = null;

    // Use this to maintain state across page changes.
    this.selectedData = null;

    // jQuery selectors for common page Elements.
    this.selectors = {
        dialog_container: "#dialogContainer",
        page_container: "#pageContainer"
    };

    // A collection of web service URLs keyed by name.
    this.webServices = null;


    //----------------------------------------------------------------------------------------------------------------------


    // Use the builder object to populate the HTML fragment that has already been added.
    this.buildComponent = function (component_, fragment_) {

        if (isEmpty(component_)) { self.displayError("Invalid component", "buildComponent()"); return false; }
        if (isEmpty(fragment_)) { self.displayError("Invalid fragment", "buildComponent()"); return false; }
        // Has the builder object already been instantiated?
        if (typeof (component_.builder) === "undefined" || component_.builder === null) {

            // Load the script.
            jQuery.getScript(component_.builder_url)
                .done(function (script_, textStatus_) {
                    self.updateBuilder(component_);
                    component_.builder.initialize(fragment_);
                })
                .fail(function (jqxhr_, settings_, exception_) {
                    self.displayError(exception_, "buildComponent()");
                });
        } else {
            component_.builder.initialize(fragment_);
        }
    };
    

    /*// Make an AJAX GET request. Replace it with getWebServiceURL ?
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
   };*/



// Close the dialog specified by the selector.
    this.closeDialog = function (selector_) {

        if (isEmpty(selector_)) { self.displayError("Invalid selector", "closeDialog()"); return false; }

        jQuery(selector_).modal("hide");
        // TODO: how to destroy this modal? Do we need to???
    };


    // Display the HTML fragment that represents a dialog, page, or panel.
    this.displayComponent = function (componentKey_, parentSelector_) {

        // Validate the component key parameter
        if (isEmpty(componentKey_)) { self.displayError("Invalid component key", "displayComponent()"); return false; }

        // Get the component that has this key.
        var component = self.components[componentKey_];
        if (isEmpty(component)) { self.displayError("Invalid component for key " + componentKey_, "displayComponent()"); return false; }

        // Validate the HTML fragment key.
        if (isEmpty(component.fragment_key)) { self.displayError("Invalid fragment key", "displayComponent()"); return false; }

        // Load the HTML fragment using the fragment key and validate it.
        var fragment = self.htmlFragments[component.fragment_key];
        if (isEmpty(fragment)) { self.displayError("Invalid fragment", "displayComponent()"); return false; }
        if (isEmpty(fragment.selector)) { self.displayError("Invalid fragment selector", "displayComponent()"); return false; }
        if (isEmpty(fragment.url)) { self.displayError("Invalid fragment URL", "displayComponent()"); return false; }

        // The component type will determine which parent selector to use (where the component will be loaded).
        if (fragment.type === "dialog") {

            // The dialog will be loaded into the dialog container DOM Element.
            parentSelector_ = self.selectors["dialog_container"];

        } else if (fragment.type === "page") {

            // The page will be loaded into the page container DOM Element.
            parentSelector_ = self.selectors["page_container"];

        } else if (fragment.type === "panel") {

            // The panel will be loaded into the specified DOM Element.
            if (isEmpty(parentSelector_)) { self.displayError("Parent selector is required for panel component type", "displayComponent()"); return false; }

        } else {
            self.displayError("Unrecognized fragment type " + fragment.type, "displayComponent()"); return false;
        }

        // Has this fragment already been added to the DOM?
        var existingFragment = jQuery(parentSelector_).find(fragment.selector);
        if (typeof (existingFragment) === "undefined" || existingFragment.length < 1) {

            // Load the fragment into the page container.
            jQuery(parentSelector_).load(fragment.url, function () {

                // After the fragment is loaded, use the builder object to populate it.
                self.buildComponent(component, fragment);
            });
        } else {
            // Since the fragment has already been loaded, use the builder object to populate it.
            self.buildComponent(component, fragment);
        }
    };


    // This is a placeholder method that displays an error message to the user. This should be improved with
    // a proper error dialog! Note that the location parameter is optional, but message is required.
    this.displayError = function (message_, location_) {

        if (isEmpty(message_)) { message_ = "An unknown error occurred"; }
        if (!isEmpty(location_)) { message_ += " in " + location_; }

        alert(message_);
    };


    // Get the requested web service URL
    this.getWebServiceURL = function (name_) {

        if (isEmpty(name_)) { self.displayError("Invalid name", "getWebServiceURL()"); return null; }

        var url = self.webServices.get(name_);
        if (isEmpty(url)) { self.displayError("No valid URL for " + name_); return null; }

        console.log("Checking URL inside RegistryManager: "+url);

        return url;
    };


    // Initialize the Registry Manager using the Registry object provided.
    this.initialize = function () {

        // Get the collection of component metadata.
        self.components = self.registry.getComponents();

        // Get the collection of HTML fragment metadata.
        self.htmlFragments = self.registry.getHtmlFragments();

        // Create a Web Services object
        self.webServices = new WebServices(self.environment);
    };


    // Maintain this key/value pair as "selected"
    this.selectData = function (attributeKey_, value_) {

        if (isEmpty(attributeKey_)) { self.displayError("Invalid attribute key"); return false; }

        if (self.selectedData === null) { self.selectedData = {}; }

        self.selectedData[attributeKey_] = value_;
    };


    // Create an instance of the builder object described by the component, then cache the instance in the  
    // component and replace it in its collection.
    this.updateBuilder = function (component_) {

        // Create a new instance and store it in the component.
        eval("component_.builder = new " + component_.builder_class + "();");

        // Replace the component object
        self.components[component_.key] = component_;
    };


    // Validate input parameters and set member variables
    if (typeof (registry_) === "undefined" || registry_ === null) { self.displayError("Invalid registry parameter", "RegistryManager"); return false; }
    self.registry = registry_;

    if (isEmpty(environment_)) { self.displayError("Environment is a required parameter", "RegistryManager"); return false; }
    if (environment_ !== "dev" && environment_ !== "local" && environment_ !== "localws" && environment_ !== "prod") { self.displayError("Environment must be 'dev', 'local', 'localws' or 'prod'", "RegistryManager"); return false; }
    self.environment = environment_;

    // Initialize the Registry Manager
    self.initialize();
};

