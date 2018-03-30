
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The encounters panel builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "patient_summary_page.encounters_panel";

        // The currently-selected patient.
        this.patient = null;

        this.selectors = {
            count: "#encounter_panel_count",
            edit_dialog: "#encounter_panel_edit_dialog",
            grid: "#encounter_panel_grid"
        };

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // Web service URL keys.
        this.urlKeys ={
            get_encounters: "getencounters"
        };


        // Initialize the component.
        this.initialize = function () {

            // Get the selected patient from application data.
            self.patient = app_.getPatient();
            if (!self.patient || !self.patient.mrn) { return app_.displayError("Invalid selected patient", "encounters initialize"); }

            var data = {
                cache: false,
                mrn: self.patient.mrn,
                registry_id: app_.registry.id
            };

            // Request encounters data from the web service.
            app_.get(data, self.processEncounters, app_.processError, self.urlKeys.get_encounters);

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };

        
        this.processEncounters = function (data_, textStatus_, jqXHR_) {
            
            var collection = data_.encounters;
            if (!collection) {
                // Populate the count before exiting.
                $_(self.selectors.count).html("(0)");
                return false;
            }

            // Populate the count.
            $_(self.selectors.count).html("(" + collection.length + ")");

            var source = {
                localdata: collection,
                datatype: "array"
            };
            var dataAdapter = new $_.jqx.dataAdapter(source, {
                loadComplete: function (data) { },
                loadError: function (xhr, status, error) { }
            });

            var cellsrenderer = function (row, column, value) {
                return '<div style="text-align: center; margin-top: 5px;">' + value + '</div>';
            }

            var columnrenderer = function (value) {
                return '<div style="text-align: center; margin-top: 5px;">' + value + '</div>';
            }

            $_(self.selectors.grid).jqxGrid(
            {
                source: dataAdapter,
                columnsResize: true,
                width: '1185',
               /* width: '2500',*/
                pageable: true,
                sortable: true,
                autoheight: true,
                theme: 'classic',
                columns: [
                  {
                      text: 'Location', 
                      datafield: 'admitLocDescription', 
                      width: 200 //, renderer: columnrenderer, cellsrenderer: cellsrenderer
                  },
                  {
                      text: 'Type',
                      datafield: 'encounterTypeDescription',
                      width: 100,
                      renderer: columnrenderer,
                      cellsrenderer: cellsrenderer
                  },
                  {
                      text: 'Plan Description',
                      datafield: 'plandesc',
                      width: 120,
                      renderer: columnrenderer,
                      cellsrenderer: cellsrenderer
                  },
                  {
                      text: 'Start',
                      cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                          var formattedDate = formatDateString(value_);
                          return "<div class=\"dateColumn\">" + formattedDate + "</div>";
                      },
                      datafield: 'startDate',
                      width: 100,
                      renderer: columnrenderer
                      /*cellsrenderer: cellsrenderer*/
                  },
                  {
                      text: 'End',
                      cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                          var formattedDate = formatDateString(value_);
                          return "<div class=\"dateColumn\">" + formattedDate + "</div>";
                      },
                      datafield: 'endDate',
                      width: 100,
                      renderer: columnrenderer
                      /*cellsrenderer: cellsrenderer*/
                  },
                   {
                      text: 'FIN', datafield:  'financialInfoNum', 
                      width: 110,
                      renderer: columnrenderer,
                      cellsrenderer: cellsrenderer
                  },
                  {
                      text: 'Reason For Visit', datafield:'reasonForVisit' , width: 250,
                      renderer: columnrenderer,
                      cellsrenderer: cellsrenderer
                  },
                  {
                      text: 'Defined Attributes',
                      cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {

                          var name = "";
                          if (value_ && value_.length > 0) {

                              for (var i = 0; i < value_.length; i++) {
                                  if (i == 0) {
                                      name += value_[i].cvterm_name;
                                  } else {
                                      name += ", " + value_[i].cvterm_name;
                                  }
                              }
                          }
                          return "<div >" + name + "</div>";
                      },
                      datafield: 'registry_encounter_attributes', //width: 250
                  },
                  { text: 'Encounter Key', datafield: 'encounterKey', editable: false, hidden: true }
                ]
            });
            $_(self.selectors.grid).jqxGrid({ height: 150 });
            $_(self.selectors.grid).on('rowclick', function (event) {

                var rowIndex = event.args.rowindex;
                var datarow = $_(self.selectors.grid).jqxGrid('getrowdata', rowIndex);
                if (!datarow) { return self.displayError("Invalid encounter row for index " + rowIndex); }

                var encounterKey = datarow["encounterKey"]; 
                
                console.log("Checking Financial Number on September 14th");
                var finNumKey = datarow["financialInfoNum"];
                console.log(finNumKey);

                app_.setData("selected_encounter_key", encounterKey);
                app_.setData("selected_financialNumber_key",finNumKey)

                // Display the encounter attributes dialog.
                app_.displayComponent("encounter_attributes_dialog", "application_dialog_container");
            });

        };
        
    };

})(jQuery, jQuery.phedrs.app));