package edu.uab.registry.json;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uab.registry.dao.impl.DetectionCodesDaoImpl;
import edu.uab.registry.domain.RegistryDetectionCode;
import edu.uab.registry.domain.RegistryDetectionCodeList;
import edu.uab.registry.domain.StatusMessage;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.WebServiceUtils;

public class DetectionCodesView extends Views 
{
	public static String DetectionCodesToJsonString(RegistryDetectionCodeList rdcL, boolean success, String statusMessage, boolean pretty)
	{
		StatusMessage statusMsg = WebServiceUtils.generateStatusMessage(success, statusMessage);
		rdcL.setWebservice_status(statusMsg);
		String registryDetectionCodesJsonString = "";
		try {			
	        ObjectMapper objectMapper = new ObjectMapper();	        	        
	        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, pretty);	         	        	       	        	     	        
	        registryDetectionCodesJsonString = objectMapper.writerWithView(Views.Normal.class).writeValueAsString(rdcL);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return registryDetectionCodesJsonString;
	}
	
	public static void main(String[] args) 
	{		
		try {					
			DetectionCodesDaoImpl detectionCodesDao = (DetectionCodesDaoImpl) context.getBean("detectionCodesDao");
			String mrn = "423508";
			int registryId = 2861;
			RegistryDetectionCodeList registryDetectionCodeList = detectionCodesDao.getDetectionCodes(mrn, registryId);	
			List<RegistryDetectionCode> rdcLIst = registryDetectionCodeList.getRegistry_detection_code_list();			
			String registryDetectionCodesJsonString = DetectionCodesToJsonString(registryDetectionCodeList, true, Constants.EMPTY, true);
			System.out.println("Detection Codes List JSON is\n"+registryDetectionCodesJsonString);	
			System.out.println("Number of Detection Codes :" + rdcLIst.size());
		} catch (Throwable th) {	
			th.printStackTrace();
		} finally {
			context.close();
			context = null;
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DetectionCodesView.class);
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
}
