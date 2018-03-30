

// The CRCP Registry Control Panel application
function CRCP() {

    // This registry's display label
    this.displayLabel = "Cancer Registry Control Panel";

    // The DOM Element that will contain the virtual pages
    this.pageContainer = "#pageContainer";

    // This registry's ID
    this.registryID = 234;

    

    // Return AJAX URLs for a particular environment (dev, local, or prod).
    this.getAjaxURLs = function (environment_) {

        if (isEmpty(environment_)) { alert("Invalid environment in getAjaxURLs()"); return false; }

        // TODO: consider abstracting these outside of this object since they will likely be used for
        // any Registry Control Panel application.
        var allURLs = {

            // Get a list of users
            getUserList: {
                dev: "user/profiles/profiles_list",
                local: "jsonFiles/panel_user_list.json?v=" + Common.uniqueID(),
                //local: "http://localhost:8080/RegistryWS/registrypatients",
                prod: "user/profiles/profiles_list"
            },

            // For crcp_home_page
            getCrcpDocumentCount: {
                dev: null,
                local: "jsonFiles/crcp_document_count.json?v=" + Common.uniqueID(),
                prod: null
            },
            getCrcpInProcessList: {
                dev: null,
                local: "jsonFiles/crcp_in_process_list.json?v=" + Common.uniqueID(),                
                prod: null
            },
            getCrcpMyList: {
                dev: null,
                //local: "jsonFiles/crcp_my_list.json?v=" + Common.uniqueID(),
                local: "http://localhost:8080/RegistryWS/getregistrypatients?registry_id=2689&assigner_id=&status=Validated,Rejected",
                prod: null
            }
        };

        var urls = {};

        // Limit the URLs to a specific environment
        for (var property in allURLs) {
            if (allURLs.hasOwnProperty(property)) {
                var url = allURLs[property][environment_];
                urls[property] = url;
            }
        }

        return urls;
    };


    //----------------------------------------------------------------------------------------------------------------------
    // Return a collection of HTML fragment metadata.
    //
    // Example fragment object:
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
    //    // The selector that identifies the fragment on the page. Virtual page fragments 
    //    // will use id's as selectors and page components will use classes.
    //    selector: "#home_page", 
    //
    //    // If this is a templated fragment, the following will be a list of all templates
    //    // in the fragment that need to be replaced. 
    //    template_identifiers: [
    //      "application_title",
    //      "result_count"
    //    ],
    //
    //    // Is this a virtual page or a page component?
    //    type: "virtual_page",
    //
    //    // The relative URL of the HTML fragment
    //    url: "fragments/home.html"
    // }
    //----------------------------------------------------------------------------------------------------------------------
    this.getHtmlFragments = function () {

        return {

            // About page
            about_page: {
                is_templated: false,
                key: "about_page",
                selector: "#about_page",
                template_identifiers: null,
                type: "virtual_page",
                url: "fragments/about_page.html"
            },

            // CRCP Home page
            crcp_home_page: {
                is_templated: false,
                key: "crcp_home_page",
                selector: "#crcp_home_page",
                template_identifiers: null,
                type: "virtual_page",
                url: "fragments/crcp_home_page.html"
            },

            // Diagnosis dialog
            diagnosis_dialog: {
                is_templated: false,
                key: "diagnosis_dialog",
                selector: "#diagnosis_dialog",
                template_identifiers: null,
                type: "dialog",
                url: "fragments/diagnosis_dialog.html"
            },

            // Review patient page
            review_patient_page: {
                is_templated: false,
                key: "review_patient_page",
                selector: "#review_patient_page",
                template_identifiers: null,
                type: "virtual_page",
                url: "fragments/review_patient_page.html"
            }
        };
    };


    //----------------------------------------------------------------------------------------------------------------------
    // Return a collection of virtual page metadata keyed by a virtual page identifier
    //
    // Example:
    //
    // virtual_page = {
    //
    //    // An instance of a JavaScript "builder" object will be stored here the first time
    //    // it is needed.
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
    //    // The virtual page's key in this collection
    //    key: "home"
    // }
    //----------------------------------------------------------------------------------------------------------------------
    this.getVirtualPages = function () {

        return {

            // About page
            about_page: {
                builder: null,
                builder_class: "AboutPage",
                builder_url: "scripts/builders/AboutPage.js",
                fragment_key: "about_page",
                key: "about_page"
            },

            // CRCP Home page
            crcp_home_page: {
                builder: null,
                builder_class: "CRCP_HomePage",
                builder_url: "scripts/builders/CRCP_HomePage.js",
                fragment_key: "crcp_home_page",
                key: "crcp_home_page"
            },

            // Review patient page
            review_patient_page: {
                builder: null,
                builder_class: "ReviewPatientPage",
                builder_url: "scripts/builders/ReviewPatientPage.js",
                fragment_key: "review_patient_page",
                key: "review_patient_page"
            }
        };
    };
};