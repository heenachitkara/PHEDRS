package edu.uab.registry.json;

import edu.uab.registry.domain.AuthenticatedUser;
import edu.uab.registry.util.WebServiceResult;

public class AuthenticateResult extends WebServiceResult {

	private AuthenticatedUser _user = null;
	
	
	public AuthenticatedUser getUser() {
		return _user;
	}
	public void setUser(AuthenticatedUser user_) {
		_user = user_;
	}
}
