package edu.uab.registry.json;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.RegistryAttributeInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class RegistryAttributeInfoView extends Views {
	
	
	
	 public static String createRegistryAttributeListInfoToJsonString(RegistryAttributeInfo registryAttributeInfo, boolean success, String errorMessage, boolean pretty)
		{
			StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
			registryAttributeInfo.setWebservice_status(statusMsg);
			StringWriter registryAttributeInfoJsonString = new StringWriter();
			try {			
		        ObjectMapper objectMapper = new ObjectMapper();	        	        
		        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
		        objectMapper.writeValue(registryAttributeInfoJsonString, registryAttributeInfo);	        
			} catch(Exception e) {
				e.printStackTrace();
			}
			return registryAttributeInfoJsonString.toString();
		}
	
	

}
