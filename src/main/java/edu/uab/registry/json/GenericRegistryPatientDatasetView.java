package edu.uab.registry.json;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.dao.GenericRegistryPatientDatasetDao;
import edu.uab.registry.domain.GenericRegistryPatientDatasetList;
import edu.uab.registry.domain.GenericRegistryPatient;

public class GenericRegistryPatientDatasetView extends Views 
{
	public static String GenericRegistryPatientDatasetToJsonString(GenericRegistryPatientDatasetList grdpLs, boolean pretty)
	{
		String searchRegistryPatientJsonString = "";
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       	        	     	        
	        searchRegistryPatientJsonString = objectMapper.writerWithView(Views.Manager.class).writeValueAsString(grdpLs);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return searchRegistryPatientJsonString;
	}
	
	public static GenericRegistryPatientDatasetList createRegistryDatasetPatientList(List<GenericRegistryPatient> genericRegistryPatientsDataset)
	{		
		GenericRegistryPatientDatasetList generiRegistryPatientsDataset = new GenericRegistryPatientDatasetList();
		generiRegistryPatientsDataset.setPatient_dataset_list(genericRegistryPatientsDataset);
		return generiRegistryPatientsDataset;
	}	
	
	public static void main(String[] args) 
	{		
		try {					
			//List<GenericRegistryPatient> genericRegistryPatients = generateGenericRegistryPatientList();
			GenericRegistryPatientDatasetDao grdpDao = (GenericRegistryPatientDatasetDao) context.getBean("genericRegistryPatientDatasetDao");
			int registryId = 2689;	
			String datasetName = "Multiple Myeloma Kidney Dataset";		
			String datasetStatus = "";
			List<GenericRegistryPatient> genericRegistryPatients = grdpDao.getGenericRegistryPatientsDataset(registryId, datasetName, datasetStatus);			
			GenericRegistryPatientDatasetList grdpLs = createRegistryDatasetPatientList(genericRegistryPatients);
			String genericRegistryDatasetPatientJsonString = GenericRegistryPatientDatasetToJsonString(grdpLs, true);
			System.out.println("Generic Registry Dataset Patient List JSON is\n"+genericRegistryDatasetPatientJsonString);	
			System.out.println("Number of Generic Dataset Registry Patients :" + genericRegistryPatients.size());
		} catch (Throwable th) {	
			th.printStackTrace();
		} finally {
			context.close();
			context = null;
		}
	}
	
	private static List<GenericRegistryPatient> generateGenericRegistryPatientList()
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
	}
	
	private static final Logger logger = LoggerFactory.getLogger(GenericRegistryPatientDatasetView.class);
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
}
