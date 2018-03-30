package edu.uab.registry.json;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.RegConfigEncounterAttributesInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class RegConfigEncounterAttributesInfoView extends Views {
	
	
	public static String createRegEncounterAttributesInfoToJsonString(RegConfigEncounterAttributesInfo regConfigEncounterAttributesInfo, boolean success, String errorMessage, boolean pretty)

	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		regConfigEncounterAttributesInfo.setWebservice_status(statusMsg);
		StringWriter regConfigEncounterAttributesInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(regConfigEncounterAttributesInfoJsonString, regConfigEncounterAttributesInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return regConfigEncounterAttributesInfoJsonString.toString();
	}
	
	
	public static void main (String[] args){
		
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(RegConfigEncounterAttributesInfo.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	
	
	
	
	
	
	
	
	
	

}
