package edu.uab.registry.domain;

import java.util.List;

public class GetAllCVInfo {

	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	
	public void setWebservice_status(StatusMessage webservice_status) {
		 this.webservice_status = webservice_status;
	}
	
	public List<GetAllCVList> getCvList(){
		return cv_all_list;
	}
	
	
	public void setCvList(List<GetAllCVList> cv_all_list) {
		this.cv_all_list = cv_all_list;
	}
	
	
	private StatusMessage webservice_status;
	private List<GetAllCVList> cv_all_list; 

}
