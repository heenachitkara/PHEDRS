package edu.uab.registry.json;

import java.util.List;

import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.util.WebServiceResult;

// Not needed if you aren't using Don't approach
public class SearchByCvName  extends WebServiceResult {
	
	protected List<GenericRegistryPatient> registry_cvnames = null;
	
	public List<GenericRegistryPatient> getRegistryPatients() {
		return registry_cvnames;
	}
	/*public void setRegistryCVNames(List<GenericRegistryPatient> cvNames_) {
		registry_cvnames = cvNames_;
	}*/

}
