package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class EncounterCVList {

	public void setRegistryId(int registryId) {
		this.registryId = registryId;
		
	}
	
	public int getRegistryId() {
		return registryId;
	}

	public void setEncounterCvName(String encounterCVName) {
		this.encounterCVName = encounterCVName;
		
	}
	
	
	@JsonView(Views.Normal.class)
	private int registryId;
	
		
	@JsonView(Views.Normal.class)
	private String encounterCVName;

}
