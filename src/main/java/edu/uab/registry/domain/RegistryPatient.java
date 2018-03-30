package edu.uab.registry.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class RegistryPatient 
{	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getConsult_type() {
		return consult_type;
	}
	public void setConsult_type(String consult_type) {
		this.consult_type = consult_type;
	}
	public String getCondition_type() {
		return condition_type;
	}
	public void setCondition_type(String condition_type) {
		this.condition_type = condition_type;
	}
	public String getApp_set_time() {
		return app_set_time;
	}
	public void setApp_set_time(String app_set_time) {
		this.app_set_time = app_set_time;
	}
	public String getConsulting_specialist() {
		return consulting_specialist;
	}
	public void setConsulting_specialist(String consulting_specialist) {
		this.consulting_specialist = consulting_specialist;
	}
	public String getService_end_date() {
		return service_end_date;
	}
	public void setService_end_date(String service_end_date) {
		this.service_end_date = service_end_date;
	}
	public String getPaccept() {
		return paccept;
	}
	public void setPaccept(String paccept) {
		this.paccept = paccept;
	}
	public int getCrec_state() {
		return crec_state;
	}
	public void setCrec_state(int crec_state) {
		this.crec_state = crec_state;
	}
	public String getCrec_comment() {
		return crec_comment;
	}
	public void setCrec_comment(String crec_comment) {
		this.crec_comment = crec_comment;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	public String getMrn() {
		return mrn;
	}
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}
	public String getConcept_id() {
		return concept_id;
	}
	public void setConcept_id(String concept_id) {
		this.concept_id = concept_id;
	}
	public String getPerson_sk() {
		return person_sk;
	}
	public void setPerson_sk(String person_sk) {
		this.person_sk = person_sk;
	}
	public String getConcept_text() {
		return concept_text;
	}
	public void setConcept_text(String concept_text) {
		this.concept_text = concept_text;
	}
	public int getDocument_id() {
		return document_id;
	}
	public void setDocument_id(int document_id) {
		this.document_id = document_id;
	}
	public String getDocument_type() {
		return document_type;
	}
	public void setDocument_type(String document_type) {
		this.document_type = document_type;
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
	public int getRegistry_id() {
		return registry_id;
	}
	public void setRegistry_id(int registry_id) {
		this.registry_id = registry_id;
	}
	public String getLast_review_date() {
		return last_review_date;
	}
	public void setLast_review_date(String last_review_date) {
		this.last_review_date = last_review_date;
	}
	public int getAnalysis_id() {
		return analysis_id;
	}
	public void setAnalysis_id(int analysis_id) {
		this.analysis_id = analysis_id;
	}

	@JsonView(Views.Normal.class)
	private int id;
	@JsonView(Views.Normal.class)
	private String name;
	@JsonView(Views.Normal.class)
	private String consult_type;
	@JsonView(Views.Normal.class)
	private String condition_type;
	@JsonView(Views.Normal.class)
	private String app_set_time;
	@JsonView(Views.Normal.class)
	private String consulting_specialist;
	@JsonView(Views.Normal.class)
	private String service_end_date;
	@JsonView(Views.Normal.class)
	private String paccept;
	@JsonView(Views.Normal.class)
	private int crec_state;
	@JsonView(Views.Normal.class)
	private String crec_comment;
	@JsonView(Views.Normal.class)
	private Date updated_at;
	@JsonView(Views.Normal.class)
	private String mrn;
	@JsonView(Views.Normal.class)
	private String concept_id;
	@JsonView(Views.Normal.class)
	private String person_sk;
	@JsonView(Views.Normal.class)
	private String concept_text;
	@JsonView(Views.Normal.class)
	private int document_id;
	@JsonView(Views.Normal.class)
	private String document_type;
	@JsonView(Views.Normal.class)
	private String nc_subtype;
	@JsonView(Views.Normal.class)
	private String nc_type;
	@JsonView(Views.Normal.class)
	private int registry_id;	
	@JsonView(Views.Normal.class)
	private String last_review_date;
	@JsonView(Views.Normal.class)
	private int analysis_id;
	
}
