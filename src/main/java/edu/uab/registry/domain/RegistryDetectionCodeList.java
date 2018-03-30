package edu.uab.registry.domain;

import java.util.List;

public class RegistryDetectionCodeList 
{	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	public void setWebservice_status(StatusMessage webservice_status) {
		this.webservice_status = webservice_status;
	}
	public List<RegistryDetectionCode> getRegistry_detection_code_list() {
		return registry_detection_code_list;
	}
	public void setRegistry_detection_code_list(List<RegistryDetectionCode> registry_detection_code_list) {
		this.registry_detection_code_list = registry_detection_code_list;
	}

	private StatusMessage webservice_status;
	private List<RegistryDetectionCode> registry_detection_code_list;	
}
