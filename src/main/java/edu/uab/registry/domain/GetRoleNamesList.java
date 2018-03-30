package edu.uab.registry.domain;

import com.fasterxml.jackson.annotation.JsonView;

import edu.uab.registry.json.Views;

public class GetRoleNamesList {
	
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
    public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	@JsonView(Views.Normal.class)
	private int roleId ;
	
	@JsonView(Views.Normal.class)
	private String roleName;

}
