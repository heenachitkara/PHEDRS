package edu.uab.registry.controller;

import java.util.Date;
import java.util.List;

import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uab.registry.domain.RegistryPatientStatus;
import edu.uab.registry.dao.RegistryPatientStatusDao;
import edu.uab.registry.dao.impl.RegistryPatientStatusDaoImpl;
import edu.uab.registry.json.GetStatusHistoryResult;
import edu.uab.registry.orm.dao.impl.RegistryPatientDaoImpl;
import edu.uab.registry.orm.RegistryPatient;
import edu.uab.registry.orm.RegistryPatientHistory;
import edu.uab.registry.orm.dao.RegistryPatientDao;
import edu.uab.registry.util.ApplicationProps;
import edu.uab.registry.util.Constants.RegistryStatusType;
import edu.uab.registry.util.StopWatch;
import edu.uab.registry.util.WebServiceResult;

@RestController
public class RegistryPatientStatusHistoryController 
{	
	@RequestMapping(value="/addregistrypatienthistory", method=RequestMethod.GET)
    public String addRegistryPatientHistory
    (
    	@RequestParam(value="registry_patient_id", defaultValue="0") Integer registry_patient_id,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
    	@RequestParam(value="previous_status_id", defaultValue="0") Integer previous_status_id,
    	@RequestParam(value="current_status_id", defaultValue="0") Integer current_status_id,    	
    	@RequestParam(value="changer_id", defaultValue="0") Integer changer_id,
    	@RequestParam(value="reg_status_change_comment", defaultValue="0") String reg_status_change_comment,
    	@RequestParam(value="workflow_status_change_comment", defaultValue="0") String workflow_status_change_comment,
    	@RequestParam(value="prev_review_status_id", defaultValue="0") Integer prev_review_status_id,
    	@RequestParam(value="cur_review_status_id", defaultValue="0") Integer cur_review_status_id
    ) 
    {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("addregistrypatienthistory ws params : registry_patient_id:" + registry_patient_id +
					 ", registry_id:" + registry_id +
					 ", previous_status_id:" + previous_status_id +
					 ", current_status_id:" + current_status_id +
					 ", changer_id:" + changer_id +
					 ", reg_status_change_comment:" + reg_status_change_comment +
					 ", workflow_status_change_comment:" + workflow_status_change_comment +
					 ", prev_review_status_id:" + prev_review_status_id +
					 ", cur_review_status_id:" + cur_review_status_id
					);		

		WebServiceResult result = new WebServiceResult();

		try {
			edu.uab.registry.orm.dao.RegistryPatientHistoryDao rphHibDao = 
					(edu.uab.registry.orm.dao.RegistryPatientHistoryDao)context.getBean("registryPatientHistoryDao");
			
			RegistryPatientHistory rph = new RegistryPatientHistory();
			rph.setRegistryPatientId(registry_patient_id);
			rph.setRegistryId(registry_id);
			rph.setPreviousStatusId(previous_status_id);
			rph.setCurrentStatusId(current_status_id);
			rph.setChangeDate(new Date());			
			rph.setChangerId(changer_id);
			rph.setRegStatusChangeComment(reg_status_change_comment);
			rph.setWorkflowStatusChangeComment(workflow_status_change_comment);			
			rph.setPrevReviewStatusId(prev_review_status_id);
			rph.setCurReviewStatusId(cur_review_status_id);

			if (rphHibDao.insert(rph)) {
				result.setWebServiceStatus("Inserted REGISTRY_PATIENT_HISTORY Record", true);
			} else {
				result.setWebServiceStatus("Insertion into REGISTRY_PATIENT_HISTORY failed", false);
			}

		} catch(Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.addregistrypatienthistory=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");

		return result.toJSON();
    }
	
	@RequestMapping(value="/updateregistrypatientstatushistory", method=RequestMethod.GET)
    public String updateRegistryPatientStatusHistory
    (
    	@RequestParam(value="registry_patient_id", defaultValue="0") Integer registry_patient_id,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
    	@RequestParam(value="new_status_id", defaultValue="0") Integer new_status_id,
    	@RequestParam(value="new_review_status_id", defaultValue="0") Integer new_review_status_id,
    	@RequestParam(value="changer_id", defaultValue="0") Integer changer_id,
    	@RequestParam(value="reg_status_change_comment", defaultValue="") String reg_status_change_comment,
    	@RequestParam(value="workflow_status_change_comment", defaultValue="") String workflow_status_change_comment
    ) 
    {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("updateregistrypatientstatushistory ws params : "+ 
					"registry_patient_id="+registry_patient_id+
					",registry_id:"+registry_id+
					",new_status_id:"+new_status_id+
					",new_review_status_id:"+new_review_status_id+
					",reg_status_change_comment:"+reg_status_change_comment+
					",workflow_status_change_comment:"+workflow_status_change_comment
					);

		WebServiceResult result = new WebServiceResult();

		try {
			// Validate the input parameters.
			if (changer_id < 1) { throw new Exception("Invalid changer ID"); }
			if (registry_id < 1) { throw new Exception("Invalid registry ID"); }
			if (registry_patient_id < 1) { throw new Exception("Invalid registry pateint ID"); }
			
			// Get and validate the registry patient.
			RegistryPatientDao rpDao = context.getBean("registryPatientDao", RegistryPatientDaoImpl.class);
			RegistryPatient registryPatient = rpDao.findByRegistryPatientId(registry_patient_id);
			if (registryPatient == null) { throw new Exception("Invalid registry patient"); }
			
			// Use the registry patient status dao to 1) update the registry patient and 2) add a new history record.
			RegistryPatientStatusDao rpsDao = context.getBean("registryPatientStatusDao", RegistryPatientStatusDaoImpl.class);
			rpsDao.updateStatus(context, changer_id, registryPatient, reg_status_change_comment, new_status_id, workflow_status_change_comment, new_review_status_id);
			
			result.setWebServiceStatus("Updated REGISTRY_PATIENT table, Inserted REGISTRY_PATIENT_HISTORY record", true);

			logger.info("Status history successfully updated");

		} catch(Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		} 
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.updateregistrypatientstatushistory="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
		return result.toJSON();
    }

	@RequestMapping(value="/getregistrystatushistory", method=RequestMethod.GET)
    public String getRegistryStatusHistory
    (
    	@RequestParam(value="mrn", defaultValue="") String mrn_,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getregistrystatushistory parameters: mrn = " + mrn_ + ", registry_id = " + registryID_);

		GetStatusHistoryResult result = new GetStatusHistoryResult();

		try {
			// Validate the MRN.
			if (StringUtils.isNullOrEmpty(mrn_)) { 
				throw new Exception("Invalid MRN (empty)"); 
			} else {
				mrn_ = mrn_.trim();
			}
			
			// Validate the registry ID.
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
						
			// Get the registry patient for the MRN and registry ID provided.
			RegistryPatientDao rpDao = (RegistryPatientDao)context.getBean("registryPatientDao");
			RegistryPatient rp = rpDao.findByMrnAndRegistryId(mrn_, registryID_);
			System.out.println("Checking history just like for get_cv_metadata");
			System.out.println(rp);
			if (rp != null) {

				// Populate the registry status history list.
				RegistryPatientStatusDao statusDao = (RegistryPatientStatusDao) context.getBean("registryPatientStatusDao");
				List<RegistryPatientStatus> statuses = statusDao.getStatusHistory(rp, RegistryStatusType.registry);

				// Populate the result.
				result.setStatuses(statuses);
				result.setWebServiceStatus("", true);
				
			} else {
				result.setWebServiceStatus("Invalid registry patient", false);
			}
			
		} catch(Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getregistrystatushistory="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
		return result.toJSON();
    }
	

	@RequestMapping(value="/getworkflowstatushistory", method=RequestMethod.GET)
    public String getWorkflowStatusHistory
    (
    	@RequestParam(value="mrn", defaultValue="") String mrn_,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getworkflowstatushistory parameters: mrn = " + mrn_ + ", registry_id = " + registryID_);

		GetStatusHistoryResult result = new GetStatusHistoryResult();

		try {
			// Validate the MRN.
			if (StringUtils.isNullOrEmpty(mrn_)) { 
				throw new Exception("Invalid MRN (empty)"); 
			} else {
				mrn_ = mrn_.trim();
			}
		    
			// Validate the registry ID.
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
						
			// Get the registry patient for the MRN and registry ID provided.
			RegistryPatientDao rpDao = (RegistryPatientDao)context.getBean("registryPatientDao");
			RegistryPatient rp = rpDao.findByMrnAndRegistryId(mrn_, registryID_);	
			if (rp != null) {

				// Populate the workflow status history list.
				RegistryPatientStatusDao statusDao = (RegistryPatientStatusDao) context.getBean("registryPatientStatusDao");
				List<edu.uab.registry.domain.RegistryPatientStatus> statuses = statusDao.getStatusHistory(rp, RegistryStatusType.workflow);

				// Populate the result.
				result.setStatuses(statuses);
				result.setWebServiceStatus("", true);
				
			} else {
				result.setWebServiceStatus("Invalid registry patient", false);
			}
			
		} catch(Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getworkflowstatushistory="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
		return result.toJSON();
    }
		
	private static final Logger logger = LoggerFactory.getLogger(RegistryPatientStatusHistoryController.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	private static ApplicationProps appProps = (ApplicationProps)context.getBean("appProps");
}
