
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "workflow status history" dialog builder
    return new function () {

        var self = this;

        this.dialogSelector = "#workflow_status_history_dialog";

        // This object's key in the Application.components collection.
        this.key = "workflow_status_history_dialog";

        this.patient = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service key.
        this.urlKey = "getworkflowstatushistory";


        // Initialize the page
        this.initialize = function () {

            // Get the selected patient from application data.
            self.patient = app_.getPatient();
            if (!self.patient || !self.patient.mrn) { return app_.displayError("Invalid selected patient", "initialize"); }
        	
            $_("#workflow_status_person").html(self.patient.name);
            $_("#workflow_status_mrn").html("MRN: " + self.patient.mrn);

            // Display this dialog.
            $_(self.dialogSelector).modal();

        	var data = {
        	    mrn: self.patient.mrn,
        	    registry_id: app_.registry.id
        	};

        	app_.getNoPreValidate(data, self.processHistoryDetails, app_.processError, self.urlKey);

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };



        this.processHistoryDetails = function (data_, textStatus_, jqXHR_) {

            // Make sure data is a JSON object.
            data_ = app_.processResponse(data_, jqXHR_);

            console.log(data_);

            var history = data_.statuses;
            if (!history) { alert("TODO: no history available for this patient"); return false; }

            var source = {
                localdata: history,
                datatype: "array"
            };
            var dataAdapter = new $_.jqx.dataAdapter(source, {
                loadComplete: function (data) { console.log("load is complete"); },
                loadError: function (xhr, status, error) { }
            });

            $_("#historyDetailsPanel").jqxGrid({
                source: dataAdapter,
                width: 750,
                height: 150,

                columns: [
                    {
                        text: 'Date',
                        /*cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                            return formatDateString(value_);
                        },*/
                        cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {

                        return formatDateString(value_);
                        },
                        datafield: 'changeDate',
                        width: 100
                    },
                    {
                        text: 'Workflow status',
                        datafield: 'statusText',
                        width: 100
                    }, 
                    {
                        text: 'Comment',
                        datafield: 'annotatorComment',
                        width: 350
                    },
                    {
                        text: 'Changed By',
                        datafield: 'annotatorName',
                        width: 200
                    } 

                ]
            });

        };
    };

})(jQuery, jQuery.phedrs.app));