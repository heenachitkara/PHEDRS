package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class GetAllCVList {
	
	public int getRegistryId() {
		return registryId;
	}
	public void setRegistryId(int registryId) {
		this.registryId = registryId;
	}
	
	public String getCvName() {
		return cvName;
	}

	public void setCvName(String cvName) {
		this.cvName = cvName;
	}
	
	public int getCvId() {
		return cvId;
	}
	public void setCvId(int cvId) {
		this.cvId = cvId;
	}
	
	@JsonView(Views.Normal.class)
	private int registryId;
	
	@JsonView(Views.Normal.class)
	private String cvName;
	
	@JsonView(Views.Normal.class)
	private int cvId;

}
