package edu.uab.registry.domain;

import java.util.List;

public class RegistryPatientStatuses {

	protected List<RegistryPatientStatus> _statuses;
	protected StatusMessage _webserviceStatus;
	
	// C-tor
	public RegistryPatientStatuses(List<RegistryPatientStatus> statuses_) {
		_statuses = statuses_;
	}
	
	// Getters and setters
	public List<RegistryPatientStatus> getStatuses() {
		return _statuses;
	}
	public void setStatuses(List<RegistryPatientStatus> statuses_) {
		_statuses = statuses_;
	}
	public StatusMessage getWebServiceStatus() {
		return _webserviceStatus;
	}
	public void setWebServiceStatus(StatusMessage webserviceStatus_) {
		_webserviceStatus = webserviceStatus_;
	}
}
