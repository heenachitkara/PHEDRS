package edu.uab.registry.controller.test;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class AuthorizeTest 
{
	@Test
	public void test_authorizen()
	{
		RestTemplate restTemplate = new RestTemplate();
		String wsUrl = "http://localhost:8080/RegistryWS/authorize?registry_id=52408&login_id=assamal";
		boolean authorized = restTemplate.getForObject(wsUrl, Boolean.class);
		System.out.println("authorized:" + authorized);		
	}
}
