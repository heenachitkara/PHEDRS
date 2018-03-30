package edu.uab.registry.domain;

import java.util.List;

public class CvtermNameInfo {
	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	
	public void setWebservice_status(StatusMessage webservice_status) {
		 this.webservice_status = webservice_status;
	}
	
	public List<CvtermNameList> getCvtermNameList(){
		return cvterm_name_list;
	}

	public void setCvTermNameList(List<CvtermNameList> cvNameLists) {
		this.cvterm_name_list = cvNameLists;
		
	}
	
	
	private StatusMessage webservice_status;
	private List<CvtermNameList> cvterm_name_list; 

}
