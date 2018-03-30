package edu.uab.registry.json;

import edu.uab.registry.domain.RegistryEncounter;
import edu.uab.registry.util.WebServiceResult;

import java.util.List;

public class GetRegistryEncountersResult extends WebServiceResult {

	protected List<RegistryEncounter> encounters = null;

	public List<RegistryEncounter> getEncounters() {
		return encounters;
	}
	public void setEncounters(List<RegistryEncounter> encounters_) {
		encounters = encounters_;
	}
}
