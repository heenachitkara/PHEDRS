package edu.uab.registry.json;

import java.io.StringWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.dao.NlpDocumentsDao;
import edu.uab.registry.domain.NlpPatientHitsDocsList;
import edu.uab.registry.domain.NlpShowDocument;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;
import edu.uab.registry.util.WebServiceUtils;

public class NlpDocumentsView extends Views 
{	
	public static String createNlpShowDocumentToJsonString(NlpPatientHitsDocsList nlpShowDocList, boolean success, String errorMessage, boolean pretty)
	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, errorMessage);
		nlpShowDocList.setWebservice_status(statusMsg);
		StringWriter nlpShowDocumentJsonString = new StringWriter();
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       
	        objectMapper.writeValue(nlpShowDocumentJsonString, nlpShowDocList);	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		return nlpShowDocumentJsonString.toString();
	}
		
	public static void main(String[] args) 
	{
		String nlpShowDocumentJsonString = null;
		NlpPatientHitsDocsList nlpPatHitsDocsLIst = new NlpPatientHitsDocsList();
		
		
		
		
		try {
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
						
			String docId = "12771596";
			
			String registryID = "2861";
			
			NlpDocumentsDao nlpDocDao = (NlpDocumentsDao) context.getBean("nlpDocumentsDao");
			
			List<NlpShowDocument> docWithHits = nlpDocDao. getNlpShowDocument(docId,registryID);	
						
			nlpPatHitsDocsLIst.setNlpShowDocsList(docWithHits);
			nlpShowDocumentJsonString = createNlpShowDocumentToJsonString(nlpPatHitsDocsLIst, true, Constants.EMPTY, true);
			System.out.println("NLP Show Document JSON is\n"+nlpShowDocumentJsonString);
			System.out.println("Log.ElapsedTime.DocumentView=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		} catch(Throwable th) {
			th.printStackTrace();			
			nlpShowDocumentJsonString = createNlpShowDocumentToJsonString(nlpPatHitsDocsLIst, false, th.getMessage(), true);
			System.out.println("NLP Show Document JSON is\n"+nlpShowDocumentJsonString);
		} finally {			
			context.close();
			context = null;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(NlpDocumentsView.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
}
