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

import edu.uab.registry.dao.GenericRegistryPatientDatasetDao;
import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class GenericRegistryPatientDatasetDaoImpl implements GenericRegistryPatientDatasetDao 
{
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	public List<GenericRegistryPatient> getGenericRegistryPatientsDataset(int registryId, String datasetName, String datasetStatus) throws DaoException
	{	  
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List <GenericRegistryPatient> genericRegistryPatients = new ArrayList<GenericRegistryPatient>();		
	    
		try {			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();		
			String sql = buildSQLStatement(datasetStatus);
			pstmt = conn.prepareStatement(sql);			
			pstmt.setInt(1, registryId);
			pstmt.setString(2, datasetName);						
			logger.debug("sql query :" + selectGenericDatasetRegistryPatients_SQL);	
			logger.debug("sql parameters, registryId:" + registryId + ", datasetName:" + datasetName + ", datasetStatus:" + datasetStatus);						
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				GenericRegistryPatient grp = new GenericRegistryPatient();
				grp.setAssigned_by_name(rs.getString("owner_id"));
				grp.setMrn(rs.getString("mrn"));
				grp.setFull_name(rs.getString("input_patient_name"));
				grp.setRegistry_patient_id(registryId);
				grp.setRegistry_status_id(rs.getInt("status_id"));	
				grp.setRegistry_status(rs.getString("name"));
				grp.setLast_review_date(rs.getString("last_review_date"));
				//System.out.println("MRN:" + rp.getMrn() + ", Name:" + rp.getName() + ", status:" + rp.getCrec_state());
				genericRegistryPatients.add(grp);	            
	        }
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
		logger.debug("Number of RegistryPatients :" + genericRegistryPatients.size());
		logger.info("Log.ElapsedTime.dao.getGenericRegistryPatients=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return genericRegistryPatients;
	}
	
	private String buildSQLStatement(String datasetStatus) 
	{
		String sql = selectGenericDatasetRegistryPatients_SQL;
		if (!"".equals(datasetStatus)) {
			sql = sql + "rda.assignment_status IN (SELECT cvterm_id FROM cvterm WHERE name = ?) ";
		}
		return sql;		
	}
		
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
			GenericRegistryPatientDatasetDao grdpDao = (GenericRegistryPatientDatasetDao) context.getBean("genericRegistryPatientDatasetDao");
			int registryId = 2689;	
			String datasetName = "Multiple Myeloma Kidney Dataset";		
			String datasetStatus = "";
			List<GenericRegistryPatient> grps = grdpDao.getGenericRegistryPatientsDataset(registryId, datasetName, datasetStatus);								
			for (int i=0; i<grps.size(); i++) {
				GenericRegistryPatient grp = grps.get(i);
				System.out.println("MRN:" + grp.getMrn() + ", Name:" + grp.getFull_name() + ", status_id:" + grp.getRegistry_status_id() +
						", status:"+ grp.getRegistry_status() + ", last review date:" + grp.getLast_review_date());
			}
			System.out.println("Number of Generic Dataset Registry Patients :" + grps.size());
		} catch(Throwable th) {
			th.printStackTrace();
		}
	}				
		
	private String selectGenericDatasetRegistryPatients_SQL = 
			"SELECT rda.mrn, rda.input_patient_name, rda.registry_patient_id, rda.assignment_date, rp.owner_id, rp.status_id, cv.name, rp.last_review_date " + 														 				
			"FROM registry_patient rp " +
				"LEFT JOIN registry_dataset_assignment rda ON rp.registry_patient_id = rda.registry_patient_id " +
				"LEFT JOIN cvterm cv ON rp.status_id = cv.cvterm_id " + 				 
			"WHERE rp.registry_id = ? AND " +
				"rda.dataset_id IN (SELECT cvterm_id FROM cvterm WHERE name = ?) "			    
			;		
									 	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(GenericRegistryPatientDatasetDaoImpl.class);	
	
}
