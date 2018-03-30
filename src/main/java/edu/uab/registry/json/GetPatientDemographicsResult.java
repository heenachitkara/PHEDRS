package edu.uab.registry.json;

import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.domain.RegistryPatientDemographics;
import edu.uab.registry.util.WebServiceResult;

public class GetPatientDemographicsResult extends WebServiceResult {

	private RegistryPatientDemographics _patientDemographics = null;
	private GenericRegistryPatient _registryPatient = null;
	
	
	public RegistryPatientDemographics getPatientDemographics() {
		return _patientDemographics;
	}
	public void setPatientDemographics(RegistryPatientDemographics patientDemographics_) {
		_patientDemographics = patientDemographics_;
	}

	public GenericRegistryPatient getRegistryPatient() {
		return _registryPatient;
	}
	public void setRegistryPatient(GenericRegistryPatient registryPatient_) {
		_registryPatient = registryPatient_;
	}
}
