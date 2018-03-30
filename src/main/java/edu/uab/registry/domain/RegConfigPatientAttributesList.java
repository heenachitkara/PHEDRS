package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class RegConfigPatientAttributesList {
	
	public String getpatienrCvTermName() {
		return patienrCvTermName;
	}
	public void setpatienrCvTermName(String patienrCvTermName) {
		this.patienrCvTermName = patienrCvTermName;
	}
	
	public String getpatientCvTermDefinition() {
		return patientCvTermDefinition;
	}
	public void setpatientCvTermDefinition(String patientCvTermDefinition) {
		this.patientCvTermDefinition = patientCvTermDefinition;
	}
	
	public int getCvID() {
		return cvID;
	}
	public void setCvID(int cvID) {
		this.cvID = cvID;
	}
	
	public int getCvTermID() {
		return cvTermID;
	}
	public void setCvTermID(int cvTermID) {
		this.cvTermID = cvTermID;
	}
	
	
	
	@JsonView(Views.Normal.class)
	private String patienrCvTermName;
	@JsonView(Views.Normal.class)
	private String patientCvTermDefinition;
	@JsonView(Views.Normal.class)
	private int cvID;
	@JsonView(Views.Normal.class)
	private int cvTermID;
	

}
