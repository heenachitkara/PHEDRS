package edu.uab.registry.controller.test;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class RegistryPatientListTest 
{			
	@Test
	public void test_getAllRegistryPatients()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatients Output:");
		System.out.println(retrievedPatientJsonString);		
	}
	
	@Test
	public void test_getRegistryPatientsByMrn()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients?mrn=3036420";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsByMrn Output:");
		System.out.println(retrievedPatientJsonString);		
	}
	
	@Test
	public void test_getRegistryPatientsByName()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients?name=TERRY LONG";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsByName Output:");
		System.out.println(retrievedPatientJsonString);		
	}
	
	@Test
	public void test_getRegistryPatientsByMrnAndName()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients?mrn=3036420&name=TERRY LONG";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsByMrnAndName Output:");
		System.out.println(retrievedPatientJsonString);		
	}
	
	@Test
	public void test_getRegistryPatientsByLastReviewedDate()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients?begin_date=01-JAN-14&end_date=01-JAN-16";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsByLastReviewedDate Output:");
		System.out.println(retrievedPatientJsonString);		
	}
	
	@Test
	public void test_getRegistryPatientsByStatus()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients?status=Validated,UnderReview,Rejected";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsByStatus Output:");
		System.out.println(retrievedPatientJsonString);		
	}
	
	@Test
	public void test_getRegistryPatientsByMrnAndLastReviewedDate()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients?begin_date=01-JAN-14&end_date=01-JAN-16&mrn=1731238";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsByMrnAndLastReviewedDate Output:");
		System.out.println(retrievedPatientJsonString);		
	}
	
	@Test
	public void test_getRegistryPatientsByLastReviewedDateAndName()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients?begin_date=01-JAN-14&end_date=01-JAN-16&mrn=1731238&name=TOMMIE WHITT";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsByLastReviewedDateAndName Output:");
		System.out.println(retrievedPatientJsonString);		
	}
	
	@Test
	public void test_getRegistryPatientsByStatusAndName()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients?status=Rejected&name=JOHN TALMaGE";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsByStatusAndName Output:");
		System.out.println(retrievedPatientJsonString);		
	}
	
	@Test
	public void test_getRegistryPatientsByStatusAndMrn()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients?status=Rejected&mrn=2906970";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsByStatusAndMrn Output:");
		System.out.println(retrievedPatientJsonString);		
	}
	
	@Test
	public void test_getRegistryPatientsByStatusAndLastReviewDate()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/registrypatients?status=Rejected&begin_date=01-SEP-15&end_date=01-JAN-16";
		String retrievedPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsByStatusAndLastReviewDate Output:");
		System.out.println(retrievedPatientJsonString);		
	}
}
