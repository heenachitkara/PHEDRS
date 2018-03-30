package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class CvtermNameList {

	
	public int getCvtermId() {
		return cvtermId;
	}
	
	public void setCvtermId(Integer cvtermID_) {
		this.cvtermId = cvtermID_;
		
	}
	
	public int getCvId() {
		return cvId;
	}
	
	public void setCvId(Integer cvID_) {
		this.cvId = cvID_;
		
	}
	
	
	
	
	public String getCvtermName() {
		return cvtermName;
	}

	public void setCvtermName(String cvtermName) {
		this.cvtermName = cvtermName;
		
	}
	
	
	public String getCvtermDefinition() {
		return cvtermDefinition;
	}

	public void setCvtermDefinition(String cvtermDefinition) {
		this.cvtermDefinition = cvtermDefinition;
		
	}
	
	
	
	
	@JsonView(Views.Normal.class)
	private int cvtermId;
	
	@JsonView(Views.Normal.class)
	private String cvtermName;
	
	@JsonView(Views.Normal.class)
	private String cvtermDefinition;
	
	@JsonView(Views.Normal.class)
	private int cvId;
	

}
