package edu.uab.registry.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class NlpPatientHitsDocs 
{		
	public String getMrn() {
		return mrn;
	}
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}
	
	public int getDocument_id() {
		return document_id;
	}
	public void setDocument_id(int document_id) {
		this.document_id = document_id;
	}
	
	public Date getNc_dos() {
		return nc_dos;
	}
	public void setNc_dos(Date nc_dos) {
		this.nc_dos = nc_dos;
	}
	public String getNc_subtype() {
		return nc_subtype;
	}
	public void setNc_subtype(String nc_subtype) {
		this.nc_subtype = nc_subtype;
	}
	public String getNc_type() {
		return nc_type;
	}
	public void setNc_type(String nc_type) {
		this.nc_type = nc_type;
	}
	
	public String getLast_review_date() {
		return last_review_date;
	}
	public void setLast_review_date(String last_review_date) {
		this.last_review_date = last_review_date;
	}
	public String getNote_content() {
		return note_content;
	}
	public void setNote_content(String note_content) {
		this.note_content = note_content;
	}
	/*public int getRegistry_id() {
	return registry_id;
	}
	public void setRegistry_id(int registry_id) {
		this.registry_id = registry_id;
	}*/
	/*public String getDocument_type() {
	return document_type;
	}
	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}*/
	/*public int getAnalysis_id() {
		return analysis_id;
	}
	public void setAnalysis_id(int analysis_id) {
		this.analysis_id = analysis_id;
	}
	public String getNote_content() {
		return note_content;
	}
	public void setNote_content(String note_content) {
		this.note_content = note_content;
	}
	public String getUmls_cui() {
		return umls_cui;
	}
	public void setUmls_cui(String umls_cui) {
		this.umls_cui = umls_cui;
	}
	public int getStop() {
		return stop;
	}
	public void setStop(int stop) {
		this.stop = stop;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public String getIs_negated() {
		return is_negated;
	}
	public void setIs_negated(String is_negated) {
		this.is_negated = is_negated;
	}
	*/
	/*public String getConcept_id() {
	return concept_id;
	}
	public void setConcept_id(String concept_id) {
		this.concept_id = concept_id;
	}*/
	/*public String getConcept_text() {
		return concept_text;
	}
	public void setConcept_text(String concept_text) {
		this.concept_text = concept_text;
	}*/
	@JsonView(Views.Normal.class)
	private String mrn;
	@JsonView(Views.Normal.class)
	private int document_id;
	@JsonView(Views.Normal.class)
	private Date nc_dos;
	@JsonView(Views.Normal.class)
	private String nc_subtype;
	@JsonView(Views.Normal.class)
	private String nc_type;
	@JsonView(Views.Normal.class)
	private String last_review_date;
	@JsonView(Views.Normal.class)
	private String note_content;
	/*@JsonView(Views.Normal.class)
	private String concept_id;
	@JsonView(Views.Normal.class)
	private String concept_text;*/
	
	/*@JsonView(Views.Normal.class)
	private String document_type;*/
	
	/*@JsonView(Views.Normal.class)
	private int registry_id;	*/
	
	/*@JsonView(Views.Normal.class)
	private int analysis_id;*/
	
	/*@JsonView(Views.Normal.class)
	private String umls_cui;
	@JsonView(Views.Normal.class)
	private int stop;
	@JsonView(Views.Normal.class)
	private int start;
	@JsonView(Views.Normal.class)
	private String is_negated;*/
}
