package edu.uab.registry.domain;

import java.util.List;

public class PatientAttributeCVInfo {
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	public void setWebservice_status(StatusMessage webservice_status) {
		 this.webservice_status = webservice_status;
	}
	
	
	public List<PatientCVList> getPatientCVList(){
		return patient_cv_list;
	}
	
	public void setPatientCVList(List<PatientCVList> patient_cv_list) {
		this.patient_cv_list = patient_cv_list;
	}
	
	private StatusMessage webservice_status; 
	private List<PatientCVList> patient_cv_list;

}
