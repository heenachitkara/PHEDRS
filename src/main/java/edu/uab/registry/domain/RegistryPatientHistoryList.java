package edu.uab.registry.domain;

import java.util.List;

public class RegistryPatientHistoryList 
{	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}

	public void setWebservice_status(StatusMessage webservice_status) {
		this.webservice_status = webservice_status;
	}

	public List<RegistryPatientHistory> getRegistry_patient_history_list() {
		return registry_patient_history_list;
	}

	public void setRegistry_patient_history_list(List<RegistryPatientHistory> registry_patient_history_list) {
		this.registry_patient_history_list = registry_patient_history_list;
	}

	private StatusMessage webservice_status;
	private List<RegistryPatientHistory> registry_patient_history_list;	
}
