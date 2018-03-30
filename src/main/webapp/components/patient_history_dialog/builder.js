
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The patient history dialog builder
    return new function () {

        var self = this;

        this.dialogSelector = "#patient_history_dialog";

        // This object's key in the Application.components collection.
        this.key = "patient_history_dialog";

        this.patient = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service key.
        this.urlKey = "getregistrypatienthistory";



        // Initialize the page
        this.initialize = function () {

        	// Get the selected patient from application data.
        	self.patient = app_.getPatient();
        	if (!self.patient || !self.patient.mrn) { return app_.displayError("Invalid selected patient", "initialize"); }
        	
            var url = app_.lookupWebServiceURL(self.urlKey);
            if (!url) { return app_.displayError("Invalid URL", "initialize"); }

            var data = {
                mrn: self.patient.mrn,
                registry_id: app_.registry.id
            };

            // Get the patient history (asynchronously).
            app_.get(data, self.processHistoryDetails, app_.processError, self.urlKey);

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };



        this.processHistoryDetails = function (data_, textStatus_, jqXHR_) {

            // Make sure data is a JSON object.
            data_ = app_.processResponse(data_, jqXHR_);

            var history = data_.registry_patient_history_list;
            if (!history) { alert("TODO: no history avaiable for this patient"); return false; }

            $_(self.dialogSelector).attr("title", "History for " + self.patient.name);

            $_(self.dialogSelector).dialog({
                position: { my: 'center', at: 'center' },
                modal: true,
                width: 830,
                height: 300,
                dialogClass: "dialogHistory",
                buttons: {
                    Close: function () {
                        $_(this).dialog("close");
                    }
                },
                close: function () {
                    // Always destroy the dialog.
                    $_(self.dialogSelector).dialog("destroy");
                    $_(app_.selectors.dialog_container).html("");
                }
            });

            var source = {
                localdata: history,
                datatype: "array"
            };
            var dataAdapter = new $_.jqx.dataAdapter(source, {
                //loadComplete: function (data) { console.log("load is complete"); },
                loadError: function (xhr, status, error) { }
            });

            $_("#historyDetailsPanel").jqxGrid({
                source: dataAdapter,
                width: 750,
                height: 150,

                columns: [
                    {
                        text: 'Date',
                        cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                            return formatDateString(value_);
                        },
                        datafield: 'change_date',
                        width: 100
                    },
                    {
                        text: 'Registry status',
                        datafield: 'current_status', 
                        width: 100
                    }, {
                        text: 'Workflow status',
                        datafield: 'curr_review_status',
                        width: 100
                    },
                    {
                        text: 'Registry comment',
                        datafield: 'reg_status_change_comment',
                        width: 150
                    }, {
                        text: 'Workflow comment',
                        datafield: 'workflow_status_change_comment',
                        width: 150
                    },
                    {
                        text: 'Changed By',
                        datafield: 'changer', // 'changed_by',
                        width: 150
                    } 

                ]
            });
        };
    };

})(jQuery, jQuery.phedrs.app));