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

import edu.uab.registry.dao.PatientCVDao;
import edu.uab.registry.domain.PatientCVList;
import edu.uab.registry.domain.PatientCVList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;

public class PatientCVDaoImpl implements PatientCVDao {
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	
	public List<PatientCVList> getPatientAttributeCV(int registryId) throws DaoException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		List<PatientCVList> patientCVDataList = new ArrayList<PatientCVList>();
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectPatientAttributeCV_SQL);
			logger.debug("sql query :" + selectPatientAttributeCV_SQL);
			System.out.println("February 28 Test");
			System.out.println(selectPatientAttributeCV_SQL);
			pstmt.setInt(1, registryId);
			logger.debug("sql parameters, registryId:" +  registryId);
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				
				PatientCVList    patientCVList  = new PatientCVList();
				patientCVList.setRegistryId(registryId);
				patientCVList.setPatientCvName(rs.getString("name"));
				patientCVDataList.add(patientCVList);
						
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
		logger.debug("Number of CV MetaData List :" +patientCVDataList.size());
		logger.info("Log.ElapsedTime.dao.getcvMetaData=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return patientCVDataList;
				
	}
	
	
	private String selectPatientAttributeCV_SQL = 
			"SELECT  " 
	             + "c.name,"
				 + "r.registry_id, "
				 + "r.attribute_cvterm_id,"
				 + "r.value_display_name,"
				 + "r.value_cv_id " +  				 			
			"FROM " + 				 
				"CV c,REGISTRY_CV_METADATA r " +				
			"WHERE c.CV_ID = r.VALUE_CV_ID" +								
				" AND registry_id = ? " + 
				" AND attribute_cvterm_id  = "+Constants.PATIENT_ATTRIBUTE_CVTERM_ID;
			
			;	
	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(PatientCVDaoImpl.class);
	
	

}
