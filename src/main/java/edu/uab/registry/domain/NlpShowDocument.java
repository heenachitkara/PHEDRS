// This is specifically for show document webservice
package edu.uab.registry.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class NlpShowDocument {

	
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
	public String getUmls_cui() {
		return umls_cui;
	}
	public void setUmls_cui(String umls_cui) {
		this.umls_cui = umls_cui;
	}
	public String getIs_negated() {
		return is_negated;
	}
	public void setIs_negated(String is_negated) {
		this.is_negated = is_negated;
	}
	
	public int getisGenericValue() {
		return is_generic;
	}
	public void setIsGenericValue(int document_id) {
		this.document_id = document_id;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getIsPossible() {
		return is_possible;
	}
	public void setIsPossible(int is_possible) {
		this.is_possible = is_possible;
	}
	
	public int getIsConditional() {
		return is_conditional;
	}
	public void setIsConditional(int is_conditional) {
		this.is_conditional = is_conditional;
	}
	
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
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
	public String getHitText() {
		return hitText;
	}
	public void setHitText(String hitText) {
		this.hitText = hitText;
	}
	
	// Adding getters and setters for getting value from cvtermprop
	public String getCVTermPropValue() {
		return CVTermPropvalue;
	}
	public void setCVTermPropValue(String CVTermPropvalue) {
		this.CVTermPropvalue = CVTermPropvalue;
	}
	
	
	
	
	
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
	@JsonView(Views.Normal.class)
	private String concept_text;
	@JsonView(Views.Normal.class)
	private String hitText;
	@JsonView(Views.Normal.class)
	private String umls_cui;
	@JsonView(Views.Normal.class)
	private String is_negated;
	@JsonView(Views.Normal.class)
	private String subject;
	@JsonView(Views.Normal.class)
	private int is_generic;
	@JsonView(Views.Normal.class)
	private int score;
	@JsonView(Views.Normal.class)
	private int is_possible;
	@JsonView(Views.Normal.class)
	private int is_conditional;
	@JsonView(Views.Normal.class)
	private String course;
	@JsonView(Views.Normal.class)
	private String severity;
	@JsonView(Views.Normal.class)
	private int stop;
	@JsonView(Views.Normal.class)
	private int start;
	
	//Declaring variables for getting value
	@JsonView(Views.Normal.class)
	private String CVTermPropvalue;
	
	
}
