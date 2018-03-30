package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class SearchCvNameList {
	
	public String getCVName() {
		return cvname;
		}
	public void setCVName(String cvname) {
		this.cvname = cvname;
	}
	public int getCVId() {
		return cvid;
	}
	public void setCVId(int cvid) {
		this.cvid = cvid;
	}
	
	
	@JsonView(Views.Normal.class)
	private String cvname;
	
	@JsonView(Views.Normal.class)
	private int cvid;
	

}
