

// The Multiple Myeloma Registry Control Panel application
function MultipleMyeloma() {

    // This registry's display label
    this.displayLabel = "Multiple Myeloma Registry Control Panel";

    // The DOM Element that will contain the virtual pages
    this.pageContainer = "#pageContainer";

    // This registry's ID
    this.registryID = 345;


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
                prod: "user/profiles/profiles_list"
            },

            // For list_page
            getFollowUpList: {
                dev: null,
                local: "jsonFiles/follow_up_list.json?v=" + Common.uniqueID(),
                prod: null
            },
            getUrgentList: {
                dev: null,
                local: "jsonFiles/urgent_list.json?v=" + Common.uniqueID(),
                prod: null
            },

            getYesterdayList: {
                dev: null,
                local: "jsonFiles/yesterday_list.json?v=" + Common.uniqueID(),
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

            // Home page
            home_page: {
                is_templated: false,
                key: "home_page",
                selector: "#home_page",
                template_identifiers: null,
                type: "virtual_page",
                url: "fragments/home_page.html"
            },

            // Lists page
            lists_page: {
                is_templated: false,
                key: "lists_page",
                selector: "#lists_page",
                template_identifiers: null,
                type: "virtual_page",
                url: "fragments/lists_page.html"
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

            // Home page
            home_page: {
                builder: null,
                builder_class: "HomePage",
                builder_url: "scripts/builders/HomePage.js",
                fragment_key: "home_page",
                key: "home_page"
            },

            // Lists page
            lists_page: {
                builder: null,
                builder_class: "ListsPage",
                builder_url: "scripts/builders/ListsPage.js",
                fragment_key: "lists_page",
                key: "lists_page"
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