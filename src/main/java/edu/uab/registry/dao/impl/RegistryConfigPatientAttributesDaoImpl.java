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

import edu.uab.registry.dao.RegConfigPatientAttributesDao;
import edu.uab.registry.domain.RegConfigPatientAttributesList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;
import edu.uab.registry.util.Constants;

public class RegistryConfigPatientAttributesDaoImpl implements RegConfigPatientAttributesDao  {
	
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	
	public List<RegConfigPatientAttributesList> getRegConfigPatientAttr(int registryID_) throws DaoException
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		
		List<RegConfigPatientAttributesList> regPatAttributeList = new ArrayList<RegConfigPatientAttributesList>();
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectRegPatientAttributes_SQL);
			logger.debug("sql query :" + selectRegPatientAttributes_SQL);
			System.out.println("September 22 Test");
			System.out.println(selectRegPatientAttributes_SQL);
			pstmt.setInt(1, registryID_);
			logger.debug("sql parameters, Registry ID:" +  registryID_);
			System.out.println(Constants.PATIENT_ATTRIBUTE_CVTERM_ID);
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				
				
				RegConfigPatientAttributesList patientAttributeList = new RegConfigPatientAttributesList();
				patientAttributeList.setpatienrCvTermName(rs.getString("name"));
				patientAttributeList.setpatientCvTermDefinition(rs.getString("definition"));
				patientAttributeList.setCvID(rs.getInt("cv_id"));
				patientAttributeList.setCvTermID(rs.getInt("cvterm_id"));
				regPatAttributeList.add(patientAttributeList);
				
			}
		} catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
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
		logger.debug("Number of Registry Config Pat Attribute  List :" +regPatAttributeList.size());
		logger.info("Log.ElapsedTime.dao.getRegConfigPatientAttr=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return regPatAttributeList;
	}
	
	private String selectRegPatientAttributes_SQL = 
			"SELECT NAME, DEFINITION, CV_ID, CVTERM_ID FROM CVTERM WHERE IS_OBSOLETE = 0 AND CV_ID IN"+
	        "(SELECT VALUE_CV_ID FROM REGISTRY_CV_METADATA WHERE registry_id = ? AND attribute_cvterm_id = "+Constants.PATIENT_ATTRIBUTE_CVTERM_ID+ ")";
	             			 			
			
			    
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(RegistryConfigPatientAttributesDaoImpl.class);
	
	
	

}
