package edu.uab.registry.json;

import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.util.WebServiceResult;

import java.util.List;

public class SearchResult extends WebServiceResult {

	protected List<GenericRegistryPatient> registry_patients = null;

	// Registry patients
	public List<GenericRegistryPatient> getRegistryPatients() {
		return registry_patients;
	}
	public void setRegistryPatients(List<GenericRegistryPatient> registryPatients_) {
		registry_patients = registryPatients_;
	}
}
