package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class RegistryEncounterInsuranceList {
	
	
	public String getHealthQuestFIN() {
		return healthQuestFIN;
	}
	public void setHealthQuestFIN(String healthQuestFIN) {
		this.healthQuestFIN = healthQuestFIN;
	}
	
	public String getInsurancePlanInfo(){
		return insurancePlanInfo;
	}
	
	public void setInsurancePlanInfo(String insurancePlanInfo) {
		this.insurancePlanInfo = insurancePlanInfo;
	}
	
	public String getDateOfService(){
		return dateOfService;
	}
	
	public void setDateOfService(String dateOfService) {
		this.dateOfService = dateOfService;
	}
	
	public String getLocationDescription(){
		return locationDescription;
	}
	
	
	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
		
	}
	
	
	
	@JsonView(Views.Normal.class)
	private String healthQuestFIN;
	
	@JsonView(Views.Normal.class)
	private String insurancePlanInfo;
	
	@JsonView(Views.Normal.class)
	private String dateOfService;
	
	@JsonView(Views.Normal.class)
	private String locationDescription;

	

}
