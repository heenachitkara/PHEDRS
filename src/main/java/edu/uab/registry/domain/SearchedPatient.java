package edu.uab.registry.domain;

import java.util.List;

public class SearchedPatient 
{	
	public List<CrecPatient> getSearch_list() {
		return search_list;
	}

	public void setSearch_list(List<CrecPatient> search_list) {
		this.search_list = search_list;
	}

	private List<CrecPatient> search_list;
}
