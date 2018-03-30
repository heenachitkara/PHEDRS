package edu.uab.registry.controller.test;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class GetRegistryPatientsTest 
{			
	@Test
	public void test_getRegistryPatients_TestCase_1()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryPatients_TestCase_1  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrypatients?registry_id=2689&assigner_id=&status=Validated,Rejected";
		String registryPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatients_TestCase_1 Output:");
		System.out.println(registryPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryPatients_TestCase_1--DONE-----------------------------------");
	}
	
	@Test
	public void test_getRegistryPatients_TestCase_2()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryPatients_TestCase_2  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrypatients?registry_id=2689&assigner_id=assamal&status=Validated,Rejected";
		String registryPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatients_TestCase_2 Output:");
		System.out.println(registryPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryPatients_TestCase_2--DONE-----------------------------------");
	}
	
	@Test
	public void test_getRegistryPatients_TestCase_3()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryPatients_TestCase_3  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrypatients?registry_id=2690&assigner_id=assamal&status=Validated";
		String registryPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatients_TestCase_3 Output:");
		System.out.println(registryPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryPatients_TestCase_3--DONE-----------------------------------");
	}	
	
	@Test
	public void test_getRegistryPatients_TestCase_4()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryPatients_TestCase_4  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrypatients?registry_id=2689&assigner_id=assamal&status=";
		String registryPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatients_TestCase_4 Output:");
		System.out.println(registryPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryPatients_TestCase_4--DONE-----------------------------------");
	}
	
	@Test
	public void test_getRegistryPatients_TestCase_5()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryPatients_TestCase_5  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrypatients?registry_id=&assigner_id=assamal&status=";
		String registryPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryPatients_TestCase_5 Output:");
		System.out.println(registryPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryPatients_TestCase_5--DONE-----------------------------------");
	}
}
