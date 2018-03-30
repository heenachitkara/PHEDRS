package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.util.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.domain.AuthenticatedUser;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.orm.dao.AuthenticatedUserDao;


public class AuthenticatedUserDaoImpl implements AuthenticatedUserDao {

	
	private JdbcTemplate jdbcTemplate;	
	
	public AuthenticatedUser get(String name_) throws DaoException {
		
		if (StringUtils.isNullOrEmpty(name_)) { throw new DaoException("Invalid name"); }
		String name = name_.trim();
		
		AuthenticatedUser user = null;
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();	
			
			// Generate the query
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT login as display_name, "); 
			sql.append("name as login, ");
			sql.append("registry_actor_id as user_id ");
			sql.append("FROM registry_actor ");
			sql.append("WHERE name = ? ");
			
			// Create the prepared statement and add the parameter.
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, name);
			
			// Execute the query and validate the results.
			rs = ps.executeQuery();
			if (rs == null || !rs.next()) { throw new DaoException("Empty result set"); }

			// Get the values from the result row.
			String display_name = rs.getString("display_name");
			String login = rs.getString("login");
			int user_id = rs.getInt("user_id");
			
			// Validate the display name and login.
			if (StringUtils.isNullOrEmpty(display_name)) { throw new DaoException("Invalid user display name"); }
			if (StringUtils.isNullOrEmpty(login)) { throw new DaoException("Invalid user login"); }
			
			// Create an instance of AuthenticatedUser and populate it.
			user = new AuthenticatedUser();
			user.setDisplayName(display_name);
			user.setLogin(login);
			user.setUserID(user_id);
		}
		catch (Throwable th_) {
			throw new DaoException(th_.getMessage(), th_);
		} 
		finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }}
			if (ps != null) { try { ps.close(); } catch(SQLException sqe) { sqe.printStackTrace(); }}
			if (conn != null) { try { conn.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }}
		}	
		
		return user;
	}
	
	public void setDataSource(DataSource dataSource) {		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
}
