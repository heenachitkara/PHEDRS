package edu.uab.registry.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistryEncounter 
{
	private String mrn;
	private int registryId;
	private int encounterKey;
	private Date startDate;
	private Date endDate;
	private String encounterTypeClassRef;
	private String encounterTypeDescription;
	private String admitLoc;
	private String admitLocDescription;
	private String reasonForVisit;
	private String financial_info_number;
	private String plandesc;

	private List<RegistryEncounterAttribute> registry_encounter_attributes;



	// C-tor
	public RegistryEncounter() {
		registry_encounter_attributes = new ArrayList<>();
	}

	public void addEncounterAttribute(RegistryEncounterAttribute attr_) {
		registry_encounter_attributes.add(attr_);
	}


	public String getMrn() {
		return mrn;
	}
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}
	public int getRegistryId() {
		return registryId;
	}
	public void setRegistryId(int registryId) {
		this.registryId = registryId;
	}
	public int getEncounterKey() {
		return encounterKey;
	}
	public void setEncounterKey(int encounterKey) {
		this.encounterKey = encounterKey;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getEncounterTypeClassRef() {
		return encounterTypeClassRef;
	}
	public void setEncounterTypeClassRef(String encounterTypeClassRef) {
		this.encounterTypeClassRef = encounterTypeClassRef;
	}
	public String getAdmitLoc() {
		return admitLoc;
	}
	public void setAdmitLoc(String admitLoc) {
		this.admitLoc = admitLoc;
	}
	public String getEncounterTypeDescription() {
		return encounterTypeDescription;
	}
	public void setEncounterTypeDescription(String encounterTypeDescription) {
		this.encounterTypeDescription = encounterTypeDescription;
	}
	public String getAdmitLocDescription() {
		return admitLocDescription;
	}
	public void setAdmitLocDescription(String admitLocDescription) {
		this.admitLocDescription = admitLocDescription;
	}
	public String getReasonForVisit() {
		return reasonForVisit;
	}
	public void setReasonForVisit(String reasonForVisit) {
		this.reasonForVisit = reasonForVisit;
	}
	public List<RegistryEncounterAttribute> getRegistry_encounter_attributes() {
		return registry_encounter_attributes;
	}
	public void setRegistry_encounter_attributes(List<RegistryEncounterAttribute> registry_encounter_attributes) {
		this.registry_encounter_attributes = registry_encounter_attributes;
	}
	
	public String getFinancialInfoNum() {
		return financial_info_number;
	}
	public void setFinancialInfoNum(String finalcial_info_number) {
		this.financial_info_number = finalcial_info_number;
	}

	public String getPlandesc() {
		return plandesc;
	}

	public void setPlandesc(String plandesc) {
		this.plandesc = plandesc;
	}


}
