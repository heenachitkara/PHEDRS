package edu.uab.registry.domain;

import java.util.List;

public class SearchCvNameInfo {

	

	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	
	public void setWebservice_status(StatusMessage webservice_status) {
		 this.webservice_status = webservice_status;
	}
	
	public List<SearchCvNameList> getSearchCVNameList(){
		return search_cv_name_list;
	}
	
	public void setCVNameLists(List<SearchCvNameList> search_cv_name_list ){
		this.search_cv_name_list = search_cv_name_list;
	}
	
	private StatusMessage webservice_status;
	private List<SearchCvNameList> search_cv_name_list; 
	
}
