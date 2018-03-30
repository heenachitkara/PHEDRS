package edu.uab.registry.domain;

import java.util.List;

public class RegistryUserInfo {

	
		
		public StatusMessage getWebservice_status() {
			return webservice_status;
		}
		
		public void setWebservice_status(StatusMessage webservice_status) {
			 this.webservice_status = webservice_status;
		}
		
		public List<RegistryUserList> getRegistryUserList(){
			return users;
		}
		
		public void setRegistryUserList(List<RegistryUserList> users ){
			this.users = users;
		}
		
		
		
		private StatusMessage webservice_status;
		private List<RegistryUserList> users; 
		
		
		
	
	}
	

