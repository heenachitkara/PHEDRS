package edu.uab.registry.json;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.dao.CvTermsDao;
import edu.uab.registry.domain.CvTerms;
import edu.uab.registry.domain.CvTermsList;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.WebServiceUtils;

public class CvTermsView extends Views 
{
	public static String cvtermsToJsonString(CvTermsList cvtermList, boolean success, String statusMessage, boolean pretty)
	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, statusMessage);
		cvtermList.setWebservice_status(statusMsg);
		String cvTermsJsonString = "";
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       	        	     	        
	        cvTermsJsonString = objectMapper.writerWithView(Views.Normal.class).writeValueAsString(cvtermList);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return cvTermsJsonString;
	}		
	
	public static void main(String[] args) 
	{		
		CvTermsList cvtermList = new CvTermsList();
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml")){
			CvTermsDao cvTermsDao = (CvTermsDao) context.getBean("cvTermsDao");			
			String cvs = "DRG Codes,Workflow Status CV,UAB COPD Registry Encounter Attribute CV,PHEDRS Patient Status CV,PHEDRS Actor Roles"; 						
			List<CvTerms> cvList = cvTermsDao.getCvterms(cvs);						
			cvtermList.setCvtermList(cvList);
			logger.debug("Number of cvterms :" + cvList.size());
			String cvTermsJsonString = cvtermsToJsonString(cvtermList, true, "", true);
			logger.debug("CvTerms JSON is\n"+cvTermsJsonString);
		} catch (Throwable th) {	
			th.printStackTrace();
			String cvTermsJsonString = cvtermsToJsonString(cvtermList, false, th.getMessage(), true);
			logger.debug("CvTerms JSON is\n"+cvTermsJsonString);
		}
	}
		
	private static final Logger logger = LoggerFactory.getLogger(CvTermsView.class);
}
