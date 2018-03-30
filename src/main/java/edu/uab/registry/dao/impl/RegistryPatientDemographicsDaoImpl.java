package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.RegistryPatientDemographicsDao;
import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.domain.RegistryNlpPatientInfo;
import edu.uab.registry.domain.RegistryPatientDemographics;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class RegistryPatientDemographicsDaoImpl implements RegistryPatientDemographicsDao
{

	// The query used to retrieve demographics. Note that there are 2 parameters to replace: 1) MRN, 2) registry ID.
	private String _demographicsSQL = null;


	// dmd testing 03/23/17
	public RegistryPatientDemographicsDaoImpl() {
		super();

		// Generate the query.
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");

		// Demographics
		sql.append("d.source_dob, ");
		sql.append("d.source_gender, ");
		sql.append("d.source_race, ");
		sql.append("d.source_lastname, ");
		sql.append("d.source_firstname, ");
		sql.append("d.source_mrn, ");
		sql.append("d.source_ethnic_group, ");
		sql.append("d.source_zipcode, ");
		sql.append("d.source_state, ");
		sql.append("d.source_city, ");
		sql.append("d.source_phone_number, ");

		// Registry patient
		sql.append("rp.registry_patient_id, ");

		// Registry status
		sql.append("rp.status_id, ");
		sql.append("registrystatus.name AS status_name, ");
		sql.append("rp.status_assignment_date, ");
		sql.append("assigner.login as status_assigner, ");
		sql.append("rp.registrar_comment as status_comment, ");

		// Workflow/review status
		sql.append("rp.review_status_id, ");
		sql.append("workflowstatus.name AS review_status_name, ");
		sql.append("rp.last_review_date, ");
		sql.append("reviewer.login as reviewer_name, ");
		sql.append("rp.workflow_comment ");

		sql.append("FROM registry_patient rp ");
		sql.append("LEFT JOIN registry_actor assigner ON (assigner.registry_actor_id = rp.assigner_id) ");
		sql.append("LEFT JOIN registry_actor reviewer ON (reviewer.registry_actor_id = rp.reviewer_id) ");

		// Since there can be multiple demographics for each registry patient, make sure only one is returned.
		sql.append("JOIN (");
		sql.append("	SELECT source_dob, source_gender, source_race, source_lastname, source_firstname, source_mrn, ");
		sql.append("	source_ethnic_group, source_zipcode, source_state, source_city, source_phone_number, ");
		sql.append("	row_number() OVER (partition by source_mrn ORDER BY source_max_enc_date desc) AS d_rownum ");
		sql.append("	FROM registry_patient_demographics d ");
		sql.append(") d ON d.source_mrn = rp.uab_mrn ");
		sql.append("LEFT JOIN cvterm registrystatus ON registrystatus.cvterm_id = rp.status_id " );
		sql.append("LEFT JOIN cvterm workflowstatus ON workflowstatus.cvterm_id = rp.review_status_id ");
		sql.append("WHERE rp.uab_mrn = ? ");
		sql.append("AND rp.registry_id = ? ");
		sql.append("AND d_rownum = 1 ");

		_demographicsSQL = sql.toString();
	}


	public void get(RegistryPatientDemographics demographics_, String mrn_, int registryID_, GenericRegistryPatient registryPatient_) throws DaoException
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		DataSource ds = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			// Validate the input parameters.
			if (demographics_ == null) { throw new Exception("The demographics object must be pre-initialized"); }
			if (StringUtils.isNullOrEmpty(mrn_)) { throw new Exception("Invalid MRN"); }
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }
			if (registryPatient_ == null) { throw new Exception("The registry patient object must be pre-initialized"); }

			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();

			if (StringUtils.isNullOrEmpty(_demographicsSQL)) {throw new Exception("The demographics SQL was not initialized"); }
			ps = conn.prepareStatement(_demographicsSQL);
			ps.setString(1, mrn_);
			ps.setInt(2, registryID_);

			rs = ps.executeQuery();
			if (rs != null && rs.next()) {

				// Populate the patient demographics object.
				demographics_.setCity(rs.getString("source_city"));
				demographics_.setBirth_date(rs.getDate("source_dob"));
				demographics_.setEthnicity(rs.getString("source_ethnic_group"));
				demographics_.setFirst_name(rs.getString("source_firstname"));
				demographics_.setLast_name(rs.getString("source_lastname"));
				demographics_.setFull_name(demographics_.getFirst_name() + " " + demographics_.getLast_name());
				demographics_.setGender(rs.getString("source_gender"));
				demographics_.setMrn(rs.getString("source_mrn"));
				demographics_.setPhone_number(rs.getString("source_phone_number"));
				demographics_.setRace(rs.getString("source_race"));
				demographics_.setZipcode(rs.getString("source_zipcode"));

				// Populate the registry patient object.
				registryPatient_.setRegistry_patient_id(rs.getInt("registry_patient_id"));

				// Registry status
				registryPatient_.setRegistry_status_id(rs.getInt("status_id"));
				registryPatient_.setRegistry_status(rs.getString("status_name"));
				registryPatient_.setAssignment_date(rs.getString("status_assignment_date"));
				registryPatient_.setAssigned_by_name(rs.getString("status_assigner"));
				registryPatient_.setRegistrar_comment(rs.getString("status_comment"));

				// Workflow/review status
				registryPatient_.setRegistry_workflow_status_id(rs.getInt("review_status_id"));
				registryPatient_.setRegistryWorkflowStatus(rs.getString("review_status_name"));
				registryPatient_.setLast_review_date(rs.getString("last_review_date"));
				registryPatient_.setReviewer_name(rs.getString("reviewer_name"));
				registryPatient_.setWorkflow_comment(rs.getString("workflow_comment"));
			}

		} catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }}
			if (ps != null) { try { ps.close(); } catch(SQLException sqe) { sqe.printStackTrace(); }}
			if (conn != null) { try { conn.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }}
		}

		stopWatch.stop();
		logger.info("Log.ElapsedTime.dao.get=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
	}


	/*
	public RegistryNlpPatientInfo getRegistryPatientDemographics(String mrn_, int registryID_) throws DaoException
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();	
		
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		RegistryNlpPatientInfo patientInfo = null;
		RegistryPatientDemographics demographics = null;
		GenericRegistryPatient registryPatient = null;

        try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();

			if (StringUtils.isNullOrEmpty(_demographicsSQL)) {throw new Exception("The demographics SQL was not initialized"); }
			ps = conn.prepareStatement(_demographicsSQL);
			ps.setString(1, mrn_);
			ps.setInt(2, registryID_);
			
			rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				
				// Initialize and populate the patient demographics object.
				demographics = new RegistryPatientDemographics();
				demographics.setCity(rs.getString("source_city"));
				demographics.setBirth_date(rs.getDate("source_dob"));
				demographics.setEthnicity(rs.getString("source_ethnic_group"));
				demographics.setFirst_name(rs.getString("source_firstname"));
				demographics.setLast_name(rs.getString("source_lastname"));
				demographics.setFull_name(demographics.getFirst_name() + " " + demographics.getLast_name());
				demographics.setGender(rs.getString("source_gender"));				
				demographics.setMrn(rs.getString("source_mrn"));
				demographics.setPhone_number(rs.getString("source_phone_number"));
				demographics.setRace(rs.getString("source_race"));				
				demographics.setZipcode(rs.getString("source_zipcode"));								
				
				// Initialize and populate the registry patient object.
				registryPatient = new GenericRegistryPatient();
				registryPatient.setRegistry_patient_id(rs.getInt("registry_patient_id"));
				registryPatient.setRegistry_status_id(rs.getInt("status_id"));
				registryPatient.setRegistry_status(rs.getString("status_name"));
				registryPatient.setRegistry_workflow_status_id(rs.getInt("review_status_id"));
				registryPatient.setRegistryWorkflowStatus(rs.getString("review_status_name"));
				registryPatient.setRegistrar_comment(rs.getString("registrar_comment"));
				registryPatient.setWorkflow_comment(rs.getString("workflow_comment"));

				// Initialize and populate the object to be returned.
				patientInfo = new RegistryNlpPatientInfo();
				patientInfo.setPatient_demographics(demographics);
				patientInfo.setRegistry_patient(registryPatient);
			}
			
		} catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }}
			if (ps != null) { try { ps.close(); } catch(SQLException sqe) { sqe.printStackTrace(); }}
			if (conn != null) { try { conn.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }}
		}
		
		stopWatch.stop();
		logger.info("Log.ElapsedTime.dao.getRegistryPatientDemographics=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		
		return patientInfo;
	}*/
	
	public static void main(String[] args) {}
	 

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(RegistryPatientDemographicsDaoImpl.class);
}



