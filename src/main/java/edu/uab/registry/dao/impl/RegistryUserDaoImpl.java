package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.RegistryUserDao;
import edu.uab.registry.domain.RegistryUserList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.domain.RegistryUserList;
import edu.uab.registry.util.StopWatch;

public class RegistryUserDaoImpl implements RegistryUserDao 
{
	
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}

	@Override
	public List<RegistryUserList> getRegistryUsers(Integer registryID_) throws DaoException{
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<RegistryUserList> regUserList = new ArrayList<RegistryUserList>();
		
		try{
			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectRegistryUsers);
			logger.debug("sql query :" + selectRegistryUsers);
			System.out.println(selectRegistryUsers);
			pstmt.setInt(1, registryID_);
			logger.debug("sql parameters, registryId:" +  registryID_);
			rs = pstmt.executeQuery();
            while(rs.next()) {
				
				RegistryUserList    regUList  = new RegistryUserList();
				regUList.setRegistryID(registryID_);
				regUList.setUserID(rs.getInt("actor_id"));
				regUList.setFullName(rs.getString("full_name"));
				regUList.setEmail(rs.getString("email"));
				regUList.setRoleName(rs.getString("role_name"));
				regUList.setRoleID(rs.getInt("role_id"));
				regUList.setLoginID(rs.getString("login_id"));
				regUserList.add(regUList);
						
			}
					
		} catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
		}finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }}
			if (pstmt != null) { try { pstmt.close(); } catch(SQLException sqe) { sqe.printStackTrace(); }}
			if (conn != null) { try { conn.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }}
		}	
		
		return regUserList;
	}
	
	private String selectRegistryUsers = "SELECT"
												+ " registry_actor_id as actor_id,"
												+ " registry_actor.login as full_name,"
												+ " registry_actor.name as login_id,"
												+ " registry_actor.email as email,"
												+ " registry_role.name as role_name,"
												+ " role_id "
										+ "FROM "
										        + "registry_authz"
												+ " JOIN registry_actor ON registry_actor.registry_actor_id = registry_authz.actor_id"
												+ " JOIN cvterm registry_role ON registry_role.cvterm_id = registry_authz.role_id"
										 + " WHERE "
										        + " registry_authz.registry_id = ?"
												+ " ORDER BY registry_actor.login";
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(RegistryUserDaoImpl.class);

}



