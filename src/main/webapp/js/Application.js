
function Application($_) {

    var self = this;

    //----------------------------------------------------------------------------------------------------------------------------------------
    // "Constant" values that shouldn't be modified.
    //----------------------------------------------------------------------------------------------------------------------------------------

    // Events that can be triggered by the Application.
    this.events = {
        application_initialized: "application_initialized",
        builder_initialized: "app_builder_initialized",
        builder_loaded: "app_builder_loaded",
        registry_loaded: "app_registry_loaded"
    };

    // "Constant" keys that refer to local collections.
    this.keys = {
        app_data: "app_data",
        page_key: "page_key",
        registry: "registry",
        registry_id: "registry_id",
        selected_registry_id: "selected_registry_id",
        selected_registry_roleName: "selected_registry_roleName",
        selected_registry_name: "selected_registry_name",
        new_registry_id: "new_registry_id",
        user_data: "user_data",
        window_guid: "window_guid"
    };

    // jQuery selectors for common DOM Elements.
    this.selectors = {
        dialog_container: "#application_dialog_container",
        menubar_registry_label: ".navbar .navbar-header .navbar-brand .registry-label",
        menubar_user_name: ".navbar .nav.navbar-nav .user-name",
        page_container: "#application_container",
        template_container: "#application_templates"
    };

    // The user will always have a single state from the values below. This represents whether or not they have 
    // been authenticated, authorized, if a registry has been selected, and if an access-related error has occurred.
    this.userStates = {

        //-------------------------------------------------------------------------------------------------
        // Valid user states
        //-------------------------------------------------------------------------------------------------

        // Needs to authenticate, display the login dialog. If authentication is unsuccessful, the next state is "invalid credentials".
        unauthenticated: "unauthenticated",

        // The user has been authenticated, call "authorize" web service and let the user select their preferred registry/role. 
        // If the user has no available registries/roles, the next state is "unable_to_authorize".
        not_yet_authorized: "not_yet_authorized",

        // The user has been authenticated, authorized, and has selected a registry/role.
        registry_selected: "registry_selected",

        //-------------------------------------------------------------------------------------------------
        // Error states
        //-------------------------------------------------------------------------------------------------

        // An error state that is assigned if the authentication is unsuccessful.
        invalid_credentials: "invalid_credentials",

        // An error state that's assigned if the user has no available registries/roles
        unable_to_authorize: "unable_to_authorize",

        // A catch-all state that implies that some type of error has occurred.
        invalid_state: "invalid_state"
    };

    // NOTE: this is added out of alphabetical order because it refers to self.userStates.
    this.defaults = {

        // These components are outside the context of a registry and should always be available.
        components: {
            error_page: {
                key: "error_page",
                selector: "#error_page",
                type: "page"
            },
            login_page: {
                key: "login_page",
                selector: "#login_page",
                type: "page"
            },
            registry_selection_page: {
                key: "registry_selection_page",
                selector: "#registry_selection_page",
                type: "page"
            }
        },

        user: {
            display_name: null,
            id: null,
            login_id: null,
            next_authentication: null, // TODO: Not yet implemented!
            next_authorization: null, // TODO: Not yet implemented!
            state: self.userStates.unauthenticated
        }
    };


    //----------------------------------------------------------------------------------------------------------------------------------------
    // Member variables
    //----------------------------------------------------------------------------------------------------------------------------------------

    // A container for key/value data to be persisted across multiple pages.
    this.appData = null;

    // Collections and methods that synchronize the loading and initialization of builder objects.
    this.builder = {

        // Cached builder objects.
        // TODO: I don't think we're currently taking advantage of these when reloading!
        cache: {},

        // Asynchronously-loaded builder objects are placed in this queue when another builder object
        // is currently being initialized (indicated by the "is locked" attribute).
        initializationQueue: [],

        // Use this lock to synchronize builder object initialization.
        is_locked: false,

        // Clear and reinitialize this object's contents.
        reset: function () {
            self.builder.cache = {};
            self.builder.initializationQueue = [];
            self.builder.is_locked = false;
        },

        // Builder objects will be in one of these 3 statuses: "loaded" (once the file has been loaded from the filesystem and the object has 
        // been "immediately-invoked"), "ready to initialize" (if another builder is being initialized and this object must be placed in the 
        // initialization queue), and "initialized".
        statuses: {
            loaded: "loaded",
            ready_to_initialize: "ready_to_initialize",
            initialized: "initialized"
        }
    };

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
    //    // The component's key in this collection
    //    key: "home_page",
    //
    //    // The selector that identifies the component's outermost container.
    //    selector: ".home-panel",
    //
    //    // The component's type (dialog, page, or panel)
    //    type: "page"
    // }
    //----------------------------------------------------------------------------------------------------------------------
    this.components = null;

    
    // Configuration settings for the Application.
    this.settings = {

        // After authentication, how long until the user needs to be re-authenticated?
        // TODO: Not yet implemented!
        authentication_interval: null,

        // After authorization, how long until the user needs to be re-authorized?
        // TODO: Not yet implemented!
        authorization_interval: null,

        // Base URLs for use when determining web service URLs appropriate to the environment.
        base_urls: null,

        // Environment can be "dev", "local", "prod", etc.
        environment: null,

        // The path to JSON files used as an alternative to actually calling web services.
        json_path: "jsonFiles",

        // Certain user states require that we override the current (or requested) page key.
        page_key_overrides: {

            invalid_credentials: "login_page",

            not_yet_authorized: "registry_selection_page",

            //registry_selected: null, 

            unable_to_authorize: "error_page?msg=No registries are available",

            unauthenticated: "login_page"
        },

        // Is the application in verbose mode?
        verbose: true,

        // Where in Web Storage should application data be persisted: "local" or "session"?
        web_storage_type: "local"
    };
 
    //----------------------------------------------------------------------------------------------------------------------
    // The user's current registry. When populated, this will contain the following:
    // - id: The registry's numeric ID
    // - name: The display name of the registry
    // - role_id: The numeric ID of the user's role in the registry
    // - role_name: The display name of the user's role in the registry 
    //----------------------------------------------------------------------------------------------------------------------
    this.registry = {};

    // User info including authentication and authorization details.
    this.user = {

        // The user's display name.
        display_name: null,

        // The user's numeric ID.
        id: null,
        
        // The user's login ID.
        login_id: null,

        // The date/time the user needs to re-authenticate.
        // TODO: Not yet implemented!
        next_authentication: null,

        // The date/time the user needs to re-authorize and select a registry/role.
        // TODO: Not yet implemented!
        next_authorization: null,

        // The user's current state with respect to authentication, authorization, and registry selection.
        state: self.userStates.unauthenticated
    };

    

    //----------------------------------------------------------------------------------------------------------------------------------------
    // Member methods
    //----------------------------------------------------------------------------------------------------------------------------------------

    
    // Load the builder object defined by the component metadata provided.
    this.buildComponent = function (component_) {

        if (!component_) { return self.displayError("Invalid component", "buildComponent"); }

        // Replace periods in the key with slashes in case the component data is stored in a subdirectory.
        var keyAsPath = component_.key.replace(/\./g, "/");

        // The location of the JavaScript "builder" file.
        var builderURL = "components/" + keyAsPath + "/builder.js";
   
        // Load the script.
        $_.getScript(builderURL)
        .success(function (script_, textStatus_) {
            
        })
        .error(function (jqXHR_, textStatus_, errorThrown_) {
            return self.displayError(errorThrown_, "buildComponent");
        });
    };


    // Close the dialog specified by the selector.
    this.closeDialog = function (selector_) {

        if (!selector_) { self.displayError("Invalid selector", "closeDialog()"); return false; }

        $_(selector_).modal("hide");
        // TODO: how to destroy this modal? Do we need to???
    };


    // Display the HTML fragment that represents a dialog, page, or panel.
    this.displayComponent = function (key_, parentSelector_) {
        
        // Validate the component key parameter and the registry.
        if (!key_) { return self.displayError("Invalid component key", "displayComponent"); }
     
        // Get the component that has this key.
        var component = self.getComponent(key_);

        /* console.log("Component Check inside getComponent of Application.js",component);*/
        
        if (!component) { return self.displayError("Invalid component for key " + key_, "displayComponent"); }

        // The component type will determine which parent selector to use (where the component will be loaded).
        if (component.type === "dialog") {

            // The dialog will be loaded into the dialog container DOM Element.
            parentSelector_ = self.selectors.dialog_container;

            // Go ahead and clear the dialog container's contents.
            $_(self.selectors.dialog_container).html("");

        } else if (component.type === "page") {

            // The page will be loaded into the page container DOM Element.
            parentSelector_ = self.selectors.page_container;

        } else if (component.type === "panel") {

            // The panel will be loaded into the specified DOM Element.
            if (!parentSelector_) { return self.displayError("Parent selector is required for panel component type", "displayComponent"); }

        } else {
            return self.displayError("Unrecognized component type " + component.type, "displayComponent");
        }

        // Replace periods in the key with slashes in case the component data is stored in a subdirectory.
        var keyAsPath = key_.replace(/\./g, "/");

        // Construct the URLs.
        var fragmentURL = "components/" + keyAsPath + "/fragment.html";
        var builderURL = "components/" + keyAsPath + "/builder.js";
        var styleURL = "components/" + keyAsPath + "/style.css";
        
        // TODO: the HTML fragments and style sheets are getting cached. This isn't desirable if we regularly replace them, hence the unique ID appended to the end.
        fragmentURL += "?" + Common.uniqueID();
        styleURL += "?" + Common.uniqueID();

        // The DOM ID of the link (stylesheet) Element.
        var styleID = key_ + "_css";

        // Load the stylesheet (the function will ensure that the stylesheet hasn't already been loaded).
        self.loadCSS(styleID, styleURL);

        // Load the fragment into the page container.
        $_(parentSelector_).load(fragmentURL, function () {

            // Use the builder object to populate it.
            self.buildComponent(component);
        });
    };

    // Display the HTML fragment that represents a dialog, page, or panel.
    this.displayComponent = function (key_, parentSelector_, userdata_) {
        this.userData = userdata_;
        // Validate the component key parameter and the registry.
        if (!key_) { return self.displayError("Invalid component key", "displayComponent"); }

        // Get the component that has this key.
        var component = self.getComponent(key_);
        if (!component) { return self.displayError("Invalid component for key " + key_, "displayComponent"); }

        // The component type will determine which parent selector to use (where the component will be loaded).
        if (component.type === "dialog") {

            // The dialog will be loaded into the dialog container DOM Element.
            parentSelector_ = self.selectors.dialog_container;

            // Go ahead and clear the dialog container's contents.
            $_(self.selectors.dialog_container).html("");

        } else if (component.type === "page") {

            // The page will be loaded into the page container DOM Element.
            parentSelector_ = self.selectors.page_container;

        } else if (component.type === "panel") {

            // The panel will be loaded into the specified DOM Element.
            if (!parentSelector_) { return self.displayError("Parent selector is required for panel component type", "displayComponent"); }

        } else {
            return self.displayError("Unrecognized component type " + component.type, "displayComponent");
        }

        // Replace periods in the key with slashes in case the component data is stored in a subdirectory.
        var keyAsPath = key_.replace(/\./g, "/");

        // Construct the URLs.
        var fragmentURL = "components/" + keyAsPath + "/fragment.html";
        var builderURL = "components/" + keyAsPath + "/builder.js";
        var styleURL = "components/" + keyAsPath + "/style.css";

        // TODO: the HTML fragments and style sheets are getting cached. This isn't desirable if we regularly replace them, hence the unique ID appended to the end.
        fragmentURL += "?" + Common.uniqueID();
        styleURL += "?" + Common.uniqueID();

        // The DOM ID of the link (stylesheet) Element.
        var styleID = key_ + "_css";

        // Load the stylesheet (the function will ensure that the stylesheet hasn't already been loaded).
        self.loadCSS(styleID, styleURL);

        // Load the fragment into the page container.
        $_(parentSelector_).load(fragmentURL, function () {

            // Use the builder object to populate it.
            self.buildComponent(component);
        });
    };


    // This is a placeholder method that displays an error message to the user. Note that the location 
    // parameter is optional, but message is required.   
    // TODO: This should be improved/replaced with a proper error dialog! 
    this.displayError = function (message_, location_) {
        if (!message_) { message_ = "An unknown error occurred"; }
        if (location_) { message_ += " in " + location_; }
        //alert(message_);
        dalert.alert(message_,"Error");
        return false;  
    };



    // Make an AJAX GET request.
    this.get = function (data_, done_, fail_, webServiceKey_) {

        // Lookup the requested web service URL
        var url = self.lookupWebServiceURL(webServiceKey_);
        if (!url) { return self.displayError("Invalid URL", "get"); }

        // If a registry has been selected and its ID hasn't been added to the data, add it now.
        if (self.registry && self.registry.id) { data_[self.keys.registry_id] = self.registry.id; }
       
        // Create the AJAX request.
        $_.ajax({
            cache: false,
            data: data_,
            method: "GET",
            url: url            
        })
        .success(self.wrapResponseCallback(done_))
        .error(fail_);
    };

    // Retrieve a component from the collection by its' key.
    this.getComponent = function (key_) {

        var keyTokens = key_.split(".");
        
        // Make sure there's at least one key token.
        if (!keyTokens || keyTokens.length < 1) { return self.displayError("Invalid key tokens", "getComponent"); }

        // Get a component for the first key.
        var component = self.components[keyTokens[0]];

        if (!component) { return self.displayError("Invalid component for key " + keyTokens[0]); }

        if (keyTokens.length > 1) {
            for (var t = 1; t < keyTokens.length; t++) {
                component = component[keyTokens[t]];
                if (!component) { return self.displayError("Invalid component for key " + keyTokens[t]); }
            }
        }

        return component;
    };

    // Make an AJAX GET request but don't automatically pre-validate the response.
    this.getNoPreValidate = function (data_, done_, fail_, webServiceKey_) {

        // Lookup the requested web service URL
        var url = self.lookupWebServiceURL(webServiceKey_);
        if (!url) { return self.displayError("Invalid URL", "get"); }

        // If a registry has been selected and its ID hasn't been added to the data, add it now.
        if (self.registry && self.registry.id) { data_[self.keys.registry_id] = self.registry.id; }

        // Create the AJAX request.
        $_.ajax({
            data: data_,
            method: "GET",
            url: url
        })
        .success(done_)
        .error(fail_);
    };

    // Make an AJAX GET request but don't automatically pre-validate the response.
    this.getNoPreValidateConfigReg = function (data_, done_, fail_, webServiceKey_) {
        // Lookup the requested web service URL
        var url = self.lookupWebServiceURL(webServiceKey_);
        if (!url) { return self.displayError("Invalid URL", "get"); }
        
        // Create the AJAX request.
        $_.ajax({
            data: data_,
            method: "GET",
            url: url
        })
        .success(done_)
        .error(fail_);
    };

    // Get a key's value stored in application data.
    this.getData = function (key_) {

        // Validate the key.
        if (!key_) { self.displayError("Invalid key in getData"); return null; }

        return self.appData[key_];
    };


    // Get the window location hash.
    this.getHash = function () {

        var hash = window.location.hash.replace("#", "");
        if (hash) {
            // If a hash exists, remove any "non hash" characters.
            var qIndex = hash.indexOf("?");
            if (qIndex >= 0) { hash = hash.slice(0, qIndex); }
        }

        return hash;
    };

    // Does the current user state have an associated page key? If so, it needs to override the current page key.
    this.getPageKeyOverride = function () {

        var pageKey = null;

        // If page key overrides exist, lookup the user state in the collection.
        if (self.settings.page_key_overrides) { pageKey = self.settings.page_key_overrides[self.user.state]; }
        if (typeof (pageKey) === "undefined") { pageKey = null; }

        return pageKey;
    };

    // This is a convenience method that gets a patient object that was saved as application data. 
    this.getPatient = function () {
        return self.getData("selected_patient");
    };

    // This function handles a change to the window.location.hash and updates the application_contents panel accordingly.
    this.handleHashChange = function () {
        
        // Get the (filtered) hash (which should be a page key).
        var currentPageKey = self.getHash();

        if (currentPageKey === "login_page") {

            // Display the login page.
            self.displayComponent("login_page", null);
            return true;
        }

        // If the current page key is empty or needs to change based on the user state, we 
        // will assign a value to "next page key". If "next page key" is ultimately empty, we
        // can proceed with displaying "current page key". Otherwise, if "next page key" isn't 
        // empty, we need to display that page key, instead (and modify the window location hash
        // accordingly).
        var nextPageKey = null;

        // If no page key was provided as a hash, go to the "default page".
        if (!currentPageKey) {
            if (self.registry) {
                nextPageKey = self.registry.default_page;
            } else {
                nextPageKey = "login_page";
            }
        }

        // Is the current user state supposed to override this page key?
        var pageKeyOverride = self.getPageKeyOverride();
        if (pageKeyOverride && pageKeyOverride !== currentPageKey) { nextPageKey = pageKeyOverride; }

        if (nextPageKey) {
            // Since "next page key" isn't empty, we need to update GrapeJelly's "current page key" and
            // modify the window location hash in order to display the correct page key.
            window.location.hash = "#" + nextPageKey;
            return true;
        } else {
            // Proceed with "current page key".
            self.displayComponent(currentPageKey, null);
        }
    };


    // Initialize the application.
    this.initialize = function () {
       
        // Make sure we can use HTML5 web storage.
        if (typeof (Storage) === "undefined") { return self.displayError("Error: web storage is not supported by your browser. Please notify an administrator.", null); }

        // Initialize the builder objects.
        self.builder.reset();
      
        // Load default components.
        self.components = {};
        self.updateComponents(self.defaults.components);

        // Load any user, registry, and application data maintained in (HTML5) Web Storage.
        self.loadSavedUserData();
        self.loadSavedRegistry();
        self.loadSavedAppData();

        // Has a registry already been loaded? If so, add the registry's components to the local collection.
        if (self.registry && self.registry.components) { self.updateComponents(self.registry.components); }

        // Handle the "hash change" event.
        $_(window).on("hashchange", function () {
            self.handleHashChange();
        });

        // The "registry loaded" event is triggered when a registry object has been loaded.
        $_(window).on(self.events.registry_loaded, function (event_, registry_) {

            // Update the user's state to indicate that a valid registry has been selected.
            self.user.state = self.userStates.registry_selected;

            // Maintain the registry object.
            self.registry = registry_;

            // Get the actual registry ID that was selected and use it to populate our registry object.
            var registryID = self.getData(self.keys.selected_registry_id);
            if (!registryID) { return self.displayError("Invalid selected registry ID", "handling registry_loaded event"); }
            self.registry.id = registryID;

            // Load cv terms
            self.loadRegistryTerms();

            // Save the updated user and registry in web storage.
            self.saveUserData();
            self.saveRegistry();

            // Populate the local collection of components with defaults and those defined by the registry.
            self.components = {};
            self.updateComponents(self.defaults.components);
            self.updateComponents(self.registry.components);

            // Since the registry has loaded, update the UI accordingly.
            self.updateUIFromUserState();

            // Navigate to the registry's default page.
            self.setPage(self.registry.default_page);
        });

        // The "builder loaded" event is triggered when a builder object has been loaded.
        $_(window).on(self.events.builder_loaded, function (event_, builder_) {

            if (!builder_) { return self.displayError("Invalid builder", "handling builder_loaded event"); }

            if (self.builder.is_locked) {

                // Another builder is being initialized. Update this one's status and add it to the initialization queue.
                builder_.status = self.builder.statuses.ready_to_initialize;
                self.builder.initializationQueue.push(builder_);
            } else {
                
                // We can go ahead and initialize this one. Set the lock to true.
                self.builder.is_locked = true;
                self.builder.cache[builder_.key] = builder_;
                self.builder.cache[builder_.key].initialize();
            }
        });

        // The "builder initialized" event is triggered when an already-loaded builder object has been initialized. 
        $_(window).on(self.events.builder_initialized, function (event_, data_) {

            if (!data_ || !data_.key) { return self.displayError("Invalid builder data", "builder_initialized"); }
           
            // Presumably, this is the one that most recently set the mutex lock.
            if ((self.builder.cache[data_.key]) != undefined) {
                self.builder.cache[data_.key].status = self.builder.statuses.initialized;
            }
            // Release the lock.
            self.builder.is_locked = false;

            // Process any pending builders in the initialization queue.
            if (self.builder.initializationQueue.length > 0) {
                var builder = self.builder.initializationQueue[0];
                if (builder) {
                    // We can go ahead and initialize this one. Set the lock to true.
                    self.builder.is_locked = true;
                    self.builder.cache[builder_.key] = builder;

                    // Remove from the queue.
                    self.builder.initializationQueue.splice(0, 1);

                    // Initialize the builder.
                    self.builder.cache[builder_.key].initialize();
                }
            }
        });

        // Trigger an event to indicate that the application has initialized.
        $_(window).trigger(self.events.application_initialized);

        // Display the current page.
        self.handleHashChange();

       
        if (self.appData != null) {
            var user_data = this.getData(self.keys.selected_registry_roleName);
            // alert("user_data" + self.appData[self.keys.selected_registry_roleName]);
            if (user_data == "Manager") {
                $_("#addRegList").hide();
            } else if (user_data == "Registrar") {
                $_("#adminDropDown").hide();
            }
        }
    };


    // Retrieve application data from (HTML5) Web Storage.
    this.loadSavedAppData = function () {

        // This will contain "stringified" json persisted in HTML5 Web Storage.
        var strAppData = null;

        if (self.settings.web_storage_type === "local") {
            strAppData = localStorage.getItem(self.keys.application_data);
        } else if (self.settings.web_storage_type === "session") {
            strAppData = sessionStorage.getItem(self.keys.application_data);
        }

        // Restore the application data from the stringified JSON.
        if (strAppData && strAppData !== "") {
            self.appData = JSON.parse(strAppData);
        } else {
            self.appData = {};
        }
    };

    // Dynamically load a stylesheet associated with a component.
    this.loadCSS = function (id_, url_) {

        if (!id_) { return self.displayError("Invalid ID parameter", "loadCSS"); }
        if (!url_) { return self.displayError("Invalid URL parameter", "loadCSS"); }

        // Has the style already been added to the DOM? If so, remove it so it can be re-added.
        if ($_("#" + id_).length > 0) { $_("#" + id_).remove(); }

        var stylesheet = document.createElement("link");
        stylesheet.href = url_;
        stylesheet.id = id_;
        stylesheet.rel = "stylesheet";
        stylesheet.type = "text/css";
        stylesheet.onload = function () {
            // NOTE: at this point, we know that the file has been loaded.
        }

        document.getElementsByTagName("head")[0].appendChild(stylesheet);
    };


    // Load the metadata object and stylesheet for the registry with the specified key.
    this.loadRegistryMetadata = function (key_) {

        // Validate the parameter.
        if (!key_) { return self.displayError("Invalid registry key parameter", "loadRegistryMetadata"); }

        // Determine the URLs.
        var registryURL = "registries/registry.js";
        var styleURL = "registries/style.css";

        // The ID of the link (stylesheet) Element.
        // TESTING: if all registry stylesheets use the same DOM ID, can we ensure that only one will be loaded at a time?
        var styleID = "registry_css"; // "registry_" + key_ + "_css";

        // Load the stylesheet (the function will ensure that the stylesheet hasn't already been loaded.
        self.loadCSS(styleID, styleURL);

        // Load the registry metadata (registry.js).
        $_.getScript(registryURL)
        .success(function (script_, textStatus_) {
            if (self.settings.verbose) { console.log("The " + key_ + " registry loaded successfully"); }
        })
        .error(function (jqxhr_, settings_, exception_) {
            self.displayError(exception_, "loadRegistryMetadata");
        });
    };

    this.loadRegistryTerms = function () {

        // TODO: This shouldn't be hard-coded here. What's a more centralized location for web service URLs?
        var cvTermsURL = "getcvterms";
        var data = {
            registry_id: self.registry.id,
            cvs: self.registry.registry_cvs

            /*cvs: new String("PHEDRS Patient Attribute Ontology," +
                "PHEDRS Patient Status CV," +
                "PHEDRS Review Status CV," +
                "UAB COPD Registry Encounter Attribute CV," +
                "UAB COPD Registry Patient Attributes," +
                "Patient Registry Evidence Codes")*/
        };
        
        self.get(data, self.processRegistryTerms, self.processError, cvTermsURL);
    };

    // Retrieve registry data from (HTML5) Web Storage.
    this.loadSavedRegistry = function () {

        var strRegistry = null;

        if (self.settings.web_storage_type === "local") {
            strRegistry = localStorage.getItem(self.keys.registry);
        } else if (self.settings.web_storage_type === "session") {
            strRegistry = sessionStorage.getItem(self.keys.registry);
        }

        // If no valid registry was persisted in web storage, there's no need to proceed.
        if (!strRegistry) {
            self.registry = null;
            return true;
        }

        // Restore the JSON object from the stringified representation.
        self.registry = JSON.parse(strRegistry);

        // Are the registry and components valid? 
        if (self.registry && self.registry.components) {

            // Update the local components with the registry's components.
            self.updateComponents(self.registry.components);

            // Update the UI.
            self.updateUIFromUserState();
        }
    };

    // Retrieve user data from (HTML5) Web Storage.
    this.loadSavedUserData = function () {

        // This will contain "stringified" json persisted in HTML5 Web Storage.
        var user_data = null;

        if (self.settings.web_storage_type === "local") {
            user_data = localStorage.getItem(self.keys.user_data);
        } else if (self.settings.web_storage_type === "session") {
            user_data = sessionStorage.getItem(self.keys.user_data);
        }

        // Restore the user data from the stringified JSON.
        if (user_data) {
            self.user = JSON.parse(user_data);
        } else {
            // Provide an empty/default version of user data.
            self.user = JSON.parse(JSON.stringify(self.defaults.user));
        }
    };


    // Logout of the application.
    this.logout = function () {

        self.reset();
        
        // Redirect to the login page.
        self.setPage("login_page"); 
    };


    // Lookup the requested web service URL
    this.lookupWebServiceURL = function (key_) {

        if (!key_) { return self.displayError("Invalid key", "lookupWebServiceURL"); }
        if (!self.settings.environment) { return self.displayError("Invalid environment", "lookupWebServiceURL"); }

        // Lookup the URL that corresponds to the current environment.
        var url = new String(self.settings.base_urls[self.settings.environment]);
        if (!url) { return self.displayError("Invalid base url for environment " + self.settings.environment, "lookupWebServiceURL"); }

        url += key_;

        if (self.settings.environment === "local") { url += ".json?v=" + Common.uniqueID(); }
        
        return url;
    };


    // Populate a select list with cv terms.
    this.populateTermList = function (cvName_, listSelector_, includeEmptyOption_) {

       if (!cvName_) { return self.displayError("Invalid cv name", "populateTermList"); }
       if (!listSelector_) { return self.displayError("Invalid list selector", "populateTermList"); }


       /*console.log("March 23 Testing "):
       console.log("Name of the CV"+cvName_);*/

       var cvNames = cvName_.split(',');
       // if empty, nothing was passed
       if (!cvNames.length) { return self.displayError("No cv names supplied"); }
       // iterate over all matches
       var html = "";

       if (includeEmptyOption_) { html = "<option value=''></option>"; }

       cvNames.forEach(function(cvName) {
           var cv = self.registry.cv[cvName];
           if (!cv) { return self.displayError("Invalid CV: " + cvName, "populateTermList"); }

           $_.each(cv.terms, function (index_, term_) {

              if (!term_ || !term_.id || !term_.name) { return self.displayError("Invalid term at index " + index_, "populateTermList"); }

              html += "<option value='" + term_.id + "'>" + term_.name + "</option>";
           });
     
        });

        $_(listSelector_).html(html);
    };
    

     
    
    // Make an AJAX POST request.
    this.post = function (data_, done_, fail_, webServiceKey_) {

        // Lookup the requested web service URL
        var url = self.lookupWebServiceURL(webServiceKey_);
        if (!url) { return self.displayError("Invalid URL", "post"); }

        // If a registry has been selected and its ID hasn't been added to the data, add it now.
        if (self.registry && self.registry.id) { data_[self.keys.registry_id] = self.registry.id; }

        // Only GET requests are valid when environment is local (when retrieving JSON objects).
        var method = "POST";
        if (self.settings.environment === "local") { method = "GET"; }

        // Create the AJAX request.
        $_.ajax({
            data: data_,
            method: method,
            url: url
        })
        .success(self.wrapResponseCallback(done_))
        .error(fail_);
    };

    // Make an AJAX POST request but don't automatically pre-validate the response.
    this.postNoPreValidate = function (data_, done_, fail_, webServiceKey_) {

        // Lookup the requested web service URL
        var url = self.lookupWebServiceURL(webServiceKey_);
        if (!url) { return self.displayError("Invalid URL", "post"); }

        // If a registry has been selected and its ID hasn't been added to the data, add it now.
        if (self.registry && self.registry.id) { data_[self.keys.registry_id] = self.registry.id; }

        // Only GET requests are valid when environment is local (when retrieving JSON objects).
        var method = "POST";
        if (self.settings.environment === "local") { method = "GET"; }

        // Create the AJAX request.
        $_.ajax({
            data: data_,
            method: method,
            url: url
        })
        .success(done_)
        .error(fail_);
    };


    // A function that can be used in the "fail" AJAX request callback.
    // TODO: This should be improved/replaced with a proper error dialog! 
    this.processError = function (jqXHR_, textStatus_, errorThrown_) {

        if (jqXHR_ && jqXHR_.responseJSON) {

            // Use "data" as a convenient alias.
            var data = jqXHR_.responseJSON;

            // Get the current page key from window.location.hash.
            var currentPageKey = self.getHash();

            // Does the current user state override the page key? If so, display the overridden page key (unless it's already the current page key).
            var pageKeyOverride = self.getPageKeyOverride();
            if (pageKeyOverride && pageKeyOverride !== currentPageKey) {
                //if (self.settings.verbose) { console.log("overriding page key " + currentPageKey + " with " + pageKeyOverride); }
                self.setPage(pageKeyOverride);
            }
        } else {
            return self.displayError("Error: " + errorThrown_, "processError");
        }
    };


    this.processRegistryTerms = function (data_, textStatus_, jqXHR_) {

        // Initialize the collection.
        self.registry.cv = {};

        // Iterate thru the cv terms that were returned, parse them as CV's and associated terms, and add them to the collection.
        $_.each(data_.cvtermList, function (index_, term_) {

            var cv = self.registry.cv[term_.cvName];
            if (!cv) {
                cv = {
                    id: term_.cvId,
                    name: term_.cvName,
                    terms: []
                };
            }
            
            cv.terms.push({
                id: term_.cvtermId,
                name: term_.cvtermName
            });
            
            // Add (or replace) the cv to the collection.
            self.registry.cv[cv.name] = cv;
        });

        // Make sure the terms get saved along with their registry in web storage.
        self.saveRegistry();
    };


    // If the JSON data was returned with content type of "text/plain", parse as JSON before returning.
    this.processResponse = function (data_, jqXHR_) {

        return (typeof(data_) === "object" ? data_ : JSON.parse(data_));
    };


    // Process the application settings.
    this.processSettings = function () {

        if (!$_.phedrs.settings) { self.displayError("Invalid settings", "processSettings"); return false; }

        // After authentication, how long until the user needs to be re-authenticated? [OPTIONAL] 
        if ($_.phedrs.settings.authentication_interval) { self.settings.authentication_interval = $_.phedrs.settings.authentication_interval; }

        // After authorization, how long until the user needs to be re-authorized? [OPTIONAL] 
        if ($_.phedrs.settings.authorization_interval) { self.settings.authorization_interval = $_.phedrs.settings.authorization_interval; }

        // Base URLs for use when determining web service URLs appropriate to the environment.
        if (!$_.phedrs.settings.base_urls) { return self.displayError("Invalid base_urls", "processSettings"); }
        self.settings.base_urls = $_.phedrs.settings.base_urls;

        // Verbose mode? [OPTIONAL] 
        if ($_.phedrs.settings.verbose) { self.settings.verbose = $_.phedrs.settings.verbose; }

        // The execution environment.
        if (!$_.phedrs.settings.environment) { return self.displayError("Invalid environment", "processSettings"); }
        self.settings.environment = $_.phedrs.settings.environment;

        // The path to JSON files used as an alternative to actually calling web services. [OPTIONAL] 
        if ($_.phedrs.settings.json_path) { self.settings.json_path = $_.phedrs.settings.json_path; }

        // The web storage type has a default that can be overwritten. [OPTIONAL] 
        if ($_.phedrs.settings.web_storage_type) {
            if ($_.phedrs.settings.web_storage_type !== "local" && $_.phedrs.settings.web_storage_type != "session") {
                alert("Web storage type is invalid"); return false;
            } else {
                self.settings.web_storage_type = $_.phedrs.settings.web_storage_type;
            }
        }
    };

    // Reset all data to its' original state.
    this.reset = function () {

        //-----------------------------------------------------------------------------
        // Application data
        //-----------------------------------------------------------------------------
        self.appData = null;

        if (self.settings.web_storage_type === "local") {
            localStorage.removeItem(self.keys.app_data);
        } else if (self.settings.web_storage_type === "session") {
            sessionStorage.removeItem(self.keys.app_data);
        }

        //-----------------------------------------------------------------------------
        // Components
        //-----------------------------------------------------------------------------
        self.components = {};
        self.updateComponents(self.defaults.components);

        //-----------------------------------------------------------------------------
        // Registry
        //-----------------------------------------------------------------------------
        self.registry = null;

        if (self.settings.web_storage_type === "local") {
            localStorage.removeItem(self.keys.registry);
        } else if (self.settings.web_storage_type === "session") {
            sessionStorage.removeItem(self.keys.registry);
        }

        //-----------------------------------------------------------------------------
        // User
        //-----------------------------------------------------------------------------
        self.user = self.defaults.user;
        var strUser = JSON.stringify(self.user);

        if (self.settings.web_storage_type === "local") {
            localStorage.setItem(self.keys.user_data, strUser);
        } else if (self.settings.web_storage_type === "session") {
            sessionStorage.setItem(self.keys.user_data, strUser);
        }
    };

    // Save everything
    this.saveAll = function () {
        self.saveAppData();
        self.saveRegistry();
        self.saveUserData();
    };

    // Persist application data in (HTML5) Web Storage.
    this.saveAppData = function () {

        var strAppData = null;

        // Stringify the JSON object.
        if (self.appData) { strAppData = JSON.stringify(self.appData); }

        if (self.settings.web_storage_type === "local") {
            localStorage.setItem(self.keys.application_data, strAppData);
        } else if (self.settings.web_storage_type === "session") {
            sessionStorage.setItem(self.keys.application_data, strAppData);
        }
    };

    // Persist the current registry in (HTML5) Web Storage.
    this.saveRegistry = function () {

        var strRegistry = null;
        if (self.registry) { strRegistry = JSON.stringify(self.registry); }
        
        if (self.settings.web_storage_type === "local") {
            localStorage.setItem(self.keys.registry, strRegistry);
        } else if (self.settings.web_storage_type === "session") {
            sessionStorage.setItem(self.keys.registry, strRegistry);
        }
    };

    // Persist user data in (HTML5) Web Storage.
    this.saveUserData = function () {

        var user_data = null;

        // Stringify the JSON object.
        if (self.user) { user_data = JSON.stringify(self.user); }

        if (self.settings.web_storage_type === "local") {
            localStorage.setItem(self.keys.user_data, user_data);
        } else if (self.settings.web_storage_type === "session") {
            sessionStorage.setItem(self.keys.user_data, user_data);
        }
    };

    // Maintain this key/value pair in application data.
    this.setData = function (key_, value_) {

        // Validate the key and ensure that application data has been initialized.
        if (!key_) { return self.displayError("Invalid key", "setData"); }
        if (!self.appData) { self.appData = {}; }

        // Add the key/value.
        self.appData[key_] = value_;

        // Persist in Web Storage.
        self.saveAppData();
    };


    // Modify the URL's hash to change the current "page" (the contents of the "application_container"). 
    this.setPage = function (pageKey_) {

        if (!pageKey_) { return self.displayError("Invalid page key in setPage()"); }

        // Is the current user state supposed to override the requested page key?
        var pageKeyOverride = self.getPageKeyOverride();
        if (pageKeyOverride) { pageKey_ = pageKeyOverride; }

        // Get the current hash (page key) for comparison.
        var currentPageKey = self.getHash();
        if (!currentPageKey) { return self.displayError("The current hash is empty in setPage()"); }

        // Before proceeding, save anything that might have been modified and needs to be maintained in web storage.
        self.saveAll();

        if (currentPageKey === pageKey_) {

            // Since we're trying to refresh the current page, the hashchange event has to be manually triggered. 
            window.location.hash = "#" + pageKey_;
            $_(window).trigger("hashchange");

        } else {
            window.location.hash = "#" + pageKey_;
        }
    };


    // This is a convenience method that creates a patient object and saves it as application data. 
    this.setPatient = function (mrn_, patientName_) {

        if (!mrn_) { return self.displayError("Invalid patient MRN", "setPatient"); }
        if (!patientName_) { return self.displayError("Invalid patient name", "setPatient"); }
        
        // Create a patient object and add it to application data. 
        var patient = {
            mrn: mrn_,
            name: patientName_,
            registry_patient_id: null,
            registry_status: {
                id: null,
                label: null
            },
            workflow_status: {
                id: null,
                label: null
            }
        };

        self.setData("selected_patient", patient);
    };


    // General-purpose UI/display methods.
    this.ui = {

        //----------------------------------------------------------------------------------------------------------------------------------------
        // Registry-specific labels or text.
        //----------------------------------------------------------------------------------------------------------------------------------------
        hideRegistryInfo: function () {
            $_(self.selectors.menubar_registry_label).html("");
        },

        showRegistryInfo: function () {

            var registryLabel = "";
            if (self.registry && self.registry.title) { registryLabel = self.registry.title; }
            if (!registryLabel) { registryLabel = "(Unknown registry)"; }

            $_(self.selectors.menubar_registry_label).html(registryLabel);
        },

        //----------------------------------------------------------------------------------------------------------------------------------------
        // The user's displayed login name/ID.
        //----------------------------------------------------------------------------------------------------------------------------------------
        hideUserInfo: function () {
            $_(self.selectors.menubar_user_name).html("");
        },

        showUserInfo: function () {
            var userName = self.user.display_name;
            if (!userName) { userName = self.user.display_name; }
            if (!userName) { userName = "(Unknown user)"; }
            $_(self.selectors.menubar_user_name).html(userName);
        }
    };

    // Update the local collection of components.
    this.updateComponents = function (components_) {

        if (!components_) { return self.displayError("Invalid components", "updateComponents"); }

        if (!self.components) { self.components = {}; }

        $_.each(Object.keys(components_), function (index_, key_) {
            var component = components_[key_];
            if (component) { self.components[key_] = component; }
        });
    };

    // Clear or show user and registry-related text based on the user's current state.
    this.updateUIFromUserState = function () {

        if (!self.user.state) { self.user.state = self.userStates.unauthenticated; }
        
        if (self.user.state === self.userStates.unauthenticated) {

            // Clear the user and registry info in the display.
            self.ui.hideRegistryInfo();
            self.ui.hideUserInfo();

        } else if (self.user.state === self.userStates.invalid_credentials) {

            // Clear the user and registry info in the display.
            self.ui.hideRegistryInfo();
            self.ui.hideUserInfo();

        } else if (self.user.state === self.userStates.not_yet_authorized) {

            // Display the user info but hide the registry info.
            self.ui.hideRegistryInfo();
            self.ui.showUserInfo();

        } else if (self.user.state === self.userStates.unable_to_authorize) {

            // Display the user info but hide the registry info.
            self.ui.hideRegistryInfo();
            self.ui.showUserInfo();

        } else if (self.user.state === self.userStates.registry_selected) {

            // Display the user and registry info.
            self.ui.showRegistryInfo();
            self.ui.showUserInfo();
        }
    };


    // Use this to validate every JSON response from a web service. 
    this.validateResponse = function (data_, textStatus_, jqXHR_) {

        var message = null;
        var status = null;

        // Unfortunately, the web services can return the status in several different ways.
        if (data_.webServiceStatus) {
            message = data_.webServiceStatus.message;
            status = data_.webServiceStatus.status;
        } else if (data_.webservice_status) {
            message = data_.webservice_status.message;
            status = data_.webservice_status.status;
        } else if (data_.status) {
            message = data_.message;
            status = data_.status;
        }

        if (!status || status !== "SUCCESS") {
            if (!message) { message = "(Unknown error)"; }
            return self.displayError(message, "validateResponse");
        } else {
            return true;
        }
    };

    // Create a wrapper function for an AJAX "done" callback that always validates the response before invoking the callback function provided.
    this.wrapResponseCallback = function (callback_) {
        return function (data_, textStatus_, jqXHR_) {

            // Make sure a JSON object was returned.
            data_ = self.processResponse(data_, jqXHR_);

            // Validate the response.
            if (!self.validateResponse(data_, textStatus_, jqXHR_)) { return null; }

            // Call the callback.
            callback_(data_, textStatus_, jqXHR_);
        };
    };


    //----------------------------------------------------------------------------------------------------------------------------------------
    // Validate the object's parameters.
    //----------------------------------------------------------------------------------------------------------------------------------------

    // Process the settings parameter.
    self.processSettings();
};