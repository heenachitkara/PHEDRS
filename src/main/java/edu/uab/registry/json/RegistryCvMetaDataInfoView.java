package edu.uab.registry.json;

import java.io.StringWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.RegistryCvMetaDataInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;


public class RegistryCvMetaDataInfoView extends Views{
	
	public static String createRegistryCvMetaDataInfoToJsonString(RegistryCvMetaDataInfo registryCvMetaDataInfo, boolean success, String errorMessage, boolean pretty)
	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		registryCvMetaDataInfo.setWebservice_status(statusMsg);
		StringWriter registryCvMetaDataInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(registryCvMetaDataInfoJsonString, registryCvMetaDataInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return registryCvMetaDataInfoJsonString.toString();
	}
	
	
	public static void main (String[] args){
		
		
	}
	
	
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(RegistryCvMetaDataInfoView.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
}
