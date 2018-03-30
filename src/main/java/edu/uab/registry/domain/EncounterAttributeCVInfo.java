package edu.uab.registry.domain;

import java.util.List;

public class EncounterAttributeCVInfo {
	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	public void setWebservice_status(StatusMessage webservice_status) {
		 this.webservice_status = webservice_status;
	}
	
	public List<EncounterCVList> getEncounterCVList(){
		return encounter_cv_list;
	}

	public void setEncounterCVList(List<EncounterCVList> encounter_cv_list) {
		this.encounter_cv_list = encounter_cv_list;
		
	}

	
	
	private StatusMessage webservice_status; 
	private List<EncounterCVList> encounter_cv_list;
	
	
}
