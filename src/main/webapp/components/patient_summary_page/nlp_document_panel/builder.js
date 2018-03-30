

// follow encounters_panel
jQuery(window).trigger(jQuery.phedrs.app.events.builder_loaded, (function ($_, app_) {

    // The diagnosis panel builder
    return new function () {

        var self = this;

        // This object's key in the Application.components collection.
        this.key = "patient_summary_page.nlp_documents_panel";

        // Selectors for the jqx grid and the count element in the title.

        this.selectors = {
          count : "#nlp_document_list_count",
          grid  : "#nlp_document_list_grid"
        }


        this.countSelector = "#nlpDocumentCount";
        this.gridSelector = "#nlpDocumentPanel";

        // The selected patient from application data.
        this.patient = null;

        // The builder's current status.
        this.status = app_.builder.statuses.loaded;

       
        this.urlKeys = {
            show_nlp_document_list: "getdocuments",
            show_document_content: "showdocument"
        };

        

        // Initialize the virtual page
        this.initialize = function () {

            console.log("Loading nlpDocumentPanel panel");

            // Get the selected patient from application data.
            self.patient = app_.getPatient();
            if (!self.patient || !self.patient.mrn) { return app_.displayError("Invalid selected patient", "nlpdocument panel initialize"); }

            var data = {
                mrn: self.patient.mrn,
                registry_id: app_.registry.id
            };

            // Request nlp document list from the web service.
            app_.get(data, self.processNLPDocuments, app_.processError, self.urlKeys.show_nlp_document_list);

            // Trigger the "builder initialized" event.
            $_(window).trigger(app_.events.builder_initialized, { key: self.key });
        };

        this.processNLPDocuments = function (data_, textStatus_, jqXHR_) {
             
             // convert data to JSON, if necessary
             data_ = app_.processResponse(data_, jqXHR_);
             var attributeCount = 0;
             var collection = data_.nlp_document_list;
            if (collection) { attributeCount = collection.length; }
            // Display the count
            $_(self.selectors.count).html("(" + attributeCount + ")");

            var source = {
                localdata: collection,
                datatype: "array"
            };
             
            var dataAdapter = new $_.jqx.dataAdapter(source, {
                loadComplete: function (data) { },
                loadError: function (xhr, status, error) { }
            });
            // Remove this after testing and make it compatible with the framework function(s) 
            var cellsrenderer = function (row, column, value) {
            return '<div style="text-align: center; margin-top: 5px;">' + value + '</div>';
            }

            var columnrenderer = function (value) {
                return '<div style="text-align: center; margin-top: 5px;">' + value + '</div>';
            }

            $_(self.selectors.grid).jqxGrid({
                source: dataAdapter,
                width: '1000',
                height: 150,
                pageable: true,
                sortable: true,
                autoheight:true,
                theme: 'classic',
                columns: [{
                text: 'Type',
                datafield: 'nc_type',
                renderer: columnrenderer,
                cellsrenderer: cellsrenderer
                }, 
                {
                text: 'SubType',
                datafield: 'nc_subtype',
                renderer: columnrenderer,
                cellsrenderer: cellsrenderer
                }, 
                {
                text: 'Date',
                cellsrenderer: function (row_, columnfield_, value_, defaulthtml_, columnproperties_) {
                               return formatDateString(value_);
                },
                datafield: 'nc_dos',
                renderer: columnrenderer,
                cellsrenderer:cellsrenderer
                               
               }]

            });

                      
            $(self.selectors.grid).on('rowclick', function (event) {  
                  var args = event.args;
                  var boundIndex = args.rowindex;
                  var data = $(self.selectors.grid).jqxGrid('getrowdata', boundIndex);
                  var docID = data["document_id"];
                  var docData = {
                        
                          registry_id: app_.registry.id,
                          doc_id: docID
                  } 
                  if(docID){
                   app_.getNoPreValidate(docData, self.processShowNLPDocument, app_.processError, self.urlKeys.show_document_content);
                  }
                  else {
                    dalert.alert("Sorry, No Document Found!","Document Alert!");
                  }
            });
        
        }; // End of processNLPDocuments


          $('#concepts-lists').on('click','#list-hrec li', function(event) {

            var liID = $(this).attr('id').substring(2);
            
            $('.the-doc').find('span').removeClass('docr_word_selected');
            $('.the-doc').find('span').removeClass('docr_word_selected_isneg');
            $('#l_'+liID).parent().find('li').removeClass('hrec_li_selected');
            $('#l_'+liID).parent().find('li').removeClass('hrec_li_selected_isneg');
            
            if ( $(this).hasClass('l_is_negated') ){
                $(this).addClass('hrec_li_selected_isneg');
                $('#n_'+liID).addClass('docr_word_selected_isneg');
            }else{
                $(this).addClass('hrec_li_selected');
                $('#n_'+liID).addClass('docr_word_selected');
            }
            
            var occurCnt=0;
            if ( $(this).find('.mult-occur-cnt').length > 0 ){
                occurCnt = $(this).find('.mult-occur-cnt').attr('id').substring(3);
            }
                       


            var container = $('.the-doc'),
            scrollTo = $('#n_'+liID);
            container.scrollTop( scrollTo.offset().top - container.offset().top + container.scrollTop() );
            
           
            $('.the-doc .ocs').hide();
            $('#ocs_'+liID).show();

            
           
        });

        
        this.processShowNLPDocument = function(data_,textStatus_,jqXHR_) {

                        jQuery('#doc-view-hdr').hide();
                        jQuery('#review-document #doc-view').html('');
                        jQuery('#list-hrec').html('');
                        data = app_.processResponse(data_,jqXHR_);
                        var collection = data.nlpShowDocsList;
                        if(data.nlpShowDocsList.length != 0){
                             self.loadDocFromJSON(data.nlpShowDocsList);
                        }
                        else{
                            dalert.alert("Sorry, No Document Found!","Document Alert!");
                        }
         }


         this.loadDocFromJSON = function(data){
                if (!data instanceof Array) {
                    return;
                }
               
                /*
                 * I did this preprosssing because some of the stop starts can span other stop starts
                 * so this sorts by char position
                 */
                var unsortedHits = [];
                if ( data ){
                    for( hcnt=0 ; hcnt < data.length; hcnt++ ){
                        var hit = data[hcnt];
                        /*
                         * Two pushes because of recursive spans and overlapping. I have to put in the endpos, in order 
                         * to look for duplicate start and stops. So some of the fields are really not needed for the hit.stop
                         */
                        unsortedHits.push( {id:hcnt, occurcnt:0, 'charpos':hit.start, 'endpos':hit.stop, 'type':0, 'concept_text':hit.concept_text, 'hit_text':hit.hitText, 'is_negated':hit.is_negated, 'hit_id':hit.document_id }  );
                        unsortedHits.push( {id:hcnt, occurcnt:0, 'charpos':hit.stop,  'endpos':hit.stop, 'type':1, 'concept_text':hit.concept_text, 'hit_text':hit.hitText, 'is_negated':hit.is_negated, 'hit_id':hit.document_id }  );
                      
                    }
                }
                /*
                 * sort by charpos
                 */
                var sortedHits = unsortedHits.sort(function(obj1, obj2) {
                        return obj1.charpos - obj2.charpos;
                });
        
                /*
                 * Now they are sorted look for dups.
                 */

                for( i=0 ; i < sortedHits.length; i++ ){
                    if ( i > 0 && ( sortedHits[i].charpos == sortedHits[i-1].charpos ) && ( sortedHits[i].endpos == sortedHits[i-1].endpos ) ){
                        if ( sortedHits[i-1].occurcnt == 0 ){
                            sortedHits[i-1].occurcnt = 1;
                        }
                        sortedHits[i].occurcnt = sortedHits[i-1].occurcnt + 1;
                    }
                }

                             
               
                 if ( data.length > 0  ){
                    reviewDocString = data[0].note_content;
                    rawDocString = data[0].note_content; //used by right click functionality for false negatives
                    currentDocID = data[0].document_id;
                    var offsetCnt=0;

                    
                    for( hcnt=0 ; hcnt < sortedHits.length; hcnt++ ){

                            if(sortedHits[hcnt].is_negated === null){
                                            console.log("is_negated is NULL");
                    	       }
                    	      else {
                                 var isNegated = sortedHits[hcnt].is_negated.toLowerCase() == 'y' ? true : false;
                                 if ( sortedHits[hcnt].type == 0 ){//type 0 is start char count 
                                        var ocxtra ='';
                                        if ( sortedHits[hcnt].occurcnt > 0  ){
                                              ocxtra = '<span class="ocs" id="ocs_'+sortedHits[hcnt].id+'" style="display:none">('+sortedHits[hcnt].occurcnt+')</span>';
                                         }

                                        var negTxt = (isNegated ? "s_is_negated" : "") ;
                                        var thespan = '<span rel="popover" class="ws '+negTxt+'" data-content="'+sortedHits[hcnt].concept_text
                                                     +'" title="'+ (isNegated ? 'is negated' : '' ) +'" id="n_'+sortedHits[hcnt].id+'">'+ocxtra;
                                }
                                else {
                                        var thespan = '</span>';
                                }

                                  reviewDocString = reviewDocString.substring(0,(sortedHits[hcnt].charpos+offsetCnt) )
                                                     +thespan
                                                     +reviewDocString.substring( (sortedHits[hcnt].charpos+offsetCnt) );
                                                      offsetCnt += thespan.length;

                                  if ( sortedHits[hcnt].type == 0 ){
                                      var ocnt='';
                                      if ( sortedHits[hcnt].occurcnt > 0  ){
                                      ocnt = ' <span class="mult-occur-cnt" id="oc_'+sortedHits[hcnt].occurcnt+'">('+sortedHits[hcnt].occurcnt+')</span>';
                                      }
                                      else {
                                            ocnt='';
                                      }

                                      jQuery('#list-hrec').append('<li class="'+ (isNegated ? 'l_is_negated' : '' )  
                               
                                          +'" title="'+sortedHits[hcnt].hit_text+'" id="l_'+sortedHits[hcnt].id
                                          +'">'+sortedHits[hcnt].concept_text+ocnt+'<div id="hitid_'+sortedHits[hcnt].hit_id+'" title="tag as incorrect concept"></div></li>');
                                   } // END of if sortedHits[hcnt].type

                             }// END if else for sortedHits[hcnt].is_negated 
                          }
                      } // end of if
                
                jQuery('#review-document #doc-view').html('');
                jQuery('#doc-view-hdr').show();
                if ( sortedHits.length > 0  ){
                    jQuery('#doc-right').show();
                }else{
                    jQuery('#doc-right').hide();
                }
                
                
                jQuery('#review-document .pagination').hide();
                jQuery('#review-document #doc-view')
                        .append('<div id="p_'+0+'" class="the-doc">'+
                                 self.formatDoc(reviewDocString)+'</div>');

                
    } // end the loadDocfromJson method

          this.formatDoc = function(docData){
                  if ( docData ){
                      docData = docData.replace(/\r/g, "<br />");
                      docData = docData.replace(/\n/g, "<br />");
                      docData = docData.replace(/\r\n/g, "<br />");
                      return docData;
                  }else{
                      return '';
                  }
              }; 
      
     
    };

})(jQuery, jQuery.phedrs.app));


