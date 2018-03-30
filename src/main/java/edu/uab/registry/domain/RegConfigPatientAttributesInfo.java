package edu.uab.registry.domain;

import java.util.List;

public class RegConfigPatientAttributesInfo {
	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	public List<RegConfigPatientAttributesList> get_reg_config_patient_attributes() {
		return reg_config_patient_attr__list;
	}
	public void set_reg_config_patient_attributes(List<RegConfigPatientAttributesList> reg_config_patient_attr__list) {
		this.reg_config_patient_attr__list = reg_config_patient_attr__list;
	}
	
	public void setWebservice_status(StatusMessage webservice_status) {
		this.webservice_status = webservice_status;
		}
	
	// Variable Declaration
	private StatusMessage webservice_status;
	private List<RegConfigPatientAttributesList> reg_config_patient_attr__list; 
	

}
