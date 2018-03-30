package edu.uab.registry.json;

import edu.uab.registry.util.WebServiceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.domain.PrintRegistryIDwithStatus;




public class GetAddRegistryResult  extends Views  {
	
	
	public static String OrmStatusToJsonString(boolean success, int cvTermID, boolean pretty)
	{
		PrintRegistryIDwithStatus statusMsgAndRegID = WebServiceUtils.printRegistryID(success, cvTermID);
		String genericOrmStatusJsonString = null;
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       	        	     	        
	        genericOrmStatusJsonString = objectMapper.writerWithView(Views.Normal.class).writeValueAsString(statusMsgAndRegID);
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
			genericOrmStatusJsonString = OrmStatusToJsonString(true, 0, true);
			System.out.println("Generic ORM Status JSON is\n"+genericOrmStatusJsonString);
		} catch(Throwable th) {
			th.printStackTrace();
			genericOrmStatusJsonString = OrmStatusToJsonString(false, 0, true);
			System.out.println("Generic ORM Status JSON is\n"+genericOrmStatusJsonString);
		}
	}
	
	
	

}
