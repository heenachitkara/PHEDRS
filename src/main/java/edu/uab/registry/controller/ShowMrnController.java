package edu.uab.registry.controller;

import java.util.List;

import edu.uab.registry.domain.*;
import edu.uab.registry.json.GetPatientDemographicsResult;
import edu.uab.registry.json.GetPatientDiagnosisResult;
import edu.uab.registry.util.Utilities;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uab.registry.dao.NlpPatientDao;
import edu.uab.registry.dao.RegistryEncounterAttributesDao;
import edu.uab.registry.dao.RegistryEncountersDao;
import edu.uab.registry.dao.RegistryPatientAttributesDao;
import edu.uab.registry.dao.RegistryPatientDemographicsDao;
import edu.uab.registry.json.RegistryNlpPatientInfoView;
import edu.uab.registry.json.GetRegistryEncountersResult;
import edu.uab.registry.util.ApplicationProps;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;

@RestController
public class ShowMrnController 
{
	@RequestMapping(value="/getpatientdemographics", method=RequestMethod.GET)
	public String getpatientdemographics
	(
		@RequestParam(value="mrn", defaultValue="0") String mrn_,
		@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getpatientdemographics parameters: mrn = " + mrn_ + ", registry_id = " + registryID_);

		GetPatientDemographicsResult result = new GetPatientDemographicsResult();
		try {
			// Validate the MRN.
			if (StringUtils.isNullOrEmpty(mrn_)) { 
				throw new Exception("Invalid MRN (empty)"); 
			} else {
				mrn_ = mrn_.trim();
			}
			
			// Validate the registry ID
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }


			// Retrieve the patient demographics and registry patient.
			RegistryPatientDemographicsDao rpdDao = (RegistryPatientDemographicsDao) context.getBean("registryPatientDemographicsDao");			

			// Initialize the result objects before passing them to the bean.
			RegistryPatientDemographics demographics = new RegistryPatientDemographics();
			GenericRegistryPatient registryPatient = new GenericRegistryPatient();

			// Retrieve demographics and registry patient.
			rpdDao.get(demographics, mrn_, registryID_, registryPatient);

            if (StringUtils.isNullOrEmpty(demographics.getMrn())) {
                result.setWebServiceStatus("Invalid demographics data", false);
            } else if (registryPatient.getRegistry_patient_id() < 1) {
				result.setWebServiceStatus("Invalid registry patient", false);
			} else {
                // Populate the result.
				result.setPatientDemographics(demographics);
				result.setRegistryPatient(registryPatient);
                result.setWebServiceStatus("", true);
            }
		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(),false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getpatientdemographics="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");

		logger.info(result.toJSON());

		return result.toJSON();
	}


	@RequestMapping(value="/getdocuments", method=RequestMethod.GET)
	public String getdocuments
	(
		@RequestParam(value="mrn", defaultValue="0") String mrn
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getdocuments ws params : mrn="+mrn);
		RegistryNlpPatientInfo registryNlpPatientInfo = new RegistryNlpPatientInfo();
		String registryNlpPatientInfoJsonString =  "";

		try {
			// Validate the MRN.
			if (StringUtils.isNullOrEmpty(mrn)) { 
				throw new Exception("Invalid MRN (empty)"); 
			} else {
				mrn = mrn.trim();
			}
			
			NlpPatientDao nlpDao = (NlpPatientDao) context.getBean("nlpPatientDao");
			List<NlpPatientHitsDocs> nlpPatients = nlpDao.getNlpPatients(mrn);
			registryNlpPatientInfo.setNlp_document_list(nlpPatients);
			
			registryNlpPatientInfoJsonString = RegistryNlpPatientInfoView.createRegistryNlpPatientInfoToJsonString(registryNlpPatientInfo, true, Constants.EMPTY, true);
			logger.debug("getdocuments JSON is\n"+registryNlpPatientInfoJsonString);
			if ((nlpPatients != null) && (nlpPatients.size() > 0)) {
				logger.info("Number of NLP Patients :" + nlpPatients.size());
			}			
		} catch (Throwable th) {
			th.printStackTrace();
			registryNlpPatientInfoJsonString = RegistryNlpPatientInfoView.createRegistryNlpPatientInfoToJsonString(registryNlpPatientInfo, false, th.getMessage(), true);
		}		
		logger.info("Log.ElapsedTime.ws.getdocuments="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return registryNlpPatientInfoJsonString;
	}
	
	@RequestMapping(value="/getpatientattributes", method=RequestMethod.GET)
	public String getpatientattributes
	(
		@RequestParam(value="mrn", defaultValue="0") String mrn_,
		@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getpatientattributes ws params : mrn="+mrn_+", registry_id="+registryID_);
		RegistryNlpPatientInfo registryNlpPatientInfo = new RegistryNlpPatientInfo();
		String json =  "";

		try {
			// Validate the MRN.
			if (StringUtils.isNullOrEmpty(mrn_)) { 
				throw new Exception("Invalid MRN (empty)"); 
			} else {
				mrn_ = mrn_.trim();
			}
			
			// Validate the registry ID
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
						
			RegistryPatientAttributesDao rpeaDao = (RegistryPatientAttributesDao) context.getBean("registryPatientAttributesDao");
			
			List <RegistryPatientAttribute> registryPatientAttrList = rpeaDao.getRegistryPatientAttributes(mrn_, registryID_);											
			registryNlpPatientInfo.setRegistry_all_patient_attribute_list(registryPatientAttrList);			
			json = RegistryNlpPatientInfoView.createRegistryNlpPatientInfoToJsonString(registryNlpPatientInfo, true, Constants.EMPTY, true);
			logger.debug("getpatientattributes JSON is\n"+json);
			if ((registryPatientAttrList != null) && (registryPatientAttrList.size() > 0)) {
				logger.info("Number of Registry Patient Attributes :" + registryPatientAttrList.size());
			}			
		} catch (Throwable th) {
			th.printStackTrace();
			json = RegistryNlpPatientInfoView.createRegistryNlpPatientInfoToJsonString(registryNlpPatientInfo, false, th.getMessage(), true);
		}		
		
		logger.info("Log.ElapsedTime.ws.getpatientattributes="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
		return json;
	}
	
	@RequestMapping(value="/getencounters", method=RequestMethod.GET)
	public String getencounters
	(
		@RequestParam(value="mrn", defaultValue="0") String mrn_,
		@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getencounters ws params : mrn="+mrn_+", registry_id="+registryID_);

		List<RegistryEncounter> encounters = null;
		GetRegistryEncountersResult result = new GetRegistryEncountersResult();

		try {
			// Validate the MRN and registry ID.
			if (StringUtils.isNullOrEmpty(mrn_)) {
				throw new Exception("Invalid MRN");
			} else {
				mrn_ = mrn_.trim();
			}
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }

			RegistryEncountersDao reDao = (RegistryEncountersDao) context.getBean("registryEncountersDao");	

			// Get the patient's encounters and any associated attributes.
			encounters = reDao.get(mrn_, registryID_);

			Integer resultCount = 0;
			if (encounters != null) { resultCount = encounters.size(); }

			result.setEncounters(encounters);
			result.setWebServiceStatus(resultCount.toString() + " results", true);

		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getencounters="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}


	@RequestMapping(value="/getpatientdiagnosis", method=RequestMethod.GET)
	public String getpatientdiagnosis
	(
		@RequestParam(value="mrn", defaultValue="0") String mrn_,
		@RequestParam(value="type", defaultValue="") String type_,
		@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getpatientdiagnosis ws params : mrn="+mrn_ + ", type="+type_ + ", registry_id="+registryID_);

		GetPatientDiagnosisResult result = new GetPatientDiagnosisResult();

		try {
			// Validate the MRN.
			if (StringUtils.isNullOrEmpty(mrn_)) { 
				throw new Exception("Invalid MRN (empty)"); 
			} else {
				mrn_ = mrn_.trim();
			}			
			
			RegistryEncounterAttributesDao rpeaDao = (RegistryEncounterAttributesDao) context.getBean("registryEncounterAttributesDao");
			List<RegistryPatientCode> diagnoses = rpeaDao.getPatientDiagnoses(mrn_,type_,registryID_);

			Integer resultCount = 0;
			if (diagnoses != null) { resultCount = diagnoses.size(); }

			result.setDiagnoses(diagnoses);
			result.setWebServiceStatus(resultCount.toString() + " results", true);

		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}		

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getpatientdiagnosis="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
		return result.toJSON();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ShowMrnController.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	private static ApplicationProps appProps = (ApplicationProps)context.getBean("appProps");
}
