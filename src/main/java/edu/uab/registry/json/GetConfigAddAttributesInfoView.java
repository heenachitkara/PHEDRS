package edu.uab.registry.json;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.GetConfigAddAttributesInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class GetConfigAddAttributesInfoView {

	public static String createAddConfigAttributesListInfoToJsonString(GetConfigAddAttributesInfo configAddAttrInfo,boolean success, String errorMessage, boolean pretty) {
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		configAddAttrInfo.setWebservice_status(statusMsg);
		StringWriter configAddAttrInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(configAddAttrInfoJsonString, configAddAttrInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return configAddAttrInfoJsonString.toString();
	}

}
