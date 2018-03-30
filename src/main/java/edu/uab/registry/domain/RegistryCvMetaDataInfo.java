package edu.uab.registry.domain;

import java.util.List;

public class RegistryCvMetaDataInfo {
	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	/*public void setWebservice_status(StatusMessage webservice_status) {
		this.webservice_status = webservice_status;
	}*/
	public List<CvMetaDataList> getCv_metadata_list() {
		return cv_metadata_list;
	}
	public void setCv_metadata_list(List<CvMetaDataList> cv_metadata_list) {
		this.cv_metadata_list = cv_metadata_list;
	}
	
	
	
	// Variable Declaration
	private StatusMessage webservice_status;
	private List<CvMetaDataList> cv_metadata_list; 
	public void setWebservice_status(StatusMessage webservice_status) {
	 this.webservice_status = webservice_status;
	}

}
