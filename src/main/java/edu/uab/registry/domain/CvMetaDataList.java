package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;


public class CvMetaDataList {
	
	
	
	
	public int getRegistryId() {
		return registryId;
	}
	public void setRegistryId(int registryId) {
		this.registryId = registryId;
	}
	
	public int getAttributeCvtermId() {
		return attributeCvtermId;
	}

	public void setAttributeCvtermId(int attributeCvtermId) {
		this.attributeCvtermId = attributeCvtermId;
	}
	
	public String getValueDisplayName() {
		return valueDisplayName;
	}

	public void setValueDisplayName(String valueDisplayName) {
		this.valueDisplayName = valueDisplayName;
	}
	
	public int getValueCvId() {
		return valueCvId;
	}

	public void setValueCvId(int valueCvId) {
		this.valueCvId = valueCvId;
	}
	
	public String getCvName() {
		return cvName;
	}

	public void setCvName(String cvName) {
		this.cvName = cvName;
	}
	
	
	@JsonView(Views.Normal.class)
	private int registryId;
	
	@JsonView(Views.Normal.class)
	private int attributeCvtermId;
	
	@JsonView(Views.Normal.class)
	private String valueDisplayName;
	
	@JsonView(Views.Normal.class)
	private int valueCvId;
	
	@JsonView(Views.Normal.class)
	private String cvName;
	

}
