package edu.uab.registry.domain;

import java.util.Date;

public class RegistryEncounterAttribute 
{
	public String getMrn() {
		return mrn;
	}
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}
	public int getEvent_cvterm_id() {
		return event_cvterm_id;
	}
	public void setEvent_cvterm_id(int event_cvterm_id) {
		this.event_cvterm_id = event_cvterm_id;
	}
	public int getEncounter_key() {
		return encounter_key;
	}
	public void setEncounter_key(int encounter_key) {
		this.encounter_key = encounter_key;
	}
	public int getAssigner_id() {
		return assigner_id;
	}
	public void setAssigner_id(int assigner_id) {
		this.assigner_id = assigner_id;
	}
	public int getRegistry_id() {
		return registry_id;
	}
	public void setRegistry_id(int registry_id) {
		this.registry_id = registry_id;
	}
	public int getCvterm_id() {
		return cvterm_id;
	}
	public void setCvterm_id(int cvterm_id) {
		this.cvterm_id = cvterm_id;
	}
	public String getCvterm_name() {
		return cvterm_name;
	}
	public void setCvterm_name(String cvterm_name) {
		this.cvterm_name = cvterm_name;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getRegistrar_comment() {
		return registrar_comment;
	}
	public void setRegistrar_comment(String registrar_comment) {
		this.registrar_comment = registrar_comment;
	}
	public Date getAssignment_date() {
		return assignment_date;
	}
	public void setAssignment_date(Date assignment_date) {
		this.assignment_date = assignment_date;
	}
	public int getCode_id() {
		return code_id;
	}
	public void setCode_id(int code_id) {
		this.code_id = code_id;
	}
	public String getActor_name() {
		return actor_name;
	}
	public void setActor_name(String actor_name) {this.actor_name = actor_name;}

	public String getDbxref_accession() { return this.dbxref_accession; }
	public void setDbxref_accession(String dbxrefAccession_) { this.dbxref_accession = dbxrefAccession_; }
	public String getDbxref_description() {return this.dbxref_description; }
	public void setDbxref_description(String dbxrefDescription_) { this.dbxref_description = dbxrefDescription_; }

	public String getIs_valid() { return this.is_valid; }
	public void setIs_valid(String isValid_) { this.is_valid = isValid_; }
	
	/*public int getFin_info_num() {
		return financial_information_number;
	}
	public void setFin_info_num(int financial_information_number) {
		this.financial_information_number = financial_information_number;
	}*/

	private String actor_name;
	private int assigner_id;
	private Date assignment_date;
	private int code_id;
	private int cvterm_id;
	private String cvterm_name;
	private String dbxref_accession;
	private String dbxref_description;
	private int encounter_key;
	private int event_cvterm_id;
	private String is_valid;
	private String mrn;
	private String registrar_comment;
	private int registry_id;
	private int value;
	//private int financial_information_number;
}
