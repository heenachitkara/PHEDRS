package edu.uab.registry.controller;

import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.json.SearchByCvName;
import edu.uab.registry.json.SearchByMrnOrNameResult;
import edu.uab.registry.json.SearchCvNameInfoView;
import edu.uab.registry.json.SearchResult;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uab.registry.dao.SearchCvNameAttributeConfigDao;
import edu.uab.registry.dao.SearchRegistryPatientDao;
import edu.uab.registry.dao.impl.GenericRegistryPatientsTabDaoImpl;
import edu.uab.registry.domain.GenericRegistryPatientList;
import edu.uab.registry.domain.SearchCvNameInfo;
import edu.uab.registry.domain.SearchCvNameList;
import edu.uab.registry.json.SearchRegistryPatientView;
import edu.uab.registry.util.ApplicationProps;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;

import java.util.List;

@RestController
public class SearchController 
{

	@RequestMapping(value="/search_by_mrn_or_name", method=RequestMethod.GET)
	public String search_by_mrn_or_name
			(
					@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
					@RequestParam(value="search_text", defaultValue="") String searchText_
			)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("search_by_mrn_or_name parameters: " +
				"registry_id="+registryID_+
				",search_text="+searchText_
		);

		List<GenericRegistryPatient> registryPatients = null;
		SearchByMrnOrNameResult result = new SearchByMrnOrNameResult();

		try {
			// Validate the registry ID
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			if (StringUtils.isNullOrEmpty(searchText_)) { throw new Exception("Invalid search text (empty)"); }

			SearchRegistryPatientDao srpDao = (SearchRegistryPatientDao) context.getBean("searchRegistryPatientDao");
			registryPatients = srpDao.searchForMrnOrName(registryID_, searchText_);

			Integer resultCount = 0;
			if (registryPatients != null) { resultCount = registryPatients.size(); }

			result.setRegistryPatients(registryPatients);
			result.setWebServiceStatus( resultCount.toString() + " results", true);

		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.search_by_mrn_or_name="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}


	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String search
	(
		@RequestParam(value="assigned_by", defaultValue="") String assignedBy_,
		@RequestParam(value="assignment_date_from", defaultValue="") String assignmentDateFrom_,
		@RequestParam(value="assignment_date_to", defaultValue="") String assignmentDateTo_,
		@RequestParam(value="detection_events", defaultValue="") String detectionEvents_,
		@RequestParam(value="encounter_attributes", defaultValue="") String encounterAttributes_,
		@RequestParam(value="encounter_date_from", defaultValue="") String encounterDateFrom_,
		@RequestParam(value="encounter_date_to", defaultValue="") String encounterDateTo_,
		@RequestParam(value="first_name", defaultValue="") String firstName_,
		@RequestParam(value="last_name", defaultValue="") String lastName_,
		@RequestParam(value="last_review_from", defaultValue="") String lastReviewFrom_,
		@RequestParam(value="last_review_to", defaultValue="") String lastReviewTo_,
		@RequestParam(value="mrn", defaultValue="") String mrn_,
		@RequestParam(value="patient_attributes", defaultValue="") String patientAttributes_,
		@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
		@RequestParam(value="registry_status_id", defaultValue="") String registryStatusID_,
		@RequestParam(value="reviewed_by", defaultValue="") String reviewedBy_,
		@RequestParam(value="ssn", defaultValue="") String ssn_,
		@RequestParam(value="workflow_status_id", defaultValue="") String workflowStatusID_
		)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("search parameters: " +
			"assigned_by="+assignedBy_+
			"assignment_date_from="+assignmentDateFrom_+
			"assignment_date_to="+assignmentDateTo_+
			"detection_events="+detectionEvents_+
			"encounter_attributes="+encounterAttributes_+
			"encounter_date_from="+encounterDateFrom_+
			"encounter_date_to="+encounterDateTo_+
			"first_name="+firstName_+
			"last_name="+lastName_+
			"last_review_from="+lastReviewFrom_+
			"last_review_to="+lastReviewTo_+
			"mrn="+mrn_+
			"patient_attributes="+patientAttributes_+
			"registry_id="+registryID_+
			"registry_status_id="+registryStatusID_+
			"reviewed_by="+reviewedBy_+
			"ssn="+ssn_+
			"workflow_status_id="+workflowStatusID_
			);

		List<GenericRegistryPatient> registryPatients = null;
		SearchResult result = new SearchResult();

		try {
			// Validate the registry ID
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			//if (StringUtils.isNullOrEmpty(mrn_)) { throw new Exception("Invalid MRN Entered in the Search Textbox!");}

			SearchRegistryPatientDao srpDao = (SearchRegistryPatientDao) context.getBean("searchRegistryPatientDao");
			registryPatients = srpDao.search(
					assignedBy_,
					assignmentDateFrom_,
					assignmentDateTo_,
					detectionEvents_,
					encounterAttributes_,
					encounterDateFrom_,
					encounterDateTo_,
					firstName_,
					lastName_,
					lastReviewFrom_,
					lastReviewTo_,
					mrn_,
					patientAttributes_,
					registryID_,
					registryStatusID_,
					reviewedBy_,
					ssn_,
					workflowStatusID_
			);

			Integer resultCount = 0;
			if (registryPatients != null) {resultCount = registryPatients.size(); }

			result.setRegistryPatients(registryPatients);
			result.setWebServiceStatus(resultCount.toString() + " results", true);

		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.search="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
	}
	
	
	
	/*DELETE IT FROM THE CODE ; NOt USED ANYWHERE */
	@RequestMapping(value = "/search_attribute_config_cvname", method = RequestMethod.GET)
	public String searchAttributeConfigCvName
	(
			@RequestParam(value = "name", defaultValue = "ICD10") String cvName_
	)

	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("search_attribute_config_cvnames ws params : CV Name=" + cvName_);

		SearchCvNameInfo searchInfo = new SearchCvNameInfo();
		String searchInfoJsonString = "";

		try {

			SearchCvNameAttributeConfigDao searchDao = (SearchCvNameAttributeConfigDao) context.getBean("searchCvNameDao");
			List <SearchCvNameList> searchLists = searchDao.searchByCVName(cvName_);
			searchInfo.setCVNameLists(searchLists);

			searchInfoJsonString = SearchCvNameInfoView.createSearchInfoToJsonString(searchInfo, true, Constants.EMPTY, true);

		} catch(Throwable th) {
			th.printStackTrace();
			searchInfoJsonString = SearchCvNameInfoView.createSearchInfoToJsonString(searchInfo, false,th.getMessage() , true);

		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.search_attribute_config_cvname=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return searchInfoJsonString;
	}

	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	private static ApplicationProps appProps = (ApplicationProps) context.getBean("appProps");
	}
