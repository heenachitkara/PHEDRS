package edu.uab.registry.json;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.PatientAttributeCVInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class PatientAttributeCVInfoView {

	public static String createPatientAttributeCvInfoToJsonString(PatientAttributeCVInfo patientCVDataInfo, boolean success, String errorMessage, boolean pretty) {
		
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		patientCVDataInfo.setWebservice_status(statusMsg);
		StringWriter patientCVDataInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(patientCVDataInfoJsonString, patientCVDataInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return patientCVDataInfoJsonString.toString();
		
	}

}
