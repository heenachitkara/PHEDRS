package edu.uab.registry.domain;

public class RegistryPatientAttribute 
{				
	public String getMrn() {
		return mrn;
	}
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}
	public String getName() {
		return name;
	}
	public int getRegsitry_id() {
		return regsitry_id;
	}
	public void setRegsitry_id(int regsitry_id) {
		this.regsitry_id = regsitry_id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus_assignment_date() {
		return status_assignment_date;
	}
	public void setStatus_assignment_date(String status_assignment_date) {
		this.status_assignment_date = status_assignment_date;
	}
	public int getRegistry_patient_cvterm_id() {
		return registry_patient_cvterm_id;
	}
	public void setRegistry_patient_cvterm_id(int registry_patient_cvterm_id) {
		this.registry_patient_cvterm_id = registry_patient_cvterm_id;
	}
	public int getRegistry_patient_id() {
		return registry_patient_id;
	}
	public void setRegistry_patient_id(int registry_patient_id) {
		this.registry_patient_id = registry_patient_id;
	}
	public int getAnnotator_id() {
		return annotator_id;
	}
	public void setAnnotator_id(int annotator_id) {
		this.annotator_id = annotator_id;
	}
	public String getAnnotation_date() {
		return annotation_date;
	}
	public void setAnnotation_date(String annotation_date) {
		this.annotation_date = annotation_date;
	}
	public String getAnnotator_comment() {
		return annotator_comment;
	}
	public void setAnnotator_comment(String annotator_comment) {
		this.annotator_comment = annotator_comment;
	}
	public String getIs_valid() {
		return is_valid;
	}
	public void setIs_valid(String is_valid) {
		this.is_valid = is_valid;
	}
	public String getStart_assignment_date() {
		return start_assignment_date;
	}
	public void setStart_assignment_date(String start_assignment_date) {
		this.start_assignment_date = start_assignment_date;
	}
	public String getEnd_assignment_date() {
		return end_assignment_date;
	}
	public void setEnd_assignment_date(String end_assignment_date) {
		this.end_assignment_date = end_assignment_date;
	}
	
	private String mrn;		    	     	// registry_patient.uab_mrn
	private int regsitry_id;					// registry_patient.registry_id
	private String name;   		 		 	// cv.name
	private String status_assignment_date; 	// registry_patient.status_assignment_date
	private int registry_patient_cvterm_id; // registry_patient_cvterm.registry_patient_cvterm_id
	private int registry_patient_id;        // registry_patient_cvterm.registry_patient_id
	private int annotator_id;   		 	// registry_patient_cvterm.annotator_id
	private String annotation_date;      	// registry_patient_cvterm.annotation_date
	private String annotator_comment;       // registry_patient_cvterm.annotator_comment 
	private String is_valid;				// registry_patient_cvterm..is_valid
	private String start_assignment_date;   // registry_patient_cvterm.start_assignment_date
	private String end_assignment_date;     // registry_patient_cvterm.end_assignment_date
}
