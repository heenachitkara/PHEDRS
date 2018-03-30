package edu.uab.registry.util;

public class ApplicationProps 
{		
	private String env = "";	
	private String email_to = "";
	private String ldap_url = "";
	private String search_base = "";
	
	public ApplicationProps() {	
	}
	
	public String getEnv() {
		return env;
	}
	public void setEnv(String env) {
		this.env = env;
	}
	public String getEmail_to() {
		return email_to;
	}
	public void setEmail_to(String email_to) {
		this.email_to = email_to;
	}
	public String getLdap_url() {
		return ldap_url;
	}
	public void setLdap_url(String ldap_url) {
		this.ldap_url = ldap_url;
	}
	public String getSearch_base() {
		return search_base;
	}
	public void setSearch_base(String search_base) {
		this.search_base = search_base;
	}	
}
