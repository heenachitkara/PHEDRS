package edu.uab.registry.domain;

import java.util.List;

public class GetConfigAddAttributesInfo {

	public void setAddConfigAttributesList(List<GetConfigAddAttributesList> configAddAttributesLists) {
		this.config_add_attr_list = configAddAttributesLists;
		
	}
	
	public List<GetConfigAddAttributesList> getConfigAddAttributesList(){
		return config_add_attr_list;
	}
	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}

	public void setWebservice_status(StatusMessage webservice_status) {
		this.webservice_status = webservice_status;
		
	}

	
	private StatusMessage webservice_status;
	private List<GetConfigAddAttributesList> config_add_attr_list; 
	
	
	
	
}
