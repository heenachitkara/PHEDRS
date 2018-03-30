package edu.uab.registry.controller;

import java.util.List;

import edu.uab.registry.json.*;
import edu.uab.registry.util.*;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uab.registry.orm.dao.AuthorizeDao;
import edu.uab.registry.dao.RegistryUserDao;
import edu.uab.registry.domain.AuthenticatedUser;
import edu.uab.registry.domain.Registry;
import edu.uab.registry.domain.RegistryUserInfo;
import edu.uab.registry.domain.RegistryUserList;
import edu.uab.registry.orm.RegistryActor;
import edu.uab.registry.orm.RegistryAuthz;
import edu.uab.registry.orm.dao.AuthenticatedUserDao;
import edu.uab.registry.orm.dao.RegistryActorDao;
import edu.uab.registry.orm.dao.RegistryAuthzDao;

@RestController
public class AdminController 
{
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public String authenticate
	(
			@RequestParam(value="username", defaultValue="") String username_,
	    	@RequestParam(value="password", defaultValue="") String password_	
	)
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("authenticate parameters: username = "+username_);	
		
		AuthenticateResult result = new AuthenticateResult();
		
		try {
			logger.info("LDAP URL:" + appProps.getLdap_url());
			logger.info("SEARCH BASE:" + appProps.getSearch_base());	
			
			if (StringUtils.isNullOrEmpty(username_)) {
				result.setWebServiceStatus("Username cannot be blank", false);
				
			} else if (StringUtils.isNullOrEmpty(password_)) {
				result.setWebServiceStatus("Password cannot be blank", false);
				
			} else {
				
				Authenticate auth = new Authenticate(appProps.getLdap_url(), appProps.getSearch_base());
				if (auth.authenticateUser(username_, password_)) {
					
					// Populate the AuthenticatedUser object using the username (which is actually a login ID).
					AuthenticatedUserDao authenticatedUserDao = (AuthenticatedUserDao)context.getBean("authenticatedUserDao");
					AuthenticatedUser user = authenticatedUserDao.get(username_);
					
					result.setUser(user);
					result.setWebServiceStatus("Authentication was successful", true);
					
				} else {
					result.setWebServiceStatus("Authentication of " + username_ + " Failed!", false);
				}
			} 
			
		} catch (Throwable th) {
			th.printStackTrace();	
			result.setWebServiceStatus(th.getMessage(), false);
		}		
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.authenticate="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
        return result.toJSON(true);   
    }

	@RequestMapping(value="/authorize", method=RequestMethod.GET)
    public String authorize
    (    	
    	@RequestParam(value="username", defaultValue="") String username_    	
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("authorize parameters: username = "+username_);	
		
		AuthorizeResult result = new AuthorizeResult();
		
		try {			
			AuthorizeDao authDao = (AuthorizeDao) context.getBean("authorizeDao");			 		
			List<Registry> registries = authDao.authorize(username_);
			
			if (registries != null && registries.size() > 0) { 
				result.setRegistries(registries); 
				result.setWebServiceStatus("", true);
			} else {
				result.setWebServiceStatus("No registries available", false);
			}
						
		} catch (Throwable th) {
			th.printStackTrace();			
			result.setWebServiceStatus(th.getMessage(), false);
		}
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.authorize="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        
		return result.toJSON(true);
    }
	
		
	@RequestMapping(value="/getregistryusers", method=RequestMethod.GET)
    public String getregistryusers
    (
    	@RequestParam(value="registry_id", defaultValue="0") Integer registryID_	
    	
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.info("-----------------------------------------------------------------------------------------------------------");		
		logger.info("getregistryusers ws params : registry_id="+registryID_);
		
		RegistryUserInfo regUserInfo = new RegistryUserInfo();
		String registryUserInfoJsonString =  "";
		
		try {
						
			// Validate the registry ID.
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			
			RegistryUserDao regUserDao = (RegistryUserDao) context.getBean("registryUserDao");
			List<RegistryUserList> regUsersLists = regUserDao.getRegistryUsers(registryID_);
			regUserInfo.setRegistryUserList(regUsersLists);
			
			registryUserInfoJsonString = RegistryUserInfoView.createRegistryUsersListInfoToJsonString(regUserInfo, true, Constants.EMPTY, true);
			if ((regUsersLists != null) && (regUsersLists.size() > 0)) {
				logger.info("Number of Registry Users :" + regUsersLists.size());
			}	
			
		} catch (Throwable th) {
			th.printStackTrace();
			registryUserInfoJsonString = RegistryUserInfoView.createRegistryUsersListInfoToJsonString(regUserInfo,false,th.getMessage(), true);
		}
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.getregistryusers="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
		
        return registryUserInfoJsonString;
    }

    // TODO: validate the role of the user trying to add a registry actor!
	@RequestMapping(value="/addregistryactor", method=RequestMethod.GET)
    public String addRegistryActor
    (
    	@RequestParam(value="full_name", defaultValue="") String fullName_,
    	@RequestParam(value="login_id", defaultValue="") String loginID_,
    	@RequestParam(value="email", defaultValue="") String email_,
		@RequestParam(value="is_enabled", defaultValue="") String isEnabled_,
    	@RequestParam(value="is_machine", defaultValue="") String isMachine_,
		@RequestParam(value="registry_id", defaultValue="0") Integer registryID_,
		@RequestParam(value="role_id", defaultValue="0") Integer roleID_
    ) 
    {
		// NOTE: the switch between name and login in this method is intentional. A "login" is typically an active directory ID, and
		// "name" is usually the user's actual name. Since this is potentially confusing to future developers, the standard meaning
		// of the words is used as much as possible in the UI and web services code.

    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("addregistryactor parameters: " +
					"full_name="+fullName_+
					",login_id="+loginID_+
					",email="+email_+
					",is_enabled="+isEnabled_ +
					",is_machine="+isMachine_+
					",registry_id=" + registryID_ +
					",role_id=" + roleID_ +
					"\n\n");

		WebServiceResult result = new WebServiceResult();

		try {
			// Validate the input parameters.
			if (StringUtils.isNullOrEmpty(fullName_)) { throw new Exception("Invalid full name parameter"); }
			if (StringUtils.isNullOrEmpty(loginID_)) { throw new Exception("Invalid login ID parameter"); }
			if (StringUtils.isNullOrEmpty(email_)) { throw new Exception("Invalid email parameter"); }
			if (registryID_ < 1) {throw new Exception("Invalid registry ID parameter"); }
			if (roleID_ < 1) {throw new Exception("Invalid role ID parameter"); }

			// TODO: is_enabled

			// Convert is_machine to "Y" or "N".
			if (StringUtils.isNullOrEmpty(isMachine_) || isMachine_.equalsIgnoreCase("false")  || isMachine_.equalsIgnoreCase("no")) {
				isMachine_ = "N";
			} else if (isMachine_.equalsIgnoreCase("true") || isMachine_.equalsIgnoreCase("yes")) {
				isMachine_ = "Y";
			} else {
				throw new Exception("Unrecognized value for is_machine (" + isMachine_ + ")");
			}

			RegistryActorDao registryActorDao = (RegistryActorDao)context.getBean("registryActorDao");	

			// Is this user already a registry actor?
			List<RegistryActor> actors = registryActorDao.findByName(loginID_);
			if (actors != null && actors.size() > 0) { throw new Exception("This user is already a registry actor"); }

			// TODO: in the future, use the code from the authenticate web service to make sure this is a valid Active Directory login ID.

			// Create the new registry actor.
			RegistryActor registryActor = new RegistryActor();
			registryActor.setName(loginID_);
			registryActor.setLogin(fullName_);
			registryActor.setEmail(email_);
			registryActor.setIsMachine(isMachine_);

			if (registryActorDao.insert(registryActor)) {

				// Associate the user with the registry and role provided.
				RegistryAuthzDao registryAuthzDao = (RegistryAuthzDao)context.getBean("registryAuthzDao");

				// Authorize the new actor for the specified registry and role.
				RegistryAuthz registryAuthz = new RegistryAuthz();
				registryAuthz.setActorId(registryActor.getRegistryActorId());
				registryAuthz.setRegistryId(registryID_);
				registryAuthz.setRoleId(roleID_);

				if (registryAuthzDao.insert(registryAuthz)) {
					result.setWebServiceStatus("Registry actor created successfully", true);
				} else {
					result.setWebServiceStatus("Unable to authorize user", false);
				}
			} else {
				result.setWebServiceStatus("Unable to create registry actor", false);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			result.setWebServiceStatus(th.getMessage(), false);
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.ws.addregistryactor="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");

        return result.toJSON(true);
    }
	
	@RequestMapping(value="/updateregistryactor", method=RequestMethod.GET)
    public String updateRegistryActor
    (
    	@RequestParam(value="name", defaultValue="") String name,    	
    	@RequestParam(value="login", defaultValue="") String login,
    	@RequestParam(value="email", defaultValue="") String email,
    	@RequestParam(value="is_machine", defaultValue="") String is_machine,
    	@RequestParam(value="actor_id", defaultValue="0") Integer actor_id,
    	@RequestParam(value="role_id", defaultValue="0") Integer role_id,
    	@RequestParam(value="registry_id", defaultValue= "0") Integer registry_id
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("updateregistryactor ws params : " + 
					"name="+name+
					",login_id="+login+
					",email="+email+
					",is_machine="+is_machine+
					",actor_id="+actor_id+
					",role_id="+role_id+
					",registry_id="+registry_id
					);
		boolean updateStatus = false;
		boolean updateStatusActorID = false;
		try {
			
			// Validate the input parameters.
			if (StringUtils.isNullOrEmpty(name)) { throw new Exception("Invalid name parameter"); }
		    if (StringUtils.isNullOrEmpty(login)) { throw new Exception("Invalid login parameter"); }
			if (StringUtils.isNullOrEmpty(email)) { throw new Exception("Invalid email parameter"); }
			if (StringUtils.isNullOrEmpty(is_machine)) { throw new Exception("Invalid is_machine parameter"); }
		    if (registry_id < 1 ) {throw new Exception("Invalid Registry ID parameter"); }
			if (role_id < 1) {throw new Exception("Invalid role ID parameter"); }
			if (actor_id < 1) {throw new Exception("Invalid actor ID parameter"); }
			
			// Convert is_machine to "Y" or "N".
			/*if (StringUtils.isNullOrEmpty(is_machine) || is_machine.equalsIgnoreCase("false")  || is_machine.equalsIgnoreCase("no")) {
				is_machine = "N";
			} else if (is_machine.equalsIgnoreCase("true") || is_machine.equalsIgnoreCase("yes")) {
					is_machine = "Y";
			} else {
				throw new Exception("Unrecognized value for is_machine (" + is_machine + ")");
			 }*/
			
			
			
			RegistryActorDao registryActorDao = (RegistryActorDao)context.getBean("registryActorDao");
			RegistryAuthzDao registryAuthzDao = (RegistryAuthzDao)context.getBean("registryAuthzDao");
			RegistryActor registryActorFromDB = null;	
			RegistryAuthz registryRoleIDFromDB = null;	
			List<RegistryActor> raList = registryActorDao.findByName(name);
			List<RegistryAuthz> raAuthz = registryAuthzDao.findByActorIdRegistryId(actor_id,registry_id);
			String statusMessage = null;
			if ((raList != null) && (!raList.isEmpty()) && (raAuthz != null) && (!raAuthz.isEmpty())) {
				registryActorFromDB = raList.get(0);
				registryRoleIDFromDB = raAuthz.get(0);
				
				registryActorFromDB.setName(name);
				registryActorFromDB.setLogin(login);
				registryActorFromDB.setEmail(email);
				registryActorFromDB.setIsMachine(is_machine);
				registryRoleIDFromDB.setRoleId(role_id);
				
				updateStatus = registryActorDao.update(registryActorFromDB);
				updateStatusActorID = registryAuthzDao.update(registryRoleIDFromDB);
				logger.info("updateregistryactor status:" + updateStatus);
				logger.info("updateregistryactorID status:" + updateStatusActorID);
				if (updateStatus) {
					statusMessage = "Updated REGISTRY_ACTOR & REGISTRY_AUTHZ table!";
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, statusMessage, true);
				} else {
					statusMessage = "Update REGISTRY_ACTOR OR REGISTRY_AUTHZ table Problem!";
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, statusMessage, true);
				}
			} else {				
				statusMessage = "Did NOT find any REGISTRY_ACTOR OR REGISTRY_AUTHZ records to update!";
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, statusMessage, true);
				logger.info("updateregistryactor status: Did NOT find any REGISTRY_ACTOR or REGISTRY_AUTHZ records to update!");
			}
						
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.updateregistryactor="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	@RequestMapping(value="/addregistryauthz", method=RequestMethod.GET)
    public String addRegistryAuthz
    (
    	@RequestParam(value="actor_id", defaultValue="0") Integer actor_id,    	
    	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
    	@RequestParam(value="role_id", defaultValue="0") Integer role_id
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("addregistryauthz ws params : " + 
					"actor_id="+actor_id+
					",registry_id="+registry_id+
					",role_id="+role_id
					);
		boolean addStatus = true;		
		try {
			RegistryAuthzDao registryAuthzDao = (RegistryAuthzDao)context.getBean("registryAuthzDao");	
			RegistryAuthz registryAuthz = new RegistryAuthz();						
			registryAuthz.setActorId(actor_id);
			registryAuthz.setRegistryId(registry_id);
			registryAuthz.setRoleId(role_id);
			addStatus = registryAuthzDao.insert(registryAuthz);					
			logger.info("addregistryauthz status:" + addStatus);			
			if (addStatus) {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Inserted REGISTRY_AUTHZ Record!", true);
			} else {
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, "Insertion of REGISTRY_AUTHZ Record Problem!", true);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.addregistryauthz="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	@RequestMapping(value="/updateregistryauthz", method=RequestMethod.GET)
    public String updateRegistryAuthz
    (
    	@RequestParam(value="actor_id", defaultValue="0") Integer actor_id,    	
    	@RequestParam(value="registry_id", defaultValue="0") Integer registry_id,
    	@RequestParam(value="role_id", defaultValue="0") Integer role_id
    ) 
    {
    	StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String wsStatusJsonString = null;
		logger.info("-----------------------------------------------------------------------------------------------------------");
		logger.info("updateregistryauthz ws params : " + 
					"actor_id="+actor_id+
					",registry_id="+registry_id+
					",role_id="+role_id
					);
		boolean updateStatus = true;		
		try {
			RegistryAuthzDao registryAuthzDao = (RegistryAuthzDao)context.getBean("registryAuthzDao");	
			RegistryAuthz registryAuthzFromDB = null;
			List<RegistryAuthz> raList = registryAuthzDao.findByActorIdRegistryId(actor_id, registry_id);
			String statusMessage = null;
			if ((raList != null) && (!raList.isEmpty())) {
				registryAuthzFromDB = raList.get(0);
									
				registryAuthzFromDB.setActorId(actor_id);
				registryAuthzFromDB.setRegistryId(registry_id);
				registryAuthzFromDB.setRoleId(role_id);
				
				updateStatus = registryAuthzDao.update(registryAuthzFromDB);
				logger.info("updateregistryauthz status:" + updateStatus);
				if (updateStatus) {
					statusMessage = "Updated REGISTRY_AUTHZ table!";
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, statusMessage, true);
				} else {
					statusMessage = "Update REGISTRY_AUTHZ table Problem!";
					wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(true, statusMessage, true);
				}
			} else {				
				statusMessage = "Did NOT find any REGISTRY_AUTHZ records to update!";
				wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, statusMessage, true);
				logger.info("updateregistryauthz status: Did NOT find any REGISTRY_AUTHZ records to update!");
			}
			logger.info("updateregistryauthz status:" + updateStatus);			
		} catch (Throwable th) {
			th.printStackTrace();
			wsStatusJsonString = GenericOrmStatusView.OrmStatusToJsonString(false, th.getMessage(), true);
		}
		logger.info("Log.ElapsedTime.ws.updateregistryauthz="+stopWatch.getElapsedTimeInMilliSeconds()+"ms");			
		logger.info("-----------------------------------------------------------------------------------------------------------");
        return wsStatusJsonString;
    }
	
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);	
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	private static ApplicationProps appProps = (ApplicationProps)context.getBean("appProps");
}
