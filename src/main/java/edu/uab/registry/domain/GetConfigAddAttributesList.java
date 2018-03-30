package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class GetConfigAddAttributesList {

	public int getConfigAddAttributeCvTermID() {
		//return cvterm_id;
		return configAddAttributeCvTermID;
	}

	public void setConfigAddAttributeCvTermID(int cvterm_id) {
		//this.cvterm_id = cvterm_id;
		//this.type_id = cvterm_id;
		this.configAddAttributeCvTermID = cvterm_id;
	}
	
	public String getConfigAddAttributeName() {
		return configAddAttributeName;
	}

	public void setConfigAddAttributeName(String attributeName) {
		this.configAddAttributeName = attributeName;
	}
	
	public int getConfigAddAttributeCvID() {
		return configAddAttributeCvID;
	}

	public void setConfigAddAttributeCvID(int cv_id) {
		this.configAddAttributeCvID = cv_id;
	}
	
	/*public String getConfigAddAttributeDefinition() {
		return attributeDefinition;
	}

	public void setConfigAddAttributeDefinition(String attributeDefinition) {
		this.attributeDefinition = attributeDefinition;
	}
	*/
		
	
	@JsonView(Views.Normal.class)
	//private int cvterm_id;
	private int configAddAttributeCvTermID;
	
	@JsonView(Views.Normal.class)
	private String configAddAttributeName;
	
	@JsonView(Views.Normal.class)
	private int configAddAttributeCvID;
	
	/*@JsonView(Views.Normal.class)
	private String attributeDefinition;	
	*/
	
	
	
}
