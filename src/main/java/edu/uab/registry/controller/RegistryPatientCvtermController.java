package edu.uab.registry.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uab.registry.dao.EncounterInsuranceListDao;
import edu.uab.registry.dao.RegConfigPatientAttributesDao;
import edu.uab.registry.domain.RegConfigPatientAttributesInfo;
import edu.uab.registry.domain.RegistryEncounterInsuranceInfo;
import edu.uab.registry.domain.RegConfigPatientAttributesList;
import edu.uab.registry.json.EncounterInsuranceInfoView;
import edu.uab.registry.json.GenericOrmStatusView;
import edu.uab.registry.json.RegConfigPatientAttributesInfoView;
import edu.uab.registry.orm.RegistryPatient;
import edu.uab.registry.orm.RegistryPatientCvterm;
import edu.uab.registry.orm.dao.RegistryPatientDao;
import edu.uab.registry.util.ApplicationProps;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;

@RestController
public class RegistryPatientCvtermController 
{	
	@RequestMapping(value="/addregistrypatientcvterm", method=RequestMethod.GET)
    public String addRegistryPatientCvterm
    (
    	@RequestParam(value="registry_patient_id", defaultValue="0") Integer registry_patient_id,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
    	@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id,
    	@RequestParam(value="start_assignment_date", defaultValue="") String start_assignment_date,    	
    	@RequestParam(value="annotator_id", defaultValue="0") Integer annotator_id,    	    	
    	@RequestParam(value="annotator_comment", defaultValue="") String annotator_comment,
    	@RequestParam(value="end_assignment_date", defaultValue="") String end_assignment_date,
    	@RequestParam(value="is_valid", defaultValue="0") String is_valid,
    	@RequestParam(value="evidence_code_id", defaultValue="0") Integer evidence_code_id,
    	@RequestParam(value="encounter_key", defaultValue="0") Integer encounter_key,
    	@RequestParam(value="evidence_hit_id", defaultValue="0") Integer evidence_hit_id,
    	@RequestParam(value="annotation_date", defaultValue="") String annotation_date
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("addregistrypatientcvterm ws params : " + 
					 "registry_patient_id:" + registry_patient_id +
					 ", registry_id:" + registry_id +					 
					 ", cvterm_id:" + cvterm_id +
					 ", start_assignment_date:" + start_assignment_date +
					 ", annotator_id:" + annotator_id +
					 ", annotator_comment:" + annotator_comment +
					 ", end_assignment_date:" + end_assignment_date +
					 ", is_valid:" + is_valid +
					 ", evidence_code_id:" + evidence_code_id +
					 ", encounter_key:" + encounter_key +
					 ", evidence_hit_id:" + evidence_hit_id +
					 ", annotation_date:" + annotation_date
					);		
		try {						
			edu.uab.registry.orm.dao.RegistryPatientCvtermDao rpcHibDao = 
					(edu.uab.registry.orm.dao.RegistryPatientCvtermDao)context.getBean("registryPatientCvtermDao");
			
			RegistryPatientCvterm rpc = new RegistryPatientCvterm();
			rpc.setRegistryPatientId(registry_patient_id);
			rpc.setRegistryId(registry_id);
			rpc.setCvtermId(cvterm_id);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
			if ((start_assignment_date == null) || "".equals(start_assignment_date) || "null".equals(start_assignment_date)) {
				rpc.setStartAssignmentDate(new Date());
			} else {
				rpc.setStartAssignmentDate(sdf.parse(start_assignment_date));
			}
			rpc.setAnnotatorId(annotator_id);
			rpc.setAnnotatorComment(annotator_comment);
			if ((end_assignment_date == null) || "".equals(end_assignment_date) || "null".equals(end_assignment_date)) {
				rpc.setEndAssignmentDate(null);
			} else {
				rpc.setEndAssignmentDate(sdf.parse(end_assignment_date));
			}			
			rpc.setIsValid(is_valid);
			rpc.setEvidenceCodeId(evidence_code_id);
			rpc.setEncounterKey(encounter_key);
			rpc.setEvidenceHitId(evidence_hit_id);
			if ((annotation_date == null) || "".equals(annotation_date) || "null".equals(annotation_date)) {
				rpc.setAnnotationDate(new Date());
			} else {
				rpc.setAnnotationDate(sdf.parse(annotation_date));
			}					
			rpcHibDao.insert(rpc);	        
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Inserted REGISTRY_PATIENT_CVTERM Record!", true);
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addregistrypatientcvterm=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	@RequestMapping(value="/updateregistrypatientcvtermhistory", method=RequestMethod.GET)
    public String updateRegistryPatientCvtermHistory
    (
    	@RequestParam(value="registry_patient_id", defaultValue="0") Integer registry_patient_id,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
    	@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id,
    	@RequestParam(value="start_assignment_date", defaultValue="") String start_assignment_date,    	
    	@RequestParam(value="annotator_id", defaultValue="0") Integer annotator_id,    	    	
    	@RequestParam(value="annotator_comment", defaultValue="0") String annotator_comment,
    	@RequestParam(value="end_assignment_date", defaultValue="") String end_assignment_date,
    	@RequestParam(value="is_valid", defaultValue="0") String is_valid,
    	@RequestParam(value="evidence_code_id", defaultValue="0") Integer evidence_code_id,
    	@RequestParam(value="encounter_key", defaultValue="0") Integer encounter_key,
    	@RequestParam(value="evidence_hit_id", defaultValue="0") Integer evidence_hit_id,
    	@RequestParam(value="annotation_date", defaultValue="") String annotation_date
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("updateregistrypatientcvtermhistory ws params : " + 
					 "registry_patient_id:" + registry_patient_id +
					 ", registry_id:" + registry_id +					 
					 ", cvterm_id:" + cvterm_id +
					 ", start_assignment_date:" + start_assignment_date +
					 ", annotator_id:" + annotator_id +
					 ", annotator_comment:" + annotator_comment +
					 ", end_assignment_date:" + end_assignment_date +
					 ", is_valid:" + is_valid +
					 ", evidence_code_id:" + evidence_code_id +
					 ", encounter_key:" + encounter_key +
					 ", evidence_hit_id:" + evidence_hit_id +
					 ", annotation_date:" + annotation_date
					);		
		try {						
			edu.uab.registry.orm.dao.RegistryPatientCvtermDao rpcHibDao = 
					(edu.uab.registry.orm.dao.RegistryPatientCvtermDao)context.getBean("registryPatientCvtermDao");
			
			List<RegistryPatientCvterm> rpcList = rpcHibDao.findByRegistryIdAndRegistryPatientId(registry_id, registry_patient_id);
			RegistryPatientCvterm rpc = null;
			String statusMessage = null;
			if ((rpcList != null) && !rpcList.isEmpty()) {
				rpc = rpcList.get(0);
				
				rpc.setCvtermId(cvterm_id);
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
				if ((start_assignment_date == null) || "".equals(start_assignment_date)
						|| "null".equals(start_assignment_date)) {
					rpc.setStartAssignmentDate(new Date());
				} else {
					rpc.setStartAssignmentDate(sdf.parse(start_assignment_date));
				}
				rpc.setAnnotatorId(annotator_id);
				rpc.setAnnotatorComment(annotator_comment);
				if ((end_assignment_date == null) || "".equals(end_assignment_date) || "null".equals(end_assignment_date)) {
					rpc.setEndAssignmentDate(null);
				} else {
					rpc.setEndAssignmentDate(sdf.parse(end_assignment_date));
				}
				rpc.setIsValid(is_valid);
				rpc.setEvidenceCodeId(evidence_code_id);
				rpc.setEncounterKey(encounter_key);
				rpc.setEvidenceHitId(evidence_hit_id);				
				if ((annotation_date == null) || "".equals(annotation_date) || "null".equals(annotation_date)) {
					rpc.setAnnotationDate(new Date());
				} else {
					rpc.setAnnotationDate(sdf.parse(annotation_date));
				}
				int updateStatus = rpcHibDao.updateRegistryPatientCvtermInsertHistory(rpc);
				logger.info("updateregistrypatientcvtermhistory status:" + updateStatus);								
				if (updateStatus == 0) {
					statusMessage = "Updated REGISTRY_PATIENT_CVTERM table, Inserted REGISTRY_PATIENT_CVTERM_HIST record!";
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, statusMessage, true);
				} else {
					statusMessage = "Update REGISTRY_PATIENT_CVTERM table, Insert of REGISTRY_PATIENT_CVTERM_HIST Problem!";
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, statusMessage, true);
				}
			} else {				
				statusMessage = "Did NOT find any REGISTRY_PATIENT_CVTERM records to update!";
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, statusMessage, true);
				logger.info("updateregistrypatientcvtermhistory status: Did NOT find any registry_patient_cvterm records to update!");
			}							        
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.updateregistrypatientcvtermhistory=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	@RequestMapping(value="/deleteregistrypatientattribute", method=RequestMethod.GET)
    public String deleteRegistryPatientAttribute
    (
    	@RequestParam(value="registry_patient_cvterm_id", defaultValue="0") Integer registry_patient_cvterm_id    	
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("deleteregistrypatientattribute ws params : " + "registry_patient_cvterm_id:" + registry_patient_cvterm_id);		
		try {					
			edu.uab.registry.orm.dao.RegistryPatientCvtermDao rpcHibDao = 
					(edu.uab.registry.orm.dao.RegistryPatientCvtermDao)context.getBean("registryPatientCvtermDao");
			
			int deleteStatus = rpcHibDao.softDeleteByRegistryPatientCvtermId(registry_patient_cvterm_id);
			String statusMessage = null;
			logger.info("deleteregistrypatientattribute status:" + deleteStatus);								
			if (deleteStatus >= 1) {
				statusMessage = "Deleted registry patient attribute with registry_patient_cvterm_id=" + registry_patient_cvterm_id;
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, statusMessage, true);
			} else if (deleteStatus == 0) {
				statusMessage = "Did not find registry patient attribute with registry_patient_cvterm_id=" + registry_patient_cvterm_id;
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, statusMessage, true);			
			} 						        
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.deleteregistrypatientattribute=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	@RequestMapping(value="/addregistrypatientattribute", method=RequestMethod.GET)
    public String addRegistryPatientAttribute
    (
    	@RequestParam(value="mrn", defaultValue="0") String mrn,
    	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
    	@RequestParam(value="cvterm_id", defaultValue="0") Integer cvterm_id,
    	@RequestParam(value="start_assignment_date", defaultValue="") String start_assignment_date,   
    	@RequestParam(value="end_assignment_date", defaultValue="") String end_assignment_date,
    	@RequestParam(value="annotator_id", defaultValue="0") Integer annotator_id,
    	@RequestParam(value="annotator_comment", defaultValue="") String annotator_comment,
    	@RequestParam(value="annotation_date", defaultValue="") String annotation_date
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("addregistrypatientattribute ws params : " + 
					 "mrn:" + mrn +
					 ", registry_id:" + registry_id +					 
					 ", cvterm_id:" + cvterm_id +
					 ", start_assignment_date:" + start_assignment_date +
					 ", end_assignment_date:" + end_assignment_date +
					 ", annotator_id:" + annotator_id +
					 ", annotator_comment:" + annotator_comment +
					 ", annotation_date:" + annotation_date
					);		
		try {	
			RegistryPatientCvterm rpc = new RegistryPatientCvterm();
			
			rpc.setRegistryPatientCvtermId(0);
			
			RegistryPatientDao rpDao = (RegistryPatientDao)context.getBean("registryPatientDao");
			RegistryPatient rp = rpDao.findByMrnAndRegistryId(mrn,registry_id);			
			if (rp != null) {
				rpc.setRegistryPatientId(rp.getRegistryPatientId());
				logger.debug("addregistrypatientattribute : retrieved registry_patient_id="+rp.getRegistryPatientId());
			}
			rpc.setRegistryId(registry_id);
			rpc.setCvtermId(cvterm_id);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
			if ((start_assignment_date == null) || "".equals(start_assignment_date) || "null".equals(start_assignment_date)) {
				rpc.setStartAssignmentDate(new Date());
			} else {
				rpc.setStartAssignmentDate(sdf.parse(start_assignment_date));
			}
			rpc.setAnnotatorId(annotator_id);			
			if ((annotation_date == null) || "".equals(annotation_date) || "null".equals(annotation_date)) {
				rpc.setAnnotationDate(new Date());
			} else {
				rpc.setAnnotationDate(sdf.parse(annotation_date));
			}					
			
			rpc.setAnnotatorComment(annotator_comment);
			
			rpc.setEndAssignmentDate(null);		
			if ((end_assignment_date == null) || "".equals(end_assignment_date) || "null".equals(end_assignment_date)) {
				rpc.setEndAssignmentDate(null);
			} else {
				rpc.setEndAssignmentDate(sdf.parse(end_assignment_date));
			}
			rpc.setIsValid("Y");
			rpc.setEvidenceCodeId(0);
			rpc.setEncounterKey(0);
			rpc.setEvidenceHitId(0);
			
			edu.uab.registry.orm.dao.RegistryPatientCvtermDao rpcHibDao = 
					(edu.uab.registry.orm.dao.RegistryPatientCvtermDao)context.getBean("registryPatientCvtermDao");						
			int addStatus = rpcHibDao.addRegistryPatientCvtermInsertWithHistory(rpc);
			if (addStatus == 0) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Added registry patient attribute successfully!", true);
			} else if (addStatus == 1) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Registry patient attribute already Exists!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Error adding registry patient attribute!", true);
			}
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addregistrypatientattribute=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	@RequestMapping(value="/updateregistrypatientattribute", method=RequestMethod.GET)
    public String updateRegistryPatientAttribute
    (
    	@RequestParam(value="registry_patient_cvterm_id", defaultValue="0") Integer registry_patient_cvterm_id,    
    	@RequestParam(value="start_assignment_date", defaultValue="") String start_assignment_date,
    	@RequestParam(value="end_assignment_date", defaultValue="") String end_assignment_date,
    	@RequestParam(value="is_valid", defaultValue="") String is_valid,
    	@RequestParam(value="annotator_comment", defaultValue="") String annotator_comment
    ) 
    {		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("updateregistrypatientattribute ws params : " + 
					 "registry_patient_cvterm_id:" + registry_patient_cvterm_id +
					 ", start_assignment_date:" + start_assignment_date +
					 ", end_assignment_date:" + end_assignment_date +					 
					 ", is_valid:" + is_valid +
					 ", annotator_comment:" + annotator_comment
					);		
		try {	
			RegistryPatientCvterm rpc = new RegistryPatientCvterm();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
			
			// required fields
			rpc.setRegistryPatientCvtermId(registry_patient_cvterm_id);
			rpc.setStartAssignmentDate(sdf.parse(start_assignment_date));
			
			rpc.setEndAssignmentDate(null);
			if ((end_assignment_date == null) || "".equals(end_assignment_date) || "null".equals(end_assignment_date)) {
				rpc.setEndAssignmentDate(null);
			} else {
				rpc.setEndAssignmentDate(sdf.parse(end_assignment_date));
			}
			//rpc.setEndAssignmentDate(sdf.parse(end_assignment_date));
			rpc.setIsValid(is_valid);
			rpc.setAnnotatorComment(annotator_comment);
			
			// default values
			rpc.setRegistryId(0);
			rpc.setRegistryPatientId(0);
			rpc.setCvtermId(0);			
			rpc.setAnnotatorId(0);
			rpc.setEvidenceCodeId(0);
			rpc.setEncounterKey(0);
			rpc.setEvidenceHitId(0);
			rpc.setAnnotationDate(null);
			
			edu.uab.registry.orm.dao.RegistryPatientCvtermDao rpcHibDao = 
					(edu.uab.registry.orm.dao.RegistryPatientCvtermDao)context.getBean("registryPatientCvtermDao");			
			int updateStatus = rpcHibDao.updateRegistryPatientCvtermInsertHistory(rpc);
			if (updateStatus == 0) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Updated registry patient attribute successfully!", true);
			} else if (updateStatus == 1) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Did not find registry patient attribute to update!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, "Error adding registry patient attribute!", true);
			}
		} catch(Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.updateregistrypatientattribute=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		return wsStatusJsonString;
    }
	
	
	@RequestMapping(value="/getRegistryPatientAttributes", method=RequestMethod.GET)
	public String getRegistryPatientAttribute
	(
			@RequestParam(value="registry_id", defaultValue="0") Integer registryID_
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("getRegistryPatientAttributes parameters: Registry ID = " +registryID_);
		
		RegConfigPatientAttributesInfo regconfigPatientAttrInfo = new RegConfigPatientAttributesInfo();
		String regconfigPatientAttrInfoJsonString =  "";
		
		try {
			RegConfigPatientAttributesDao regConfigPADao = (RegConfigPatientAttributesDao) context.getBean("registryConfigPatientAttrListDao");
			List<RegConfigPatientAttributesList> regConfigPtAttr  = regConfigPADao.getRegConfigPatientAttr(registryID_);
			regconfigPatientAttrInfo.set_reg_config_patient_attributes(regConfigPtAttr);
			
			regconfigPatientAttrInfoJsonString = RegConfigPatientAttributesInfoView.createRegPatientAttributesInfoToJsonString(regconfigPatientAttrInfo, true, Constants.EMPTY, true);
			logger.debug("getRegistryPatientAttributes JSON is\n"+regconfigPatientAttrInfoJsonString);
			if ((regConfigPtAttr != null) && (regConfigPtAttr.size() > 0)) {
				logger.info("Number of Encounter Insurance Records :" + regConfigPtAttr.size());
			}			
		} catch (Throwable th) {
			th.printStackTrace();
			regconfigPatientAttrInfoJsonString = RegConfigPatientAttributesInfoView.createRegPatientAttributesInfoToJsonString(regconfigPatientAttrInfo, false, th.getMessage(), true);
		}		
		
		return regconfigPatientAttrInfoJsonString;
		
    }
	
	
	private static final Logger logger = LoggerFactory.getLogger(RegistryPatientCvtermController.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	private static ApplicationProps appProps = (ApplicationProps)context.getBean("appProps");
}
