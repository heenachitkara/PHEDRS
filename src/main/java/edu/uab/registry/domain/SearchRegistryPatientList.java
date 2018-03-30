package edu.uab.registry.domain;

import java.util.List;

public class SearchRegistryPatientList 
{						
	public List<GenericRegistryPatient> getSearch_list() {
		return search_list;
	}
	public void setSearch_list(List<GenericRegistryPatient> search_list) {
		this.search_list = search_list;
	}

	private List<GenericRegistryPatient> search_list;	
}
