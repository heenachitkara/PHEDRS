package edu.uab.registry.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class NlpPatientHitsDocsList 
{	
	public StatusMessage getWebservice_status() {
		return webservice_status;
	}
	public void setWebservice_status(StatusMessage webservice_status) {
		this.webservice_status = webservice_status;
	}
	
	public List<NlpShowDocument> getNlpShowDocsList() {
		return nlpShowDocumentList;
	}
	public void setNlpShowDocsList(List<NlpShowDocument> nlpShowDocumentList) {
		this.nlpShowDocumentList = nlpShowDocumentList;
	}

	@JsonView(Views.Normal.class)
	private StatusMessage webservice_status;
	List<NlpShowDocument> nlpShowDocumentList;
}
