package edu.uab.registry.json;

import java.util.List;

import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.util.WebServiceResult;

public class GetRegistryPatientsInTabsResult extends WebServiceResult {

	protected List<GenericRegistryPatient> registry_patients = null;
	protected String tab_key = null;
	
	
	// Registry patients
	public List<GenericRegistryPatient> getRegistryPatients() {
		return registry_patients;
	}
	public void setRegistryPatients(List<GenericRegistryPatient> registryPatients_) {
		registry_patients = registryPatients_;
	}

	// Tab key 
	public String getTabKey() {
		return tab_key;
	}
	public void setTabKey(String tabKey_) {
		tab_key = tabKey_;
	}
}
