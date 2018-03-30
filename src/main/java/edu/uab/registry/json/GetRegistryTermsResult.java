package edu.uab.registry.json;

import edu.uab.registry.domain.ControlledVocabulary;
import edu.uab.registry.util.WebServiceResult;

import java.util.List;

public class GetRegistryTermsResult extends WebServiceResult {

	private List<ControlledVocabulary> cvs = null;
	
	public List<ControlledVocabulary> getCvs() {
		return cvs;
	}
	public void setCvs(List<ControlledVocabulary> cvs_) { cvs = cvs_; }
}
