package edu.uab.registry.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.json.Views;


// A generic object that can be used to return data and a standardized status message from a web service.
public class WebServiceResult {
	
	protected StatusMessage webservice_status;
	
	
	// C-tor
	public WebServiceResult() {}
	
	// C-tor
	public WebServiceResult(StatusMessage webserviceStatus_) {
		webservice_status = webserviceStatus_;
	}
	
	
	// Web service status
	public StatusMessage getWebServiceStatus() {
		return webservice_status;
	}
	public void setWebServiceStatus(String message_, boolean success_) {
		webservice_status = new StatusMessage();
		if (success_) {			
			webservice_status.setStatus(Constants.SUCCESS);
			webservice_status.setMessage(message_);
		} else {			
			webservice_status.setStatus(Constants.ERROR);
			webservice_status.setMessage(message_);
		}
	}

	// Export the object's contents as JSON.
	public String toJSON(boolean pretty_) {
		
		String json = "";
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty_);	 
	        
			// Convert the object to JSON.  	        	       	        	     	        
	        json = objectMapper.writerWithView(Views.Normal.class).writeValueAsString(this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return json;
	}

	public String toJSON() {
		return toJSON(true);
	}
}
