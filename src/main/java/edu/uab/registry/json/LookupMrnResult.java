package edu.uab.registry.json;


import edu.uab.registry.domain.RegistryPatientDemographics;
import edu.uab.registry.util.Constants.MrnLookupStatus;
import edu.uab.registry.util.WebServiceResult;

// This object is the result returned from the lookupmrn web service.
public class LookupMrnResult extends WebServiceResult {


	private MrnLookupStatus _lookupStatus = MrnLookupStatus.invalid_mrn;

	private RegistryPatientDemographics _patientDemographics = null;
	//RegistryNlpPatientInfo _patientInfo = null;
	
	
	// C-tor
	public LookupMrnResult() {}
		
	// C-tor
	//public LookupMrnResult(StatusMessage webserviceStatus_) {
	//	super(webserviceStatus_);
	//}
	
	
	public MrnLookupStatus getLookupStatus() {
		return _lookupStatus;
	}
	public void setLookupStatus(MrnLookupStatus lookupStatus_) {
		_lookupStatus = lookupStatus_;
	}
	
	public RegistryPatientDemographics getDemographics() {
		return _patientDemographics;
	}
	public void setDemographics(RegistryPatientDemographics patientDemographics_) {
		_patientDemographics = patientDemographics_;
	}
	
}
