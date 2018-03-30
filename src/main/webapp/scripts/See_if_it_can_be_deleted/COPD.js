

// The COPD Registry Control Panel application
function COPD(registryID_) {

    var self = this;
   // debugger;
    // This registry's display label
    this.displayLabel = "COPD Registry Control Panel";

    // This registry's ID
    this.id = null;

    // The component key of the registry's start (home) page
    this.startPage = "copd_home_page";


    //----------------------------------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------------------------------
    // Return a collection of component metadata keyed by an identifier
    //
    // Example:
    //
    // component = {
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
    //    // This component's key in the collection
    //    key: "home"
    // }
    //----------------------------------------------------------------------------------------------------------------------
    this.getComponents = function () {

        //alert ("I am in COPD.js page !");
        //console.log ("I am in COPD.js page getComponents !");
        

        return {

            // Home page
            copd_home_page: {
                builder: null,
                builder_class: "COPD_HomePage",
                builder_url: "scripts/builders/COPD_HomePage.js",
                fragment_key: "copd_home_page",
                key: "copd_home_page"
            },

            //console.log ("After copd home page !");

            // Patient history dialog
            patient_history_dialog: {
                builder: null,
                builder_class: "PatientHistoryDialog",
                builder_url: "scripts/builders/PatientHistoryDialog.js",
                fragment_key: "patient_history_dialog",
                key: "patient_history_dialog"
            },

            // Review patient page
            patient_review_page: {
                builder: null,
                builder_class: "PatientReviewPage",
                builder_url: "scripts/builders/PatientReviewPage.js",
                fragment_key: "patient_review_page",
                key: "patient_review_page"
            },


            // Registry Users List 
             user_list_page: {
                builder: null,
                builder_class: "UserList",
                builder_url: "scripts/builders/UserList.js",
                fragment_key: "user_list_page",
                key: "user_list_page"

            },


            
            // NLP Document Dialog (No Longer in USE as we are not displaying Popup dialog)
             document_details_dialog: {
                builder: null,
                builder_class: "DocumentDetailsDialog",
                builder_url: "scripts/builders/DocumentDetailsDialog.js",
                fragment_key: "document_details_dialog",
                key: "document_details_dialog"

            },

           /* detection_event_dialog: {
                builder: null,
                builder_class: "DetectionEventDialog",
                builder_url: "scripts/builders/DetectionEventDialog.js",
                fragment_key: "detection_event_dialog",
                key: "detection_event_dialog"

            },*/


             // Concept Details Dialog
            concept_details_dialog: {
                builder: null,
                builder_class: "ConceptDetailsDialog",
                builder_url: "scripts/builders/ConceptDetailsDialog.js",
                fragment_key: "concept_details_dialog",
                key: "concept_details_dialog"

            },


            

            // Advanced search page
            //TODO add  advanced_search js ( Webservice needs to match the input parameters)
            advanced_search_page: {
                builder: null,
                builder_class: "PatientReviewPage",
                builder_url: "scripts/builders/AdvancedSearchPage.js",
                fragment_key: "advanced_search_page",
                key: "advanced_search_page"
            },

            
            //Add by MRN Page
            add_by_mrn_page: {
                builder: null,
                builder_class: "AddByMRNPage",
                builder_url: "scripts/builders/AddByMRNPage.js",
                fragment_key: "add_by_mrn_page",
                key: "add_by_mrn_page"
            },

            //Generate Report Page

            generate_report_page: {
                builder: null,
                builder_class: "GenerateReportPage",
                builder_url: "scripts/builders/GenerateReportPage.js",
                fragment_key: "generate_report_page",
                key: "generate_report_page"
            },

             // User Administration Page
            
            user_list_page: {
                builder: null,
                builder_class: "UserList",
                builder_url: "scripts/builders/UserList.js",
                fragment_key: "user_list_page",
                key: "user_list_page"
               
            }
        };
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
    //    // Is this a dialog, page, or a panel component?
    //    type: "page",
    //
    //    // The relative URL of the HTML fragment
    //    url: "fragments/home.html"
    // }
    //----------------------------------------------------------------------------------------------------------------------
    this.getHtmlFragments = function () {

        return {

            // COPD home page
            copd_home_page: {
                is_templated: false,
                key: "copd_home_page",
                selector: "#copd_home_page",
                template_identifiers: null,
                type: "page",
                url: "fragments/copd_home_page.html"
            },

            // Patient history dialog
            patient_history_dialog: {
                is_templated: false,
                key: "patient_history_dialog",
                selector: "#patient_history_dialog",
                template_identifiers: null,
                type: "dialog",
                url: "fragments/patient_history_dialog.html"
            },
              
           

            // Patient review page
            patient_review_page: {
                is_templated: false,
                key: "patient_review_page",
                selector: "#patient_review_page",
                template_identifiers: null,
                type: "page",
                url: "fragments/patient_review_page.html"
            },


            // Patient review page
            user_list_page: {
                is_templated: false,
                key: "user_list_page",
                selector: "#user_list_page",
                template_identifiers: null,
                type: "page",
                url: "fragments/user_list_page.html"
            },

             // NLP Document dialog (Not Used right now for the same reason mentioned above)
            document_details_dialog: {
                is_templated: false,
                key: "document_details_dialog",
                selector: "#document_details_dialog",
                template_identifiers: null,
                type: "dialog",
                url: "fragments/document_details_dialog.html"
            },

            /*detection_event_dialog: {
                is_templated: false,
                key: "document_details_dialog",
                selector: "#document_details_dialog",
                template_identifiers: null,
                type: "dialog",
                url: "fragments/document_details_dialog.html"
            },*/


             // Concept Details dialog
            concept_details_dialog: {
                is_templated: false,
                key: "concept_details_dialog",
                selector: "#concept_details_dialog",
                template_identifiers: null,
                type: "dialog",
                url: "fragments/concept_details_dialog.html"
            },

            // Advanced search page
            advanced_search_page: {
                is_templated: false,
                key: "advanced_search_page",
                selector: "#advanced_search_page",
                template_identifiers: null,
                type: "page",
                url: "fragments/advanced_search.html"
            },


            // Add by MRN Page
            add_by_mrn_page: {
                is_templated: false,
                key: "add_by_mrn_page",
                selector: "#add_by_mrn_page",
                template_identifiers: null,
                type: "page",
                url: "fragments/add_by_mrn_page.html"
            },


            // Generate Report Page
            generate_report_page: {
                is_templated: false,
                key: "generate_report_page",
                selector: "#generate_report_page",
                template_identifiers: null,
                type: "page",
                url: "fragments/generate_report_page.html"
            },


            // User List page
            user_list_page: {
                is_templated: false,
                key: "user_list_page",
                selector: "#user_list_page",
                template_identifiers: null,
                type: "page",
                url: "fragments/user_list_page.html"
            }


        };
    };


    // Validate input parameters
    if (isEmpty(registryID_)) { alert("Invalid registry ID in COPD"); return null; }
    self.id = registryID_;
};