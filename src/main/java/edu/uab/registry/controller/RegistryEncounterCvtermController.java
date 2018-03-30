package edu.uab.registry.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uab.registry.dao.CvMetaDataListDao;
import edu.uab.registry.dao.EncounterInsuranceListDao;
import edu.uab.registry.dao.RegConfigEncounterAttributesDao;
import edu.uab.registry.dao.RegConfigPatientAttributesDao;
import edu.uab.registry.dao.RegistryEncounterAttributesDao;
import edu.uab.registry.dao.RegistryEncountersDao;
import edu.uab.registry.domain.CvMetaDataList;
import edu.uab.registry.domain.RegConfigEncounterAttributesInfo;
import edu.uab.registry.domain.RegConfigEncounterAttributesList;
import edu.uab.registry.domain.RegConfigPatientAttributesInfo;
import edu.uab.registry.domain.RegConfigPatientAttributesList;
import edu.uab.registry.domain.RegistryCvMetaDataInfo;
import edu.uab.registry.domain.RegistryEncounterInsuranceInfo;
import edu.uab.registry.domain.RegistryEncounterInsuranceList;
import edu.uab.registry.domain.RegistryEncounter;
import edu.uab.registry.json.EncounterInsuranceInfoView;
import edu.uab.registry.json.GetEncounterAttributesResult;
import edu.uab.registry.json.RegConfigEncounterAttributesInfoView;
import edu.uab.registry.json.RegConfigPatientAttributesInfoView;
import edu.uab.registry.json.RegistryCvMetaDataInfoView;
import edu.uab.registry.orm.RegistryEncounterCvterm;
import edu.uab.registry.orm.RegistryEncounterCvtermHist;
import edu.uab.registry.orm.dao.RegistryEncounterCvtermDao;
import edu.uab.registry.orm.dao.RegistryEncounterCvtermHistDao;
import edu.uab.registry.util.*;
import edu.uab.registry.util.Constants.SimpleEncounterAttribute;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


// TODO: Note that I have begun changing the web service names to separate words using underscores. The original
// versions (without underscores) were more difficult to read. For example, compare the readability of
// "addencounterattributecomment" versus "add_encounter_attribute_comment".

@RestController
public class RegistryEncounterCvtermController
{
	@RequestMapping(value="/add_encounter_attribute_comment", method=RequestMethod.GET)
	public String addEncounterAttributeComment(
			@RequestParam(value="comment", defaultValue="") String comment_,
			@RequestParam(value="event_cvterm_id", defaultValue="0") Integer eventCvtermID_,
			@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
			@RequestParam(value="user_id", defaultValue="0") Integer userID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("add_encounter_attribute_comment parameters: comment=" + comment_ +
				", event_cvterm_id=" + eventCvtermID_ +
				", registry_id=" + registryID_ +
				", user_id=" + userID_);

		WebServiceResult result = new WebServiceResult();

		try {
			// Validate the input parameters.
			if (eventCvtermID_ < 1) { throw new Exception("Invalid event cvterm ID"); }
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			if (userID_ < 1) { throw new Exception("Invalid user ID"); }

			// Make sure the comment isn't null and trim whitespace.
			String comment = (StringUtils.isNullOrEmpty(comment_) ? "" : comment_.trim());

			// Add the new object to the database.
			RegistryEncounterCvtermDao recDao = (RegistryEncounterCvtermDao)context.getBean("registryEncounterCvtermDao");
			if (!recDao.addComment(comment, eventCvtermID_, userID_)) { throw new Exception("Unable to add comment to encounter attribute"); }

			result.setWebServiceStatus("", true);

		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.add_encounter_attribute_comment="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}


	@RequestMapping(value="/create_encounter_attribute", method=RequestMethod.GET)
	public String createEncounterAttribute(
			@RequestParam(value="comment", defaultValue="") String comment_,
			@RequestParam(value="cvterm_id", defaultValue="0") Integer cvtermID_,
			@RequestParam(value="encounter_key", defaultValue="0") Integer encounterKey_,
			@RequestParam(value="mrn", defaultValue="") String mrn_,
			@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
			@RequestParam(value="user_id", defaultValue="0") Integer userID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("create_encounter_attribute parameters: comment=" + comment_ +
				", cvterm_id=" + cvtermID_ +
				", encounter_key=" + encounterKey_ +
				", mrn=" + mrn_ +
				", registry_id=" + registryID_ +
				", user_id=" + userID_);

		WebServiceResult result = new WebServiceResult();

		try {
			// Validate the input parameters.
			if (cvtermID_ < 1) { throw new Exception("Invalid cvterm ID"); }
			if (encounterKey_ < 1) { throw new Exception("Invalid encounter key"); }
			if (StringUtils.isNullOrEmpty(mrn_)) { throw new Exception("Invalid MRN"); } else { mrn_ = mrn_.trim(); }
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			if (userID_ < 1) { throw new Exception("Invalid user ID"); }


			// Populate a registry encounter cvterm object.
			RegistryEncounterCvterm rec = new RegistryEncounterCvterm();
			rec.setAssignerId(userID_);
			rec.setAssignmentDate(new Date());
			rec.setCvtermId(cvtermID_);
			rec.setEncounterKey(encounterKey_);
			rec.setRegistrarComment(comment_);
			rec.setRegistryId(registryID_);

			// Add the new object to the database.
			RegistryEncounterCvtermDao recDao = (RegistryEncounterCvtermDao)context.getBean("registryEncounterCvtermDao");
			if (!recDao.insert(rec)) { throw new Exception("An error occurred inserting the registry encounter cvterm"); }

			result.setWebServiceStatus("", true);

		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.create_encounter_attribute="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}


	@RequestMapping(value="/delete_encounter_attribute", method=RequestMethod.GET)
	public String deleteEncounterAttribute(
			@RequestParam(value="event_cvterm_id", defaultValue="0") Integer eventCvtermID_,
			@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
			@RequestParam(value="user_id", defaultValue="0") Integer userID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("delete_encounter_attribute parameters: event_cvterm_id=" + eventCvtermID_ +
				", registry_id=" + registryID_ +
				", user_id=" + userID_);

		WebServiceResult result = new WebServiceResult();

		try {
			// Validate the input parameters.
			if (eventCvtermID_ < 1) { throw new Exception("Invalid event cvterm ID"); }
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			if (userID_ < 1) { throw new Exception("Invalid user ID"); }

			// "Soft delete" the encounter attribute.
			RegistryEncounterCvtermDao recDao = (RegistryEncounterCvtermDao)context.getBean("registryEncounterCvtermDao");
			if (!recDao.delete(eventCvtermID_, userID_)) { throw new Exception("Unable to delete encounter attribute"); }

			result.setWebServiceStatus("", true);

		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.delete_encounter_attribute="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}

	@RequestMapping(value="/getencounterattributes", method=RequestMethod.GET)
	public String getEncounterAttributes(
			@RequestParam(value="encounter_key", defaultValue="0") Integer encounterKey_,
			@RequestParam(value="mrn", defaultValue="") String mrn_,
			@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getencounterattributes parameters: encounter_key=" + encounterKey_ + ", mrn=" + mrn_ + ", registry_id=" + registryID_);

		RegistryEncounter encounter = null;
		GetEncounterAttributesResult result = new GetEncounterAttributesResult();

		try {
			// Validate the input parameters.
			if (encounterKey_ < 1) { throw new Exception("Invalid encounter key"); }
			if (StringUtils.isNullOrEmpty(mrn_)) { throw new Exception("Invalid MRN"); }
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }

			RegistryEncountersDao reDao = (RegistryEncountersDao) context.getBean("registryEncountersDao");
			encounter = reDao.get(encounterKey_, mrn_, registryID_);

			if (encounter == null || encounter.getEncounterKey() < 1) {
				result.setWebServiceStatus("Invalid encounter", false);
			} else {
				result.setEncounter(encounter);
				result.setWebServiceStatus("", true);
			}

		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getencounterattributes="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}
	
	
	
	@RequestMapping(value="/get_encounters_insurance", method=RequestMethod.GET)
	public String getEncounterInsurance
	(
			@RequestParam(value="hqFinNum", defaultValue="") String hqFinNum
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("get_encounters_insurance parameters: FIN Number = " +hqFinNum);
		
		RegistryEncounterInsuranceInfo registryEncInsInfo = new RegistryEncounterInsuranceInfo();
		String encounterInsuranceInfoJsonString =  "";
		
		try {
			EncounterInsuranceListDao encInsDao = (EncounterInsuranceListDao) context.getBean("encounterInsuranceListDao");
			List<RegistryEncounterInsuranceList> regEncInsLists = encInsDao.getEncounterInsurance(hqFinNum);
			registryEncInsInfo.set_encounters_insurance_list(regEncInsLists);
			
			encounterInsuranceInfoJsonString = EncounterInsuranceInfoView.createEncounterInsuranceInfoToJsonString(registryEncInsInfo, true, Constants.EMPTY, true);
			logger.debug("get_encounters_insurance JSON is\n"+encounterInsuranceInfoJsonString);
			if ((regEncInsLists != null) && (regEncInsLists.size() > 0)) {
				logger.info("Number of Encounter Insurance Records :" + regEncInsLists.size());
			}			
		} catch (Throwable th) {
			th.printStackTrace();
			encounterInsuranceInfoJsonString = EncounterInsuranceInfoView.createEncounterInsuranceInfoToJsonString(registryEncInsInfo, false, th.getMessage(), true);
		}		
		
		return encounterInsuranceInfoJsonString;
		
    }
	
	@RequestMapping(value="/getRegistryEncounterAttributes", method=RequestMethod.GET)
	public String getRegistryEncounterAttribute
	(
			@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getRegistryEncounterAttributes parameters: Registry ID = " +registryID_);
		
		RegConfigEncounterAttributesInfo regconfigEncounterAttrInfo = new RegConfigEncounterAttributesInfo();
		String regconfigEncounterAttrInfoJsonString =  "";
		
		try {
			RegConfigEncounterAttributesDao regConfigPADao = (RegConfigEncounterAttributesDao) context.getBean("registryConfigEncounterAttrListDao");
			List<RegConfigEncounterAttributesList> regConfigPtAttr  = regConfigPADao.getRegConfigEncounterAttr(registryID_);
			regconfigEncounterAttrInfo.set_reg_config_encounter_attributes(regConfigPtAttr);
			
			regconfigEncounterAttrInfoJsonString = RegConfigEncounterAttributesInfoView.createRegEncounterAttributesInfoToJsonString(regconfigEncounterAttrInfo, true, Constants.EMPTY, true);
			logger.debug("getRegistryEncounterAttributes JSON is\n"+regconfigEncounterAttrInfoJsonString);
			if ((regConfigPtAttr != null) && (regConfigPtAttr.size() > 0)) {
				logger.info("Number of Encounter Insurance Records :" + regConfigPtAttr.size());
			}			
		} catch (Throwable th) {
			th.printStackTrace();
			regconfigEncounterAttrInfoJsonString = RegConfigEncounterAttributesInfoView.createRegEncounterAttributesInfoToJsonString(regconfigEncounterAttrInfo, false, th.getMessage(), true);
		}		
		
		return regconfigEncounterAttrInfoJsonString;
		
    }


	/*
	@RequestMapping(value="/updateencounterattributes", method=RequestMethod.GET)
	public String updateEncounterAttributes
	(
			@RequestParam(value="encounter_attributes", defaultValue="") String encounterAttributes_,
			@RequestParam(value="encounter_key", defaultValue="0") Integer encounterKey_,
			@RequestParam(value="mrn", defaultValue="") String mrn_,
			@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
			@RequestParam(value="user_id", defaultValue="0") Integer userID_
			)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("updateEncounterAttributes parameters: encounter_attributes=" + encounterAttributes_ + ", encounter_key="+encounterKey_ + ", mrn="+mrn_ + ", registry_id="+registryID_);


		WebServiceResult result = new WebServiceResult();

		try {
			// Validate the input parameters.
			String encounterAttributes = (encounterAttributes_ == null ? "" : encounterAttributes_.trim());
			if (encounterKey_ < 1) { throw new Exception("Invalid encounter key"); }
			if (StringUtils.isNullOrEmpty(mrn_)) { throw new Exception("Invalid MRN"); }
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			if (userID_ < 1) { throw new Exception("Invalid user ID"); }

			// Convert the encounter_attributes JSON string to an array of objects.
			ObjectMapper om = new ObjectMapper();
			SimpleEncounterAttribute[] encAttrs = om.readValue(encounterAttributes, SimpleEncounterAttribute[].class);

			if (encAttrs == null) {
				logger.info("enc attrs is null");
			} else {
				logger.info("enc attrs = " + Integer.toString(encAttrs.length));
			}


			RegistryEncounterAttributesDao reaDao = (RegistryEncounterAttributesDao) context.getBean("registryEncounterAttributesDao");
			reaDao.updateEncounterAttributes(encounterKey_, encAttrs, registryID_, userID_);

			result.setWebServiceStatus("Update successful", true);

		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.updateEncounterAttributes="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}
	*/



	/*
	@RequestMapping(value="/getencounterattributes", method=RequestMethod.GET)
	public String getEncounterAttributes
	(
		@RequestParam(value="encounter_key", defaultValue="0") Integer encounterKey_,
		@RequestParam(value="mrn", defaultValue="") String mrn_,
		@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getencounterattributes parameters: encounter_key="+encounterKey_ + ", mrn="+mrn_ + ", registry_id="+registryID_);

		RegistryEncounter encounter = null;
		GetEncounterAttributesResult result = new GetEncounterAttributesResult();

		try {
			// Validate the input parameters.
			if (encounterKey_ < 1) { throw new Exception("Invalid encounter key"); }
			if (StringUtils.isNullOrEmpty(mrn_)) { throw new Exception("Invalid MRN"); }
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }

			RegistryEncountersDao reDao = (RegistryEncountersDao) context.getBean("registryEncountersDao");
			encounter = reDao.get(encounterKey_, mrn_, registryID_);

			if (encounter == null) {
				result.setWebServiceStatus("Invalid encounter", false);
			} else {
				result.setEncounter(encounter);
				result.setWebServiceStatus("", true);
			}

		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getencounterattributes="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}


	@RequestMapping(value="/addregistryencountercvterm", method=RequestMethod.GET)
    public String addRegistryPatientCvterm
    (    	
    	@RequestParam(value="encounter_key", defaultValue="0") Integer encounter_key,	
    	@RequestParam(value="assigner_id", defaultValue="0") Integer assigner_id,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
    	@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id,
    	@RequestParam(value="value", defaultValue="") String value,    	    	    	    
    	@RequestParam(value="registrar_comment", defaultValue="") String registrar_comment,
    	@RequestParam(value="assignment_date", defaultValue="") String assignment_date,    	
    	@RequestParam(value="code_id", defaultValue="0") Integer code_id
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("addregistryencountercvterm ws params : " + 
					 ", encounter_key:" + encounter_key +
					 ", assigner_id:" + assigner_id +
					 ", registry_id:" + registry_id +					 
					 ", cvterm_id:" + cvterm_id +
					 ", value:" + value +
					 ", registrar_comment:" + registrar_comment +
					 ", assignment_date:" + assignment_date +
					 ", code_id:" + code_id
					);		
		try {						
			RegistryEncounterCvtermDao recDao = (RegistryEncounterCvtermDao)context.getBean("registryEncounterCvtermDao");
			RegistryEncounterCvterm rec = new RegistryEncounterCvterm();
			rec.setEncounterKey(encounter_key);
			rec.setAssignerId(assigner_id);
			rec.setRegistryId(registry_id);					
			rec.setCvtermId(cvterm_id);
			if (Constants.EMPTY.equals(value)) {
				rec.setValue(null);			
			} else {
				rec.setValue(value);
			}
			rec.setRegistrarComment(registrar_comment);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
			if ((assignment_date == null) || "".equals(assignment_date) || "null".equals(assignment_date)) {
				rec.setAssignmentDate(new Date());
			} else {
				rec.setAssignmentDate(sdf.parse(assignment_date));
			}
			if (code_id == 0) {
				rec.setCodeId(null);			
			} else {
				rec.setCodeId(code_id);
			}
			recDao.insert(rec);	   
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Inserted REGISTRY_ENCOUNTER_CVTERM Record!", true);
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addregistryencountercvterm=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	@RequestMapping(value="/updateregistryencountercvterm", method=RequestMethod.GET)
    public String updateRegistryEncounterCvterm
    (    	
    	@RequestParam(value="encounter_key", defaultValue="0") Integer encounter_key,	
    	@RequestParam(value="assigner_id", defaultValue="0") Integer assigner_id,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
    	@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id,
    	@RequestParam(value="value", defaultValue="") String value,    	    	    	    
    	@RequestParam(value="registrar_comment", defaultValue="") String registrar_comment,
    	@RequestParam(value="assignment_date", defaultValue="") String assignment_date,    	
    	@RequestParam(value="code_id", defaultValue="0") Integer code_id
    ) 
    {
		boolean updateStatus = true;
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("updateregistryencountercvterm ws params : " + 
					 ", encounter_key:" + encounter_key +
					 ", assigner_id:" + assigner_id +
					 ", registry_id:" + registry_id +					 
					 ", cvterm_id:" + cvterm_id +
					 ", value:" + value +
					 ", registrar_comment:" + registrar_comment +
					 ", assignment_date:" + assignment_date +
					 ", code_id:" + code_id
					);		
		try {						
			RegistryEncounterCvtermDao recDao = (RegistryEncounterCvtermDao)context.getBean("registryEncounterCvtermDao");
			RegistryEncounterCvterm rec = new RegistryEncounterCvterm();
			rec.setEncounterKey(encounter_key);
			rec.setAssignerId(assigner_id);
			rec.setRegistryId(registry_id);					
			rec.setCvtermId(cvterm_id);
			if (Constants.EMPTY.equals(value)) {
				rec.setValue(null);			
			} else {
				rec.setValue(value);
			}	
			rec.setRegistrarComment(registrar_comment);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
			if ((assignment_date == null) || "".equals(assignment_date) || "null".equals(assignment_date)) {
				rec.setAssignmentDate(new Date());
			} else {
				rec.setAssignmentDate(sdf.parse(assignment_date));
			}
			if (code_id == 0) {
				rec.setCodeId(null);			
			} else {
				rec.setCodeId(code_id);
			}			
			updateStatus = recDao.updateByEncounterKey(rec);	
			if (updateStatus) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Updated REGISTRY_ENCOUNTER_CVTERM Record!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Update of REGISTRY_ENCOUNTER_CVTERM Problem!", true);
			}
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.updateregistryencountercvterm=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }	
	
	@RequestMapping(value="/deleteregistryencountercvterm", method=RequestMethod.GET)
    public String deleteRegistryEncounterCvterm
    (    	
    	@RequestParam(value="encounter_key", defaultValue="0") Integer encounter_key,	    	
    	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
    	@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id    	
    ) 
    {
		boolean deleteStatus = true;
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("deleteregistryencountercvterm ws params : " + 
					 ", encounter_key:" + encounter_key +					 
					 ", registry_id:" + registry_id +					 
					 ", cvterm_id:" + cvterm_id
					);		
		try {						
			RegistryEncounterCvtermDao recDao = (RegistryEncounterCvtermDao)context.getBean("registryEncounterCvtermDao");
			RegistryEncounterCvterm rec = new RegistryEncounterCvterm();
			rec.setEncounterKey(encounter_key);			
			rec.setRegistryId(registry_id);					
			rec.setCvtermId(cvterm_id);
			deleteStatus = recDao.deleteRegistryEncounterCvterm(encounter_key, registry_id, cvterm_id);	 
			if (deleteStatus) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Deleted REGISTRY_ENCOUNTER_CVTERM Record!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Deletion of REGISTRY_ENCOUNTER_CVTERM Problem!", true);
			}
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.deleteregistryencountercvterm=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	@RequestMapping(value="/deleteregistryencounterattribute", method=RequestMethod.GET)
    public String deleteregistryencounterattribute
    (
    	@RequestParam(value="event_cvterm_id", defaultValue="0") Integer event_cvterm_id    	
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("deleteregistryencounterattribute ws params : " + "registry_encounter_cvterm_id:" + event_cvterm_id);		
		try {					
			edu.uab.registry.orm.dao.RegistryEncounterCvtermDao recHibDao = 
					(edu.uab.registry.orm.dao.RegistryEncounterCvtermDao)context.getBean("registryEncounterCvtermDao");
			
			int deleteStatus = recHibDao.deleteByEventCvtermId(event_cvterm_id);
			String statusMessage = null;
			logger.info("deleteregistryencounterattribute status:" + deleteStatus);								
			if (deleteStatus == 0) {
				statusMessage = "Deleted registry encounter attribute with event_cvterm_id=" + event_cvterm_id;
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, statusMessage, true);
			} else if (deleteStatus == 1) {
				statusMessage = "Did not find registry encounter attribute with event_cvterm_id=" + event_cvterm_id;
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, statusMessage, true);			
			} 						        
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.deleteregistryencounterattribute=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	@RequestMapping(value="/addregistryencounterattribute", method=RequestMethod.GET)
    public String addRegistryEncounterAttribute
    (
    		@RequestParam(value="encounter_key", defaultValue="0") Integer encounter_key,	
        	@RequestParam(value="assigner_id", defaultValue="0") Integer assigner_id,
        	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
        	@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id,        	    	    
        	@RequestParam(value="registrar_comment", defaultValue="") String registrar_comment,
        	@RequestParam(value="assignment_date", defaultValue="") String assignment_date        	        
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("addregistryencounterattribute ws params : " + 
				 	", encounter_key:" + encounter_key +
				 	", assigner_id:" + assigner_id +
				 	", registry_id:" + registry_id +					 
				 	", cvterm_id:" + cvterm_id +				 	
				 	", registrar_comment:" + registrar_comment +
				 	", assignment_date:" + assignment_date
					);		
		try {	
			RegistryEncounterCvtermDao recDao = (RegistryEncounterCvtermDao)context.getBean("registryEncounterCvtermDao");
			RegistryEncounterCvterm rec = new RegistryEncounterCvterm();
			rec.setEncounterKey(encounter_key);
			rec.setAssignerId(assigner_id);
			rec.setRegistryId(registry_id);
			rec.setCvtermId(cvterm_id);
			rec.setRegistrarComment(registrar_comment);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
			if ((assignment_date == null) || "".equals(assignment_date) || "null".equals(assignment_date)) {
				rec.setAssignmentDate(new Date());
			} else {
				rec.setAssignmentDate(sdf.parse(assignment_date));
			}											
			boolean insertStatus = recDao.insert(rec);
			if (insertStatus) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Added registry encounter attribute successfully!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Error adding registry encounter attribute!", true);
			}
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addregistryencounterattribute=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	*/

	private static final Logger logger = LoggerFactory.getLogger(RegistryEncounterCvtermController.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	private static ApplicationProps appProps = (ApplicationProps)context.getBean("appProps");
}
