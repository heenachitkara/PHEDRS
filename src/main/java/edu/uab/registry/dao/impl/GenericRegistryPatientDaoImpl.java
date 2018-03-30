package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import edu.uab.registry.util.Utilities;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.GenericRegistryPatientDao;
import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class GenericRegistryPatientDaoImpl implements GenericRegistryPatientDao 
{
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	public List<GenericRegistryPatient> getGenericRegistryPatients(int registryID_, String assignerID_, String statuses_) throws DaoException
	{	  
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		DataSource ds = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List <GenericRegistryPatient> genericRegistryPatients = new ArrayList<GenericRegistryPatient>();		
	    
		try {			
			// Build the query
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			sql.append("rp.uab_mrn, ");
			sql.append("rp.registry_patient_id, ");
			sql.append("rp.status_id, ");
			sql.append("rp.review_status_id, ");
			sql.append("registrystatus.name as status_name, ");
			sql.append("workflowstatus.name workflowstatus_name ");	
			
			sql.append("FROM registry_patient rp ");	
			sql.append("LEFT JOIN cvterm registrystatus ON registrystatus.cvterm_id = rp.status_id "); 
			sql.append("LEFT JOIN cvterm workflowstatus ON workflowstatus.cvterm_id = rp.review_status_id ");
			
			if (!StringUtils.isNullOrEmpty(assignerID_)) {
				sql.append("LEFT JOIN registry_actor ra ON rp.owner_id = ra.registry_actor_id ");
			}
			
			sql.append("WHERE rp.person_sk IS NOT NULL ");		
			sql.append("AND rp.registry_id = ? ");
			
			if (!StringUtils.isNullOrEmpty(assignerID_)) {
				sql.append("AND ra.login = ? ");
			}
			
			// Were any statuses provided?
			if (!StringUtils.isNullOrEmpty(statuses_)) {
				StringBuilder statusSubquery = new StringBuilder();
				String[] statusArray = statuses_.split(",");
				if (statusArray.length > 0) {
					
					// Append every status to a comma-delimited list.
					for (String status : statusArray) {
						if (!StringUtils.isNullOrEmpty(status)) { statusSubquery.append("'" + status.trim() + "',"); }
					}
					
					// Flatten to a string and remove the trailing comma.
					String subquery = statusSubquery.toString();
					if (subquery.length() > 0) {subquery = subquery.substring(0, subquery.lastIndexOf(",")); }
					
					// Update the main query.
					sql.append("AND rp.status_id IN (");
					sql.append("	SELECT cvterm_id ");
					sql.append("	FROM cvterm ");
					sql.append("	JOIN cv on cv.cv_id = cvterm.cv_id ");
					sql.append("	WHERE cv.name = 'PHEDRS Patient Status CV' ");
					sql.append("	AND cvterm.name IN (");
					sql.append(subquery);
					sql.append(		") ");
					sql.append(") ");
				}
			}
			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, registryID_);

			if (!StringUtils.isNullOrEmpty(assignerID_)) { ps.setString(2, assignerID_); }

			rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					GenericRegistryPatient grp = new GenericRegistryPatient();
					grp.setAssigned_by_name(rs.getString("assigner_id"));
					grp.setMrn(rs.getString("uab_mrn"));
					grp.setFull_name(rs.getString("source_firstname") + " " + rs.getString("source_lastname"));
					grp.setRegistry_patient_id(rs.getInt("registry_patient_id"));
					grp.setRegistry_status_id(rs.getInt("status_id"));
					grp.setRegistry_status(rs.getString("name"));
					grp.setLast_review_date(rs.getString("last_review_date"));

					genericRegistryPatients.add(grp);
				}
			}
		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			Utilities.closeDbResources(conn, ps, rs);
		}

		logger.debug("Number of RegistryPatients :" + genericRegistryPatients.size());
		logger.info("Log.ElapsedTime.dao.getGenericRegistryPatients=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return genericRegistryPatients;
	}
	
	
	public List<GenericRegistryPatient> getGenericRegistryPatient(String mrn_, int registryID_) throws DaoException
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();		
		
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List <GenericRegistryPatient> genericRegistryPatients = new ArrayList<GenericRegistryPatient>();
		
		try {			
			// Validate the MRN.
			if (StringUtils.isNullOrEmpty(mrn_)) { 
				throw new Exception("Invalid MRN (empty)"); 
			} else {
				mrn_ = mrn_.trim();
			}
			
			// Validate the registry ID
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			
			// Build the query
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			sql.append("rp.uab_mrn, ");
			sql.append("rp.registry_patient_id, ");
			sql.append("rp.status_id, ");
			sql.append("rp.review_status_id, ");
			sql.append("registrystatus.name as status_name, ");
			sql.append("workflowstatus.name workflowstatus_name ");	
			
			sql.append("FROM registry_patient rp ");	
			sql.append("LEFT JOIN cvterm registrystatus ON registrystatus.cvterm_id = rp.status_id "); 
			sql.append("LEFT JOIN cvterm workflowstatus ON workflowstatus.cvterm_id = rp.review_status_id ");
			sql.append("WHERE rp.person_sk IS NOT NULL ");		
			sql.append("AND rp.uab_mrn = ? ");
			sql.append("AND rp.registry_id = ? ");
			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, mrn_);
			ps.setInt(2, registryID_);
			
			rs = ps.executeQuery();
			
			if (rs != null && rs.next()) {
				
				GenericRegistryPatient grp = new GenericRegistryPatient();		
				grp.setMrn(rs.getString("uab_mrn"));
				grp.setRegistry_patient_id(rs.getInt("registry_patient_id"));
				grp.setRegistry_status_id(rs.getInt("status_id"));
				grp.setRegistry_status(rs.getString("status_name"));
				grp.setRegistry_workflow_status_id(rs.getInt("review_status_id"));
				grp.setRegistryWorkflowStatus(rs.getString("workflowstatus_name"));				
				
				genericRegistryPatients.add(grp);	            
	        }
		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			Utilities.closeDbResources(conn, ps, rs);
		}
		
		logger.debug("Number of RegistryPatients :" + genericRegistryPatients.size());
		logger.info("Log.ElapsedTime.dao.getRegistryPatient=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		
		return genericRegistryPatients;
	}
	
	
	public static void main(String[] args) 
	{
	}
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(GenericRegistryPatientDaoImpl.class);
}
