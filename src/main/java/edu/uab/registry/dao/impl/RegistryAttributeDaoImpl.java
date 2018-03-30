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

import edu.uab.registry.dao.RegistryAttributeDao;
import edu.uab.registry.domain.RegistryAttributeList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class RegistryAttributeDaoImpl implements RegistryAttributeDao  {
	
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	

	
	public List<RegistryAttributeList> getRegistryAttribute(int registryId) throws DaoException {
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<RegistryAttributeList> regAttrList = new ArrayList<RegistryAttributeList>();
		
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectRegistryAttribute_SQL);
			logger.debug("sql query :" + selectRegistryAttribute_SQL);
			System.out.println(selectRegistryAttribute_SQL);
			pstmt.setInt(1, registryId);
			logger.debug("sql parameters, registryId:" +  registryId);
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				
				RegistryAttributeList    regAList  = new RegistryAttributeList();
				regAList.setRegistryId(registryId);
				regAList.setCvtermName(rs.getString("name"));
				regAList.setCvtermPropName(rs.getString("value"));
				regAList.setCvtermPropID(rs.getInt("cvtermprop_id"));
				regAList.setAttributeConfigID(rs.getInt("cv_id"));
				regAList.setTypeId(rs.getInt("type_id"));
				regAList.setAttributeCvName(rs.getString("cvname"));
				regAttrList.add(regAList);
						
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
		
		
			
		return regAttrList;
	}
	
	
	private String selectRegistryAttribute_SQL = 
			"SELECT  " 
			     + "cv.name as cvname,"
	             + "t.name,cvtermprop.type_id, cvtermprop.value,"//for display
				 + " cvtermprop.cvtermprop_id, "//--primary key for editing
				 + " cvtermprop.cvterm_id,"//--registry_id
				 + " t.cv_id " +
				/* + " t.cvterm_id " +*/
				 
			"FROM " + 				 
				  "cvtermprop JOIN cvterm t on (cvtermprop.type_id=t.cvterm_id) " +	
				  "JOIN cv on t.cv_id=cv.cv_id" +	
			" WHERE cvtermprop.cvterm_id = ?" +
			"AND  t.IS_OBSOLETE = 0 "+
			"ORDER BY cvtermprop.cvtermprop_id DESC"								
			;	

	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(RegistryAttributeDaoImpl.class);
	
}
