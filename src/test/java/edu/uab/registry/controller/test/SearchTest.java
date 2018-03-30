package edu.uab.registry.controller.test;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class SearchTest 
{			
	private static String label = "test_search_TestCase_1";
	
	@Test
	public void test_search_TestCase_1()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_1";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&mrn=1434747";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_2()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_2";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&full_name=DELMAS PRICE";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_3()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_3";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&review_begin_date=01-JAN-14&review_end_date=01-JAN-16";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_4()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_4";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&status=Candidate,Rejected";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_5()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_5";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&review_begin_date=01-JAN-14&review_end_date=01-JAN-16&mrn=1434747";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_6()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_6";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&review_begin_date=01-JAN-14&review_end_date=01-JAN-16&mrn=1434747&name=DELMAS PRICE";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_7()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_7";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&status=Rejected&full_name=JOHN TALMaGE";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_8()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_8";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&status=Rejected&mrn=2906970";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_9()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_9";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&status=Rejected&review_begin_date=01-SEP-15&review_end_date=01-JAN-16";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_10()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_10";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&status=Rejected&review_begin_date=01-SEP-15&review_end_date=01-JAN-16&hscontact_begin_date=01-JAN-16&hscontact_end_date=31-MAY-16";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_11()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_11";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&status=Rejected&hscontact_begin_date=01-JAN-16&hscontact_end_date=31-MAY-16";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
	@Test
	public void test_search_TestCase_12()
	{
		RestTemplate restTemplate = new RestTemplate();	
		label = "test_search_TestCase_12";
		System.out.println("---------------------------------" + label + " STARTED--------------------------------");
		String wsUrl = "http://localhost:8080/RegistryWS/search?registry_id=2689&assigner_id=&status=Rejected&hscontact_begin_date=01-JAN-16&hscontact_end_date=01-MAY-16";
		String searchJsonString = restTemplate.getForObject(wsUrl, String.class);
		System.out.println(label+ ":");
		System.out.println(searchJsonString);		
		System.out.println("---------------------------------" + label + "--DONE-----------------------------------");
	}
	
}
