

/* 

This "immediately-invoked function expression" (also called a "self-initializing function") sets up the PHEDRS 
namespace under jQuery and adds the top level sections: 

- app: the application instance
- settings: Application configuration options/settings
    - (TODO: add details about individual settings)

Once jQuery handles the "ready" event (when the DOM hierarchy has been constructed), the initialize function
is called to actually create an instance of the application object.

*/
(function ($_) {

    $_.phedrs = {

        // The application object.
        app: null,

        // Call this function to initialize the application.
        initialize: function () {
            $_.phedrs.app = new Application($_);
            $_.phedrs.app.initialize();
        },

        // Application settings
        settings: {

            // The web service URLs for user authentication and authorization.
            authentication_url: "authenticate",
            authorization_url: "authorize",

            // Base URLs for use when determining web service URLs appropriate to the environment.
            base_urls: {
                //dev: "https://cashew-dev.hs.uab.edu:8443/RegistryWS/",*/
                dev: "https://phedrsdvweb1.hs.uab.edu/RegistryWS/",
                local: "jsonFiles/",
                localws: "http://localhost:8080/RegistryWS/",
                prod: ""
            },

            // Environment can be "dev", "local", or "prod".
            environment: "dev"
        }
    };

})(jQuery);