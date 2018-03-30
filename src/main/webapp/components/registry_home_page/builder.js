jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The COPD home page builder
    return new function () {

        var self = this;

        // This maintains state after a cell is clicked and prevents double clicks from triggering an event twice.
        this.cellClicked = false;
        this.defaultTab = "";
        // The builder's current status.
        this.status = app_.builder.statuses.loaded;
        var regName = app_.getData(app_.keys.selected_registry_name);
        /*if (regName.indexOf("COPD") > -1) {
            this.defaultTab = "registry_inpatient_review";

            // This object's key in the Application.components collection.
           


            //FIXME - the tabs to display needs to come from a web service, this is also duplicate in COPD_HomePage.js
            this.tabsInfo = [
                {
                    index: "REGISTRY_INPATIENT_REVIEW",
                    key: "registry_inpatient_review"
                },
                {
                    index: "90_DAY_WINDOW",
                    key: "ninety_day_window"
                },
                {
                    index: "90_DAY_WINDOW_MEDICARE",
                    key: "ninety_day_window_medicare"
                },
                {
                    index: "REGISTRY_INPATIENT",
                    key: "registry_inpatient"
                },
                {
                    index: "ALL_REGISTRY_PATIENTS",
                    key: "all_registry_patients"
                }
            ];
        } else if (regName.indexOf("CRCP") > -1) {
            this.defaultTab = "potential_exacerbations";

            // This object's key in the Application.components collection.
            this.key = "crcp_home_page";

            //FIXME - this tab information needs to come from web service
            this.tabsInfo = [
                {
                    index: "POTENTIAL_EXACERBATION_ADMISSIONS",
                    key: "potential_exacerbations"
                },
                {
                    index: "90_DAY_WINDOW",
                    key: "ninety_day_window"
                },
                {
                    index: "CURRENT_PATIENT_ADMISSIONS",
                    key: "current_patient_admissions"
                }
            ];

        } else if (regName.indexOf("Multiple Myeloma") > -1) {
            this.defaultTab = "registry_inpatient";

            // This object's key in the Application.components collection.
            this.key = "multiplemyeloma_home_page";
           
            this.tabsInfo = [
                {
                    index: "REGISTRY_INPATIENT",
                    key: "registry_inpatient"
                },
                {
                    index: "ALL_REGISTRY_PATIENTS",
                    key: "all_registry_patients"
                }
            ];
        } */
        // Call the authorize web service with the user's login ID.
       
        this.tabbedDataGrids = null;

        this.urlKey = "getregistrypatientsintabs";

        // Initialize the virtual page
        // http://www.jqwidgets.com/jquery-widgets-documentation/documentation/jqxgrid/jquery-grid-api.htm
        this.initialize = function () {
            var data = {
                registry_id: app_.registry.id
            };
           
            app_.getNoPreValidateConfigReg(data, self.processRegistryTabs, app_.processError, "getRegistryTabs");
           

        };


        this.processRegistryTabs = function (data_, textStatus_, jqXHR_) {
            
            data_ = app_.processResponse(data_, jqXHR_);
            
            var attributeCount = 0;
            var collection = data_.registryTabsList;
            if (collection) {
                attributeCount = collection.length;
            }
            var tabsArray = [];
            $_.each(collection, function (index_, term_) {
                tabsArray.push({
                    index: term_.cvtermName,
                    key: term_.cvtermName
                })
                
            });
            this.tabsInfo = tabsArray;
            self.tabbedDataGrids = {};

            // Setup the data grids
            $_.each(tabsArray, function (index_, tabInfo_) {
                // Initialize the tabbed data grid and maintain it in a collection.
                self.tabbedDataGrids[tabInfo_.key] = new TabbedDataGrid($_, tabInfo_.key, tabInfo_.index, self.urlKey);
               
            });
            if (regName.indexOf("COPD") > -1) {
                
                this.defaultTab = "REGISTRY_INPATIENT_REVIEW";
                this.key = "copd_home_page";
                $_("#copd_home_page").css("display", "");
            } else if (regName.indexOf("CRCP") > -1) {
                this.defaultTab = "potential_exacerbations";
               
                this.key = "crcp_home_page";
                $_("#crcp_home_page").css("display", "");
            } else if (regName.indexOf("Multiple Myeloma") > -1) {
                this.defaultTab = "TAB_REVIEW";
                this.key = "multiplemyeloma_home_page";
                $_("#multiplemyeloma_home_page").css("display", "");
            }
             // Load the default tab
            var defaultDataGrid = self.tabbedDataGrids[this.defaultTab];
            if (!defaultDataGrid) { alert("Invalid data grid for " + self.defaultTab); return null; }

            $_(defaultDataGrid.tabSelector).trigger("click");
            
            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: "registry_home_page" });
        };
    };

})(jQuery, jQuery.phedrs.app));
