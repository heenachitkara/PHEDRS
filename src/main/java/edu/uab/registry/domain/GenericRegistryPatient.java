package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class GenericRegistryPatient 
{
	@JsonView(Views.Normal.class)
	private int registry_patient_id;

	@JsonView(Views.Normal.class)
	private String mrn;

	@JsonView(Views.Normal.class)
	private String full_name;

	@JsonView(Views.Normal.class)
	private String lastContactDate;
	

	//------------------------------------------------------------------------------------------------------------------
	// Search CV Name
	//------------------------------------------------------------------------------------------------------------------
	@JsonView(Views.Normal.class)
	private String name;
	
	@JsonView(Views.Normal.class)
	private int cv_id;
	

	//------------------------------------------------------------------------------------------------------------------
	// Registry status
	//------------------------------------------------------------------------------------------------------------------
	@JsonView(Views.Normal.class)
	private int registry_status_id;

	@JsonView(Views.Normal.class)
	private String registry_status;

	@JsonView(Views.Normal.class)
	private String assignment_date;

	@JsonView(Views.Normal.class)
	private String assigned_by_name;

	@JsonView(Views.Normal.class)
	private String registrar_comment;

	//------------------------------------------------------------------------------------------------------------------
	// Workflow/review status
	//------------------------------------------------------------------------------------------------------------------
	@JsonView(Views.Normal.class)
	private int registry_workflow_status_id;

	@JsonView(Views.Normal.class)
	private String registryWorkflowStatus;

	@JsonView(Views.Normal.class)
	private String workflow_status;

	@JsonView(Views.Normal.class)
	private String last_review_date;

	@JsonView(Views.Normal.class)
	private String reviewer_name;

	@JsonView(Views.Normal.class)
	private String workflow_comment;

	//------------------------------------------------------------------------------------------------------------------
	// Detection event(s)
	//------------------------------------------------------------------------------------------------------------------
	@JsonView(Views.Normal.class)
	private int detectionEventId;

	@JsonView(Views.Normal.class)
	private String detectionEventName;

	@JsonView(Views.Normal.class)
	private String detectionEventAbbr;





	public int getRegistry_patient_id() {
		return registry_patient_id;
	}
	public void setRegistry_patient_id(int registry_patient_id) {
		this.registry_patient_id = registry_patient_id;
	}
	public String getMrn() {
		return mrn;
	}
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}
	public String getFull_name() {
		return full_name;
	}
	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}
	public String getLastContactDate() {
		return lastContactDate;
	}
	public void setLastContactDate(String lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	//------------------------------------------------------------------------------------------------------------------
	// Registry status
	//------------------------------------------------------------------------------------------------------------------
	public int getRegistry_status_id() {
		return registry_status_id;
	}
	public void setRegistry_status_id(int registry_status_id) {
		this.registry_status_id = registry_status_id;
	}
	public String getRegistry_status() {
		return registry_status;
	}
	public void setRegistry_status(String registry_status) {
		this.registry_status = registry_status;
	}
	public String getAssignment_date() { return assignment_date; }
	public void setAssignment_date(String assignment_date_) { this.assignment_date = assignment_date_; }
	public String getAssigned_by_name() {
		return assigned_by_name;
	}
	public void setAssigned_by_name(String assigned_by_name) {
		this.assigned_by_name = assigned_by_name;
	}
	public String getRegistrar_comment() {
		return registrar_comment;
	}
	public void setRegistrar_comment(String registrar_comment) {
		this.registrar_comment = registrar_comment;
	}

	//------------------------------------------------------------------------------------------------------------------
	// Workflow/review status
	//------------------------------------------------------------------------------------------------------------------
	public int getRegistry_workflow_status_id() {
		return registry_workflow_status_id;
	}
	public void setRegistry_workflow_status_id(int registry_workflow_status_id) { this.registry_workflow_status_id = registry_workflow_status_id; }
	public String getRegistryWorkflowStatus() {
		return registryWorkflowStatus;
	}
	public void setRegistryWorkflowStatus(String registryWorkflowStatus) { this.registryWorkflowStatus = registryWorkflowStatus; }
	public String getWorkflow_status() {
		return workflow_status;
	}
	public void setWorkflow_status(String workflow_status) {
		this.workflow_status = workflow_status;
	}
	public String getLast_review_date() {
		return last_review_date;
	}
	public void setLast_review_date(String last_review_date) { this.last_review_date = last_review_date; }
	public String getReviewer_name() { return reviewer_name; }
	public void setReviewer_name(String reviewer_name_) { this.reviewer_name = reviewer_name_; }
	public String getWorkflow_comment() {
		return workflow_comment;
	}
	public void setWorkflow_comment(String workflow_comment) {
		this.workflow_comment = workflow_comment;
	}

	//------------------------------------------------------------------------------------------------------------------
	// Detection event(s)
	//------------------------------------------------------------------------------------------------------------------
	public int getDetectionEventId() {
		return detectionEventId;
	}
	public void setDetectionEventId(int detectionEventId) {
		this.detectionEventId = detectionEventId;
	}
	public String getDetectionEventName() {
		return detectionEventName;
	}
	public void setDetectionEventName(String detectionEventName) {
		this.detectionEventName = detectionEventName;
	}
	public String getDetectionEventAbbr() {
		return detectionEventAbbr;
	}
	public void setDetectionEventAbbr(String detectionEventAbbr) {
		this.detectionEventAbbr = detectionEventAbbr;
	}



}
