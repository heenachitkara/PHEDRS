package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class PatientCVList {
	
	public int getRegistryId() {
		return registryId;
	}

	public void setRegistryId(int registryId) {
		this.registryId = registryId;
		
	}

	public void setPatientCvName(String patientCVName) {
		this.patientCVName = patientCVName;
		
	}
	
	@JsonView(Views.Normal.class)
	private int registryId;
	
		
	@JsonView(Views.Normal.class)
	private String patientCVName;
	
	
	
	

}
