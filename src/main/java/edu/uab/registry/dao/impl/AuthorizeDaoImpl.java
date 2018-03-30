package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.orm.dao.AuthorizeDao;
import edu.uab.registry.domain.Registry;
import edu.uab.registry.domain.RegistryAttributeList;
import edu.uab.registry.domain.RegistryRole;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class AuthorizeDaoImpl implements AuthorizeDao 
{

	
	
	/*public List<AuthorizeList> getAuthRoleList(String username_) throws DaoException {
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<AuthorizeList> authRoleList = new ArrayList<AuthorizeList>();
		
		try{
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectRole_SQL);
			System.out.println(selectRole_SQL);
			pstmt.setString(1, username_);
			rs = pstmt.executeQuery();	
		
			while(rs.next()){
				
				
				AuthorizeList authList = new AuthorizeList();
			    authList.setUserName(username_);
			    authList.setRole(rs.getString("role_name"));
			    authList.setRegistryName(rs.getString("registry_name"));
			    System.out.println(rs.getString("registry_name"));
			    authList.setRegistryId(rs.getInt("registry_id")); 
			    authList.setRoleId(rs.getInt("role_id"));
			    authRoleList.add(authList);
				
				
				
				
			}
	}catch(Throwable th) {
		throw new DaoException(th.getMessage(), th);
	}finally {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {					
				e.printStackTrace();
			}
			rs = null;
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch(SQLException sqe) {
				sqe.printStackTrace();
			}
			pstmt = null;
		} 
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
			conn = null;
		}
		if (ds != null) {				
			ds = null;
		}
	}
		
		
		
		
		
		return authRoleList;
	}	
	
	private String selectRole_SQL = "SELECT"
											+ " registry_cvterm.CVTERM_ID AS registry_id,"
											+ " registry_cvterm.NAME AS registry_name,"
											+ " registry_role_cvterm.CVTERM_ID AS role_id,"
											+ " registry_role_cvterm.NAME AS role_name"
								 + " FROM "
								 			+ " registry_actor "
								 			+ " JOIN registry_authz ON registry_authz.ACTOR_ID = registry_actor.REGISTRY_ACTOR_ID"
								 			+ " JOIN cvterm registry_cvterm ON registry_cvterm.CVTERM_ID = registry_authz.REGISTRY_ID"
								 			+ " JOIN cvterm registry_role_cvterm ON registry_role_cvterm.CVTERM_ID = registry_authz.ROLE_ID"
								 			+ " WHERE registry_actor.NAME = ?"
								 			+ " AND registry_role_cvterm.name not in ('Inactive')"
								 			+ " ORDER BY registry_name, role_name";*/
	
	public List<Registry> authorize(String loginID_) throws DaoException
	{	  
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		DataSource ds;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		
		List<Registry> registries = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("registry_cvterm.CVTERM_ID AS registry_id, ");
		sql.append("registry_cvterm.NAME AS registry_name, ");
		sql.append("registry_role_cvterm.CVTERM_ID AS role_id, ");
		sql.append("registry_role_cvterm.NAME AS role_name ");

		sql.append("FROM registry_actor ");
		sql.append("JOIN registry_authz ON registry_authz.ACTOR_ID = registry_actor.REGISTRY_ACTOR_ID ");
		sql.append("JOIN cvterm registry_cvterm ON registry_cvterm.CVTERM_ID = registry_authz.REGISTRY_ID ");
		sql.append("JOIN cvterm registry_role_cvterm ON registry_role_cvterm.CVTERM_ID = registry_authz.ROLE_ID ");
		sql.append("WHERE registry_actor.NAME = ? ");
		sql.append("AND registry_role_cvterm.name not in ('Inactive') ");
		sql.append("ORDER BY registry_name, role_name ");

		logger.info(sql.toString());

		try {
			if (StringUtils.isNullOrEmpty(loginID_)) {throw new Exception("Invalid login ID (empty)"); }

			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, loginID_);
			rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					Registry r = new Registry();
					r.setId(rs.getInt("registry_id"));
					r.setName(rs.getString("registry_name"));
				   
					RegistryRole rr = new RegistryRole();
					rr.setRole_id(rs.getInt("role_id"));
					rr.setRole_name(rs.getString("role_name"));
					r.setRole(rr);
					

					registries.add(r);
				}
			}

		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }}
			if (ps != null) { try { ps.close(); } catch(SQLException sqe) { sqe.printStackTrace(); }}
			if (conn != null) { try { conn.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }}
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.dao.authorize=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");

		return registries;
	}


	public static void main(String[] args) {
	}

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(AuthorizeDaoImpl.class);
		
}
