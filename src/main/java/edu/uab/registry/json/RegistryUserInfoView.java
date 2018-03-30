package edu.uab.registry.json;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.RegistryUserInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class RegistryUserInfoView {

	public static String createRegistryUsersListInfoToJsonString(RegistryUserInfo regUserInfo, boolean success, String errorMessage,
			boolean pretty) {
		
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		regUserInfo.setWebservice_status(statusMsg);
		StringWriter regUserInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(regUserInfoJsonString, regUserInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return regUserInfoJsonString.toString();
		
	}

}
