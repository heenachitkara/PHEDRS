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
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.DetectionCodesDao;
import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.domain.GenericRegistryPatientList;
import edu.uab.registry.domain.RegistryDetectionCode;
import edu.uab.registry.domain.RegistryDetectionCodeList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;

public class DetectionCodesDaoImpl implements DetectionCodesDao 
{

	
	public RegistryDetectionCodeList getDetectionCodes(String mrn, int registryId) throws DaoException
	{	  
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		RegistryDetectionCodeList registryDetectionCodeList = new RegistryDetectionCodeList(); 
		List<RegistryDetectionCode> rdcList = new ArrayList<RegistryDetectionCode>();
		
		try {						
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();								
			
			pstmt = conn.prepareStatement(selectDetectionCodes_SQL);
			logger.debug("detection codes sql query :" + selectDetectionCodes_SQL);
			logger.debug("detection codes sql parameters, mrn:" + mrn + ", registryId:" + registryId);
			
			int param = 1;
			pstmt.setString(param, mrn);
			param++;
			pstmt.setInt(param, registryId);
												
			rs = pstmt.executeQuery();					
			while(rs.next()) {
				RegistryDetectionCode rdc = new RegistryDetectionCode();
				rdc.setMrn(mrn);
				rdc.setRegistry_id(registryId);
				rdc.setAccession(rs.getString("accession"));
				rdc.setCode_assignment_date(rs.getString("code_assignment_date"));
				rdc.setCode_value(rs.getString("code_value"));
								
				rdcList.add(rdc);	       
	        }
			registryDetectionCodeList.setRegistry_detection_code_list(rdcList);
		} catch (Throwable th) {
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
		logger.debug("Number of Detection Codes :" + registryDetectionCodeList.getRegistry_detection_code_list().size());		
		return registryDetectionCodeList;
	}	
	

	
	private String selectDetectionCodes_SQL =
			"SELECT " +
				"dbx.accession, rpc.code_assignment_date, rpc.code_value " + 
			"FROM " +
				"registry_patient_codes rpc " + 
				"JOIN dbxref dbx ON (dbx.dbxref_id = rpc.code_dbxref) " +
			"WHERE " +
				"rpc.uab_mrn = ? " +
				"AND rpc.code_dbxref IN " + 
				"( " +
					"SELECT " + 
						"cvterm.dbxref_id " +
					"FROM " +
						"cvterm " +
						"JOIN dbxref ON (cvterm.dbxref_id = dbxref.dbxref_id) " + 
						"JOIN cvtermprop ON (cvtermprop.cvterm_id = cvterm.cvterm_id) " +
					"WHERE " +
						"type_id = ? " +
						"AND (cvtermprop.value LIKE '%diagnosis criteria'OR cvtermprop.value LIKE '%inclusion criteria') " +
				") "
			;

	public static void main(String[] args)
	{
	}
	public void setDataSource(DataSource dataSource)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = LoggerFactory.getLogger(DetectionCodesDaoImpl.class);		
	
}
