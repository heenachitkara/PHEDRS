package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistryUserList {
	
	public String getFullName() { return fullName; }
	public void setFullName(String fullName_) {
		this.fullName = fullName_;
	}
	
	public String getLoginID() { return loginID; }
	public void setLoginID(String loginID_) {
		this.loginID = loginID_;
	}

	public String getRoleName() { return roleName; }
	public void setRoleName(String roleName_) { this.roleName = roleName_; }

	public int getUserID() { return user_id; }
	public void setUserID(int userID_) { this.user_id = userID_; }
	
	public int getRegistryID() { return registryID; }
	public void setRegistryID(int registry_id) { this.registryID = registry_id; }
	
	public int getRoleID() { return roleID; }
	public void setRoleID(int roleID_) { this.roleID = roleID_; }
	
	public String getEmail() { return email; }
	public void setEmail(String Email_) {
		this.email = Email_;
	}
	
	
	@JsonProperty("full_name")
	private String fullName;

	@JsonProperty("login_id")
	private String loginID;

	@JsonProperty("name")
	private String roleName;

	@JsonProperty("userID")
	private int user_id;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("role_id")
	private int roleID;
	
	@JsonProperty("registry_id")
	private int registryID;
	
	

}
