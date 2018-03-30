package edu.uab.registry.domain;

public class RegistryDetectionCode 
{			
	public String getMrn() {
		return mrn;
	}
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}
	public int getRegistry_id() {
		return registry_id;
	}
	public void setRegistry_id(int registry_id) {
		this.registry_id = registry_id;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public String getCode_assignment_date() {
		return code_assignment_date;
	}
	public void setCode_assignment_date(String code_assignment_date) {
		this.code_assignment_date = code_assignment_date;
	}
	public String getCode_value() {
		return code_value;
	}
	public void setCode_value(String code_value) {
		this.code_value = code_value;
	}
	
	private String mrn;		    	     
	private int registry_id;			 
	private String accession; 		 	   		
	private String code_assignment_date; 	   		 	
	private String code_value;     	 				
}
