package edu.uab.registry.domain;

import java.util.List;

public class RetrievedPatient 
{
	
	public List<CrecPatient> getUnder_review() {
		return under_review;
	}
	public void setUnder_review(List<CrecPatient> under_review) {
		this.under_review = under_review;
	}
	public List<CrecPatient> getValidated() {
		return validated;
	}
	public void setValidated(List<CrecPatient> validated) {
		this.validated = validated;
	}	
	
	public List<CrecPatient> getNew_patient() {
		return new_patient;
	}
	public void setNew_patient(List<CrecPatient> new_patient) {
		this.new_patient = new_patient;
	}
	public List<CrecPatient> getAccepated() {
		return accepted;
	}
	public void setAccepated(List<CrecPatient> accepted) {
		this.accepted = accepted;
	}
	public List<CrecPatient> getEnrolled() {
		return enrolled;
	}
	public void setEnrolled(List<CrecPatient> enrolled) {
		this.enrolled = enrolled;
	}
	public List<CrecPatient> getQuit() {
		return quit;
	}
	public void setQuit(List<CrecPatient> quit) {
		this.quit = quit;
	}
	public List<CrecPatient> getRejected() {
		return rejected;
	}
	public void setRejected(List<CrecPatient> rejected) {
		this.rejected = rejected;
	}
	public List<CrecPatient> getExpired() {
		return expired;
	}
	public void setExpired(List<CrecPatient> expired) {
		this.expired = expired;
	}
	public List<CrecPatient> getExited() {
		return exited;
	}
	public void setExited(List<CrecPatient> exited) {
		this.exited = exited;
	}
		
	private List<CrecPatient> under_review;
	private List<CrecPatient> validated;	
	private List<CrecPatient> new_patient;
	private List<CrecPatient> accepted;
	private List<CrecPatient> enrolled;
	private List<CrecPatient> quit;
	private List<CrecPatient> rejected;
	private List<CrecPatient> expired;
	private List<CrecPatient> exited;
	
}
