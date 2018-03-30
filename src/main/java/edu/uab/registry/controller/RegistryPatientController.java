package edu.uab.registry.controller;

import java.util.List;

import edu.uab.registry.util.WebServiceResult;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uab.registry.dao.GenericRegistryPatientDao;
import edu.uab.registry.dao.I2b2CorePatientDemographicsDao;
import edu.uab.registry.dao.RegistryPatientDemographicsDao;
import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.domain.GenericRegistryPatientList;
import edu.uab.registry.domain.RegistryNlpPatientInfo;
import edu.uab.registry.domain.RegistryPatientDemographics;

import edu.uab.registry.json.LookupMrnResult;
import edu.uab.registry.orm.RegistryPatient;
import edu.uab.registry.orm.dao.RegistryPatientDao;
import edu.uab.registry.orm.dao.RegistryPatientHistoryDemographicsDao;
import edu.uab.registry.util.ApplicationProps;
import edu.uab.registry.util.Constants.MrnLookupStatus;
import edu.uab.registry.util.StopWatch;

@RestController
public class RegistryPatientController 
{
	@RequestMapping(value="/addmrn", method=RequestMethod.GET)
    public String addMrn
    (
    	@RequestParam(value="mrn", defaultValue="") String mrn_,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
    	@RequestParam(value="status_id", defaultValue="0") Integer statusID_,
    	@RequestParam(value="review_status_id", defaultValue="0") Integer reviewStatusID_,
    	@RequestParam(value="changer_id", defaultValue="0") Integer changerID_,
    	@RequestParam(value="reg_status_change_comment", defaultValue="") String statusComment_,
    	@RequestParam(value="workflow_status_change_comment", defaultValue="") String reviewStatusComment_
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		WebServiceResult result = new WebServiceResult();

		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("addmrn parameters: mrn="+mrn_+
					",registry_id="+registryID_+
					",status_id="+statusID_+
					",review_status_id="+reviewStatusID_+
					",changer_id="+changerID_+
					",reg_status_change_comment="+statusComment_+
					",workflow_status_change_comment="+reviewStatusComment_
					);
		try {
            // Validate the MRN.
            if (StringUtils.isNullOrEmpty(mrn_)) {
                throw new Exception("Invalid MRN (empty)");
            } else {
                mrn_ = mrn_.trim();
            }

            // Validate the registry ID.
            if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }

			RegistryPatientHistoryDemographicsDao rphdDao = (RegistryPatientHistoryDemographicsDao)context.getBean("registryPatientHistoryDemographicsDao");
			String formattedMrn = String.format("%012d", Integer.parseInt(mrn_));
            int addStatus = rphdDao.addMrn(mrn_, registryID_, statusID_, formattedMrn, reviewStatusID_, changerID_, statusComment_, reviewStatusComment_);
			if (addStatus == 0) {
                result.setWebServiceStatus("Patient added Successfully", true);
			} else if (addStatus == 1) {
                result.setWebServiceStatus("This MRN was not found in Emmi Demographics Table", false);
			} else if (addStatus == 2) {
                result.setWebServiceStatus("This patient already exists in the registry", false);
			} 
		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.addmrn="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");

        return result.toJSON();
    }


	@RequestMapping(value="/lookupmrn", method=RequestMethod.GET)
    public String lookupMRN
    ( 	
    	@RequestParam(value="mrn", defaultValue="") String mrn_,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
    ) 
    {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("lookupmrn parameters: registry_id = " + registryID_ + ", mrn = " + mrn_);

		LookupMrnResult result = new LookupMrnResult();
		
		try {
			// Validate the MRN.
			if (StringUtils.isNullOrEmpty(mrn_)) { 
				throw new Exception("Invalid MRN (empty)"); 
			} else {
				mrn_ = mrn_.trim();
			}
			
			// Validate the registry ID
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			
			String zeros = "000000000000";
			String formattedMRN = zeros.substring(0, 12 - mrn_.length()) + mrn_;
			logger.info("formatted mrn = " + formattedMRN);
			
			I2b2CorePatientDemographicsDao i2b2Dao = (I2b2CorePatientDemographicsDao) context.getBean("i2b2CorePatientDemographicsDao");
			RegistryPatientDemographics rpd = i2b2Dao.getDemographicInfo(formattedMRN);
			if (rpd == null || StringUtils.isNullOrEmpty(rpd.getMrn())) {
				// Invalid MRN
				result.setLookupStatus(MrnLookupStatus.invalid_mrn);
				result.setWebServiceStatus("Invalid MRN", true);
			} else {
				// The patient demographics are valid.
				result.setDemographics(rpd);
				
				// Is this person already a registry patient?
				RegistryPatientDao rpDao = (RegistryPatientDao) context.getBean("registryPatientDao");				
				RegistryPatient rp = rpDao.findByMrnAndRegistryId(mrn_, registryID_);
				if (rp == null || StringUtils.isNullOrEmpty(rp.getUabMrn())) {

                    // The patient has not yet been added to the registry.
                    result.setLookupStatus(MrnLookupStatus.valid_mrn);
                    result.setWebServiceStatus("The patient has not yet been added to the registry", true);

				} else {

                    // This patient is already in the registry.
                    result.setLookupStatus(MrnLookupStatus.already_in_registry);
                    result.setWebServiceStatus("This patient is already in the registry", true);
				}
			}
			
		} catch (Throwable th) {
			th.printStackTrace();	
			result.setWebServiceStatus(th.getMessage(), false);
		}
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.lookupmrn="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
		return result.toJSON();
    }


	private static final Logger logger = LoggerFactory.getLogger(RegistryPatientController.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	private static ApplicationProps appProps = (ApplicationProps)context.getBean("appProps");
}
