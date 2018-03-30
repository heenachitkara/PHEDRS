package edu.uab.registry.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.uab.registry.dao.CvMetaDataListDao;
import edu.uab.registry.domain.CvMetaDataList;
import edu.uab.registry.util.WebServiceResult;
/*
 * 
 * This won't be used if I end up using the same procedure as used for getdocuments. Will revisit
 * after working webservice
 * 
 */
public class GetCvMetaDataResult extends WebServiceResult {
	
	/* Did the following to make ORM work, however, not trying this approach right now
	 * 
	 * // Define jsonproperty which will show in the JSON response
	@JsonProperty("CvMetaList")
	
	//Defining CvMetaDataListDao which is equivalent to RegistryPatientStatusDao for 
	//getregistrystatushistory webservice
	private List<CvMetaDataList> _cvmetalist;

	// Getters and setters
	public List<CvMetaDataList> getCvmetalist() {
		return _cvmetalist;
	}
	public void setCvmetalist(List<CvMetaDataList> cvList) {
		_cvmetalist = cvList;
	}*/
	

	protected List<CvMetaDataList> cvmetadata = null;

	public List<CvMetaDataList> getCvMetaData() {
		return cvmetadata;
	}
	public void setCvMetaData(List<CvMetaDataList> cvmetadata_) {
		cvmetadata = cvmetadata_;
	}
	
	
	
}
