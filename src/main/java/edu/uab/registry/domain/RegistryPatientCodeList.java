package edu.uab.registry.domain;

import java.util.List;

public class RegistryPatientCodeList 
{							
	public List<RegistryPatientCode> getRegistry_patient_code_list() {
		return registry_patient_code_list;
	}
	public void setRegistry_patient_code_list(List<RegistryPatientCode> registry_patient_code_list) {
		this.registry_patient_code_list = registry_patient_code_list;
	}

	private List<RegistryPatientCode> registry_patient_code_list;	
}
