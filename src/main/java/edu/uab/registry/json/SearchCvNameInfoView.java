package edu.uab.registry.json;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.SearchCvNameInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class SearchCvNameInfoView extends Views {

	

	public static String createSearchInfoToJsonString(SearchCvNameInfo searchInfo, boolean success, String errorMessage, boolean pretty) {
		
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		searchInfo.setWebservice_status(statusMsg);
		StringWriter registrySearchCVNameInfoJsonString = new StringWriter();
		
		try{
			ObjectMapper objectMapper = new ObjectMapper();	  
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);
			objectMapper.writeValue(registrySearchCVNameInfoJsonString, searchInfo);
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
				
		return registrySearchCVNameInfoJsonString.toString();
	}

}
