package edu.uab.registry.json;

import java.io.StringWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.RegistryNlpPatientInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class RegistryNlpPatientInfoView extends Views 
{	
	public static String createRegistryNlpPatientInfoToJsonString(RegistryNlpPatientInfo registryNlpPatientInfo, boolean success, String errorMessage, boolean pretty)
	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		registryNlpPatientInfo.setWebservice_status(statusMsg);
		StringWriter registryNlpPatientInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(registryNlpPatientInfoJsonString, registryNlpPatientInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return registryNlpPatientInfoJsonString.toString();
	}
	
	public static void main(String[] args) 
	{
		/*
		// TODO Auto-generated method stub
		try {
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			
			String mrn = "3014973";			
			RegistryNlpPatientInfo registryNlpPatientInfo = new RegistryNlpPatientInfo();
			
			GenericRegistryPatientDao grpDao = (GenericRegistryPatientDao) context.getBean("genericRegistryPatientDao");
			List<GenericRegistryPatient> grps = grpDao.getGenericRegistryPatient(mrn);
			if (grps.size() > 0) {				
				registryNlpPatientInfo.setRegistry_patient(grps.get(0));
			}
			
			RegistryPatientDemographicsDao rpdDao = (RegistryPatientDemographicsDao) context.getBean("registryPatientDemographicsDao");			
			RegistryNlpPatientInfo rnlpi = rpdDao.getRegistryPatientDemographics(mrn);
			if (rnlpi.getPatient_demographics() != null) {
				registryNlpPatientInfo.setPatient_demographics(rnlpi.getPatient_demographics());
			}
			
			NlpPatientDao nlpDao = (NlpPatientDao) context.getBean("nlpPatientDao");
			List<NlpPatientHitsDocs> nlpPatients = nlpDao.getNlpPatients(mrn);
			registryNlpPatientInfo.setNlp_document_list(nlpPatients);
			
			RegistryPatientEncounterAttributesDao rpeaDao = (RegistryPatientEncounterAttributesDao) context.getBean("registryPatientEncounterAttributesDao");
			mrn = "1387275";
			RegistryPatientEncounterAttributes registryPatientEncounterAttributes = rpeaDao.getRegistryPatientEncounterAttributes(mrn);
			List <RegistryPatientCode> registryAllEncounterAttributesList = registryPatientEncounterAttributes.getRegistry_patient_code_list();
			List <RegistryPatientAttribute> registryPatientAttrList = registryPatientEncounterAttributes.getRegistry_patient_attribute_list();
			
			registryNlpPatientInfo.setRegistry_patient_diagnosis_codes_list(registryAllEncounterAttributesList);
			registryNlpPatientInfo.setRegistry_all_patient_attribute_list(registryPatientAttrList);
			
			RegistryEncountersDao reDao = (RegistryEncountersDao) context.getBean("registryEncountersDao");
			mrn = "3014973";mrn = "1387275";mrn = "424101";
			int registryId = 2861;
			RegistryEncounterInfo registryEncounterInfo = reDao.getRegistryEncounters(mrn, registryId);
			List <RegistryEncounter> registryEncountersList = registryEncounterInfo.getRegistry_encounters();			
			
			registryNlpPatientInfo.setRegistry_encounters_list(registryEncountersList);			
			
			String registryNlpPatientInfoJsonString = createRegistryNlpPatientInfoToJsonString(registryNlpPatientInfo, true, Constants.EMPTY, true);
			
			System.out.println("Registry NLP Patient Info JSON is\n"+registryNlpPatientInfoJsonString);
			System.out.println("Number of NLP Patients :" + nlpPatients.size());
			System.out.println("Number of All Registry Encounter Attributes :" + registryAllEncounterAttributesList.size());
			System.out.println("Number of All Registry Patient Attributes :" + registryPatientAttrList.size());
			System.out.println("Number of Registry Encounters :" + registryEncountersList.size());			
			System.out.println("Log.ElapsedTime.RegistryNlpPatientInfoView=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		} catch(Throwable th) {
			th.printStackTrace();
			RegistryNlpPatientInfo registryNlpPatientInfo = new RegistryNlpPatientInfo();
			String registryNlpPatientInfoJsonString = createRegistryNlpPatientInfoToJsonString(registryNlpPatientInfo, false, th.getMessage(), true);
			System.out.println("Registry NLP Patient Info JSON is\n"+registryNlpPatientInfoJsonString);
		} finally {			
			context.close();
			context = null;
		}*/
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistryNlpPatientInfoView.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
}
