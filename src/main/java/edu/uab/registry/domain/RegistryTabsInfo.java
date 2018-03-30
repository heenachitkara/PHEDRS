package edu.uab.registry.domain;

import java.util.List;

public class RegistryTabsInfo {
	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	
	public void setWebservice_status(StatusMessage webservice_status) {
		 this.webservice_status = webservice_status;
	}
	
	public List<RegistryTabsList> getRegistryTabsList(){
		return regTabsLists;
	}

	public void setRegistryTabsList(List<RegistryTabsList> regTabsLists) {
		this.regTabsLists = regTabsLists;
		
	}
	
	private StatusMessage webservice_status;
	private List<RegistryTabsList> regTabsLists; 

}
