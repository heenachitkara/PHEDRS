
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "encounter attributes" dialog builder
    return new function () {

        var self = this;

        this.dialogSelector = "#encounter_attributes_dialog";

        this.encounterKey = null;

        this.finNumKey = null;

        // This object's key in the Application.components collection.
        this.key = "encounter_attributes_dialog";

        this.patient = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service key.
        this.urlKeys = {
            add_comment: "add_encounter_attribute_comment",
            create_attribute: "create_encounter_attribute",
            delete_attribute: "delete_encounter_attribute",
            get_attributes: "getencounterattributes",
            get_insurance:"get_encounters_insurance"
        };


        // The user has decided not to add a new attribute.
        this.cancelAddAttribute = function () {
            $_(".attribute-edit-panel").hide();
            //console.log("After clicking Cancel Button!");
             //window.location.reload();

        };


        this.cancelExternalDialog = function() {
              window.location.reload();

        }

        
        // Cancel an attempt to add a new comment.
        this.cancelComment = function (eventCvtermID_) {

            // Clear the text area.
            $_(".comment-ctrl[data-attribute-id='" + eventCvtermID_ + "']").val("");

            // Hide the controls.
            $_(".new-comment-panel[data-attribute-id='" + eventCvtermID_ + "']").hide();
        };

        //dDelete confirm dialog box
        this.confirmDialog = function (message, onConfirm) {
            var fClose = function () {
                modal.modal("hide");
            };
            var modal = $("#confirmModal");
            modal.modal("show");
            $("#confirmMessage").empty().append(message);
            $("#confirmOk").one('click', onConfirm);
            $("#confirmOk").one('click', fClose);
            $("#confirmCancel").one("click", fClose);
        }

        // TODO: this needs a confirm / "are you sure?" before the delete occurs!
        this.deleteAttribute = function (eventCvtermID_) {

            if (!eventCvtermID_) { return app_.displayError("Invalid eventCvtermID", "deleteAttribute"); }

            var data = {
                cache: false,
                event_cvterm_id: eventCvtermID_,
                registry_id: app_.registry.id,
                user_id: app_.user.id
            };

            app_.get(data, self.processDeletedAttribute, app_.processError, self.urlKeys.delete_attribute);
        };


        this.displayAddAttributeControls = function () {
            
            // Clear the controls and display the panel.
            $_(".available-attributes option:selected").prop("selected", false);
            $_(".attribute-edit-panel .attribute-comment").val("");
            $_(".attribute-edit-panel").show();
        };


        this.displayAddCommentControls = function (eventCvtermID_) {

            if (!eventCvtermID_) { return app_.displayError("Invalid event cvterm ID", "displayAddCommentControls"); }

            var panelSelector = ".new-comment-panel[data-attribute-id='" + eventCvtermID_ + "']";
            $_(panelSelector).show();
        };

        // Get the encounter and any associated attributes.
        this.getEncounterAttributes = function () {

            var data = {
                cache: false,
                encounter_key: self.encounterKey,
                mrn: self.patient.mrn,
                registry_id: app_.registry.id
            };

            app_.get(data, self.processEncounterAttributes, app_.processError, self.urlKeys.get_attributes);
        };


        // Get the encounter isurance related information.
        this.getEncounterInsuranceInfo = function () {

            var data = {
                cache: false,
                hqFinNum: self.finNumKey
            };

            app_.get(data, self.processEncounterInsuranceInfo, app_.processError, self.urlKeys. get_insurance);
        };


        // Initialize the page
        this.initialize = function () {

        	// Get the selected encounter from application data.
            self.encounterKey = app_.getData("selected_encounter_key");
            if (!self.encounterKey) { return app_.displayError("Invalid selected encounter key", "initialize"); }

            // Get the selected FIN Number from application data.
            self.finNumKey = app_.getData("selected_financialNumber_key");
            if (!self.finNumKey) { return app_.displayError("Invalid selected financial number key", "initialize"); }
            console.log("Inside Initialize for Fin Num check");
            console.log(self.finNumKey);
        	
            // Get the selected patient from application data.
            self.patient = app_.getPatient();
            if (!self.patient || !self.patient.mrn) { return app_.displayError("Invalid selected patient", "initialize"); }

            // Display this dialog.
            $_(self.dialogSelector).modal();

            $_(self.dialogSelector).on("hide.bs.modal", function (event_, ui_) {

                // console.log("NOTE: this is the bootstrap 'hide modal' event");

                // TODO: this is where you could call a function to determine whether or not the user has unsaved changes. 
                // Note that it would need to return false here to keep the dialog from closing.
            });


            // Populate the "available attributes" list.
            // app_.populateTermList("UAB COPD Registry Encounter Attribute CV", ".attribute-edit-panel .available-attributes", false);
            var data1 = {
                registry_id: app_.registry.id
            };
            app_.getNoPreValidate(data1, self.addEncounterAttributesDropDowndata, app_.processError, "getRegistryEncounterAttributes");
            // Add event handlers
            $_(".attribute-edit-panel .save-attribute-ctrl").click(function () {
                self.saveAttribute();
            });
            $_(".attribute-edit-panel .cancel-attribute-ctrl").click(function () {
                self.cancelAddAttribute();

            });

            $_(".add-attribute-ctrl").click(function () {
                self.displayAddAttributeControls();
            });

             $_(" .external-cancel-button").click(function () {
                self.cancelExternalDialog();

            });

            /*
            $_("#encounter_attributes_dialog .close-icon").click(function () {
        	    // TODO: this is where you could call a function to determine whether or not the user has unsaved changes. 
                // Note that it would need to return false here to keep the dialog from closing.
        	});
        	$_("#encounter_attributes_dialog .cancel-button").click(function () {
        	    // TODO: this is where you could call a function to determine whether or not the user has unsaved changes. 
                // Note that it would need to return false here to keep the dialog from closing.
        	});
        	*/

            // Get the encounter and any associated attributes.
            self.getEncounterAttributes();

            //Get the encounter insurance related information

            self.getEncounterInsuranceInfo();

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };

        this.addEncounterAttributesDropDowndata = function (data_, textStatus_, jqXHR_) {


            data_ = app_.processResponse(data_, jqXHR_);


            var attributeCount = 0;
            var collection = data_._reg_config_encounter_attributes;
            if (collection) { attributeCount = collection.length; }


            var source = {
                localdata: collection,
                datatype: "array",
                datafields:
                [
                    { name: 'encounterCvTermName', type: 'string' },
                    { name: 'encounterCvTermDefinition', type: 'string' },
                    { name: 'cvID', type: 'string' },
                    { name: 'cvTermID', type: 'string' }
                ],
                deleterow: function (rowid, commit) {

                    commit(true);
                },

                updaterow: function (rowid, rowdata, commit) {

                    commit(true);
                }
            };
            var html = "";
            $_.each(collection, function (index_, term_) {
                //alert("term encounterCvTermName" + term_.encounterCvTermName)
                //if (!term_ || !term_.cvTermID || !term_.patienrCvTermName) { return self.displayError("Invalid term at index " + index_, "populateTermList"); }

                html += "<option value='" + term_.cvTermID + "'>" + term_.encounterCvTermName + "</option>";
            });

            $_(".attribute-edit-panel .available-attributes").html(html);
        };

        this.processDeletedAttribute = function (data_, textStatus_, jqXHR_) {

            // Refresh the list of encounter attributes.
            self.getEncounterAttributes();
        };
        
        this.processEncounterInsuranceInfo = function(data_,textStatus_,jqXHR_){
           
            if (!data_ || !data_._encounters_insurance_list ) { return app_.displayError("Invalid encounter insurance data", "processEncounterInsuranceInfo"); }

            var collection = data_._encounters_insurance_list;

            var hqNum = data_._encounters_insurance_list[0].healthQuestFIN;

            $_("#hqNum").html(hqNum);

             
            // Populate the count.
            $_("#encounter_insurance_panel_count").html("(" + collection.length + ")");
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
             
            $_("#encounter_insurance_panel_grid").jqxGrid(
            {
                source: dataAdapter,
                columnsResize: true,
                width: '600',
                pageable: true,
                sortable: true,
                autoheight: true,
                theme: 'classic',
                selectionmode: 'singlecell',
                columns: [
                 
                  {
                      text: 'Insurance Plan',
                      datafield: 'insurancePlanInfo',
                      width: 200,
                      renderer: columnrenderer,
                      cellsrenderer: cellsrenderer
                  },
                  {
                      text: 'Date of Service',
                      cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                        return '<div style="text-align: center; margin-top: 5px;">' + moment(value_, ['MMDDYYYY','MM/DD/YYYY']).format('MM/DD/YYYY'); + '</div>';
                      },
                      datafield: 'dateOfService',
                      width: 200,
                      renderer: columnrenderer
                      
                  },
                  {
                      text: 'Location Description',
                      datafield:  'locationDescription', 
                      width: 200,
                      renderer: columnrenderer,
                      cellsrenderer: cellsrenderer
                  }
                  
                ]
            });

         }

        this.processEncounterAttributes = function (data_, textStatus_, jqXHR_) {

            console.log(data_);

            if (!data_ || !data_.encounter) { return app_.displayError("Invalid encounter data", "processEncounterAttributes"); }

            //-----------------------------------------------------------------------------------------------
            // Populate the read-only encounter info.
            //-----------------------------------------------------------------------------------------------
            var dateRange = "";
            var startDate = data_.encounter.startDate;
            if (!startDate) {
                startDate = "(unknown)";
            } else {
                startDate = formatDateString(startDate);
            }

            dateRange = startDate + " - ";

            var endDate = data_.encounter.endDate;
            if (endDate) { dateRange += formatDateString(endDate); }
            
            var location = data_.encounter.admitLocDescription;
            if (!location) { location = "(Unspecified)"; }

            var encounterType = data_.encounter.encounterTypeDescription;
            if (!encounterType) { encounterType = "(Unspecified)"; }

            $_("#encounter_location").html(location);
            $_("#encounter_type").html(encounterType);
            $_("#encounter_dates").html(dateRange);
            $_("#encounter_reason").html(data_.encounter.reasonForVisit);

            
            // The HTML that will display the currently-assigned attributes.
            var ctrlRowsHTML = "<table class='attributes-table'>" +
                "<tr class='attribute-header-row'>" +
                    "<th>Attribute</th>" +
                    "<th>Last assigned</th>" +
                    "<th>Code</th>" +
                    "<th></th>" +
                "</tr>";

            // Iterate thru all encounter attributes returned from the web service.
            $_.each(data_.encounter.registry_encounter_attributes, function (index_, attribute_) {

                var eventCvtermID = attribute_.event_cvterm_id;
                if (!eventCvtermID) { return app_.displayError("Invalid event cvterm ID", "displaying encounter attributes"); }

                // Add the assigner and the assignment date.
                var assigned = "";
                var assigner = attribute_.actor_name;
                var assignment_date = formatDateString(attribute_.assignment_date);
                if (assigner) {
                    assigned += assigner;
                    if (assignment_date) { assigned += " (" + assignment_date + ")"; }
                }

                var comment = attribute_.registrar_comment;
                if (!comment || comment == "null") { comment = ""; }

                var drg_code = "";
                if (attribute_.dbxref_accession) { drg_code += "DRG: " + attribute_.dbxref_accession; }
                if (attribute_.dbxref_description) { drg_code += "<br/>(" + attribute_.dbxref_description + ")"; }

                var ctrlRow = "<tr class='assigned-attribute-control-row'>" +
                    "<td class='assigned-attribute-label'>" + attribute_.cvterm_name + "</td>" +
                    "<td class='assigned-column'>" +
                        "<div class='last-assigned-info'>" + assigned + "</div>" +
                        "<div class='assigned-comment'>" + comment + "</div>" +
                        "<div class='new-comment-panel' style='display: none;' data-attribute-id='" + eventCvtermID + "'>" +
                        "<textarea class='comment-ctrl' rows='5' data-attribute-id='" + eventCvtermID + "' placeholder='Enter new comment'></textarea>" +
                        "<button class='btn btn-xs btn-success save-comment-ctrl' data-attribute-id='" + eventCvtermID + "'>SAVE</button> " +
                        "<button class='btn btn-xs btn-warning cancel-comment-ctrl' data-attribute-id='" + eventCvtermID + "'>CANCEL</button>" +
                        "</div>" +
                    "</td>" +
                    "<td class='drg-column'>" + drg_code + "</td>" +
                    "<td class='attribute-ctrls-column'>" +
                    "<button class='btn btn-xs btn-primary add-comment-ctrl' data-attribute-id='" + eventCvtermID + "'>ADD COMMENT</button> " +
                   "<button class='btn btn-xs btn-danger delete-attribute-ctrl' data-attribute-name='" + attribute_.cvterm_name + "' data-attribute-id='" + eventCvtermID + "'>DELETE</button>" +
                    "</td>" +
                    "<tr>";

                ctrlRowsHTML += ctrlRow;
            });

            ctrlRowsHTML += "</table>";

            $_("#encounter_attributes_panel").html(ctrlRowsHTML);

            // Add click event handler for delete buttons.
            $_("#encounter_attributes_panel .delete-attribute-ctrl").each(function () {
                $_(this).click(function () {
                    var attributeID = $_(this).attr("data-attribute-id");
                    var attributeName = $_(this).attr("data-attribute-name");
                    if (!attributeID) { return app_.displayError("Invalid event_cvterm_id", "delete attribute button click"); }
                    self.confirmDialog("Are you sure you want to delete " + attributeName + "?", function () {
                        self.deleteAttribute(attributeID);
                    });
                });
            });

            // Add click event handler for "add comment" buttons.
            $_("#encounter_attributes_panel .add-comment-ctrl").each(function () {
                $_(this).click(function () {
                    var attributeID = $_(this).attr("data-attribute-id");
                    if (!attributeID) { return app_.displayError("Invalid event_cvterm_id", "add comment button click"); }

                    self.displayAddCommentControls(attributeID);
                });
            });

            // Add click event handler for "save comment" buttons.
            $_("#encounter_attributes_panel .save-comment-ctrl").each(function () {
                $_(this).click(function () {
                    var attributeID = $_(this).attr("data-attribute-id");
                    if (!attributeID) { return app_.displayError("Invalid event_cvterm_id", "save comment button click"); }

                    self.saveComment(attributeID);
                });
            });

            // Add click event handler for "cancel comment" buttons.
            $_("#encounter_attributes_panel .cancel-comment-ctrl").each(function () {
                $_(this).click(function () {
                    var attributeID = $_(this).attr("data-attribute-id");
                    if (!attributeID) { return app_.displayError("Invalid event_cvterm_id", "cancel comment button click"); }

                    self.cancelComment(attributeID);
                });
            });
        };

        this.processSavedAttribute = function (data_, textStatus_, jqXHR_) {

            $_(".attribute-edit-panel").hide();

            // Refresh the list of encounter attributes.
            self.getEncounterAttributes();

        
        };

        this.processSavedComment = function () {

            // Refresh the list of encounter attributes.
            self.getEncounterAttributes();
        };

        // Save a new attribute along with an optional comment.
        this.saveAttribute = function () {

            var selectedOption = $_(".attribute-edit-panel .available-attributes option:selected");
            if (!selectedOption) { return app_.displayError("Please select an attribute to add", null); }
            var attributeCvTermID = $_(selectedOption).attr("value");

            var comment = $_(".attribute-edit-panel .attribute-comment").val();

            var data = {
                cache: false,
                comment: comment,
                cvterm_id: attributeCvTermID,
                encounter_key: self.encounterKey,
                mrn: self.patient.mrn,
                registry_id: app_.registry.id,
                user_id: app_.user.id
            };

            console.log(data);

            app_.get(data, self.processSavedAttribute, app_.processError, self.urlKeys.create_attribute);
        };

        // Save a new comment that was just added to an attribute.
        this.saveComment = function (eventCvtermID_) {

            if (!eventCvtermID_) { return app_.displayError("Invalid event cvterm ID", "save comment"); }

            var comment = $_(".comment-ctrl[data-attribute-id='" + eventCvtermID_ + "']").val();
            if (!comment) { alert("Please enter a comment or click 'cancel' to exit"); return false; }

            // Clear the comment and hide the controls.
            self.cancelComment(eventCvtermID_);

            var data = {
                cache: false,
                comment: comment,
                event_cvterm_id: eventCvtermID_,
                registry_id: app_.registry.id,
                user_id: app_.user.id
            };

            app_.get(data, self.processSavedComment, app_.processError, self.urlKeys.add_comment);
        };
    };

})(jQuery, jQuery.phedrs.app));