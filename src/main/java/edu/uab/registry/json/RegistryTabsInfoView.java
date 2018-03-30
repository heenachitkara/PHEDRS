package edu.uab.registry.json;

import java.io.StringWriter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.RegistryTabsInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class RegistryTabsInfoView {

	public static String createRegistryTabsInfoToJsonString(RegistryTabsInfo regTabsInfo, boolean success, String errorMessage, boolean pretty)
	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		regTabsInfo.setWebservice_status(statusMsg);
		StringWriter registryTabsInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(registryTabsInfoJsonString, regTabsInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return registryTabsInfoJsonString.toString();
	}

}
