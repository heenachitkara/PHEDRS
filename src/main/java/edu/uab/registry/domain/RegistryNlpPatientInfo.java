package edu.uab.registry.domain;

import java.util.List;

public class RegistryNlpPatientInfo 
{	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	public void setWebservice_status(StatusMessage webservice_status) {
		this.webservice_status = webservice_status;
	}
	public List<NlpPatientHitsDocs> getNlp_document_list() {
		return nlp_document_list;
	}
	public void setNlp_document_list(List<NlpPatientHitsDocs> nlp_document_list) {
		this.nlp_document_list = nlp_document_list;
	}
	public List<RegistryPatientAttribute> getRegistry_all_patient_attribute_list() {
		return registry_all_patient_attribute_list;
	}
	public void setRegistry_all_patient_attribute_list(List<RegistryPatientAttribute> registry_all_patient_attribute_list) {
		this.registry_all_patient_attribute_list = registry_all_patient_attribute_list;
	}
	/*public GenericRegistryPatient getRegistry_patient() {
		return registry_patient;
	}
	public void setRegistry_patient(GenericRegistryPatient registry_patient) {
		this.registry_patient = registry_patient;
	}
	public RegistryPatientDemographics getPatient_demographics() {
		return patient_demographics;
	}
	public void setPatient_demographics(RegistryPatientDemographics patient_demographics) {
		this.patient_demographics = patient_demographics;
	}*/
	
	/*public List<RegistryEncounter> getRegistry_encounters_list() {
		return registry_encounters_list;
	}
	public void setRegistry_encounters_list(List<RegistryEncounter> registry_encounters_list) {
		this.registry_encounters_list = registry_encounters_list;
	}	*/
	/*public List<RegistryPatientCode> getRegistry_patient_diagnosis_codes_list() {
		return registry_patient_diagnosis_codes_list;
	}
	public void setRegistry_patient_diagnosis_codes_list(List<RegistryPatientCode> registry_patient_diagnosis_codes_list) {
		this.registry_patient_diagnosis_codes_list = registry_patient_diagnosis_codes_list;
	}*/
	

	private StatusMessage webservice_status;
	private List<NlpPatientHitsDocs> nlp_document_list;
	private List<RegistryPatientAttribute> registry_all_patient_attribute_list;
	/*private GenericRegistryPatient registry_patient;
	private RegistryPatientDemographics patient_demographics;*/	
	 
	/*private List<RegistryEncounter> registry_encounters_list;*/	
	/*private List<RegistryPatientCode> registry_patient_diagnosis_codes_list;*/
	
}
