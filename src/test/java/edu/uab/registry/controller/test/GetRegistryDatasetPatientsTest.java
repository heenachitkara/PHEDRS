package edu.uab.registry.controller.test;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class GetRegistryDatasetPatientsTest 
{			
	@Test
	public void test_getRegistryDatasetPatients_TestCase_1()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryDatasetPatients_TestCase_1  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrydatasetpatients?registry_id=2689&dataset_name=Multiple Myeloma Kidney Dataset";
		String registryPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryDatasetPatients_TestCase_1 Output:");
		System.out.println(registryPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryDatasetPatients_TestCase_1--DONE-----------------------------------");
	}
	
	@Test
	public void test_getRegistryDatasetPatients_TestCase_2()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryDatasetPatients_TestCase_2  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrydatasetpatients?registry_id=2690&dataset_name=Multiple Myeloma Kidney Dataset";
		String registryPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryDatasetPatients_TestCase_2 Output:");
		System.out.println(registryPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryDatasetPatients_TestCase_2--DONE-----------------------------------");
	}
	
	@Test
	public void test_getRegistryDatasetPatients_TestCase_3()
	{
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("---------------------------------test_getRegistryDatasetPatients_TestCase_3  STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/getregistrydatasetpatients?registry_id=2689&dataset_name=COPD";
		String registryPatientJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println("test_getRegistryDatasetPatients_TestCase_3 Output:");
		System.out.println(registryPatientJsonString);		
		System.out.println("---------------------------------test_getRegistryDatasetPatients_TestCase_3--DONE-----------------------------------");
	}	
}
