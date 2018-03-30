package edu.uab.registry.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.uab.registry.domain.PrintRegistryIDwithStatus;
import edu.uab.registry.domain.StatusMessage;

public class WebServiceUtils 
{
	public static StatusMessage generateStatusMessage(boolean success, String statusMessage)
	{
		StatusMessage statusMsg = new StatusMessage();
		if (success) {			
			statusMsg.setStatus(Constants.SUCCESS);
			statusMsg.setMessage(statusMessage);
		} else {			
			statusMsg.setStatus(Constants.ERROR);
			statusMsg.setMessage(statusMessage);
		}
		return statusMsg;
	}	
	
	
	public static PrintRegistryIDwithStatus printRegistryID(boolean success, int registryID)
	{
		PrintRegistryIDwithStatus statusMsgAndRegID = new PrintRegistryIDwithStatus();
		if (success) {			
			statusMsgAndRegID.setStatus(Constants.SUCCESS);
			statusMsgAndRegID.setRegistryID(registryID);
		} else {			
			statusMsgAndRegID.setStatus(Constants.ERROR);
			statusMsgAndRegID.setRegistryID(0);
			
		}
		return statusMsgAndRegID;
	}	
	
	public static String printStackTraceAsString(Throwable th)
	{
		StringWriter sw = new StringWriter();
		th.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		return exceptionAsString;
	}
}
