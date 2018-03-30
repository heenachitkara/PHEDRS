package edu.uab.registry.domain;

import java.util.List;

public class RegistryEncounterInfo 
{
	public List<RegistryEncounter> getRegistry_encounters() {
		return registry_encounters;
	}
	public void setRegistry_encounters(List<RegistryEncounter> registry_encounters) {
		this.registry_encounters = registry_encounters;
	}
	
	List<RegistryEncounter> registry_encounters;	
}
