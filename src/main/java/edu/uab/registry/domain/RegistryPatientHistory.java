package edu.uab.registry.domain;

import java.util.Date;

public class RegistryPatientHistory 
{
	public int getChanger_id() {
		return changer_id;
	}
	public void setChanger_id(int changer_id) {
		this.changer_id = changer_id;
	}
	public String getChanger() {
		return changer;
	}
	public void setChanger(String changer) {
		this.changer = changer;
	}
	public Date getChange_date() {
		return change_date;
	}
	public void setChange_date(Date change_date) {
		this.change_date = change_date;
	}
	public int getPrevious_status_id() {
		return previous_status_id;
	}
	public void setPrevious_status_id(int previous_status_id) {
		this.previous_status_id = previous_status_id;
	}
	public String getPrevious_status() {
		return previous_status;
	}
	public void setPrevious_status(String previous_status) {
		this.previous_status = previous_status;
	}
	public int getCurrent_status_id() {
		return current_status_id;
	}
	public void setCurrent_status_id(int current_status_id) {
		this.current_status_id = current_status_id;
	}
	public String getCurrent_status() {
		return current_status;
	}
	public void setCurrent_status(String current_status) {
		this.current_status = current_status;
	}
	public int getPrev_review_status_id() {
		return prev_review_status_id;
	}
	public void setPrev_review_status_id(int prev_review_status_id) {
		this.prev_review_status_id = prev_review_status_id;
	}
	public String getPrev_review_status() {
		return prev_review_status;
	}
	public void setPrev_review_status(String prev_review_status) {
		this.prev_review_status = prev_review_status;
	}
	public int getCurr_review_status_id() {
		return curr_review_status_id;
	}
	public void setCurr_review_status_id(int curr_review_status_id) {
		this.curr_review_status_id = curr_review_status_id;
	}
	public String getCurr_review_status() {
		return curr_review_status;
	}
	public void setCurr_review_status(String curr_review_status) {
		this.curr_review_status = curr_review_status;
	}
	public String getReg_status_change_comment() {
		return reg_status_change_comment;
	}
	public void setReg_status_change_comment(String reg_status_change_comment) {
		this.reg_status_change_comment = reg_status_change_comment;
	}
	public String getWorkflow_status_change_comment() {
		return workflow_status_change_comment;
	}
	public void setWorkflow_status_change_comment(String workflow_status_change_comment) {
		this.workflow_status_change_comment = workflow_status_change_comment;
	}

	private int changer_id;
	private String changer;
	private Date   change_date;
	private int    previous_status_id;
	private String previous_status;
	private int    current_status_id;
	private String current_status;
	private int    prev_review_status_id;
	private String prev_review_status;
	private int    curr_review_status_id;
	private String curr_review_status;
	private String reg_status_change_comment;
	private String workflow_status_change_comment;
}
