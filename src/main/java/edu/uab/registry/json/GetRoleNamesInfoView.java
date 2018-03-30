package edu.uab.registry.json;
import java.io.StringWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.uab.registry.domain.GetRoleNamesInfo;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class GetRoleNamesInfoView {

	public static String createRoleNamesInfoToJsonString(GetRoleNamesInfo roleNamesInfo, boolean success, String errorMessage,
			boolean pretty) {
		
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		roleNamesInfo.setWebservice_status(statusMsg);
		StringWriter roleNamesInfoJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(roleNamesInfoJsonString, roleNamesInfo);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return roleNamesInfoJsonString.toString();
	}

}
