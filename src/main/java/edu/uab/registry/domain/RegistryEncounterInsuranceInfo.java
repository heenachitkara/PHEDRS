package edu.uab.registry.domain;

import java.util.List;

public class RegistryEncounterInsuranceInfo {
	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	
	public List<RegistryEncounterInsuranceList> get_encounters_insurance_list() {
		return encounter_insurance_list;
	}
	public void set_encounters_insurance_list(List<RegistryEncounterInsuranceList> encounter_insurance_list) {
		this.encounter_insurance_list = encounter_insurance_list;
	}
	
	
	
	// Variable Declaration
	private StatusMessage webservice_status;
	private List<RegistryEncounterInsuranceList> encounter_insurance_list; 
	public void setWebservice_status(StatusMessage webservice_status) {
	 this.webservice_status = webservice_status;
	}

}
