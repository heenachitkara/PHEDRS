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

import edu.uab.registry.dao.RegistryTabsDao;
import edu.uab.registry.domain.RegistryTabsList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class RegistryTabsDaoImpl implements RegistryTabsDao {
	
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	@Override
	public List<RegistryTabsList> getRegistryTabs(int registryId) throws DaoException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<RegistryTabsList> regTabsList = new ArrayList<RegistryTabsList>();
		
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectRegistryTabs_SQL);
			logger.debug("sql query :" + selectRegistryTabs_SQL);
			System.out.println(selectRegistryTabs_SQL);
			pstmt.setInt(1, registryId);
			logger.debug("sql parameters, registryId:" +  registryId);
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				
				RegistryTabsList    regTList  = new RegistryTabsList();
				regTList.setRegistryId(registryId);
				regTList.setCvtermName(rs.getString("name"));
				regTabsList.add(regTList);
						
			}
		} catch(Throwable th) {
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
		
		
			
		return regTabsList;
	}
	

	
	
	private String selectRegistryTabs_SQL = 
			"SELECT "
					+ "cvterm.name,cvtermprop.value,cvterm.cv_id"
		+ " FROM CVTERMPROP "
		+ "JOIN "
		        + "cvterm "
		        			+ "ON (cvtermprop.type_id=cvterm.cvterm_id) "
		+ "WHERE cvtermprop.CVTERM_ID = ? "
		+ "AND "
				+ "type_id "
							+ "IN (SELECT cvterm_id FROM cvterm WHERE cv_id=25)";	
	
	
	
	
	

	
	
	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(RegistryTabsDaoImpl.class);
	
	
}
