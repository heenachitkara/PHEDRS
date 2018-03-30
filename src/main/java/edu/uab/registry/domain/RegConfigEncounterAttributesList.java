package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class RegConfigEncounterAttributesList {
	
	
	public String getEncounterCvTermName() {
		return encounterCvTermName;
	}
	public void setEncounterCvTermName(String encounterCvTermName) {
		this.encounterCvTermName = encounterCvTermName;
	}
	
	public String getEncounterCvTermDefinition() {
		return encounterCvTermDefinition;
	}
	public void setEncounterCvTermDefinition(String encounterCvTermDefinition) {
		this.encounterCvTermDefinition = encounterCvTermDefinition;
	}
	
	public int getCvID() {
		return cvID;
	}
	public void setCvID(int cvID) {
		this.cvID = cvID;
	}
	
	/*public int getEncounterCvTermID() {
		return cvTermID;
	}
	public void setEncounterCvTermID(int cvTermID) {
		this.cvTermID = cvTermID;
	}*/
	
	public int getCvTermID() {
		return cvTermID;
	}
	public void setCvTermID(int cvTermID) {
		this.cvTermID = cvTermID;
	}
	
	
	@JsonView(Views.Normal.class)
	private String encounterCvTermName;
	@JsonView(Views.Normal.class)
	private String encounterCvTermDefinition;
	@JsonView(Views.Normal.class)
	private int cvID;
	@JsonView(Views.Normal.class)
	private int cvTermID;
	

}
