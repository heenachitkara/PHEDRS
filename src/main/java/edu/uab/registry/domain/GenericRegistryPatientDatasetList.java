package edu.uab.registry.domain;

import java.util.List;

public class GenericRegistryPatientDatasetList 
{						
	public List<GenericRegistryPatient> getPatient_dataset_list() {
		return patient_dataset_list;
	}

	public void setPatient_dataset_list(List<GenericRegistryPatient> patient_dataset_list) {
		this.patient_dataset_list = patient_dataset_list;
	}

	private List<GenericRegistryPatient> patient_dataset_list;	
}
