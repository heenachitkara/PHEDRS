package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class RegistryTabsList {
	
	
	public int getRegistryId() {
		return registryId;
	}

	public void setRegistryId(int registryId) {
		this.registryId = registryId;
		
	}
	
	public String getCvtermName() {
		return cvtermName;
	}

	public void setCvtermName(String cvtermName) {
		this.cvtermName = cvtermName;
		
	}
	
	@JsonView(Views.Normal.class)
	private int registryId;
	
		
	@JsonView(Views.Normal.class)
	private String cvtermName;

}
