
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "encounter attributes" dialog builder
    return new function () {

        var self = this;

        this.dialogSelector = "#attribute_config_dialog";

        this.encounterKey = null;

        this.finNumKey = null;

        // This object's key in the Application.components collection.
        this.key = "attribute_config_dialog";

        this.patient = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service key.
        this.urlKeys = {
            add_attrib: "addAttributeConfigurationRecord",           
            delete_attribute_config_record: "delete_attribute_config_record",
            get_All_CVs_1stDrop: "getAllCVs",
            get_AttributeConfig_TypeID_2ndDrop: "get_registry_config_add_attributes"
        };


        this.cancelExternalDialog = function() {
              window.location.reload();

        }

        this.registryIdValue = 0;
        // Initialize the page
        this.initialize = function () {

            this.registryIdValue = app_.getData("new_registry_id");
            
            if (!this.registryIdValue) {
                this.registryIdValue = app_.registry.id;
            }
           
            
            // Display this dialog.
            $_(self.dialogSelector).modal();

            $_(self.dialogSelector).on("hide.bs.modal", function (event_, ui_) {
            });
            var data1 = {
                registry_id: this.registryIdValue
            };
            app_.getNoPreValidateConfigReg(data1, self.addAttributeConfig1stDropDowndata, app_.processError, self.urlKeys.get_All_CVs_1stDrop);
            
           /* $_("#attribConfigCVList").change(function () {
                var selectedOption = $_(".attribConfigCVList option:selected");
                var attributeCvID = $_(selectedOption).attr("value");               
               data1 = {
                   cv_id: attributeCvID
                    }
                app_.getNoPreValidate(data1, self.addAttributeConfig2ndDropDowndata, app_.processError, self.urlKeys.get_AttributeConfig_TypeID_2ndDrop);

            });*/
           
            $_(".external-save-button").click(function () {
                
                self.saveAttribute();
            });

             $_(" .external-cancel-button").click(function () {
                self.cancelExternalDialog();

             });
             $_(".external-search-button").click(function () {
                 
                 var selectedOption = $_(".attribConfigCVList option:selected");
                 var attributeCvID = $_(selectedOption).attr("value");
                
                 var attribConfigName = document.getElementById("attribConfigName").value;
                
                 data1 = {
                     cv_id: attributeCvID,
                     name : attribConfigName
                 }
                 app_.getNoPreValidate(data1, self.addAttributeConfig2ndDropDowndata, app_.processError, self.urlKeys.get_AttributeConfig_TypeID_2ndDrop);


             });

             
            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };

        this.addAttributeConfig1stDropDowndata = function (data_, textStatus_, jqXHR_) {


            data_ = app_.processResponse(data_, jqXHR_);


            var attributeCount = 0;
            var collection = data_.cvList;
            if (collection) { attributeCount = collection.length; }


            var source = {
                localdata: collection,
                datatype: "array",
                datafields:
                [
                    { name: 'cvName', type: 'string' },
                    { name: 'cvId', type: 'string' }
                ],
                deleterow: function (rowid, commit) {

                    commit(true);
                },

                updaterow: function (rowid, rowdata, commit) {

                    commit(true);
                }
            };
            var html = "<option value=''></option>";
            $_.each(collection, function (index_, term_) {
               html += "<option value='" + term_.cvId + "'>" + term_.cvName + "</option>";
            });

            $_(".attribConfigCVList").html(html);
        };

        this.addAttributeConfig2ndDropDowndata = function (data_, textStatus_, jqXHR_) {
            
            data_ = app_.processResponse(data_, jqXHR_);
            var attributeCount = 0;
            var collection = data_.configAddAttributesList;
            if (collection) { attributeCount = collection.length; }
            var source = {
                localdata: collection,
                datatype: "array",
                datafields:
                [
                    { name: 'configAddAttributeName', type: 'string' },
                    { name: 'configAddAttributeCvTermID', type: 'string' }
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
                html += "<option value='" + term_.configAddAttributeCvTermID + "'>" + term_.configAddAttributeName + "</option>";
            });
            
            $_(".attribConfigAttributesList").html(html);
        };

        // Save a new attribute along with an optional comment.
        this.saveAttribute = function () {
            var selectedOption = $_(".attribConfigAttributesList option:selected");
            if (!selectedOption) { return app_.displayError("Please select an attribute to add", null); }
            var attributeCvTermID = $_(selectedOption).attr("value");
            if (!attributeCvTermID) { return app_.displayError("Please select an attribute to add", null); }
            var attribConfigValue = document.getElementById("attribConfigValue").value;//("#attribConfigValue").value();
            if (!attribConfigValue) { return app_.displayError("Please enter valid value to add", null); }
           
            var data = {
                cache: false,                
                cvterm_id: this.registryIdValue,
                type_id: attributeCvTermID,
                value: attribConfigValue
            };
            
            console.log(data);
            app_.getNoPreValidateConfigReg(data, self.cancelExternalDialog, app_.processError, self.urlKeys.add_attrib);
            
        };
    };

})(jQuery, jQuery.phedrs.app));