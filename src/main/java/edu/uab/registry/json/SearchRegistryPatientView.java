package edu.uab.registry.json;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.dao.SearchRegistryPatientDao;
import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.domain.GenericRegistryPatientList;
import edu.uab.registry.domain.SearchRegistryPatientList;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.WebServiceUtils;

public class SearchRegistryPatientView extends Views 
{
	public static String searchRegistryPatientToJsonString(GenericRegistryPatientList grpLs, boolean success, String statusMessage, boolean pretty)
	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, statusMessage);
		grpLs.setWebservice_status(statusMsg);
		String searchRegistryPatientJsonString = "";
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       	        	     	        
	        searchRegistryPatientJsonString = objectMapper.writerWithView(Views.Normal.class).writeValueAsString(grpLs);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return searchRegistryPatientJsonString;
	}		
	
	public static void main(String[] args) 
	{	/*
		try {					
			//List<GenericRegistryPatient> genericRegistryPatients = generateGenericRegistryPatientList();
			SearchRegistryPatientDao srpDao = (SearchRegistryPatientDao) context.getBean("searchRegistryPatientDao");
			int registryId = 2689;
			String mrn = "3036420";mrn = "1434747"; //mrn="0";
			String name = "TERRY LONG";name = "DELMAS PRICE"; //name = "";
			String assignerId = "";
			String statuses = "Candidate,Accepted";//statuses = "";
			String reviewBeginDate = "01-JAN-14";reviewBeginDate = "";
			String reviewEndDate = "01-JAN-16";reviewEndDate = "";	
			String reviewStatus = "";
			String hscontactBeginDate = "01-JAN-16"; hscontactBeginDate = ""; 
			String hscontactEndDate = "31-MAR-16"; hscontactEndDate = "";
			String patientAttributes = "";
			String encounterAttributes = "";
			String detectionEvents = "";
												
			GenericRegistryPatientList genericRegistryPatientList = 
					srpDao.getRegistryPatients(registryId, 
												assignerId, 
												mrn, 
												name, 
												reviewBeginDate, 
												reviewEndDate, 
												statuses, 
												reviewStatus, 
												hscontactBeginDate, 
												hscontactEndDate,
												patientAttributes,
												encounterAttributes,
												detectionEvents
												);						
			String searchRegistryPatientJsonString = searchRegistryPatientToJsonString(genericRegistryPatientList, true, Constants.EMPTY, true);
			System.out.println("Search Registry Patient List JSON is\n"+searchRegistryPatientJsonString);							
			System.out.println("Number of Search Registry Patients :" + genericRegistryPatientList.getRegistry_patient_list().size());
			System.out.println("Number of Search Registry Patient Codes :" + genericRegistryPatientList.getRegistry_all_encounter_attributes_list().size());
			System.out.println("Number of Search Registry Patient Attrs :" + genericRegistryPatientList.getRegistry_all_patient_attributes_list().size());
		} catch (Throwable th) {	
			th.printStackTrace();
			GenericRegistryPatientList genericRegistryPatientList = new GenericRegistryPatientList();
			String searchRegistryPatientJsonString = searchRegistryPatientToJsonString(genericRegistryPatientList, false, th.getMessage(), true);
			System.out.println("Search Registry Patient List JSON is\n"+searchRegistryPatientJsonString);
		} finally {
			context.close();
			context = null;
		}*/
	}

	/*
	private static List<GenericRegistryPatient> generateSearchRegistryPatientList()
	{
		List<GenericRegistryPatient> grpS = new ArrayList<GenericRegistryPatient>();		
		
		GenericRegistryPatient grp = new GenericRegistryPatient();
		grp.setRegistry_patient_id(52510);
		grp.setMrn("3036420");
		grp.setFull_name("John Smith");		
		grp.setLast_review_date("04/02/13");
		grp.setRegistry_status_id(2724);
		grp.setRegistry_status("Validated");
		grp.setWorkflow_status("abc");
		grp.setAssigned_by_name("assamal");
		grpS.add(grp);
		
		grp = new GenericRegistryPatient();
		grp.setRegistry_patient_id(52511);
		grp.setMrn("3036421");
		grp.setFull_name("Amy Smith");		
		grp.setLast_review_date("02/02/13");
		grp.setRegistry_status_id(2724);
		grp.setRegistry_status("Validated");
		grp.setWorkflow_status("123");
		grp.setAssigned_by_name("assamal");
		grpS.add(grp);
		
		grp = new GenericRegistryPatient();
		grp.setRegistry_patient_id(52512);
		grp.setMrn("3036422");
		grp.setFull_name("Fanny Smith");		
		grp.setLast_review_date("05/02/13");
		grp.setRegistry_status_id(2729);
		grp.setRegistry_status("Rejected");
		grp.setWorkflow_status("xyz");
		grp.setAssigned_by_name("assamal");
		grpS.add(grp);				
		
		grp = new GenericRegistryPatient();		
		
		for (int i=0; i<grpS.size(); i++) {
			GenericRegistryPatient grpx = grpS.get(i);
			System.out.println(grpx.getRegistry_patient_id() + ", " +grpx.getMrn() + ", " + grpx.getFull_name());
		}
		
		return grpS;
	}*/
	
	private static final Logger logger = LoggerFactory.getLogger(SearchRegistryPatientView.class);
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
}
