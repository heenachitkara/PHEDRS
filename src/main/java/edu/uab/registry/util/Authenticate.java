package edu.uab.registry.util;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authenticate 
{
	private String MASTER_USER_DN = "AD\\registration";
    private String MASTER_PASSWORD = "4wzp5Q]L2UA%V7N";
    private String ldapUrl = "ldap://ad.hs.uab.edu:3268/";
    private String searchBase = "OU=UABHS Users,DC=ad,DC=hs,DC=uab,DC=edu";
    
    public Authenticate(String ldapUrl, String searchBase) 
    {
        this.ldapUrl = ldapUrl;
        this.searchBase = searchBase;
    }
    
    public boolean authenticateUser(String username, String password)
    {
    	boolean authenticated = false;
    	try {    	
	        /*
	         * 1. Authenticate using master user.
	         */
	        DirContext ctx = authenticate();
	 
	        /*
	         * 2. Searches by "sAMAccountName" to recover the full DN of the username trying to login.
	         */
	        SearchControls searchControls = new SearchControls();
	        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	        searchControls.setReturningAttributes(new String[] { "distinguishedName" });
	        NamingEnumeration<SearchResult> searchResults = ctx.search(searchBase, String.format("(sAMAccountName=%s)", username), searchControls);
	        if (!searchResults.hasMore()) {
	            /*
	             * If can't resolve DN, the user doesn't exists.
	             */
	            throw new NamingException();
	        }
	        SearchResult searchResult = searchResults.next();
	        Attributes attributes = searchResult.getAttributes();        
	        Attribute attribute = attributes.get("distinguishedName");
	        logger.debug("FOUND " + username + ", ATTRIBUTES :" + attribute);
	        String userObject = (String) attribute.get();
	 
	        /*
	         * 3. Authenticates to LDAP with the user, will throw if password is wrong.
	         */
	        ctx.close();
	        ctx = authenticate(userObject, password);
	        if (ctx != null) {
	        	authenticated = true;
	        }
	        logger.debug(userObject + " SUCCESSFULLY AUTHENTICATED!");
    	} catch(NamingException ne) {
    		ne.printStackTrace();
    	} catch(Throwable th) {
    		th.printStackTrace();
    	}
        
    	return authenticated;
    }
    
    private DirContext authenticate() throws NamingException 
    {
        return authenticate(null, null);
    }
 
    private DirContext authenticate(String username, String password) throws NamingException 
    {
        String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
        String securityAuthentication = "simple";
 
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_PRINCIPAL, username != null ? username : MASTER_USER_DN);
        env.put(Context.SECURITY_CREDENTIALS, password != null ? password : MASTER_PASSWORD);
 
        DirContext ctx = new InitialDirContext(env);
 
        return ctx;
    }
	
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		try {	
			String ldapUrl = "ldap://ad.hs.uab.edu:3268/";
		    String searchBase = "OU=UABHS Users,DC=ad,DC=hs,DC=uab,DC=edu";
			Authenticate adf = new Authenticate(ldapUrl, searchBase);
  			String username = "assamal";
  			String password = "";
  			boolean status = adf.authenticateUser(username, password);
  			System.out.println("Authentication Status:"+status);
		} catch(Throwable th) {
			th.printStackTrace();
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(Authenticate.class);

}
