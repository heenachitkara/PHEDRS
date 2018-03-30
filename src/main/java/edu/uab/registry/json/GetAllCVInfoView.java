package edu.uab.registry.json;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.GetAllCVInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class GetAllCVInfoView {

	public static String createCVListInfoToJsonString(GetAllCVInfo getAllInfo, boolean success, String errorMessage, boolean pretty) {
		
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		getAllInfo.setWebservice_status(statusMsg);
		StringWriter getAllCVInfoJsonString = new StringWriter();
		
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(getAllCVInfoJsonString, getAllInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return getAllCVInfoJsonString.toString();
		
		
		
	}

}
