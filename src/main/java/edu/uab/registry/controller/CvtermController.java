package edu.uab.registry.controller;

import java.util.Arrays;
import java.util.List;

import edu.uab.registry.dao.CvMetaDataListDao;
import edu.uab.registry.dao.CvTermsDao;
import edu.uab.registry.dao.CvtermNameDao;
import edu.uab.registry.dao.EncounterCVDao;
import edu.uab.registry.dao.GetRoleNamesDao;
import edu.uab.registry.dao.NlpPatientDao;
import edu.uab.registry.dao.PatientCVDao;
//import edu.uab.registry.dao.CvMetaDataListDao;
import edu.uab.registry.dao.RegistryEncountersDao;
import edu.uab.registry.domain.ControlledVocabulary;
import edu.uab.registry.domain.CvMetaDataList;
import edu.uab.registry.domain.CvtermNameList;
import edu.uab.registry.domain.EncounterAttributeCVInfo;
import edu.uab.registry.domain.EncounterCVList;
import edu.uab.registry.domain.GetRoleNamesInfo;
import edu.uab.registry.domain.GetRoleNamesList;
import edu.uab.registry.domain.CvTerms;
import edu.uab.registry.json.GetRegistryTermsResult;
import edu.uab.registry.json.GetRoleNamesInfoView;
import edu.uab.registry.json.PatientAttributeCVInfoView;
import edu.uab.registry.json.RegistryCvMetaDataInfoView;
import edu.uab.registry.json.RegistryNlpPatientInfoView;

import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import edu.uab.registry.domain.Cvterm;
import edu.uab.registry.domain.CvTermsList;
import edu.uab.registry.domain.CvtermNameInfo;
import edu.uab.registry.domain.NlpPatientHitsDocs;
import edu.uab.registry.domain.PatientAttributeCVInfo;
import edu.uab.registry.domain.PatientCVList;
import edu.uab.registry.domain.RegistryCvMetaDataInfo;
//import edu.uab.registry.domain.CvMetaDataList;
import edu.uab.registry.json.CvTermsView;
import edu.uab.registry.json.CvtermNameInfoView;
import edu.uab.registry.json.EncounterAttributeCVInfoView;
//import edu.uab.registry.json.GetCvMetaDataResult;
import edu.uab.registry.json.GenericOrmStatusView;
import edu.uab.registry.json.GetCvMetaDataResult;
import edu.uab.registry.orm.RegistryCvMetaData;
import edu.uab.registry.orm.Cv;
import edu.uab.registry.orm.CvMetaData;
import edu.uab.registry.orm.Cvterm;
import edu.uab.registry.orm.dao.CvDao;
import edu.uab.registry.orm.dao.CvtermDao;
import edu.uab.registry.orm.dao.RegistryCvMetaDataDao;
import edu.uab.registry.orm.dao.RegistryPatientDao;
import edu.uab.registry.util.ApplicationProps;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;
import edu.uab.registry.util.WebServiceResult;

@RestController
public class CvtermController 
{
	@RequestMapping(value="/addcvterm", method=RequestMethod.GET)
    public String addCvterm
    (
    	@RequestParam(value="name", defaultValue="") String name,
    	@RequestParam(value="is_obsolete", defaultValue="0") Integer is_obsolete,
    	@RequestParam(value="is_relationshiptype", defaultValue="0") Integer is_relationshiptype,
    	@RequestParam(value="cv_id", defaultValue="0") Integer cv_id,
    	@RequestParam(value="dbxref_id", defaultValue="0") Integer dbxref_id,
    	@RequestParam(value="definition", defaultValue="Default Definition") String definition
    	
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("addcvterm ws params : " + 
					"name="+name+
					",is_obsolete="+is_obsolete+
					",is_relationshiptype="+is_relationshiptype+
					",cv_id="+cv_id+
					",dbxref_id="+dbxref_id+
					",definition="+definition
					);
				
		boolean addStatus = true;		
		try {
			CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");				
			
			Cvterm cvterm = new Cvterm();						
			cvterm.setName(name);	
			cvterm.setIsObsolete(is_obsolete);
			cvterm.setIsRelationshipType(is_relationshiptype);
			cvterm.setCvId(cv_id);
			
			if (dbxref_id == 0) {				
				cvterm.setDbxrefId(null);
			} else {
				cvterm.setDbxrefId(dbxref_id);
			}
			cvterm.setDefinition(definition);
			
			addStatus = cvtermDao.insert(cvterm);
			logger.info("addcvterm status:" + addStatus);
			if (addStatus) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Inserted CVTERM Record!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Insertion of CVTERM Record Problem!", true);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addcvterm="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	@RequestMapping(value="/add_cv_metadata", method=RequestMethod.GET)
    public String addCvMetaData
    (
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
    	@RequestParam(value="attribute_cvterm_id", defaultValue="0") Integer attributeCvtermID_,
    	@RequestParam(value="value_display_name", defaultValue="") String valueDisplayName_,
    	@RequestParam(value="value_cv_id", defaultValue="0") Integer valueCvId_
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
		logger.info("add_cv_metadata Web Service Parameters : " + 
				"registry_id="+registryID_+
				",attribute_cvterm_id="+attributeCvtermID_+
				",value_display_name="+valueDisplayName_+
				",value_cv_id="+valueCvId_
				
				);
		boolean addStatus = true;		
		try {
			CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");				
			
			RegistryCvMetaData cvmetadata = new RegistryCvMetaData();
						
			cvmetadata.setRegistryId(registryID_);
			cvmetadata.setAttributeCvtermId(attributeCvtermID_);
			cvmetadata.setValueDisplayName(valueDisplayName_);
			cvmetadata.setValueCvId(valueCvId_);
			
						
			addStatus = cvtermDao.insert(cvmetadata);
			logger.info("addcv status:" + addStatus);
			if (addStatus) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Inserted CV METADATA Record!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Insertion of CV METADATa Record Problem!", true);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addcvterm="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	
	
	@RequestMapping(value="/addcv", method=RequestMethod.GET)
    public String addCv
    (
    	@RequestParam(value="name", defaultValue="") String name_,
    	@RequestParam(value="definition", defaultValue="") String definition_,
    	@RequestParam(value="owner_name", defaultValue="") String owner_name_
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
		logger.info("addcv Web Service Parameters : " + 
				"name="+name_+
				",definition="+definition_+
				",owner_name="+owner_name_
				
				);
		boolean addStatus = true;		
		try {
						
			CvDao cvDao = (CvDao)context.getBean("cvDao");
			Cv cvInsert = new Cv();
			cvInsert.setName(name_);
			cvInsert.setDefinition(definition_);
			cvInsert.setOwnerName(owner_name_);
			
						
			addStatus = cvDao.insert(cvInsert);
			logger.info("addcv status:" + addStatus);
			if (addStatus) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Inserted a record in CV Table!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Unable to Insert CV Record in CV Table!", true);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addcvterm="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	
	@RequestMapping(value="/updatecvterm", method=RequestMethod.GET)
    public String updateCvterm
    (
    		@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id,
    		@RequestParam(value="name", defaultValue="") String name,
        	@RequestParam(value="is_obsolete", defaultValue="0") Integer is_obsolete,
        	@RequestParam(value="is_relationshiptype", defaultValue="0") Integer is_relationshiptype,
        	@RequestParam(value="cv_id", defaultValue="0") Integer cv_id,
        	@RequestParam(value="dbxref_id", defaultValue="0") Integer dbxref_id,
        	@RequestParam(value="definition", defaultValue="") String definition
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("updatecvterm ws params : " + 
				"cvterm_id="+cvterm_id+
				",name="+name+
				",is_obsolete="+is_obsolete+
				",is_relationshiptype="+is_relationshiptype+
				",cv_id="+cv_id+
				",dbxref_id="+dbxref_id+
				",definition="+definition
				);
		boolean updateStatus = false;		
		try {
			CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");
			Cvterm cvterm = null;			
			List<Cvterm> cvList = cvtermDao.findByCvtermId(cvterm_id);
			if ((cvList != null) && (!cvList.isEmpty())) {
				cvterm = cvList.get(0);
				
				cvterm.setName(name);	
				cvterm.setIsObsolete(is_obsolete);
				cvterm.setIsRelationshipType(is_relationshiptype);
				cvterm.setCvId(cv_id);
				if (dbxref_id == 0) {				
					cvterm.setDbxrefId(null);
				} else {
					cvterm.setDbxrefId(dbxref_id);
				}
				cvterm.setDefinition(definition);
				
				updateStatus = cvtermDao.insertOrUpdate(cvterm);
				logger.info("updatecvterm status:" + updateStatus);
				if (updateStatus) {
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Updated CVTERM Record!", true);
				} else {
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Update of CVTERM Record Problem!", true);
				}
			}						
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.updatecvterm="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	@RequestMapping(value="/getcvterms", method=RequestMethod.GET)
	public String getCvTerms
	(
		@RequestParam(value="cvs", defaultValue="") String cvs
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String cvTermsJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getcvterms ws params : " + 
				"cvs="+cvs
				);
		CvTermsList cvtermList = new CvTermsList();
		try {
			CvTermsDao cvTermsDao = (CvTermsDao) context.getBean("cvTermsDao");						 					
			List<CvTerms> cvList = cvTermsDao.getCvterms(cvs);						
			cvtermList.setCvtermList(cvList);
			if ((cvList!= null) && (cvList.size()>0)) {
				logger.info("Number of cvterms :" + cvList.size());
			}
			cvTermsJsonString = CvTermsView.cvtermsToJsonString(cvtermList, true, "", true);
			logger.debug("CvTerms JSON is\n"+cvTermsJsonString);
		} catch(Throwable th) {
			th.printStackTrace();
			cvTermsJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.getcvterms="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return cvTermsJsonString;
	}
	
	
	
	@RequestMapping(value="/get_cv_metadata", method=RequestMethod.GET)
	public String getCvMetaData
	(
			@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
			@RequestParam(value="attribute_cvterm_id", defaultValue="0") Integer attributeCvtermID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("get_cv_metadata parameters: Attribute CV Term ID = " + attributeCvtermID_ + ", registry_id = " + registryID_);
		
		
		
		
		RegistryCvMetaDataInfo registryCvMetaDataInfo = new RegistryCvMetaDataInfo();
		String registryCvMetaDataInfoJsonString =  "";
		
		
		try {
			
			CvMetaDataListDao cvMetaDao = (CvMetaDataListDao) context.getBean("cvMetaDataListDao");
			List<CvMetaDataList> cvMetaDataLists = cvMetaDao.getCvMetaData(registryID_, attributeCvtermID_);
			registryCvMetaDataInfo.setCv_metadata_list(cvMetaDataLists);
			
			registryCvMetaDataInfoJsonString = RegistryCvMetaDataInfoView.createRegistryCvMetaDataInfoToJsonString(registryCvMetaDataInfo, true, Constants.EMPTY, true);
			logger.debug("get_cv_metadata JSON is\n"+registryCvMetaDataInfoJsonString);
			if ((cvMetaDataLists != null) && (cvMetaDataLists.size() > 0)) {
				logger.info("Number of CV MetaData Records :" + cvMetaDataLists.size());
			}			
		} catch (Throwable th) {
			th.printStackTrace();
			registryCvMetaDataInfoJsonString = RegistryCvMetaDataInfoView.createRegistryCvMetaDataInfoToJsonString(registryCvMetaDataInfo, false, th.getMessage(), true);
		}		
		
		return registryCvMetaDataInfoJsonString;
		
		
		
		
	}
	
	@RequestMapping(value="/getPatientAttributeCV", method=RequestMethod.GET)
	public String getPatientAttributeCV
	(
			@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
			
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getPatientAttributeCV parameters:registry_id = " + registryID_);
		
		
		
		
		PatientAttributeCVInfo patientCVDataInfo = new PatientAttributeCVInfo();
		String patientCVInfoJsonString =  "";
		
		
		try {
			
			PatientCVDao patientCvDao = (PatientCVDao) context.getBean("patientCVListDao");
			List<PatientCVList> patientCVDataLists = patientCvDao.getPatientAttributeCV(registryID_);
			patientCVDataInfo.setPatientCVList(patientCVDataLists);
			
			patientCVInfoJsonString = PatientAttributeCVInfoView.createPatientAttributeCvInfoToJsonString(patientCVDataInfo, true, Constants.EMPTY, true);
			logger.debug("getPatientAttributeCV JSON is\n"+patientCVInfoJsonString);
			if ((patientCVDataLists != null) && (patientCVDataLists.size() > 0)) {
				logger.info("Number of Patient Attcibute CV Records :" + patientCVDataLists.size());
			}			
		} catch (Throwable th) {
			th.printStackTrace();
			patientCVInfoJsonString = PatientAttributeCVInfoView.createPatientAttributeCvInfoToJsonString(patientCVDataInfo, false, th.getMessage(), true);
		}		
		
		return patientCVInfoJsonString;
		
	}
	
	
	@RequestMapping(value="/getEncounterAttributeCV", method=RequestMethod.GET)
	public String getEncounterAttributeCV
	(
			@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
			
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getEncounterAttributeCV parameters:registry_id = " + registryID_);
		
		EncounterAttributeCVInfo encounterCVDataInfo = new EncounterAttributeCVInfo();
		String encounterCVInfoJsonString =  "";
		
		
		try {
			
			EncounterCVDao encounterCvDao = (EncounterCVDao) context.getBean("encounterCVListDao");
			List<EncounterCVList> encounterCVDataLists = encounterCvDao.getEncounterAttributeCV(registryID_);
			encounterCVDataInfo.setEncounterCVList(encounterCVDataLists);
			
			encounterCVInfoJsonString = EncounterAttributeCVInfoView.createEncounterAttributeCvInfoToJsonString(encounterCVDataInfo, true, Constants.EMPTY, true);
			logger.debug("getEncounterAttributeCV JSON is\n"+encounterCVInfoJsonString);
			if ((encounterCVDataLists != null) && (encounterCVDataLists.size() > 0)) {
				logger.info("Number of Encounter Attcibute CV Records :" + encounterCVDataLists.size());
			}			
		} catch (Throwable th) {
			th.printStackTrace();
			encounterCVInfoJsonString = EncounterAttributeCVInfoView.createEncounterAttributeCvInfoToJsonString(encounterCVDataInfo, false, th.getMessage(), true);
		}		
		
		return encounterCVInfoJsonString;
		
	}
	
	
	@RequestMapping(value="/get_cvterm_name", method=RequestMethod.GET)
	public String getCvTermName
	(
			@RequestParam(value="cvterm_id", defaultValue="0") Integer cvtermID_
			
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("get_cvterm_name parameters: Registry ID = " + cvtermID_ );
		
		CvtermNameInfo cvTermNameInfo = new CvtermNameInfo();
		String cvTermNameInfoJsonString =  "";
		
		try {
			// Validate the input parameters.
			if (cvtermID_ < 1 ) {throw new Exception("Invalid Cvterm ID parameter"); }
			CvtermNameDao cvNameDao = (CvtermNameDao) context.getBean("cvTermNameDao");
			List<CvtermNameList> cvNameLists = cvNameDao.getCvTermName(cvtermID_);
			cvTermNameInfo.setCvTermNameList(cvNameLists);
			
			cvTermNameInfoJsonString = CvtermNameInfoView.createCvtermNameInfoToJsonString(cvTermNameInfo, true, Constants.EMPTY, true);
			logger.debug("get_cvterm_name JSON is\n"+cvTermNameInfoJsonString);
			if ((cvNameLists != null) && (cvNameLists.size() > 0)) {
				logger.info("Number of CV MetaData Records :" + cvNameLists.size());
			}			
		} catch (Throwable th) {
			th.printStackTrace();
			cvTermNameInfoJsonString = CvtermNameInfoView.createCvtermNameInfoToJsonString(cvTermNameInfo, false, th.getMessage(), true);
		}		
		
		return cvTermNameInfoJsonString;
	}
	
	
	@RequestMapping(value="/get_role_names", method=RequestMethod.GET)
	public String getRoleNames
	(
			@RequestParam(value="cv_id", defaultValue="22") Integer cv_id
			
	)
	{   StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("get_role_names parameter: CV ID = " + cv_id );
		GetRoleNamesInfo roleNamesInfo = new GetRoleNamesInfo();
		String roleNamesInfoJsonString =  "";
		try {
			GetRoleNamesDao roleNamesDao = (GetRoleNamesDao) context.getBean("roleNamesDao");
			List<GetRoleNamesList> roleNamesList = roleNamesDao.getRoleNames(cv_id);
			roleNamesInfo.setRoleNamesList(roleNamesList);
			roleNamesInfoJsonString = GetRoleNamesInfoView.createRoleNamesInfoToJsonString(roleNamesInfo, true, Constants.EMPTY, true);
			logger.debug("get_cvterm_name JSON is\n"+roleNamesInfoJsonString);
			if ((roleNamesList != null) && (roleNamesList.size() > 0)) {
				logger.info("Number of role related records :" + roleNamesList.size());
			}			
		} catch (Throwable th) {
			th.printStackTrace();
			roleNamesInfoJsonString = GetRoleNamesInfoView.createRoleNamesInfoToJsonString(roleNamesInfo, false, th.getMessage(), true);
		}		
		return roleNamesInfoJsonString;
	}


	@RequestMapping(value="/get_registry_terms", method=RequestMethod.GET)
	public String getRegistryTerms
	(
		@RequestParam(value="registry_id", defaultValue="0") int registryID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("get_registry_terms parameters: registry_id = " + registryID_);

		GetRegistryTermsResult result = new GetRegistryTermsResult();

		try {
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }

			CvTermsDao cvTermsDao = (CvTermsDao)context.getBean("cvTermsDao");
			List<ControlledVocabulary> cvs = cvTermsDao.getRegistryCVs(registryID_);

			Integer resultCount = 0;
			if (cvs != null) { resultCount = cvs.size(); }

			result.setCvs(cvs);
			result.setWebServiceStatus(resultCount.toString() + " results", true);

		} catch(Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.get_registry_terms="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}


	private static final Logger logger = LoggerFactory.getLogger(CvtermController.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	private static ApplicationProps appProps = (ApplicationProps)context.getBean("appProps");
}


/*Original One package edu.uab.registry.controller;

import java.util.List;

import edu.uab.registry.dao.CvTermsDao;
import edu.uab.registry.domain.ControlledVocabulary;
import edu.uab.registry.domain.CvTerms;
import edu.uab.registry.json.GetRegistryTermsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import edu.uab.registry.domain.Cvterm;
import edu.uab.registry.domain.CvTermsList;
import edu.uab.registry.json.CvTermsView;
import edu.uab.registry.json.GenericOrmStatusView;
import edu.uab.registry.orm.CvMetaData;
import edu.uab.registry.orm.Cvterm;
import edu.uab.registry.orm.dao.CvtermDao;
import edu.uab.registry.util.ApplicationProps;
import edu.uab.registry.util.StopWatch;

@RestController
public class CvtermController 
{
	@RequestMapping(value="/addcvterm", method=RequestMethod.GET)
    public String addCvterm
    (
    	@RequestParam(value="name", defaultValue="") String name,
    	@RequestParam(value="is_obsolete", defaultValue="0") Integer is_obsolete,
    	@RequestParam(value="is_relationshiptype", defaultValue="0") Integer is_relationshiptype,
    	@RequestParam(value="cv_id", defaultValue="0") Integer cv_id,
    	@RequestParam(value="dbxref_id", defaultValue="0") Integer dbxref_id,
    	@RequestParam(value="definition", defaultValue="") String definition
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("addcvterm ws params : " + 
					"name="+name+
					",is_obsolete="+is_obsolete+
					",is_relationshiptype="+is_relationshiptype+
					",cv_id="+cv_id+
					",dbxref_id="+dbxref_id+
					",definition="+definition
					);
		boolean addStatus = true;		
		try {
			CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");				
			
			Cvterm cvterm = new Cvterm();						
			cvterm.setName(name);	
			cvterm.setIsObsolete(is_obsolete);
			cvterm.setIsRelationshipType(is_relationshiptype);
			cvterm.setCvId(cv_id);
			if (dbxref_id == 0) {				
				cvterm.setDbxrefId(null);
			} else {
				cvterm.setDbxrefId(dbxref_id);
			}
			cvterm.setDefinition(definition);
			
			addStatus = cvtermDao.insert(cvterm);
			logger.info("addcvterm status:" + addStatus);
			if (addStatus) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Inserted CVTERM Record!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Insertion of CVTERM Record Problem!", true);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addcvterm="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	@RequestMapping(value="/addcv", method=RequestMethod.GET)
    public String addCv
    (
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
    	@RequestParam(value="attribute_cvterm_id", defaultValue="0") Integer attributeCvtermID_,
    	@RequestParam(value="value_display_name", defaultValue="") String valueDisplayName_,
    	@RequestParam(value="value_cv_id", defaultValue="0") Integer valueCvId_
    	
    	
    	
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
		logger.info("addcv Web Service Parameters : " + 
				"registry_id="+registryID_+
				",attribute_cvterm_id="+attributeCvtermID_+
				",value_display_name="+valueDisplayName_+
				",value_cv_id="+valueCvId_
				
				);
		boolean addStatus = true;		
		try {
			CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");				
			
			
			CvMetaData cvmetadata = new CvMetaData();
			
			cvmetadata.setRegistryId(registryID_);
			cvmetadata.setAttributeCvtermId(attributeCvtermID_);
			cvmetadata.setValueDisplayName(valueDisplayName_);
			cvmetadata.setValueCvId(valueCvId_);
			
						
			addStatus = cvtermDao.insert(cvmetadata);
			logger.info("addcv status:" + addStatus);
			if (addStatus) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Inserted CV METADATA Record!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Insertion of CV METADATa Record Problem!", true);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addcvterm="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	@RequestMapping(value="/updatecvterm", method=RequestMethod.GET)
    public String updateCvterm
    (
    		@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id,
    		@RequestParam(value="name", defaultValue="") String name,
        	@RequestParam(value="is_obsolete", defaultValue="0") Integer is_obsolete,
        	@RequestParam(value="is_relationshiptype", defaultValue="0") Integer is_relationshiptype,
        	@RequestParam(value="cv_id", defaultValue="0") Integer cv_id,
        	@RequestParam(value="dbxref_id", defaultValue="0") Integer dbxref_id,
        	@RequestParam(value="definition", defaultValue="") String definition
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("updatecvterm ws params : " + 
				"cvterm_id="+cvterm_id+
				",name="+name+
				",is_obsolete="+is_obsolete+
				",is_relationshiptype="+is_relationshiptype+
				",cv_id="+cv_id+
				",dbxref_id="+dbxref_id+
				",definition="+definition
				);
		boolean updateStatus = false;		
		try {
			CvtermDao cvtermDao = (CvtermDao)context.getBean("cvtermDao");
			Cvterm cvterm = null;			
			List<Cvterm> cvList = cvtermDao.findByCvtermId(cvterm_id);
			if ((cvList != null) && (!cvList.isEmpty())) {
				cvterm = cvList.get(0);
				
				cvterm.setName(name);	
				cvterm.setIsObsolete(is_obsolete);
				cvterm.setIsRelationshipType(is_relationshiptype);
				cvterm.setCvId(cv_id);
				if (dbxref_id == 0) {				
					cvterm.setDbxrefId(null);
				} else {
					cvterm.setDbxrefId(dbxref_id);
				}
				cvterm.setDefinition(definition);
				
				updateStatus = cvtermDao.insertOrUpdate(cvterm);
				logger.info("updatecvterm status:" + updateStatus);
				if (updateStatus) {
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Updated CVTERM Record!", true);
				} else {
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Update of CVTERM Record Problem!", true);
				}
			}						
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.updatecvterm="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	@RequestMapping(value="/getcvterms", method=RequestMethod.GET)
	public String getCvTerms
	(
		@RequestParam(value="cvs", defaultValue="") String cvs
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String cvTermsJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getcvterms ws params : " + 
				"cvs="+cvs
				);
		CvTermsList cvtermList = new CvTermsList();
		try {
			CvTermsDao cvTermsDao = (CvTermsDao) context.getBean("cvTermsDao");						 					
			List<CvTerms> cvList = cvTermsDao.getCvterms(cvs);						
			cvtermList.setCvtermList(cvList);
			if ((cvList!= null) && (cvList.size()>0)) {
				logger.info("Number of cvterms :" + cvList.size());
			}
			cvTermsJsonString = CvTermsView.cvtermsToJsonString(cvtermList, true, "", true);
			logger.debug("CvTerms JSON is\n"+cvTermsJsonString);
		} catch(Throwable th) {
			th.printStackTrace();
			cvTermsJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.getcvterms="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return cvTermsJsonString;
	}


	@RequestMapping(value="/get_registry_terms", method=RequestMethod.GET)
	public String getRegistryTerms
	(
		@RequestParam(value="registry_id", defaultValue="0") int registryID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("get_registry_terms parameters: registry_id = " + registryID_);

		GetRegistryTermsResult result = new GetRegistryTermsResult();

		try {
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }

			CvTermsDao cvTermsDao = (CvTermsDao)context.getBean("cvTermsDao");
			List<ControlledVocabulary> cvs = cvTermsDao.getRegistryCVs(registryID_);

			Integer resultCount = 0;
			if (cvs != null) { resultCount = cvs.size(); }

			result.setCvs(cvs);
			result.setWebServiceStatus(resultCount.toString() + " results", true);

		} catch(Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.get_registry_terms="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}


	private static final Logger logger = LoggerFactory.getLogger(CvtermController.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	private static ApplicationProps appProps = (ApplicationProps)context.getBean("appProps");
}
*/