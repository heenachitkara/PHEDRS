package edu.uab.registry.domain;

import java.util.List;

public class CvTermsList 
{
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	public void setWebservice_status(StatusMessage webservice_status) {
		this.webservice_status = webservice_status;
	}
	public List<CvTerms> getCvtermList() {
		return cvtermList;
	}
	public void setCvtermList(List<CvTerms> cvtermList) {
		this.cvtermList = cvtermList;
	}
	private StatusMessage webservice_status;
	private List<CvTerms> cvtermList;
}
