package edu.uab.registry.json;

import edu.uab.registry.domain.RegistryPatientCode;
import edu.uab.registry.util.WebServiceResult;

import java.util.List;

public class GetPatientDiagnosisResult extends WebServiceResult {

	protected List<RegistryPatientCode> diagnoses = null;

	public List<RegistryPatientCode> getDiagnoses() { return diagnoses; }
	public void setDiagnoses(List<RegistryPatientCode> diagnoses_) {
		diagnoses = diagnoses_;
	}
}
