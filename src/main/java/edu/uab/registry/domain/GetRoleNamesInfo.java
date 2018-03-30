package edu.uab.registry.domain;

import java.util.List;

public class GetRoleNamesInfo {

	public StatusMessage getWebservice_status() {
	   return webservice_status;
	}
	public void setWebservice_status(StatusMessage webservice_status) {
		 this.webservice_status = webservice_status;
    }
	public List<GetRoleNamesList> getRoleNames(){
		return role_names_list;
	}
	public void setRoleNamesList(List<GetRoleNamesList> roleNamesList) {
		this.role_names_list = roleNamesList;
	}
	private StatusMessage webservice_status;
	private List<GetRoleNamesList> role_names_list; 

}
