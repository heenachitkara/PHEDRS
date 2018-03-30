package edu.uab.registry.json;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.RegConfigPatientAttributesInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class RegConfigPatientAttributesInfoView extends Views {
	
	
	public static String createRegPatientAttributesInfoToJsonString(RegConfigPatientAttributesInfo regConfigPatientAttributesInfo, boolean success, String errorMessage, boolean pretty)

	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		regConfigPatientAttributesInfo.setWebservice_status(statusMsg);
		StringWriter regConfigPatientAttributesInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(regConfigPatientAttributesInfoJsonString, regConfigPatientAttributesInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return regConfigPatientAttributesInfoJsonString.toString();
	}
	
	
	public static void main (String[] args){
		
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(RegConfigPatientAttributesInfo.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	
	

}
