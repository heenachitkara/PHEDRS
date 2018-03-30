package edu.uab.registry.json;

import edu.uab.registry.domain.RegistryEncounter;
import edu.uab.registry.util.WebServiceResult;

import java.util.List;

public class GetEncounterAttributesResult extends WebServiceResult {

	protected RegistryEncounter encounter = null;

	public RegistryEncounter getEncounter() {
		return encounter;
	}
	public void setEncounter(RegistryEncounter encounter_) { encounter = encounter_; }
}
