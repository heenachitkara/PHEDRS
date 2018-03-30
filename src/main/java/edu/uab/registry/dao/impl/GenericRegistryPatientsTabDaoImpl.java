package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import edu.uab.registry.util.Utilities;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.GenericRegistryPatientsTabDao;
import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;

public class GenericRegistryPatientsTabDaoImpl implements GenericRegistryPatientsTabDao 
{

	// dmd 04/06/17 - moved SQL_COMMON_TAB_QUERY to Utilities.

	// Constraints used by the "inpatient" tab.
	private static String SQL_REGISTRY_INPATIENT_CONSTRAINTS =
				"AND rp.review_status_id NOT IN (" +
					"  SELECT cvterm_id  " +
					"  FROM cv " +
					"  JOIN cvterm on cvterm.cv_id = cv.cv_id " +
					"  WHERE cv.name = 'PHEDRS Review Status CV' " +
					"  AND cvterm.name IN ('Will Not Review') " +
					") " +
			"AND rp.status_id NOT IN ( " +
					"	SELECT cvterm_id  " +
					"	FROM cv " +
					"	JOIN cvterm on cvterm.cv_id = cv.cv_id " +
					"	WHERE cv.name = 'PHEDRS Patient Status CV' " +
					"	AND cvterm.name = 'Rejected' " +
					") " +
					"AND 0 < ( " +
					"	SELECT count(*)  " +
					"	FROM registry_encounter re " +
					"	WHERE re.formatted_medical_record_nbr = rp.FORMATTED_UAB_MRN " +
					"	AND re.inout_cd IN ('Inpatient', 'Emergency Patient', 'Bedded Emergency') " +
					"	AND re.active_status_cd LIKE '%-NOT-ENDED' " +
					"	AND re.start_date >= (SYSDATE-30) " +
					"	AND (   (reason_for_visit_txt LIKE '%COPD%') OR (reason_for_visit_txt LIKE '%SOB%')  OR (UPPER(reason_for_visit_txt) LIKE '%COUGH%') OR (UPPER(reason_for_visit_txt) LIKE '%BRONCHITIS%') OR (UPPER(reason_for_visit_txt) LIKE '%BRONCHIECTASIS%') OR (UPPER(reason_for_visit_txt) LIKE '%EMPHYSEMA%') ) "+
					") ";


	// Constraints used by the "inpatient review" tab.
	private static String SQL_REGISTRY_INPATIENT_REVIEW_CONSTRAINTS =
			"AND rp.review_status_id NOT IN (" +
					"  SELECT cvterm_id  " +
					"  FROM cv " +
					"  JOIN cvterm on cvterm.cv_id = cv.cv_id " +
					"  WHERE cv.name = 'PHEDRS Review Status CV' " +
					"  AND cvterm.name IN ('Reviewed','Will Not Review') " +
					") " +
					"AND rp.status_id NOT IN (" +
					"  SELECT cvterm_id  " +
					"  FROM cv " +
					"  JOIN cvterm on cvterm.cv_id = cv.cv_id " +
					"  WHERE cv.name = 'PHEDRS Patient Status CV' " +
					"  AND cvterm.name IN ('Rejected') " +
					") " +
					"AND 0 < ( " +
					"  SELECT count(*)  " +
					"  FROM registry_encounter re " +
					"  WHERE re.formatted_medical_record_nbr = rp.FORMATTED_UAB_MRN " +
					"  AND re.inout_cd IN ('Inpatient', 'Emergency Patient', 'Bedded Emergency') " + 
					"  AND re.active_status_cd LIKE '%-NOT-ENDED' " +
					"  AND re.start_date >= (SYSDATE-30) " + 
					"	AND (   (reason_for_visit_txt LIKE '%COPD%') OR (UPPER(reason_for_visit_txt) LIKE '%BRONCHITIS%') OR (UPPER(reason_for_visit_txt) LIKE '%BRONCHIECTASIS%') OR (UPPER(reason_for_visit_txt) LIKE '%EMPHYSEMA%') ) "+
					") ";

	
	// Constraints used by the "registry patients" tab.
	private static String SQL_REGISTRY_CONSTRAINTS =
					"AND rp.status_id NOT IN (" +
					"  SELECT cvterm_id  " +
					"  FROM cv " +
					"  JOIN cvterm on cvterm.cv_id = cv.cv_id " +
					"  WHERE cv.name = 'PHEDRS Patient Status CV' " +
					"  AND cvterm.name IN ('Rejected') " +
					") ";

	
	
	

	// Constraints used by the "90 day window" tab.
	private static String SQL_90_DAY_WINDOW_CONSTRAINTS =
			//"AND workflowstatus.name <> 'Reviewed' " + 
			"AND workflowstatus.cvterm_id != 2881 " + 
					//"AND registrystatus.name = 'Accepted' " + 
					"AND registrystatus.cvterm_id = 2824 " + 
					"AND 0 < ( " + 
					"  SELECT count(*) " +  
					"  FROM registry_encounter re " + 
					"  JOIN registry_encounter_cvterm rect ON rect.encounter_key = re.encounter_key " + 
					"  JOIN cvterm ON cvterm.cvterm_id = rect.cvterm_id " + 
					"  WHERE re.formatted_medical_record_nbr = rp.FORMATTED_UAB_MRN " + 
					"  AND rect.registry_id = rp.registry_id " + 
					//"  AND cvterm.name like '%Anchor Encounter' " + 
					//FIXME should have UabUimaTools with 2887 as a constant as a dependency
					"  AND cvterm.cvterm_id = 2887 " + 
					"  AND re.inout_cd = 'Inpatient' " + 
					"  AND nvl(re.start_date,re.end_date) >= (TRUNC(SYSDATE)-90) " +  
					") ";


	// Constraints used by the "Medicare 90 day window" tab.
	private static String SQL_MEDICARE_90_DAY_WINDOW_CONSTRAINTS =
			"AND workflowstatus.cvterm_id != 2881 " + 
					"AND registrystatus.cvterm_id = 2824 " + 
					"AND 0 < ( " + 
					"  SELECT count(*) " +  
					"  FROM registry_encounter re " + 
					"  JOIN registry_encounter_cvterm rect ON rect.encounter_key = re.encounter_key " + 
					"  JOIN cvterm ON cvterm.cvterm_id = rect.cvterm_id " + 
					"  WHERE re.formatted_medical_record_nbr = rp.FORMATTED_UAB_MRN " + 
					"  AND rect.registry_id = rp.registry_id " + 
					//FIXME should have UabUimaTools with 2887 as a constant as a dependency
					"  AND cvterm.cvterm_id = 2887 " + 
					"  AND re.inout_cd = 'Inpatient' " + 
					"  AND nvl(re.start_date,re.end_date) >= (TRUNC(SYSDATE)-90) " +  
					
				    ") AND rp.uab_mrn IN "+
				    "  (  SELECT rbc.mrn "+
				    "     FROM registry_billed_charges rbc "+
				    "     WHERE PLNFSC='81' )";

	// The default ORDER BY clause.
	private static String SQL_DEFAULT_ORDER_BY = "ORDER BY d.source_lastname asc, d.source_firstname asc ";


	// Abstract how queries are associated with registry ID and tab.
	// FIXME: This is temporary. In the future, we will dynamically retrieve the SQL
	// FIXME - generateQuery should also take in the user_id so they can pull their OWN patients
	private String generateQuery(Integer registryID_, String tabKey_) {

		StringBuilder sql = new StringBuilder();

		// Add the common SQL shared by all tab queries. Note that it is always specific to a particular registry!
		sql.append(Utilities.generateRegistryPatientPartialQuery(registryID_));

		// The tab key will determine the constraints to add.
		switch (tabKey_) {
		case Constants.TAB_ALL_REGISTRY_PATIENTS: {
			sql.append(SQL_REGISTRY_CONSTRAINTS);
			break;
		}
		case Constants.TAB_REGISTRY_INPATIENT_REVIEW_TAB: {
			sql.append(SQL_REGISTRY_INPATIENT_REVIEW_CONSTRAINTS);
			break;
		}
		case Constants.TAB_REGISTRY_INPATIENT_TAB: {
			sql.append(SQL_REGISTRY_INPATIENT_CONSTRAINTS);
			break;
		}
		case Constants.TAB_90_DAY_WINDOW: {
			sql.append(SQL_90_DAY_WINDOW_CONSTRAINTS);
			break;
		}
		case Constants.TAB_90_DAY_WINDOW_MEDICARE: {
			sql.append(SQL_MEDICARE_90_DAY_WINDOW_CONSTRAINTS);
			break;
		}
		//FIXME - write new function to pull external SQL from database
		default: logger.debug("Database query to pull needed tab SQL to append. Slower performance." );
		}

		sql.append(SQL_DEFAULT_ORDER_BY);

		return sql.toString();
	}


	// Get a list of registry patients that correspond to this registry's tab key.
	public List<GenericRegistryPatient> get(Logger logger_, Integer registryID_, String tabKey_) throws DaoException
	{	  
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();	

		DataSource ds = null;
		//Connection conn = null;
		//PreparedStatement ps = null;
		//ResultSet rs = null;

		// Create the list of patients that will be returned.
		List<GenericRegistryPatient> registryPatients = new ArrayList<GenericRegistryPatient>();

		try {			
			// Generate the appropriate SQL for this registry's tab. 
			String sql = generateQuery(registryID_, tabKey_);
			if (StringUtils.isNullOrEmpty(sql)) { throw new DaoException("Invalid SQL was generated for tab " + tabKey_); }

			logger.info(sql);

			ds = jdbcTemplate.getDataSource();
			try (Connection conn = ds.getConnection();
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()){
				if (rs != null) {
					while(rs.next()) {
						//--------------------------------------------------------------------------------------------------
						// Get data from the result set.
						//--------------------------------------------------------------------------------------------------
						String first_name = rs.getString("first_name");
						String last_contact_date = rs.getString("last_contact_date");
						String last_name = rs.getString("last_name");
						String mrn = rs.getString("mrn");
						int registry_patient_id = rs.getInt("registry_patient_id");

						// Detection event(s)
						String detection_event_abbr = rs.getString("detection_event_abbr");
						int detection_event_id = rs.getInt("detection_event_id");
						String detection_event_name = rs.getString("detection_event_name");

						// Registry status
						int status_id = rs.getInt("status_id");
						String status_name = rs.getString("status_name");
						String status_assigner = rs.getString("status_assigner");
						String status_assignment_date = rs.getString("status_assignment_date");
						String status_comment = rs.getString("status_comment");

						// Workflow/review status
						int review_status_id = rs.getInt("review_status_id");
						String review_status_name = rs.getString("review_status_name");
						String reviewed_by = rs.getString("reviewed_by");
						String last_review_date = rs.getString("last_review_date");
						String review_comment = rs.getString("review_comment");

						//--------------------------------------------------------------------------------------------------
						// Validate data
						//--------------------------------------------------------------------------------------------------
						//if (StringUtils.isNullOrEmpty(first_name)) { throw new DaoException("Invalid first name"); }
						
						// dmd 04/14/17: found registry patients for multiple myeloma registry with empty last names (and
						// strangely enough, their first names were all preceded by a *).
						//if (StringUtils.isNullOrEmpty(last_name)) { throw new DaoException("Invalid last name"); }
						if (StringUtils.isNullOrEmpty(last_name)) { last_name = ""; }

						if (StringUtils.isNullOrEmpty(mrn)) { throw new DaoException("Invalid MRN"); }

						//--------------------------------------------------------------------------------------------------
						// Create the patient object.
						//--------------------------------------------------------------------------------------------------
						GenericRegistryPatient patient = new GenericRegistryPatient();
						patient.setFull_name(last_name + ", " + first_name);
						patient.setLastContactDate(last_contact_date);
						patient.setMrn(mrn);
						patient.setRegistry_patient_id(registry_patient_id);

						// Detection event(s)
						patient.setDetectionEventAbbr(detection_event_abbr);
						patient.setDetectionEventId(detection_event_id);
						patient.setDetectionEventName(detection_event_name);

						// Registry status
						patient.setRegistry_status_id(status_id);
						patient.setRegistry_status(status_name);
						patient.setAssigned_by_name(status_assigner);
						patient.setAssignment_date(status_assignment_date);
						patient.setRegistrar_comment(status_comment);

						// Workflow/review status
						patient.setRegistry_workflow_status_id(review_status_id);
						patient.setRegistryWorkflowStatus(review_status_name);
						patient.setAssigned_by_name(reviewed_by);
						patient.setLast_review_date(last_review_date);
						patient.setWorkflow_comment(review_comment);

						//--------------------------------------------------------------------------------------------------
						// Update the collection of patients.
						//--------------------------------------------------------------------------------------------------
						registryPatients.add(patient);
					}
				}

			} catch (Throwable th) {
				throw new DaoException(th.getMessage(), th);
			} 
		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} 

		return registryPatients;
	}


	public static void main(String[] args) 
	{
	}
	public void setDataSource(DataSource dataSource)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(GenericRegistryPatientsTabDaoImpl.class);		

}
