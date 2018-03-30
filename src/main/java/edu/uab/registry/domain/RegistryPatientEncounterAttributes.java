package edu.uab.registry.domain;

import java.util.List;

public class RegistryPatientEncounterAttributes 
{
	public List<RegistryPatientCode> getRegistry_patient_code_list() {
		return registry_patient_code_list;
	}
	public void setRegistry_patient_code_list(List<RegistryPatientCode> registry_patient_code_list) {
		this.registry_patient_code_list = registry_patient_code_list;
	}
	public List<RegistryPatientAttribute> getRegistry_patient_attribute_list() {
		return registry_patient_attribute_list;
	}
	public void setRegistry_patient_attribute_list(List<RegistryPatientAttribute> registry_patient_attribute_list) {
		this.registry_patient_attribute_list = registry_patient_attribute_list;
	}
	
	private List<RegistryPatientCode> registry_patient_code_list;
	private List<RegistryPatientAttribute> registry_patient_attribute_list;
}
