package edu.uab.registry.json;


import java.util.List;

import edu.uab.registry.domain.Registry;
import edu.uab.registry.util.WebServiceResult;

public class AuthorizeResult extends WebServiceResult {

	private List<Registry> _registries = null;
	
	
	public List<Registry> getRegistries() {
		return _registries;
	}
	public void setRegistries(List<Registry> registries_) {
		_registries = registries_;
	}
}
