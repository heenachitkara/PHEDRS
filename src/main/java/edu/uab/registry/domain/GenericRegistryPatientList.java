package edu.uab.registry.domain;

import java.util.List;

public class GenericRegistryPatientList 
{		
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	public void setWebservice_status(StatusMessage webservice_status) {
		this.webservice_status = webservice_status;
	}
	public List<GenericRegistryPatient> getRegistry_patient_list() {
		return registry_patient_list;
	}
	public void setRegistry_patient_list(List<GenericRegistryPatient> registry_patient_list) {
		this.registry_patient_list = registry_patient_list;
	}
	public List<RegistryEncounterAttribute> getRegistry_all_encounter_attributes_list() {
		return registry_all_encounter_attributes_list;
	}
	public void setRegistry_all_encounter_attributes_list(List<RegistryEncounterAttribute> registry_all_encounter_attributes_list) {
		this.registry_all_encounter_attributes_list = registry_all_encounter_attributes_list;
	}
	public List<RegistryPatientAttribute> getRegistry_all_patient_attributes_list() {
		return registry_all_patient_attributes_list;
	}
	public void setRegistry_all_patient_attributes_list(List<RegistryPatientAttribute> registry_all_patient_attributes_list) {
		this.registry_all_patient_attributes_list = registry_all_patient_attributes_list;
	}

	private StatusMessage webservice_status;
	private List<GenericRegistryPatient> registry_patient_list;	
	private List<RegistryEncounterAttribute> registry_all_encounter_attributes_list;
	private List<RegistryPatientAttribute> registry_all_patient_attributes_list;
}
