package edu.uab.registry.domain;

import java.util.Date;
import edu.uab.registry.util.Constants.RegistryStatusType;

// This represents a patient's registry status.
public class RegistryPatientStatus {

	private String _annotatorComment;
	private int _annotatorID;
	private String _annotatorName;
	private Date _changeDate;
	private int _statusHistoryID;
	private int _statusID;
	private String _statusText;
	private RegistryStatusType _statusType;
	
	
	
	// Getters and setters
	public String getAnnotatorComment() {
		return _annotatorComment;
	}
	public void setAnnotatorComment(String annotatorComment_) {
		_annotatorComment = annotatorComment_;
	}
	
	public int getAnnotatorID() {
		return _annotatorID;
	}
	public void setAnnotatorID(int annotatorID_) {
		_annotatorID = annotatorID_;
	}
	
	public String getAnnotatorName() {
		return _annotatorName;
	}
	public void setAnnotatorName(String annotatorName_) {
		_annotatorName = annotatorName_;
	}
	
	public Date getChangeDate() {
		return _changeDate;
	}
	public void setChangeDate(Date changeDate_) {
		_changeDate = changeDate_;
	}
	
	public int getStatusHistoryID() {
		return _statusHistoryID;
	}
	public void setStatusHistoryID(int statusHistoryID_) {
		_statusHistoryID = statusHistoryID_;
	}
	public int getStatusID() {
		return _statusID;
	}
	public void setStatusID(int statusID_) {
		_statusID = statusID_;
	}
	
	public String getStatusText() {
		return _statusText;
	}
	public void setStatusText(String statusText_) {
		_statusText = statusText_;
	}
	
	public RegistryStatusType getStatusType() {
		return _statusType;
	}
	public void setStatusType(RegistryStatusType statusType_) {
		_statusType = statusType_;
	}
	
	
}
