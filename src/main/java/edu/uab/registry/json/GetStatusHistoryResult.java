package edu.uab.registry.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uab.registry.domain.RegistryPatientStatus;
import edu.uab.registry.util.WebServiceResult;

import java.util.List;

public class GetStatusHistoryResult extends WebServiceResult {

	@JsonProperty("statuses")
	private List<RegistryPatientStatus> _statuses;

	// Getters and setters
	public List<RegistryPatientStatus> getStatuses() {
		return _statuses;
	}
	public void setStatuses(List<RegistryPatientStatus> statuses_) {
		_statuses = statuses_;
	}
}
