package edu.uab.registry.domain;

public class RegistryPatientCode 
{			
	public String getMrn() {
		return mrn;
	}
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}
	public int getRegistry_id() {
		return registry_id;
	}
	public void setRegistry_id(int registry_id) {
		this.registry_id = registry_id;
	}
	public int getEncounter_key() {
		return encounter_key;
	}
	public void setEncounter_key(int encounter_key) {
		this.encounter_key = encounter_key;
	}
	public String getCode_value() {
		return code_value;
	}
	public void setCode_value(String code_value) {
		this.code_value = code_value;
	}
	public String getCode_assignment_date() {
		return code_assignment_date;
	}
	public void setCode_assignment_date(String code_assignment_date) {
		this.code_assignment_date = code_assignment_date;
	}
	public int getCode_type() {
		return code_type;
	}
	public void setCode_type(int code_type) {
		this.code_type = code_type;
	}
	public String getCode_description() {
		return code_description;
	}
	public void setCode_description(String code_description) {
		this.code_description = code_description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDetection_criteria() {
		return detection_criteria;
	}
	public void setDetection_criteria(String detection_criteria) {
		this.detection_criteria = detection_criteria;
	}
	public int getCvterm_id() {
		return cvterm_id;
	}
	public void setCvterm_id(int cvterm_id) {
		this.cvterm_id = cvterm_id;
	}

	private String mrn;		    	     // registry_patient_codes.uab_mrn
	private int registry_id;			 // registry_patient.registry_id
	private int encounter_key; 		 	 // registry_encounter.encounter_key
	private String code_value;   		 // dbxref.accession
	private String code_assignment_date; // registry_patient_codes.code_assignment_date
	private int code_type;   		 	 // dbxref.db_id
	private String code_description;     // dbxref.description
	private String name; 				 // db.name
	private String detection_criteria;   // detection criteria
	private int cvterm_id;               // detection cvterm
}
