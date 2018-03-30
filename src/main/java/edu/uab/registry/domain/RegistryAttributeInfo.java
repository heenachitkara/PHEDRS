package edu.uab.registry.domain;

import java.util.List;

public class RegistryAttributeInfo {
	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	
	public void setWebservice_status(StatusMessage webservice_status) {
		 this.webservice_status = webservice_status;
	}
	
	public List<RegistryAttributeList> getRegistryAttributesList(){
		return registry_attribute_list;
	}
	
	public void setRegistryAttributesList(List<RegistryAttributeList> registry_attribute_list ){
		this.registry_attribute_list = registry_attribute_list;
	}
	
	private StatusMessage webservice_status;
	private List<RegistryAttributeList> registry_attribute_list; 
	
}
