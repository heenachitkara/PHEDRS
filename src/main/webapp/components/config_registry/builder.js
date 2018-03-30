
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The "user list" page builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "config_registry";
        this.registryIdValue = 0;
        // Updated when a user clicks on a row's edit button.
        this.selectedRowIndex = -1;

        // Selectors
        this.selectors = {
            add_dialog: ".add-patient-attribute-dialog:visible",
            add_encounter_dialog: ".add-encounter-attribute-dialog:visible",
            add_detection_dialog: ".add-detection-attribute-dialog:visible",
            add_button: "#add_configRegistry_patient_attribute_button",
            add_encounter_button: "#add_configRegistry_encounters_button",
            add_detection_button: "#add_configPatient_detection_button",
            edit_dialog: "#configPatient_attributes_edit_dialog",
            edit_dialog_encounter: "#configEncounter_attributes_edit_dialog",
            edit_dialog_detection: "#configPatient_detection_edit_dialog",
            patient_grid: "#configRegistry_patient_attributes_grid",
            encounter_grid: "#configRegistry_encounters_grid",
            detection_grid: "#configPatient_detection_grid"
        };

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

        // The web service URL keys.
        this.urlKeys = {
            get_patient_attributes: "getRegistryPatientAttributes",
            get_encounter_attributes: "getRegistryEncounterAttributes",
            get_Registry_Attributes: "getRegistryAttributes",
            add_PatientAttribute_CVTerm: "addPatientAttributeCVTerm",
            add_EncounterAttribute_CVTerm: "addEncounterAttributeCVTerm",
            add_Attribute_Configuration:"addAttributeConfigurationRecord",
            delete_Registry_Patient_Attribute: "delete_config_patient_record",
            delete_attribute_config_record : "delete_attribute_config_record",
            update_patient_encounter_Attrib: "updatecvterm",
            update_attribute_config_record: "update_attribute_config_record",
            update_registry_config_record: "update_config_name",
            update_registry_config_definition: "update_registry_config_definition",
            get_AttributeConfig_TypeID_2ndDrop: "get_registry_config_add_attributes"
        };


        // Initialize the page
        this.initialize = function () {
            this.registryIdValue = app_.getData("new_registry_id");
           
            if (!this.registryIdValue) {                
                this.registryIdValue = app_.registry.id;
            }
            
            $_(self.selectors.edit_dialog).hide();
            $_(self.selectors.edit_dialog_encounter).hide();
            $_(self.selectors.edit_dialog_detection).hide();
            // Handle the "add patient attribute" button click.
            $_(self.selectors.add_button).click(function () {                
                //self.addAttribute("patient");
                app_.displayComponent("patientAttribute_config_dialog", "application_dialog_container");
            });
            // Handle the "add encounter attribute" button click.
            $_(self.selectors.add_encounter_button).click(function () {
                //self.addAttribute("encounter");
                app_.displayComponent("encounterAttribute_config_dialog", "application_dialog_container");
            });
            // Handle the "add patient detection config" button click.
            $_(self.selectors.add_detection_button).click(function () {
                //self.addAttribute("detection");
                app_.displayComponent("attribute_config_dialog", "application_dialog_container");
            });

            // Handle the click event of the patient attribute edit dialog's save button.
            $_(self.selectors.edit_dialog + " .save-button").click(function () {
                self.savePatientAttribute("patient");
            });

            // Handle the click event of the patient attribute edit dialog's cancel button.
            $_(self.selectors.edit_dialog + " .cancel-button").click(function() {
                $_(self.selectors.edit_dialog).hide();
                });


            // Handle the click event of the patient attribute edit dialog's remove button.
            $_(self.selectors.edit_dialog + " .remove-button").click(function () {
                if (self.selectedRowIndex < 0) { return app_.displayError("Invalid selected row index", "removePatientAttribute"); }
                var cvTermId = $_(self.selectors.edit_dialog + " .configPatient_attributes_edit_cvTermId").val();
                $_(self.selectors.edit_dialog).jqxWindow('close');
                self.removePatientAttribute("patient", cvTermId);
            });

            // Handle the click event of the encounter attribute edit dialog's save button.
            $_(self.selectors.edit_dialog_encounter + " .save-button").click(function () {
                self.saveEncounterAttribute("encounter");
            });

                // Handle the click event of the encounter attribute edit dialog's cancel button.
           $_(self.selectors.edit_dialog_encounter + " .cancel-button").click(function() {
                $_(self.selectors.edit_dialog_encounter).hide();
           });


            // Handle the click event of the encounter attribute edit dialog's remove button.
            $_(self.selectors.edit_dialog_encounter + " .remove-button").click(function () {
                if (self.selectedRowIndex < 0) { return app_.displayError("Invalid selected row index", "removeEncounterAttribute"); }
                var cvTermId = $_(self.selectors.edit_dialog_encounter + " .configEncounter_attributes_edit_cvTermId").val();
                $_(self.selectors.edit_dialog_encounter).jqxWindow('close');
                
                self.removePatientAttribute("encounter", cvTermId);
            });

            // Handle the click event of the patient detection config edit dialog's save button.
            $_(self.selectors.edit_dialog_detection + " .save-button").click(function () {
                self.saveAttributeConfig("detection");
            });

                    // Handle the click event of the patient detection config  edit dialog's cancel button.
            $_(self.selectors.edit_dialog_detection + " .cancel-button").click(function() {
                $_(self.selectors.edit_dialog_detection).hide();
            });
            // Handle the click event of the patient detection config edit dialog's remove button.
            $_(self.selectors.edit_dialog_detection + " .remove-button").click(function () {
                if (self.selectedRowIndex < 0) { return app_.displayError("Invalid selected row index", "removePatientDetectionAttribute"); }
                var cvTermId = $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_cvtermPropID").val();
                $_(self.selectors.edit_dialog_detection).jqxWindow('close');
                
                self.removeAttributeConfig("detection", cvTermId);
            });
            $_(".config_regsitName_update_button").click(function () {
                self.updateName();
            });

            $_(".config_regsitDef_update_button").click(function () {
                self.updateDefinition();
            });
            // Get the selected patient from application data.
            /*self.patient = app_.getPatient();
            if (!self.patient || !self.patient.mrn) { return app_.displayError("Invalid selected patient", "patient attributes initialize"); }
           */

            var data = {
                registry_id: this.registryIdValue
            };
          
            // Request patient attributes from the web service.
            app_.getNoPreValidateConfigReg(data, self.processPatientAttributes, app_.processError, self.urlKeys.get_patient_attributes);

            // Request encounter attributes from the web service.
            app_.getNoPreValidateConfigReg(data, self.processEncounterAttributes, app_.processError, self.urlKeys.get_encounter_attributes);

            // Request detection attributes from the web service.
            app_.getNoPreValidateConfigReg(data, self.processDetectionAttributes, app_.processError, self.urlKeys.get_Registry_Attributes);
            data = {
                cvterm_id: this.registryIdValue
            };
            app_.getNoPreValidateConfigReg(data, self.getConfigNameDef, app_.processError, "get_cvterm_name");


            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };

        this.getConfigNameDef = function (data_, textStatus_, jqXHR_) {

            data_ = app_.processResponse(data_, jqXHR_);
            var attributeCount = 0;
            var collection = data_.cvtermNameList;
            if (collection) {
                attributeCount = collection.length;
            }
            var source = {
                localdata: collection,
                datatype: "array",
                datafields:
                [{
                    name: 'cvtermName', type: 'string',
                    name: 'cvtermDefinition', type: 'string'
                }
                ],
                deleterow: function (rowid, commit) {

                    commit(true);
                },

                updaterow: function (rowid, rowdata, commit) {

                    commit(true);
                }
            };
            var cvtermname = "";
            $_.each(collection, function (index_, term_) {

                cvtermname = term_.cvtermName;
                console.log("CVTerm Name check in Config Registry");
                console.log(cvtermname);
                cvtermDefinition = term_.cvtermDefinition;
            });

            document.getElementById("registry_name").value = cvtermname;
            document.getElementById("registry_def").value = cvtermDefinition;
        };


        this.processPatientAttributes = function (data_, textStatus_, jqXHR_) {
            
            
            // dmd 02/22/17: convert data to JSON, if necessary.
            data_ = app_.processResponse(data_, jqXHR_);


            var attributeCount = 0;
            var collection = data_._reg_config_patient_attributes;
            if (collection) { attributeCount = collection.length; }


            var source = {
                localdata: collection,
                datatype: "array",
                datafields:
                [
                    { name: 'patienrCvTermName', type: 'string' },
                    { name: 'patientCvTermDefinition', type: 'string' },
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

            var dataAdapter = new $_.jqx.dataAdapter(source, {
                loadComplete: function (data) { },
                loadError: function (xhr, status, error) { }
            });

            var editrow = -1;


            $_(self.selectors.patient_grid).jqxGrid({
                source: dataAdapter,
                width: '450',
                height: '400',
                pageable: true,
                sortable: true,
                autoheight: true,
                columnsResize: true,
                theme: 'classic',
                columns: [
                   {
                       text: 'Name', datafield: 'patienrCvTermName', width: 200
                   },
                   {
                       text: 'Definition', datafield: 'patientCvTermDefinition', width: 200
                   },

                    {
                        text: 'Edit', datafield: 'Edit', width: 50, columntype: 'button', cellsrenderer: function () {
                            return "Edit";
                        },
                        buttonclick: function (row_) {

                            console.log(row_);

                            // Maintain the index of the selected row.
                            self.selectedRowIndex = row_;

                            // Open the "edit dialog" when the user clicks the edit button.

                            var offset = $_(self.selectors.patient_grid).offset();
                            $_(self.selectors.edit_dialog).jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 }, width: 350 });

                            // get the clicked row's data and initialize the input fields.
                            var dataRecord = $_(self.selectors.patient_grid).jqxGrid('getrowdata', row_);

                            $_(self.selectors.edit_dialog + " .configPatient_attributes_edit_registry_name").val(dataRecord.patienrCvTermName);

                            $_(self.selectors.edit_dialog + " .configPatient_attributes_edit_registry_def").val(dataRecord.patientCvTermDefinition);
                            $_(self.selectors.edit_dialog + " .configPatient_attributes_edit_cvId").val(dataRecord.cvID);
                            $_(self.selectors.edit_dialog + " .configPatient_attributes_edit_cvTermId").val(dataRecord.cvTermID);
                            

                            // show the popup window.
                            $_(self.selectors.edit_dialog).jqxWindow('open');
                        }
                    }
                ]
            });  // END of  $(self.selectors.grid).jqxGrid initialization

            var iconrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var icon = '';
                if (array[row].dates.length > 1) {
                    icon = '<img src="/images/delete.png" style="position: absolute; right: 5px;" />';
                }

                return '<span style="position: relative; width: 100%; margin: 4px; float: ' + icon + '</span>';
            }

        };


        this.processEncounterAttributes = function (data_, textStatus_, jqXHR_) {

            // dmd 02/22/17: convert data to JSON, if necessary.
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

            var dataAdapter = new $_.jqx.dataAdapter(source, {
                loadComplete: function (data) { },
                loadError: function (xhr, status, error) { }
            });

            var editrow = -1;


            $_(self.selectors.encounter_grid).jqxGrid({
                source: dataAdapter,
                width: '450',
                height: '400',
                pageable: true,
                sortable: true,
                autoheight: true,
                columnsResize: true,
                theme: 'classic',
                columns: [
                   {
                       text: 'Name', datafield: 'encounterCvTermName', width: 200
                   },
                   {
                       text: 'Definition', datafield: 'encounterCvTermDefinition', width: 200
                   },

                    {
                        text: 'Edit', datafield: 'Edit', width: 50, columntype: 'button', cellsrenderer: function () {
                            return "Edit";
                        },
                        buttonclick: function (row_) {

                            console.log(row_);

                            // Maintain the index of the selected row.
                            self.selectedRowIndex = row_;

                            // Open the "edit dialog" when the user clicks the edit button.

                            var offset = $_(self.selectors.encounter_grid).offset();
                            $_(self.selectors.edit_dialog_encounter).jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 }, width: 350 });

                            // get the clicked row's data and initialize the input fields.
                            var dataRecord = $_(self.selectors.encounter_grid).jqxGrid('getrowdata', row_);
                            
                            $_(self.selectors.edit_dialog_encounter + " .configEncounter_attributes_edit_registry_name").val(dataRecord.encounterCvTermName);
                            $_(self.selectors.edit_dialog_encounter + " .configEncounter_attributes_edit_registry_def").val(dataRecord.encounterCvTermDefinition);
                            $_(self.selectors.edit_dialog_encounter + " .configEncounter_attributes_edit_cvId").val(dataRecord.cvID);
                            $_(self.selectors.edit_dialog_encounter + " .configEncounter_attributes_edit_cvTermId").val(dataRecord.cvTermID);


                            // show the popup window.
                            $_(self.selectors.edit_dialog_encounter).jqxWindow('open');
                        }
                    }
                ]
            });  // END of  $(self.selectors.grid).jqxGrid initialization
            var iconrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var icon = '';
                if (array[row].dates.length > 1) {
                    icon = '<img src="/images/delete.png" style="position: absolute; right: 5px;" />';
                }

                return '<span style="position: relative; width: 100%; margin: 4px; float: ' + icon + '</span>';
            }
        };

        this.processDetectionAttributes = function (data_, textStatus_, jqXHR_) {

            // dmd 02/22/17: convert data to JSON, if necessary.
            data_ = app_.processResponse(data_, jqXHR_);


            var attributeCount = 0;
            var collection = data_.registryAttributesList;
            if (collection) { attributeCount = collection.length; }


            var source = {
                localdata: collection,
                datatype: "array",
                datafields:
                [
                    { name: 'attributeCvName', type: 'string' },
                    { name: 'cvtermName', type: 'string' },
                    { name: 'cvtermPropValue', type: 'string' },
                    { name: 'cvtermPropID', type: 'string' },
                    { name: 'attributeConfigCvID', type: 'string' },
                    { name: 'typeID', type: 'string' }
                ],
                deleterow: function (rowid, commit) {

                    commit(true);
                },

                updaterow: function (rowid, rowdata, commit) {

                    commit(true);
                }
            };

            var dataAdapter = new $_.jqx.dataAdapter(source, {
                loadComplete: function (data) { },
                loadError: function (xhr, status, error) { }
            });

            var editrow = -1;


            $_(self.selectors.detection_grid).jqxGrid({
                source: dataAdapter,
                width: '780',
                height: '400',
                pageable: true,
                sortable: true,
                autoheight: true,
                columnsResize: true,
                theme: 'classic',
                columns: [
                   {
                       text: 'CV Name', datafield: 'attributeCvName', width: 180
                   },
                   {
                       text: 'Name', datafield: 'cvtermName', width: 350
                   },
                   {
                       text: 'Value', datafield: 'cvtermPropValue', width: 200
                   },

                    {
                        text: 'Edit', datafield: 'Edit', width: 50, columntype: 'button', cellsrenderer: function () {
                            return "Edit";
                        },
                        buttonclick: function (row_) {

                            console.log(row_);

                            // Maintain the index of the selected row.
                            self.selectedRowIndex = row_;

                            // Open the "edit dialog" when the user clicks the edit button.

                            var offset = $_(self.selectors.detection_grid).offset();
                            $_(self.selectors.edit_dialog_detection).jqxWindow({ position: { x: parseInt(offset.left) + 160, y: parseInt(offset.top) + 60 }, width: 600 });

                            // get the clicked row's data and initialize the input fields.
                            var dataRecord = $_(self.selectors.detection_grid).jqxGrid('getrowdata', row_);

                            $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_registry_name").val(dataRecord.cvtermName);                           
                            
                            $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_registry_def").val(dataRecord.cvtermPropValue);
                            $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_cvId").val(dataRecord.attributeConfigCvID);
                            
                            $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_cvtermPropID").val(dataRecord.cvtermPropID);
                            $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_typeID").val(dataRecord.typeID);

                            var data1 = {
                                cv_id: dataRecord.attributeConfigCvID
                            }
                            app_.getNoPreValidate(data1, self.addAttributeConfig2ndDropDowndata, app_.processError, self.urlKeys.get_AttributeConfig_TypeID_2ndDrop);
                            $_("#attribConfigAttributesList").val(dataRecord.typeID);

                            // show the popup window.
                            $_(self.selectors.edit_dialog_detection).jqxWindow('open');
                        }
                    }
                ]
            });  // END of  $(self.selectors.grid).jqxGrid initialization
            var iconrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var icon = '';
                if (array[row].dates.length > 1) {
                    icon = '<img src="/images/delete.png" style="position: absolute; right: 5px;" />';
                }

                return '<span style="position: relative; width: 100%; margin: 4px; float: ' + icon + '</span>';
            }
        };    

        this.savePatientAttribute = function (attribute) {
          
            if (self.selectedRowIndex < 0) { return app_.displayError("Invalid selected row index", "savePatientAttribute"); }

            // Get the values from the edit dialog.
            // TODO: shouldn't we validate these???
            var attributeName = $_(self.selectors.edit_dialog + " .configPatient_attributes_edit_registry_name").val();
            var attributeDef = $_(self.selectors.edit_dialog + " .configPatient_attributes_edit_registry_def").val();
            var cvTermID = $_(self.selectors.edit_dialog + " .configPatient_attributes_edit_cvTermId").val();
            var cvId = $_(self.selectors.edit_dialog + " .configPatient_attributes_edit_cvId").val();

            var data = {
                cvterm_id: cvTermID,
                name: attributeName,
                is_obsolete: 0,
                is_relationshiptype: 0,
                cv_id: cvId,
                definition: attributeDef
            };

            // Call the web service to update the patient attribute.
            app_.getNoPreValidate(data, self.initialize, app_.processError, self.urlKeys.update_patient_encounter_Attrib);
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
            var configName = $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_registry_name").val();
            //alert("configName" + configName);
            $_.each(collection, function (index_, term_) {
               
                if (configName == term_.configAddAttributeName) {
                    html += "<option selected value='" + term_.configAddAttributeCvTermID + "'>" + term_.configAddAttributeName + "</option>";
                } else {
                    html += "<option value='" + term_.configAddAttributeCvTermID + "'>" + term_.configAddAttributeName + "</option>";
                }

            });

            $_(".attribConfigAttributesList").html(html);
        };
        this.saveEncounterAttribute = function (attribute) {
          
            if (self.selectedRowIndex < 0) { return app_.displayError("Invalid selected row index", "saveEncounterAttribute"); }

            // Get the values from the edit dialog.
            // TODO: shouldn't we validate these???
            var attributeName = $_(self.selectors.edit_dialog_encounter + " .configEncounter_attributes_edit_registry_name").val();
            var attributeDef = $_(self.selectors.edit_dialog_encounter + " .configEncounter_attributes_edit_registry_def").val();
            var cvTermID = $_(self.selectors.edit_dialog_encounter + " .configEncounter_attributes_edit_cvTermId").val();
            var cvId = $_(self.selectors.edit_dialog_encounter + " .configEncounter_attributes_edit_cvId").val();

            var data = {
                cvterm_id: cvTermID,
                name: attributeName,
                is_obsolete: 0,
                is_relationshiptype: 0,
                cv_id: cvId,
                definition: attributeDef
            };

            // Call the web service to update the patient attribute.
            app_.getNoPreValidate(data, self.initialize, app_.processError, self.urlKeys.update_patient_encounter_Attrib);
            };

        this.saveAttributeConfig = function (attribute) {

                // Get the values from the edit dialog.
                // TODO: shouldn't we validate these???
            var attributeName = $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_registry_name").val();
            var attributeDef = $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_registry_def").val();
            var cvTermID = $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_cvtermPropID").val();
            var typeId = document.getElementById("attribConfigAttributesList").value;
            var cvId = $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_cvId").val();
            //var typeId = $_(self.selectors.edit_dialog_detection + " .configPatient_detection_edit_typeID").val();
            var data = {
                                cvtermprop_id: cvTermID,
                                type_id: typeId,                
                                value: attributeDef
                       };
          
            // Call the web service to update the patient attribute.
            app_.getNoPreValidate(data, self.initialize, app_.processError, self.urlKeys.update_attribute_config_record);
        };

        this.updateName = function () {

            var attributeName = document.getElementById("registry_name").value;
            //alert("update name " + attributeName);
            var data = {
                cvterm_id: this.registryIdValue,
                name: attributeName
            };

            // Call the web service to update the registry name.
            app_.getNoPreValidateConfigReg(data, "", app_.processError, self.urlKeys.update_registry_config_record);
            alert("Registry name updated successfully");
        };

        this.updateDefinition = function () {

            var attributeDef = document.getElementById("registry_def").value;
            //alert("update def " + attributeDef);
            var data = {
                cvterm_id: this.registryIdValue,
                definition: attributeDef
            };

            // Call the web service to update the registry definition.
            app_.getNoPreValidateConfigReg(data, "", app_.processError, self.urlKeys.update_registry_config_definition);
            alert("Registry definition updated successfully");
           
        };

        // For Removing Patient Attribute
        this.removePatientAttribute = function (attribute, cvTermId) {
            
            var data = {                
                cvterm_id: cvTermId
            };

            // Call the web service to remove the patient attribute.
            app_.getNoPreValidate(data, self.initialize, app_.processError, self.urlKeys.delete_Registry_Patient_Attribute);
        };

        // For Removing Attribute config
        this.removeAttributeConfig = function (attribute, cvTermId) {
            
            var data = {
                cvtermprop_id: cvTermId
            };

            // Call the web service to remove the patient attribute.
            app_.getNoPreValidate(data, self.initialize, app_.processError, self.urlKeys.delete_attribute_config_record);
        };



        // Not being used now
        this.addAttribute = function (attribute) {
            
            var regName, regDef, data, button, template, dialog, txtBoxValue, url;
            if (attribute == "patient") {
                button = self.selectors.add_button;
                dialog = self.selectors.add_dialog;
                template = "#add_patient_attribute_dialog_template";
            } else if (attribute == "encounter") {
                button = self.selectors.add_encounter_button;
                dialog = self.selectors.add_encounter_dialog;
                template = "#add_encounter_attribute_dialog_template";
            } else if (attribute == "detection") {
                app_.displayComponent("attribute_config_dialog", "application_dialog_container");
            }
          
            if (button != null || button!= "") {
            // Create the dialog.
            $_(button).popModal({

                // Copy the HTML from the dialog template.
                html: $_(template).html(),
                onOkBut: function () {
                   
                    if (attribute == "patient") {
                        txtBoxValue = " .configPatient";
                        // method = self.processAddedAttribute;
                        url = self.urlKeys.add_PatientAttribute_CVTerm;
                        regName = $_(dialog + txtBoxValue + "_attributes_add_registry_name").val();
                        regDef = $_(dialog + txtBoxValue + "_attributes_add_registry_def").val();
                        data = {
                            cache: false,
                            name: regName,
                            registry_id: this.registryIdValue,
                            definition: regDef
                        };
                    }
                    else if (attribute == "encounter") {
                       
                        
                        txtBoxValue = " .configEncounter";
                        // method = self.processEncounterAttributes;
                        url = self.urlKeys.add_EncounterAttribute_CVTerm;
                        regName = $_(dialog + txtBoxValue + "_attributes_add_registry_name").val();
                        regDef = $_(dialog + txtBoxValue + "_attributes_add_registry_def").val();
                        data = {
                            cache: false,
                            name: regName,
                            registry_id: this.registryIdValue,
                            definition: regDef
                        };
                    }
                    
                    // Add the patient attribute.
                    app_.getNoPreValidateConfigReg(data, self.initialize, app_.processError, url);
                },
                onCancelBut: function () { },
                onClose: function () { }
            });

        }
        };


    };

})(jQuery, jQuery.phedrs.app));