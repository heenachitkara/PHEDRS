package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class RegistryAttributeList {
	
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
	
	public String getCvtermPropValue() {
		return cvtermPropValue;
	}

	public void setCvtermPropName(String cvtermPropValue) {
		this.cvtermPropValue = cvtermPropValue;
	}
	
	public int getCvtermPropID() {
		return cvtermPropID;
	}
	public void setCvtermPropID(int cvtermPropID) {
		this.cvtermPropID = cvtermPropID;
	}
	
	public int getAttributeConfigCvID() {
		return attributeConfigCvID;
	}
	public void setAttributeConfigID(int attributeConfigCvID) {
		this.attributeConfigCvID = attributeConfigCvID;
	}
	
	public int getTypeID() {
		return attributeConfigtypeId;
	}
	public void setTypeId(int attributeConfigtypeId) {
		this.attributeConfigtypeId = attributeConfigtypeId;
	}
	
	public String getAttributeCvName() {
		return attributeCvName;
	}

	public void setAttributeCvName(String attributeCvName) {
		this.attributeCvName = attributeCvName;
	}
	
	
	
	@JsonView(Views.Normal.class)
	private int registryId;
	
		
	@JsonView(Views.Normal.class)
	private String cvtermName;
	
	@JsonView(Views.Normal.class)
	private String cvtermPropValue;
	
	@JsonView(Views.Normal.class)
	private int cvtermPropID;
	
	@JsonView(Views.Normal.class)
	private int attributeConfigCvID;
	
	@JsonView(Views.Normal.class)
	private int attributeConfigtypeId;
	
	@JsonView(Views.Normal.class)
	private String attributeCvName;
	
	

}
