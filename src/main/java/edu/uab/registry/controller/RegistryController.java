package edu.uab.registry.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;

import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uab.registry.dao.GetAllCVDao;
import edu.uab.registry.dao.GetConfigAddAttributesDao;
import edu.uab.registry.dao.NlpDocumentsDao;
import edu.uab.registry.dao.RegistryAttributeDao;
import edu.uab.registry.dao.RegistryTabsDao;
import edu.uab.registry.dao.impl.GenericRegistryPatientsTabDaoImpl;
import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.domain.GetAllCVInfo;
import edu.uab.registry.domain.GetAllCVList;
import edu.uab.registry.domain.GetConfigAddAttributesInfo;
import edu.uab.registry.domain.GetConfigAddAttributesList;
import edu.uab.registry.domain.NlpPatientHitsDocsList;
import edu.uab.registry.domain.NlpShowDocument;
import edu.uab.registry.domain.RegistryAttributeInfo;
import edu.uab.registry.domain.RegistryAttributeList;
import edu.uab.registry.domain.RegistryTabsInfo;
import edu.uab.registry.domain.RegistryTabsList;
import edu.uab.registry.json.GenericOrmStatusView;
import edu.uab.registry.json.GetAddRegistryResult;
import edu.uab.registry.json.GetAllCVInfoView;
import edu.uab.registry.json.GetConfigAddAttributesInfoView;
import edu.uab.registry.json.GetRegistryPatientsInTabsResult;
import edu.uab.registry.json.NlpDocumentsView;
import edu.uab.registry.json.RegistryAttributeInfoView;
import edu.uab.registry.json.RegistryTabsInfoView;
import edu.uab.registry.orm.Cv;
import edu.uab.registry.orm.Cvterm;
import edu.uab.registry.orm.CvtermProp;
import edu.uab.registry.orm.RegistryCvMetaData;
import edu.uab.registry.orm.dao.CvDao;
import edu.uab.registry.orm.dao.CvtermDao;
import edu.uab.registry.orm.dao.CvtermPropDao;
import edu.uab.registry.util.ApplicationProps;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;

@RestController
public class RegistryController 
{
	@RequestMapping(value="/getregistrypatientsintabs", method=RequestMethod.GET)
    public String getRegistryPatientsInTabs
    (
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,	
    	@RequestParam(value="tab_key", defaultValue="") String tabKey_
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("getregistrypatientsintabs ws params : registry_id="+registryID_+",tab_key="+tabKey_);
		
		List<GenericRegistryPatient> registryPatients = new ArrayList<GenericRegistryPatient>();
		GetRegistryPatientsInTabsResult result = new GetRegistryPatientsInTabsResult();
		
		try {
			// Validate the tab type.
			if (StringUtils.isNullOrEmpty(tabKey_)) { 
				throw new Exception("Invalid tab_key (empty)"); 
			} else {
				tabKey_ = tabKey_.trim();
			}
			
			// Validate the registry ID.
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
						
			// Use the DAO to retrieve a list of registry patients for the specified tab. 
			GenericRegistryPatientsTabDaoImpl grpsDao = (GenericRegistryPatientsTabDaoImpl) context.getBean("genericRegistryPatientsTabDao");
			registryPatients = grpsDao.get(logger, registryID_, tabKey_);
			
			// Populate the object that will be converted to JSON and returned.
			result.setRegistryPatients(registryPatients);
			result.setTabKey(tabKey_);
			result.setWebServiceStatus("", true);
			
			logger.info("Number of Registry Patients in tab: " + tabKey_ + " :" + registryPatients.size());
			
		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getregistrypatientsintabs="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
        return result.toJSON();
    }
	
	// Web service for Add Registry section of UI
	@RequestMapping(value="/addRegistry", method=RequestMethod.GET)
    public String addRegistry
    (
    	@RequestParam(value="name", defaultValue="") String registry_name,	
    	@RequestParam(value="dbxref_id", defaultValue="0") Integer dbxref_id,
    	@RequestParam(value="owner_name", defaultValue="") String owner_name_,
    	@RequestParam(value="definition", defaultValue="") String registry_definition
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("addRegistry Webservice parameters : registry_name="+registry_name+",definition="+registry_definition+",Owner Name="+owner_name_);
		
		boolean addPatCVMetaDataStatus = true;		
		boolean addEncCVMetaDataStatus = true;
		boolean addCVEncAttrStatus = true;
		
		int cvTermID = 0;
		//Cvterm cvTermID = null;
		int patient_AttributeCvId = 0;
		int encounter_AttributeCvId = 0;
		String wsStatusJsonString = null;
		
		GetAddRegistryResult result = new GetAddRegistryResult();
		
		try {
			
			// Validate the input parameters.
			
			if (StringUtils.isNullOrEmpty(registry_name)) { throw new Exception("Invalid Registry Name Parameter"); }
			if (StringUtils.isNullOrEmpty(registry_definition)) { throw new Exception("Invalid Registry Definition Parameter"); }
			if (StringUtils.isNullOrEmpty(owner_name_)) { throw new Exception("Invalid Owner Name Parameter"); }
			
			// Calling addcvterm function
			CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");	
			Cvterm cvterm = new Cvterm();						
			cvterm.setName(registry_name);	
			cvterm.setIsObsolete(0);
			cvterm.setIsRelationshipType(0);
			cvterm.setCvId(Constants.CV_ID);
			if (dbxref_id == 0) {				
				cvterm.setDbxrefId(null);
			} else {
				cvterm.setDbxrefId(dbxref_id);
			}
			cvterm.setDefinition(registry_definition);
			cvTermID = cvtermDao.insertIntegertype(cvterm);
			
			
			//Calling addcv for Patient Attributes
			//TO DO : Add check to see whether the registry_name+"Patient Attributes" is already present in the CV table or not; If yes, then don't allow
			// it to insert
			CvDao cvDao = (CvDao)context.getBean("cvDao");
			Cv cvInsert = new Cv();
			System.out.println("Checking Concatenation Result for Patienr Attr Reg Name");
			System.out.println(registry_name+" Patient Attributes");
			String test = registry_name+"Patient Attributes";
			String check = cvInsert.getName();
			System.out.println("What is check printing");
			System.out.println(check);
			cvInsert.setName(registry_name+"Patient Attributes");
			cvInsert.setDefinition(registry_definition);
			cvInsert.setOwnerName(owner_name_);
			patient_AttributeCvId = cvDao.insertIntegertype(cvInsert);
			
			
			//Calling addcv for Encounter Attributes 
			CvDao cvEncAttrDao = (CvDao)context.getBean("cvDao");
			Cv cvInsertforEncAttr = new Cv();
			System.out.println("Checking Concatenation Result for encounter Attr Reg Name");
			System.out.println(registry_name+" Encounter Attributes");
			cvInsertforEncAttr.setName(registry_name+"Encounter Attributes");
			cvInsertforEncAttr.setDefinition(registry_definition);
			cvInsertforEncAttr.setOwnerName(owner_name_);
			encounter_AttributeCvId = cvEncAttrDao.insertIntegertype(cvInsertforEncAttr);
			
					
			//Calling add_cv_metadata for Patient Attributes 
            CvtermDao cvtermMetaDataDao = (CvtermDao)context.getBean("cvtermDao");				
			RegistryCvMetaData cvmetadata = new RegistryCvMetaData();
			cvmetadata.setRegistryId(cvTermID);
			cvmetadata.setAttributeCvtermId(Constants.PATIENT_ATTRIBUTE_CVTERM_ID);
			cvmetadata.setValueDisplayName(registry_name+" Patient Attributes");
			cvmetadata.setValueCvId(patient_AttributeCvId);
			addPatCVMetaDataStatus = cvtermMetaDataDao.insert(cvmetadata);
			
			logger.info("addRegistry Patient status:" + addEncCVMetaDataStatus);
			
			//Calling add_cv_metadata for Encounter Attributes 
            CvtermDao cvtermMetaDataEncounterDao = (CvtermDao)context.getBean("cvtermDao");				
			RegistryCvMetaData cvEncountermetadata = new RegistryCvMetaData();
			cvEncountermetadata.setRegistryId(cvTermID);
			cvEncountermetadata.setAttributeCvtermId(Constants.ENCOUNTER_ATTRIBUTE_CVTERM_ID);
			cvEncountermetadata.setValueDisplayName(registry_name+" Encounter Attributes");
			cvEncountermetadata.setValueCvId(encounter_AttributeCvId);
			addEncCVMetaDataStatus = cvtermMetaDataEncounterDao.insert(cvEncountermetadata);
			
			logger.info("addRegistry Encounter status:" + addEncCVMetaDataStatus);
			if (addEncCVMetaDataStatus) {
				wsStatusJsonString = GetAddRegistryResult.OrmStatusToJsonString(true, cvTermID, true);
			} else {
				wsStatusJsonString = GetAddRegistryResult.OrmStatusToJsonString(false, 0, true);
			}
			
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GetAddRegistryResult.OrmStatusToJsonString(false, 0, true);
		}
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.addRegistry="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	
	// For Configure Registry for adding patient attributes using ORM
	@RequestMapping(value="/addPatientAttributeCVTerm", method=RequestMethod.GET)
    public String addPatientAttributeCVTerm
    (
    	@RequestParam(value="name", defaultValue="") String name,
    	@RequestParam(value="definition", defaultValue="Default Definition") String definition,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
    	@RequestParam(value="is_obsolete", defaultValue="0") Integer is_obsolete,
    	@RequestParam(value="is_relationshiptype", defaultValue="0") Integer is_relationshiptype,
    	@RequestParam(value="dbxref_id", defaultValue="0") Integer dbxref_id
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("addPatientAttributeCVTerm ws params : " + 
				"name="+name+
				",is_obsolete="+is_obsolete+
				",is_relationshiptype="+is_relationshiptype+
				",registry_id="+registryID_+
				",dbxref_id="+dbxref_id+
				",definition="+definition
				);
				
		boolean addStatus = true;		
		try {
			CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");				
			
			RegistryCvMetaData cvmetadata = new RegistryCvMetaData();
			cvmetadata.setRegistryId(registryID_);
			Cvterm cvterm = new Cvterm();						
			cvterm.setName(name);	
			cvterm.setIsObsolete(is_obsolete);
			cvterm.setIsRelationshipType(is_relationshiptype);
			
			if (dbxref_id == 0) {				
				cvterm.setDbxrefId(null);
			} else {
				cvterm.setDbxrefId(dbxref_id);
			}
			cvterm.setDefinition(definition);
			
			addStatus = cvtermDao.insertConfigPatientAttributeCVTerm(cvterm,cvmetadata); 
			logger.info("addPatientAttributeCVTerm status:" + addStatus);
			if (addStatus) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Successfully Inserted/Configured Patient Attribute!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Error ! Unable to Insert/Configure Patient Attribute !", true);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addPatientAttributeCVTerm="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	
	@RequestMapping(value="/delete_config_patient_record", method=RequestMethod.GET)
    public String deleteRegistryPatientAttribute
    (
    	@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id    	
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("delete_config_patient_record ws params : " + "cvterm_id:" + cvterm_id);		
		try {					
			CvtermDao cvtermUpdateDao = (CvtermDao)context.getBean("cvtermDao");
			
			int deleteStatus = cvtermUpdateDao.softDeleteByCvtermId(cvterm_id);
			String statusMessage = null;
			logger.info("delete_config_patient_record status:" + deleteStatus);								
			if (deleteStatus >= 1) {
				statusMessage = "Deleted Config Patient Attribute record with cvterm_id= " + cvterm_id;
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, statusMessage, true);
			} else if (deleteStatus == 0) {
				statusMessage = "Did not find Config Patient Attribute record with cvterm_id=" + cvterm_id;
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, statusMessage, true);			
			} 						        
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.delete_config_patient_record=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	
	 @RequestMapping(value="/delete_attribute_config_record", method=RequestMethod.GET)
    public String deleteAttributeConfigRecord
    (
    	@RequestParam(value="cvtermprop_id", defaultValue="0") Integer cvtermprop_id    	
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("delete_attribute_config_record ws params : " + "cvtermprop_id:" + cvtermprop_id);		
		try {					
			CvtermPropDao cvtermUpdateDao = (CvtermPropDao)context.getBean("cvtermPropDao");
			
			int deleteStatus = cvtermUpdateDao.hardDeleteByCvTermPropID(cvtermprop_id);
			String statusMessage = null;
			logger.info("delete_attribute_config_record status:" + deleteStatus);								
			if (deleteStatus >= 1) {
				statusMessage = "Hard Deleted Config Patient Attribute record with cvtermprop_id= " + cvtermprop_id;
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, statusMessage, true);
			} else if (deleteStatus == 0) {
				statusMessage = "Did not find Config Patient Attribute record with cvtermprop_id=" + cvtermprop_id;
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, statusMessage, true);			
			} 						        
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.delete_attribute_config_record=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	// This can be used for Configuration Widget as we already have updatecvterm for Patient and Encounter config
	 @RequestMapping(value="/update_attribute_config_record", method=RequestMethod.GET)
    public String updateAttributeConfigRecord
    (
    	@RequestParam(value="cvtermprop_id", defaultValue="0") Integer cvtermPropId,
    	@RequestParam(value="type_id", defaultValue="0") Integer typeId,
    	@RequestParam(value="value", defaultValue="") String value
    	  	
     ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("update_attribute_config_record ws params : " + 
					 "Cvterm Prop ID:" + cvtermPropId,
					 "Type ID: "+ typeId,
					 "Value:" +value
					);	
		boolean updateStatus = true;
		try {	
			 
			  CvtermPropDao cvtermPropDao = (CvtermPropDao)context.getBean("cvtermPropDao");
              CvtermProp cvtermprop = null;
              List<CvtermProp> cvPropList = cvtermPropDao.findByCvtermPropId(cvtermPropId);
			  if((cvPropList != null) && (!cvPropList.isEmpty())){
				cvtermprop = cvPropList.get(0);
				cvtermprop.setTypeId(typeId);
				cvtermprop.setValue(value);
				
				
				updateStatus = cvtermPropDao.insertOrUpdate(cvtermprop);
				logger.info("update_attribute_config_record:" + updateStatus);
 				if (updateStatus) {
 					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Updated CVTERMPROP Record!", true);
 				} else {
 					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Unable to update CVTERMPROP Record!", true);
 				}		
				
							
			  }
                        
        } catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.updateregistrypatientattribute=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
		
	 @RequestMapping(value="/update_config_name", method=RequestMethod.GET)
	    public String updateRegistryConfigName
	    (       
	    		@RequestParam(value="name", defaultValue="") String name,
	    		@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id,
	    		// Injected HttpServletRequest parameter to log out the parameter the servlet container(Tomcat) sees. 
	    		HttpServletRequest request
	    ) 
	    {		
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			String wsStatusJsonString = null;
			logger.info("-----------------------------------------------------------------------------------------------------------");		
			logger.info("update_config_name ws params :Cvterm ID " +cvterm_id+"Registry Name:"+name/*+" CV_Id "+cv_id*/); 
			logger.info("HttpRequest Parameters Check");
			request.getParameterMap().entrySet().forEach(entry -> logger.info("Parameter: " + entry.getKey() + Arrays.toString(entry.getValue())));
			boolean updateStatus = true;
			
			try {
				
				// Validate the input parameters.
				if (cvterm_id < 1 ) {throw new Exception("Invalid Cvterm ID parameter"); }
				if (StringUtils.isNullOrEmpty(name)) { throw new Exception("Invalid Registry Name Parameter"); }
				 CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");
	             Cvterm cvterm = null;
	             List<Cvterm> cvtermList = cvtermDao.findByCvtermId(cvterm_id);
	             System.out.println("cvtermList check below");
				 System.out.println(cvtermList);
				 System.out.println("cvtermList.get(0) test below");
				 System.out.println(cvtermList.get(0));
				 
			      if((cvtermList != null) && (!cvtermList.isEmpty())){
					cvterm = cvtermList.get(0);
					cvterm.setName(name);
					cvterm.setCvtermId(cvterm_id);
					updateStatus = cvtermDao.insertOrUpdate(cvterm);
					logger.info("update_config_name:" + updateStatus);
	 				if (updateStatus) {
	 					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Updated Registry Name!", true);
	 				} else {
	 					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Unable to update Registry Name!", true);
	 				}		
											
				  }
	                        
	        } catch(Throwable th) {
				th.printStackTrace();
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
			}
			logger.info("Log.ElapsedTime.ws.update_registry_config_name=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
			logger.info("-----------------------------------------------------------------------------------------------------------");
			return wsStatusJsonString;
	    }
	
	 
	@RequestMapping(value="/update_registry_config_definition", method=RequestMethod.GET)
    public String updateRegistryConfigDefinition
    (
    	@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id,
    	@RequestParam(value="definition", defaultValue="") String definition
    	  	
     ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("update_registry_config_definition ws params :Cvterm ID " +cvterm_id+" New Registry Name:"+definition);
		boolean updateStatus = true;
		try {	
			// Validate the input parameters.
			if (cvterm_id < 1 ) {throw new Exception("Invalid Cvterm ID parameter"); }
			if (StringUtils.isNullOrEmpty(definition)) { throw new Exception("Invalid Registry Definition Parameter");}
			 CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");
             Cvterm cvterm = null;
             List<Cvterm> cvtermList = cvtermDao.findByCvtermId(cvterm_id);
			 
		      if((cvtermList != null) && (!cvtermList.isEmpty())){
				cvterm = cvtermList.get(0);
				cvterm.setDefinition(definition);
				updateStatus = cvtermDao.insertOrUpdate(cvterm);
				logger.info("update_registry_config_definition:" + updateStatus);
 				if (updateStatus) {
 					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Updated Definition Name!", true);
 				} else {
 					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Unable to update Definition Name!", true);
 				}		
										
			  }
                        
        } catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.update_registry_config_definition=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	 
	 
	
	
	   // For Configure Registry for adding encounter attributes using ORM
		@RequestMapping(value="/addEncounterAttributeCVTerm", method=RequestMethod.GET)
	    public String addEncounterAttributeCVTerm
	    (
	    	@RequestParam(value="name", defaultValue="") String name,
	    	@RequestParam(value="definition", defaultValue="Default Definition") String definition,
	    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
	    	@RequestParam(value="is_obsolete", defaultValue="0") Integer is_obsolete,
	    	@RequestParam(value="is_relationshiptype", defaultValue="0") Integer is_relationshiptype,
	    	@RequestParam(value="dbxref_id", defaultValue="0") Integer dbxref_id
	    ) 
	    {
	    	StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			String wsStatusJsonString = null;
			
			logger.info("-----------------------------------------------------------------------------------------------------------");
			logger.info("addEncounterAttributeCVTerm ws params : " + 
					"name="+name+
					",is_obsolete="+is_obsolete+
					",is_relationshiptype="+is_relationshiptype+
					",registry_id="+registryID_+
					",dbxref_id="+dbxref_id+
					",definition="+definition
					);
					
			boolean addStatus = true;		
			try {
				CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");				
				
				RegistryCvMetaData cvmetadata = new RegistryCvMetaData();
				
				cvmetadata.setRegistryId(registryID_);
				
				Cvterm cvterm = new Cvterm();						
				cvterm.setName(name);	
				cvterm.setIsObsolete(is_obsolete);
				cvterm.setIsRelationshipType(is_relationshiptype);
				
				if (dbxref_id == 0) {				
					cvterm.setDbxrefId(null);
				} else {
					cvterm.setDbxrefId(dbxref_id);
				}
				cvterm.setDefinition(definition);
				
				addStatus = cvtermDao.insertConfigEncounterAttributeCVTerm(cvterm,cvmetadata); 
				logger.info("addEncounterAttributeCVTerm status:" + addStatus);
				if (addStatus) {
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Successfully Inserted/Configured Encounter Attribute!", true);
				} else {
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Error ! Unable to Insert/Configure Encounter Attribute !", true);
				}
			} catch (Throwable th) {
				th.printStackTrace();
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
			}
			logger.info("Log.ElapsedTime.ws.addEncounterAttributeCVTerm="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
			logger.info("-----------------------------------------------------------------------------------------------------------");
	        return wsStatusJsonString;
	    }
		
		
		// For Configure Registry for adding attribute configuration using ORM
		@RequestMapping(value="/addAttributeConfigurationRecord", method=RequestMethod.GET)
			    public String addAttributeConfigurationRecord
			    (
			    	@RequestParam(value="cvterm_id", defaultValue="0") Integer registryID_,
			    	@RequestParam(value="type_id", defaultValue="0") Integer type_id,  
			    	@RequestParam(value="value", defaultValue="") String definition,
			    	@RequestParam(value="rank", defaultValue="1") Integer rank,
			    	HttpServletRequest request
			    	
			    ) 
			    {
			    	StopWatch stopWatch = new StopWatch();
					stopWatch.start();
					String wsStatusJsonString = null;
					
					logger.info("-----------------------------------------------------------------------------------------------------------");
					logger.info("HttpRequest Parameters Check");
					request.getParameterMap().entrySet().forEach(entry -> logger.info("Parameter: " + entry.getKey() + Arrays.toString(entry.getValue())));
					logger.info("addAttributeConfigurationRecord ws params : " + 
							
							"value="+definition+
							",cvterm_id="+registryID_+
							",type_id="+type_id+
							",rank="+rank
							);
							
					boolean addStatus = true;		
					try {
						
						// Validate only registryID_ input parameter because value parameter can be null.
						if (registryID_ < 1 ) {throw new Exception("Invalid Registry ID parameter"); }
						
						
						CvtermPropDao cvtermPropDao = (CvtermPropDao)context.getBean("cvtermPropDao");	
					    CvtermProp cvtermprop = new CvtermProp();
				        cvtermprop.setCvtermId(registryID_);
				        cvtermprop.setTypeId(type_id);
				        cvtermprop.setValue(definition);
				        cvtermprop.setRank(rank);
						
												
						addStatus = cvtermPropDao.insert(cvtermprop); 
						logger.info("addAttributeConfigurationRecord status:" + addStatus);
						if (addStatus) {
							wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Successfully Inserted Attribute Configuration Record!", true);
						} else {
							wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Error ! Unable to Insert Attribute Configuration Record !", true);
						}
					} catch (Throwable th) {
						th.printStackTrace();
						wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
					}
					logger.info("Log.ElapsedTime.ws.addAttributeConfigurationRecord="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
					logger.info("-----------------------------------------------------------------------------------------------------------");
			        return wsStatusJsonString;
			    }
			
	
	@RequestMapping(value="/getRegistryAttributes", method=RequestMethod.GET)
    public String getRegistryAttributes
    (
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_	
    	
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("getRegistryAttributes ws params : registry_id="+registryID_);
		
		RegistryAttributeInfo regAttrInfo = new RegistryAttributeInfo();
		String registryAttributeInfoJsonString =  "";
		
		try {
						
			// Validate the registry ID.
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			
			RegistryAttributeDao regAttrDao = (RegistryAttributeDao) context.getBean("regAttributeListDao");
			List<RegistryAttributeList> regAttributesLists = regAttrDao.getRegistryAttribute(registryID_);
			regAttrInfo.setRegistryAttributesList(regAttributesLists);
			
			registryAttributeInfoJsonString = RegistryAttributeInfoView.createRegistryAttributeListInfoToJsonString(regAttrInfo, true, Constants.EMPTY, true);
			if ((regAttributesLists != null) && (regAttributesLists.size() > 0)) {
				logger.info("Number of Registry Attributes :" + regAttributesLists.size());
			}	
			
		} catch (Throwable th) {
			th.printStackTrace();
			registryAttributeInfoJsonString = RegistryAttributeInfoView.createRegistryAttributeListInfoToJsonString(regAttrInfo,false,th.getMessage(), true);
		}
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getRegistryAttributes="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
        return registryAttributeInfoJsonString;
    }
	
	@RequestMapping(value="/getRegistryTabs", method=RequestMethod.GET)
    public String getRegistryTabs
    (
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_	
    	
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("getRegistryTabs ws params : registry_id="+registryID_);
		
		RegistryTabsInfo regTabsInfo = new RegistryTabsInfo();
		String registryTabsInfoJsonString =  "";
		
		try {
						
			// Validate the registry ID.
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			
			RegistryTabsDao regTabDao = (RegistryTabsDao) context.getBean("regTabsListDao");
			List<RegistryTabsList> regTabsLists = regTabDao.getRegistryTabs(registryID_);
			regTabsInfo.setRegistryTabsList(regTabsLists);
			
			registryTabsInfoJsonString = RegistryTabsInfoView.createRegistryTabsInfoToJsonString(regTabsInfo, true, Constants.EMPTY, true);
			if ((regTabsLists != null) && (regTabsLists.size() > 0)) {
				logger.info("Number of Registry Tabs :" + regTabsLists.size());
			}	
			
		} catch (Throwable th) {
			th.printStackTrace();
			registryTabsInfoJsonString = RegistryTabsInfoView.createRegistryTabsInfoToJsonString(regTabsInfo,false,th.getMessage(), true);
		}
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getRegistryTabs="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
        return registryTabsInfoJsonString;
    }
	
	@RequestMapping(value="/get_registry_config_add_attributes", method=RequestMethod.GET)
    public String getRegistryConfigAddAttributes
    (
    	@RequestParam(value="cv_id", defaultValue="0") Integer cv_id,
    	@RequestParam(value="name", defaultValue="") String attributeName
    	
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("get_registry_config_add_attributes ws params : cv_id="+cv_id+ "and Attribute Name: "+attributeName);
		
		GetConfigAddAttributesInfo configAddAttrInfo = new GetConfigAddAttributesInfo();
		String configAddAttributeInfoJsonString =  "";
		
		try {
						
			// Validate the registry ID.
			if (cv_id < 1) { throw new Exception("Invalid CV ID"); }
			GetConfigAddAttributesDao configAddAttrDao = (GetConfigAddAttributesDao) context.getBean("getconfigAddAttrDao");
			List<GetConfigAddAttributesList> configAddAttributesLists = configAddAttrDao.getConfigAddAttribute(cv_id,attributeName);
			configAddAttrInfo.setAddConfigAttributesList(configAddAttributesLists);
			
			configAddAttributeInfoJsonString = GetConfigAddAttributesInfoView.createAddConfigAttributesListInfoToJsonString(configAddAttrInfo, true, Constants.EMPTY, true);
			if ((configAddAttributesLists != null) && (configAddAttributesLists.size() > 0)) {
				logger.info("Number of Atributes :" + configAddAttributesLists.size());
			}	
			
		} catch (Throwable th) {
			th.printStackTrace();
			configAddAttributeInfoJsonString = GetConfigAddAttributesInfoView.createAddConfigAttributesListInfoToJsonString(configAddAttrInfo,false,th.getMessage(), true);
		}
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.get_registry_config_add_attributes="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
        return configAddAttributeInfoJsonString;
    }
	
	@RequestMapping(value="/getAllCVs", method=RequestMethod.GET)
    public String getAllCVs
    (
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_	
    	
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("getAllCVs ws params : registry_id="+registryID_);
		
		GetAllCVInfo getAllInfo = new GetAllCVInfo();
		String getAllCVInfotoJsonString =  "";
		
		try {
						
			// Validate the registry ID. Registry ID is not used right now. To be used in future
			//if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			
			GetAllCVDao getAllCVDao = (GetAllCVDao) context.getBean("getAllCVListDao");
			List<GetAllCVList> getAllCVLists = getAllCVDao.getAllCV();
			getAllInfo.setCvList(getAllCVLists);
			
			getAllCVInfotoJsonString = GetAllCVInfoView.createCVListInfoToJsonString(getAllInfo, true, Constants.EMPTY, true);
			if ((getAllCVLists != null) && (getAllCVLists.size() > 0)) {
				logger.info("Number getAllCVs Records :" + getAllCVLists.size());
			}	
			
		} catch (Throwable th) {
			th.printStackTrace();
			getAllCVInfotoJsonString = GetAllCVInfoView.createCVListInfoToJsonString(getAllInfo,false,th.getMessage(), true);
		}
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getAllCVs="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
        return getAllCVInfotoJsonString;
    }
		  
	
	// For my another testing:
	@RequestMapping(value="/showdocument", method=RequestMethod.GET)
    public String showDocument
    (
    	@RequestParam(value="doc_id", defaultValue="0") String doc_id,
    	@RequestParam(value="registry_id", defaultValue="2861") String registryID_
    ) 
    {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("showdocument ws params : " + 
					"doc_id="+doc_id
					);
		String nlpShowDocumentJsonString = "";		
		NlpPatientHitsDocsList nlpPatHitsDocsLIst = new NlpPatientHitsDocsList();
		
		try {	
			
			// Bean for getting the value from cvtermprop using registryId from PHEDRS (EDWDB4)
		    NlpDocumentsDao nlpDocForValueDao = (NlpDocumentsDao) context.getBean("nlpValueForDocumentsDao");
		    
		    // Bean for getting documents from CRCP Production Database (CCTSDBP)
		    NlpDocumentsDao nlpDocDao = (NlpDocumentsDao) context.getBean("nlpDocumentsDao");
		    String cvtermPropValue = nlpDocForValueDao.getCvtermPropValue(registryID_);
		    logger.info("Checking String value of cvtermPropValue inside RegistryController:" +cvtermPropValue );
		    List<NlpShowDocument> docsWithHits = nlpDocDao.getNlpShowDocument(doc_id,cvtermPropValue);
			nlpPatHitsDocsLIst.setNlpShowDocsList(docsWithHits);
			nlpShowDocumentJsonString = NlpDocumentsView.createNlpShowDocumentToJsonString(nlpPatHitsDocsLIst, true, Constants.EMPTY, true);
			logger.debug("NLP Show Document JSON is\n"+nlpShowDocumentJsonString);			
		} catch(Throwable th) {
			th.printStackTrace();			
			nlpShowDocumentJsonString = NlpDocumentsView.createNlpShowDocumentToJsonString(nlpPatHitsDocsLIst, false, th.getMessage(), true);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.showDocument=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return nlpShowDocumentJsonString;
    }
	
	private static final Logger logger = LoggerFactory.getLogger(RegistryController.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	private static ApplicationProps appProps = (ApplicationProps)context.getBean("appProps");
}
