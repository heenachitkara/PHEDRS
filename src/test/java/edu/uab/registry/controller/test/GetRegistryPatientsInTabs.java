package edu.uab.registry.controller.test;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class GetRegistryPatientsInTabs 
{			
	@Test
	public void test_getRegistryPatientsInTabs_TestCase1()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryPatientsInTabs_TestCase1  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrypatientsintabs?registry_id=2861&tab_type=REGISTRY_INPATIENT_REVIEW";
		String copdPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsInTabs_TestCase1 Output:");
		System.out.println(copdPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryPatientsInTabs_TestCase1--DONE-----------------------------------");
	}
	
	@Test
	public void test_getRegistryPatientsInTabs_TestCase2()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryPatientsInTabs_TestCase2  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrypatientsintabs?registry_id=2861&tab_type=90_DAY_WINDOW";
		String copdPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsInTabs_TestCase2 Output:");
		System.out.println(copdPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryPatientsInTabs_TestCase2--DONE-----------------------------------");
	}
	
	@Test
	public void test_getRegistryPatientsInTabs_TestCase3()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryPatientsInTabs_TestCase3  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrypatientsintabs?registry_id=2861&tab_type=REGISTRY_INPATIENT";
		String copdPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatientsInTabs_TestCase3 Output:");
		System.out.println(copdPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryPatientsInTabs_TestCase3--DONE-----------------------------------");
	}
}
