package edu.uab.registry.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.WebServiceUtils;

public class GenericOrmStatusView extends Views 
{
	public static String OrmStatusToJsonString(boolean success, String statusMessage, boolean pretty)
	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, statusMessage);		
		String genericOrmStatusJsonString = null;
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       	        	     	        
	        genericOrmStatusJsonString = objectMapper.writerWithView(Views.Normal.class).writeValueAsString(statusMsg);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return genericOrmStatusJsonString;
	}
	
	public static void main(String[] args) 
	{		
		String genericOrmStatusJsonString = null;
		try {
			//int i, j=5;
			//i = j/0;
			genericOrmStatusJsonString = OrmStatusToJsonString(true, Constants.EMPTY, true);
			System.out.println("Generic ORM Status JSON is\n"+genericOrmStatusJsonString);
		} catch(Throwable th) {
			th.printStackTrace();
			genericOrmStatusJsonString = OrmStatusToJsonString(false, th.getMessage(), true);
			System.out.println("Generic ORM Status JSON is\n"+genericOrmStatusJsonString);
		}
	}

}
