package edu.uab.registry.json;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.EncounterAttributeCVInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class EncounterAttributeCVInfoView {

	public static String createEncounterAttributeCvInfoToJsonString(EncounterAttributeCVInfo encounterCVDataInfo, boolean success, String errorMessage, boolean pretty) {
		
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		encounterCVDataInfo.setWebservice_status(statusMsg);
		StringWriter encounterCVDataInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(encounterCVDataInfoJsonString, encounterCVDataInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return encounterCVDataInfoJsonString.toString();
		
	}

}
