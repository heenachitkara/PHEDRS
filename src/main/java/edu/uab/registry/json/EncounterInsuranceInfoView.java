package edu.uab.registry.json;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.RegistryEncounterInsuranceInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class EncounterInsuranceInfoView extends Views {
	
	public static String createEncounterInsuranceInfoToJsonString(RegistryEncounterInsuranceInfo registryEncounterInsuranceInfo, boolean success, String errorMessage, boolean pretty)

	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		registryEncounterInsuranceInfo.setWebservice_status(statusMsg);
		StringWriter registryEncounterInsuranceInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(registryEncounterInsuranceInfoJsonString, registryEncounterInsuranceInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return registryEncounterInsuranceInfoJsonString.toString();
	}
	
	
	public static void main (String[] args){
		
		
	}
	
	
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(RegistryEncounterInsuranceInfo.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");

}
