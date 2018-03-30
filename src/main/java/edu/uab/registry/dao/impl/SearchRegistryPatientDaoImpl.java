package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import edu.uab.registry.util.Utilities;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.SearchRegistryPatientDao;
import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.exception.DaoException;

public class SearchRegistryPatientDaoImpl implements SearchRegistryPatientDao 
{

	// This method supports "advanced search" functionality to retrieve registry patients.
	public List<GenericRegistryPatient> search(String assignedBy_,
			String assignmentDateFrom_,
			String assignmentDateTo_,
			String detectionEvents_,
			String encounterAttributes_,
			String encounterDateFrom_,
			String encounterDateTo_,
			String firstName_,
			String lastName_,
			String lastReviewFrom_,
			String lastReviewTo_,
			String mrn_,
			String patientAttributes_,
			Integer registryID_,
			String registryStatusID_,
			String reviewedBy_,
			String ssn_,
			String workflowStatusID_) throws DaoException {

		// Validate parameters
		if (registryID_ < 1) { throw new DaoException("Invalid registry ID"); }

		DataSource ds = null;

		// Create the list of patients that will be returned.
		List<GenericRegistryPatient> registryPatients = new ArrayList<GenericRegistryPatient>();


		// Create the query and add the standard partial query to return registry patients.
		StringBuilder sql = new StringBuilder();
		sql.append(Utilities.generateRegistryPatientPartialQuery(registryID_));

		// Add WHERE constraints based on the parameters provided.

		//--------------------------------------------------------------------------------------------------------------
		// Assigned by
		//--------------------------------------------------------------------------------------------------------------
		if (!StringUtils.isNullOrEmpty(assignedBy_)) {
			String assignedBy = Utilities.validateNumericString(assignedBy_.trim());
			sql.append(String.format("AND assigner.registry_actor_id in (%s) ", assignedBy));
		}

		//--------------------------------------------------------------------------------------------------------------
		// Assignment dates
		//--------------------------------------------------------------------------------------------------------------
		try {
			String assignmentDateFrom = Utilities.parseToOracleDateString(assignmentDateFrom_);
			if (!StringUtils.isNullOrEmpty(assignmentDateFrom)) {
				sql.append(String.format("AND rp.status_assignment_date >= '%s' ", assignmentDateFrom));
			}
		} catch (ParseException pexc_) { /* Ignore this */}
		try {
			String assignmentDateTo = Utilities.parseToOracleDateString(assignmentDateTo_);
			if (!StringUtils.isNullOrEmpty(assignmentDateTo)) {
				sql.append(String.format("AND rp.status_assignment_date <= '%s' ", assignmentDateTo));
			}
		} catch (ParseException pexc_) { /* Ignore this */}

		//--------------------------------------------------------------------------------------------------------------
		// Detection Event
		//--------------------------------------------------------------------------------------------------------------
		try {
			if (!StringUtils.isNullOrEmpty(detectionEvents_)) {
				String detectionEvents = Utilities.validateNumericString(detectionEvents_.trim());
				sql.append(String.format("AND rp.evidence_code in (%s) ", detectionEvents));
			}
		} catch (NumberFormatException n_) { /* Ignore */ }

		//--------------------------------------------------------------------------------------------------------------
		// Encounter attributes
		//--------------------------------------------------------------------------------------------------------------
		try {
			if (!StringUtils.isNullOrEmpty(encounterAttributes_)) {
				String encounterAttributes = Utilities.validateNumericString(encounterAttributes_.trim());

				sql.append("AND 0 < (");
				sql.append("	SELECT count(*) ");
				sql.append("	FROM registry_encounter re ");
				sql.append("	JOIN registry_encounter_cvterm rec ON (re.encounter_key=rec.encounter_key) ");
				sql.append("	WHERE re.formatted_medical_record_nbr = rp.formatted_uab_mrn ");
				sql.append(String.format("	AND rec.cvterm_id in (%s) ", encounterAttributes));
				sql.append(") ");
			}
		} catch (NumberFormatException n_) { /* Ignore */ }

		//--------------------------------------------------------------------------------------------------------------
		// Encounter dates
		//--------------------------------------------------------------------------------------------------------------
		String encounterDateFrom = null;
		String encounterDateTo = null;

		try { encounterDateFrom = Utilities.parseToOracleDateString(encounterDateFrom_); } catch (ParseException pexc_) { /* Ignore this */}
		try { encounterDateTo = Utilities.parseToOracleDateString(encounterDateTo_); } catch (ParseException pexc_) { /* Ignore this */}

		if (!StringUtils.isNullOrEmpty(encounterDateFrom) || !StringUtils.isNullOrEmpty(encounterDateTo)) {
			sql.append("AND 0 < (");
			sql.append("	SELECT count(*) ");
			sql.append("	FROM registry_encounter ");
			sql.append("	WHERE formatted_medical_record_nbr = rp.formatted_uab_mrn ");

			if (!StringUtils.isNullOrEmpty(encounterDateFrom)) { sql.append(String.format("AND update_date >= '%s' ", encounterDateFrom)); }
			if (!StringUtils.isNullOrEmpty(encounterDateTo)) { sql.append(String.format("AND update_date <= '%s' ", encounterDateTo)); }
			sql.append(") ");
		}

		//--------------------------------------------------------------------------------------------------------------
		// First and last name
		//--------------------------------------------------------------------------------------------------------------
		if (!StringUtils.isNullOrEmpty(firstName_)) { sql.append(String.format("AND regexp_like(d.source_firstname,'%s','i') ", firstName_.trim())); }
		if (!StringUtils.isNullOrEmpty(lastName_)) { sql.append(String.format("AND regexp_like(d.source_lastname,'%s','i') ", lastName_.trim())); }

		//--------------------------------------------------------------------------------------------------------------
		// MRN
		//--------------------------------------------------------------------------------------------------------------
		try {
			if (!StringUtils.isNullOrEmpty(mrn_)) {
				sql.append(String.format("AND regexp_like(rp.uab_mrn, '%s','i') ", mrn_.trim()));
			}
		}
		catch (NumberFormatException n_) { /* Ignore */ }

		//--------------------------------------------------------------------------------------------------------------
		// Patient attributes
		//--------------------------------------------------------------------------------------------------------------
		try {
			if (!StringUtils.isNullOrEmpty(patientAttributes_)) {
				String patientAttributes = Utilities.validateNumericString(patientAttributes_.trim());

				sql.append("AND 0 < (");
				sql.append("	SELECT count(*) ");
				sql.append("	FROM registry_patient_cvterm patient_attr ");
				sql.append("	WHERE patient_attr.registry_patient_id = rp.registry_patient_id ");
				sql.append(String.format("	AND patient_attr.cvterm_id in (%s) ", patientAttributes));
				sql.append(") ");
			}
		}
		catch (NumberFormatException n_) { /* Ignore */ }

		//--------------------------------------------------------------------------------------------------------------
		// Review date
		//--------------------------------------------------------------------------------------------------------------
		try {
			String lastReviewFrom = Utilities.parseToOracleDateString(lastReviewFrom_);
			if (!StringUtils.isNullOrEmpty(lastReviewFrom)) {
				sql.append(String.format("AND rp.last_review_date >= '%s' ", lastReviewFrom));
			}
		} catch (ParseException pexc_) { /* Ignore this */}

		try {
			String lastReviewTo = Utilities.parseToOracleDateString(lastReviewTo_);
			if (!StringUtils.isNullOrEmpty(lastReviewTo)) {
				sql.append(String.format("AND rp.last_review_date <= '%s' ", lastReviewTo));
			}
		} catch (ParseException pexc_) { /* Ignore this */}

		//--------------------------------------------------------------------------------------------------------------
		// Registry status ID
		//--------------------------------------------------------------------------------------------------------------
		if (!StringUtils.isNullOrEmpty(registryStatusID_)) {
			String registryStatusID = Utilities.validateNumericString(registryStatusID_.trim());
			sql.append(String.format("AND rp.status_id in (%s) ", registryStatusID));
		}

		//--------------------------------------------------------------------------------------------------------------
		// Reviewed by
		//--------------------------------------------------------------------------------------------------------------
		if (!StringUtils.isNullOrEmpty(reviewedBy_)) {
			String reviewedBy = Utilities.validateNumericString(reviewedBy_.trim());
			sql.append(String.format("AND reviewer.registry_actor_id in (%s) ", reviewedBy));
		}


		//--------------------------------------------------------------------------------------------------------------
		// TODO: ssn
		//--------------------------------------------------------------------------------------------------------------

		//--------------------------------------------------------------------------------------------------------------
		// Workflow status ID
		//--------------------------------------------------------------------------------------------------------------
		if (!StringUtils.isNullOrEmpty(workflowStatusID_)) {
			String workflowStatusID = Utilities.validateNumericString(workflowStatusID_.trim());
			sql.append(String.format("AND rp.review_status_id in (%s) ", workflowStatusID ));
		}


		sql.append("ORDER BY d.source_lastname asc, d.source_firstname asc ");

		logger.info(sql.toString());


		try { 
			ds = jdbcTemplate.getDataSource();
		} catch (Exception dse) { throw new DaoException("Can not get data source:"+ds.toString()); }
		try (
				Connection conn = ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql.toString());
				ResultSet rs = ps.executeQuery()
				)
		{

			if (rs != null) {
				while (rs.next()) {

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
					/*if (StringUtils.isNullOrEmpty(first_name)) { throw new DaoException("Invalid first name"); }
					if (StringUtils.isNullOrEmpty(last_name)) { throw new DaoException("Invalid last name"); }
					if (StringUtils.isNullOrEmpty(mrn)) { throw new DaoException("Invalid MRN"); }
*/
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
		return registryPatients;
	}


	// Search for registry patients by MRN or by name (first and/or last).
	// TODO: this will likely need to be replaced by a faster and more lightweight implementation that supports autocomplete.
	public List<GenericRegistryPatient> searchForMrnOrName(int registryID_, String searchText_) throws DaoException {

		// Validate parameters
		if (registryID_ < 1) { throw new DaoException("Invalid registry ID"); }
		if (StringUtils.isNullOrEmpty(searchText_)) { throw new DaoException("Invalid search text (empty)"); }

		// Trim whitespace from the ends of the search text.
		searchText_ = searchText_.trim();
		if (searchText_.length() < 1) { throw new DaoException("Invalid search text (only whitespace)"); }


		DataSource ds = null;

		// Create the list of patients that will be returned.
		List<GenericRegistryPatient> registryPatients = new ArrayList<GenericRegistryPatient>();


		// Create the query.
		StringBuilder sql = new StringBuilder();
		sql.append(Utilities.generateRegistryPatientPartialQuery(registryID_));

		// Constrain by either MRN or patient name.
		if (searchText_.matches("[^0-9]+")) {

			// Since non-numeric characters exist, this can't be an MRN.
			String[] nameTokens = searchText_.split("\\s");
			if (nameTokens == null || nameTokens.length < 1) { throw new DaoException("Invalid search text (empty)"); }
			if (nameTokens.length == 1) {
				sql.append("AND (regexp_like(d.source_firstname,'" + searchText_ + "','i') OR regexp_like(d.source_lastname,'" + searchText_ + "','i')) ");
			} else {
				sql.append("AND (regexp_like(d.source_firstname,'" + nameTokens[0] + "','i') OR regexp_like(d.source_lastname,'" + nameTokens[0] + "','i') ");
				sql.append("OR regexp_like(d.source_firstname,'" + nameTokens[1] + "','i') OR regexp_like(d.source_lastname,'" + nameTokens[1] + "','i')) ");
			}

		} else {
			// Since only numeric characters are in the string, assume it's an MRN.
			String mrn = Utilities.validateNumericString(searchText_);
			sql.append(String.format("AND regexp_like(rp.uab_mrn, '%s', 'i') ", mrn));
		}

		sql.append("ORDER BY d.source_lastname asc, d.source_firstname asc ");


		logger.info(sql.toString());

		try {
			ds = jdbcTemplate.getDataSource();
		} catch (Exception dse) { throw new DaoException("Dould not get data source"+ds.toString());}
		try(
				Connection conn = ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql.toString());
				ResultSet rs = ps.executeQuery()
				){

			if (rs != null) {
				while (rs.next()) {

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
					if (StringUtils.isNullOrEmpty(first_name)) { throw new DaoException("Invalid first name"); }
					if (StringUtils.isNullOrEmpty(last_name)) { throw new DaoException("Invalid last name"); }
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

		return registryPatients;
	}



	public static void main(String[] args)
	{
	}

	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = LoggerFactory.getLogger(SearchRegistryPatientDaoImpl.class);

	public void setDataSource(DataSource dataSource)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
	}






	/*
	public GenericRegistryPatientList getRegistryPatients(int registryId, 
														  String assigner, 
														  String mrn, 
														  String fullName, 
														  String beginDate, 
														  String endDate, 
														  String status,
														  String reviewStatus,
														  String hscontactBeginDate,
														  String hscontactEndDate,
														  String patientAttributes,
														  String encounterAttributes,
														  String detectionEvents
														  ) throws DaoException
	{	  
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt_mrn = null, pstmt_enc_attrs = null, pstmt_pat_attrs = null, pstmt_date = null, pstmt_detect_events = null;
		ResultSet rs_mrn = null, rs_enc_attrs = null, rs_pat_attrs = null, rs_date = null, rs_detect_events = null;		
		int numberOfGenericRegistryPatients = 0;
		GenericRegistryPatientList genericRegistryPatientList = new GenericRegistryPatientList(); 
		List <GenericRegistryPatient> registryPatientsList = new ArrayList<GenericRegistryPatient>();		
		List <RegistryEncounterAttribute> registryEncounterAttributeList = new ArrayList<RegistryEncounterAttribute>();
		List <RegistryPatientAttribute> registryPatientAttrList = new ArrayList<RegistryPatientAttribute>();
		String firstName = "", lastName = "", parsedReviewBeginDate = "", parsedReviewEndDate = "";

		try {			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			String sql = buildSQLStatement(assigner, mrn, fullName, status, beginDate, endDate, reviewStatus);			
			pstmt_mrn = conn.prepareStatement(sql);
			int param = 1;
			if (("0".equals(mrn)) && ("".equals(fullName))) {
				// no parameters, select all
			} 
			if (registryId > 0) {
				pstmt_mrn.setInt(param, registryId);
				param++;
			}
			if (!"".equals(assigner)) {
				pstmt_mrn.setString(param, assigner);
				param++;
			}
			if (!"0".equals(mrn)) {
				pstmt_mrn.setString(param, mrn);
				param++;
			} 
			if (!"".equals(fullName)) {				
				String[] names = fullName.split(" ");				
				if (names.length >= 1) {
					firstName = names[0].toUpperCase();
				}
				if (names.length >= 2) {
					lastName = names[1].toUpperCase();
				}
				pstmt_mrn.setString(param, firstName);
				param++;
				pstmt_mrn.setString(param, lastName);
				param++;
			}			
			if (!"".equals(beginDate) && !"".equals(endDate)) {
				parsedReviewBeginDate = parseDate(beginDate);
				pstmt_mrn.setString(param, parsedReviewBeginDate);
				param++;
				parsedReviewEndDate = parseDate(endDate);
				pstmt_mrn.setString(param, parsedReviewEndDate);
				param++;
			}

			logger.info("sql query :" + sql);	
			logger.info("sql parameters, registryId:" + registryId + 
						 ",assigner:" + assigner + 
						 ",mrn:" + mrn + 
						 ",fullName:" + fullName +  
						 ",status:" + status + 
						 ",parsedReviewBeginDate:" + parsedReviewBeginDate + 
						 ",parsedReviewEndDate:" + parsedReviewEndDate +
						 ",reviewstatus:" + reviewStatus +
						 ",hscontactBeginDate:" + hscontactBeginDate + 
						 ",hscontactEndDate:" + hscontactEndDate +
						 ",patientAttributes:" + patientAttributes +						 
						 ",encounterAttributes:" + encounterAttributes +
						 ",detectionEvents:" + detectionEvents
						 );
			rs_mrn = pstmt_mrn.executeQuery();			
			while(rs_mrn.next()) {
				numberOfGenericRegistryPatients++;
				String uabMrn = rs_mrn.getString("uab_mrn");
				String formattedUabMrn = rs_mrn.getString("formatted_uab_mrn"); 

				// get the health system last contact date
				if (!"".equals(hscontactEndDate) && !"".equals(hscontactEndDate)) {
					pstmt_date = conn.prepareStatement(Constants.select_MaxUpdateDate_SQL);
					pstmt_date.setString(1, formattedUabMrn);
					logger.debug("max update date sql query " + numberOfGenericRegistryPatients + " :" + Constants.select_MaxUpdateDate_SQL);
					logger.debug("max update date sql query params " + + numberOfGenericRegistryPatients +  " :" + formattedUabMrn);
					rs_date = pstmt_date.executeQuery();
					String hsLastContactDate_DB = "", hsLastContactDate_desired = "";
					SimpleDateFormat sdf_db = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
					SimpleDateFormat sdf_desired = new SimpleDateFormat("dd-MMM-yy");					
					while(rs_date.next()) {
						hsLastContactDate_DB = rs_date.getString("last_contact_date");		
						if ((hsLastContactDate_DB != null) && (!"".equals(hsLastContactDate_DB))) {							
							Date dateDB = sdf_db.parse(hsLastContactDate_DB);
							hsLastContactDate_desired = sdf_desired.format(dateDB);
						}
					}
					if (rs_date != null) {
						rs_date.close();
						rs_date = null;
					}
					if (pstmt_date != null) {						
						pstmt_date.close();
						pstmt_date = null;
					}
					if ((hsLastContactDate_desired == null) ||
						("".equals(hsLastContactDate_desired)) ||
						!compareHealthSystemDate(hscontactBeginDate, hscontactEndDate, hsLastContactDate_desired)) {
						logger.info("Ignoring Record : Health Sys contact date (" + hsLastContactDate_desired + ") is NOT BETWEEN " + hscontactBeginDate + " AND " + hscontactEndDate);
						continue;
					}					
				}

				// get the detection event for the registry patient id
				int detectionEventId = 0;
				String detectionEventName = null;				
				pstmt_detect_events = conn.prepareStatement(Constants.select_DetectionEvent_SQL);
				pstmt_detect_events.setInt(1, rs_mrn.getInt("registry_patient_id"));
				logger.debug("detect event sql query " + numberOfGenericRegistryPatients + " :" + Constants.select_DetectionEvent_SQL);
				logger.debug("detect event sql query params " + + numberOfGenericRegistryPatients +  " :" + rs_mrn.getInt("registry_patient_id"));
				rs_detect_events = pstmt_detect_events.executeQuery();
				while(rs_detect_events.next()) {
					detectionEventId = rs_detect_events.getInt("evidence_code");
					detectionEventName = rs_detect_events.getString("name");
				}
				if (rs_detect_events != null) {
					rs_detect_events.close();
					rs_detect_events = null;
				}
				if (pstmt_detect_events != null) {
					pstmt_detect_events.close();					
					pstmt_detect_events = null;
				}

				// search for given detection events
				boolean detectionEventFound = false;
				if (!"".equals(detectionEvents)) {
					logger.info("mrn: " + rs_mrn.getString("uab_mrn") + ", Looking for detection events:" + detectionEvents);
					List<String> detectionEventsList = getDetectionEvents(detectionEvents);
					for (int j=0; j<detectionEventsList.size(); j++) {
						String detectionEvent = detectionEventsList.get(j);
						if (detectionEvent.equals(detectionEventName)) {
							detectionEventFound = true;
							logger.info("Found detection event:" + detectionEvent);
							break;
						}
					}
					// if detection event NOT found, do not include patient in search list
					if (!detectionEventFound) {
						logger.info("Ignoring Record : Did not find detection events:" + detectionEvents + " for the patient id!" );
						continue;
					}
				}				

				pstmt_enc_attrs = conn.prepareStatement(Constants.selectEncounterAttrs_SQL);
				pstmt_enc_attrs.setString(1, uabMrn);
				logger.debug("enc attrs sql query " + numberOfGenericRegistryPatients + " :" + Constants.selectEncounterAttrs_SQL);
				logger.debug("enc attrs sql query params " + + numberOfGenericRegistryPatients +  " :" + mrn);
				rs_enc_attrs = pstmt_enc_attrs.executeQuery();				
				while(rs_enc_attrs.next()) { 
					RegistryEncounterAttribute rec = new RegistryEncounterAttribute();
					rec.setMrn(mrn);
					rec.setCvterm_name(rs_enc_attrs.getString("cvterm_name"));
					rec.setEvent_cvterm_id(rs_enc_attrs.getInt("event_cvterm_id"));
					rec.setEncounter_key(rs_enc_attrs.getInt("encounter_key"));
					rec.setAssigner_id(rs_enc_attrs.getInt("assigner_id"));
					rec.setRegistry_id(rs_enc_attrs.getInt("registry_id"));
					rec.setCvterm_id(rs_enc_attrs.getInt("cvterm_id"));
					rec.setValue(rs_enc_attrs.getInt("value"));
					rec.setRegistrar_comment(rs_enc_attrs.getString("registrar_comment"));
					rec.setAssignment_date(rs_enc_attrs.getDate("assignment_date"));
					rec.setCode_id(rs_enc_attrs.getInt("code_id"));
					rec.setActor_name(rs_enc_attrs.getString("actor_name"));	

					registryEncounterAttributeList.add(rec);
				}
				if (rs_enc_attrs != null) {
					rs_enc_attrs.close();
					rs_enc_attrs = null;
				}
				if (pstmt_enc_attrs != null) {
					pstmt_enc_attrs.close();
					pstmt_enc_attrs = null;
				}				

				pstmt_pat_attrs = conn.prepareStatement(Constants.select_RegistryPatientAtributes_SQL);
				pstmt_pat_attrs.setString(1, uabMrn);
				logger.debug("pat attrs sql query " + numberOfGenericRegistryPatients + " :" + Constants.select_RegistryPatientAtributes_SQL);
				logger.debug("pat attrs sql query params " + + numberOfGenericRegistryPatients +  " :" + mrn);
				rs_pat_attrs = pstmt_pat_attrs.executeQuery();
				while(rs_pat_attrs.next()) {
					RegistryPatientAttribute rpa = new RegistryPatientAttribute();
					rpa.setName(rs_pat_attrs.getString("name"));
					rpa.setMrn(uabMrn);					
					rpa.setStatus_assignment_date(rs_pat_attrs.getString("status_assignment_date"));
					rpa.setAnnotator_id(rs_pat_attrs.getInt("annotator_id"));
					rpa.setAnnotation_date(rs_pat_attrs.getString("annotation_date"));
					rpa.setAnnotator_comment(rs_pat_attrs.getString("annotator_comment"));
					rpa.setIs_valid(rs_pat_attrs.getString("is_valid"));
					rpa.setStart_assignment_date(rs_pat_attrs.getString("start_assignment_date"));
					rpa.setEnd_assignment_date(rs_pat_attrs.getString("end_assignment_date"));

					registryPatientAttrList.add(rpa);
				}
				if (rs_pat_attrs != null) {
					rs_pat_attrs.close();
					rs_pat_attrs = null;
				}
				if (pstmt_pat_attrs != null) {
					pstmt_pat_attrs.close();					
					pstmt_pat_attrs = null;
				}

				// search for given patient attribute
				boolean patientAttributeFound = false;
				if (!"".equals(patientAttributes)) {	
					logger.info("mrn: " + rs_mrn.getString("uab_mrn") + ", Looking for patient attributes:" + patientAttributes);
					List<String> patientAttributeList = getPatientAttributes(patientAttributes);
					for (int j=0; ((!patientAttributeFound) && (j<patientAttributeList.size())); j++) {
						for (int i=0; i<registryPatientAttrList.size(); i++) {
							String patientAttribute = patientAttributeList.get(j);
							if (patientAttribute.equals(registryPatientAttrList.get(i).getName())) {
								patientAttributeFound = true;
								logger.info("Found patient attribute:" + patientAttribute);
								break;
							}						  
						}
					}
					// if patient attribute NOT found, do not include patient in search list
					if (!patientAttributeFound) {
						logger.info("Ignoring Record : Did not find patient attributes:" + patientAttributes + " in list of patient attributes!" );
						continue;
					}
				}

				// search for given encounter attribute
				boolean encounterAttributeFound = false;
				if (!"".equals(encounterAttributes)) {					
					logger.info("mrn: " + rs_mrn.getString("uab_mrn") + ", Looking for encounter attributes:" + encounterAttributes);
					List<String> encounterAttributeList = getEncounterAttributes(encounterAttributes);
					for (int j=0; ((!encounterAttributeFound) && (j<encounterAttributeList.size())); j++) {						
						for (int i=0; i<registryEncounterAttributeList.size(); i++) {
							String encounterAttribute = encounterAttributeList.get(j);
							if (encounterAttributes.equals(registryEncounterAttributeList.get(i).getCvterm_name())) {
								encounterAttributeFound = true;
								logger.info("Found encounter attribute:" + encounterAttribute);
								break;
							}						
						}
					}
					// if encounter attribute NOT found, do not include patient in search list
					if (!encounterAttributeFound) {
						logger.info("Ignoring Record : Did not find encounter attributes:" + encounterAttributes + " in list of encounter attributes!" );
						continue;
					}
				}

				GenericRegistryPatient rp = new GenericRegistryPatient();				
				rp.setMrn(rs_mrn.getString("uab_mrn"));
				rp.setRegistry_patient_id(rs_mrn.getInt("registry_patient_id"));
				rp.setRegistry_status_id(rs_mrn.getInt("status_id"));
				rp.setRegistry_status(rs_mrn.getString("name"));
				rp.setFull_name(rs_mrn.getString("source_firstname") + " " + rs_mrn.getString("source_lastname"));				
				rp.setLast_review_date(rs_mrn.getString("last_review_date"));
				rp.setAssigned_by_name(rs_mrn.getString("assigner_id"));
				rp.setDetectionEventId(detectionEventId);
				rp.setDetectionEventName(detectionEventName);
				//System.out.println("MRN:" + rp.getMrn() + ", Name:" + rp.getName() + ", status:" + rp.getCrec_state());

				registryPatientsList.add(rp);
	        }
			genericRegistryPatientList.setRegistry_patient_list(registryPatientsList);
			if ((registryPatientsList != null) && (registryPatientsList.size() > 0)) {
				genericRegistryPatientList.setRegistry_all_encounter_attributes_list(registryEncounterAttributeList);
				genericRegistryPatientList.setRegistry_all_patient_attributes_list(registryPatientAttrList);
			}			
		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			if (rs_mrn != null) {
				try {
					rs_mrn.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
				rs_mrn = null;
			}
			if (pstmt_mrn != null) {
				try {
					pstmt_mrn.close();
				} catch(SQLException sqe) {
					sqe.printStackTrace();
				}
				pstmt_mrn = null;
			} 
			if (rs_enc_attrs != null) {
				try {
					rs_enc_attrs.close();					
				} catch (Exception e) {					
					e.printStackTrace();
				}
				rs_enc_attrs = null;
			}
			if (pstmt_enc_attrs != null) {
				try {
					pstmt_enc_attrs.close();
				} catch(SQLException sqe) {
					sqe.printStackTrace();
				}
				pstmt_enc_attrs = null;
			}
			if (rs_pat_attrs != null) {
				try {
					rs_pat_attrs.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
				rs_pat_attrs = null;
			}
			if (pstmt_pat_attrs != null) {
				try {
					pstmt_pat_attrs.close();
				} catch(SQLException sqe) {
					sqe.printStackTrace();
				}
				pstmt_pat_attrs = null;
			}
			if (rs_date != null) {
				try {
					rs_date.close();
				} catch (Exception e) {					
					e.printStackTrace();
				}
				rs_date = null;
			}
			if (pstmt_date != null) {
				try {
					pstmt_date.close();
				} catch(SQLException sqe) {
					sqe.printStackTrace();
				}
				pstmt_date = null;
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
		logger.debug("Number of Registry Patients :" + registryPatientsList.size());
		logger.info("Log.ElapsedTime.dao.getRegistryPatients=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return genericRegistryPatientList;
	}

	private String buildSQLStatement(String assigner, String mrn, String name, String statuses, String beginDate, String endDate, String reviewStatus) 
	{		
		String sql = selectRegistryPatients_SQL;

		if ("".equals(assigner)) {
			sql = sql + "ra.login IS NULL ";
		} else {
			sql = sql + "ra.login = ? ";
		}		
		if ("".equals(statuses)) {
			sql = sql + "AND (rp.status_id IN (SELECT cvterm_id from cvterm where name IN ('Candidate', 'Accepted'))) "; 				
		} else {
			sql = sql + addStatusQuery(statuses);
		}		
		if (!"0".equals(mrn)) {
			sql = sql + "AND rp.uab_mrn = ? " ;			
		} 
		if (!"".equals(name)) {
			sql = sql + "AND rpd.source_firstname = ?  AND rpd.source_lastname = ? ";
		}		
		if (!"".equals(beginDate) && !"".equals(endDate)) {
			sql =  sql + "AND rp.last_review_date >= ? AND rp.last_review_date <= ? " ;
		}
		if ("".equals(reviewStatus)) {
			//sql = sql + "AND (rp.review_status_id IN (SELECT cvterm_id from cvterm where name IN ('Reviewed'))) "; 				
		} else {
			sql = sql + addReviewStatusQuery(reviewStatus);
		}
		return sql;
	}

	private static String parseDate(String dateStr)
	{
		String parsedDate = "";
		if (dateStr.contains(",")) {
			String[] dateArray = dateStr.split(",");
			parsedDate = dateArray[0];
		} else {
			parsedDate = dateStr;
		}		
		return parsedDate;
	}

	public static boolean compareHealthSystemDate(String minDateStr, String maxDateStr, String theDateStr) 
	throws ParseException
	{	
		Date minDate = null;
		Date maxDate = null;
		Date theDate = null;
		boolean status = false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
		System.out.println("minDateStr="+minDateStr+",maxDateStr="+maxDateStr+",theDateStr="+theDateStr);
		minDate = sdf.parse(minDateStr);
		maxDate = sdf.parse(maxDateStr);
		theDate = sdf.parse(theDateStr);
		status = ((theDate.compareTo(minDate) >= 0) && (theDate.compareTo(maxDate) <= 0)); 
	    return status;
	}

	private static String formatDate(Date date)
	{
		String formattedDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		formattedDate = sdf.format(date);
		return formattedDate;
	}

	private String addStatusQuery(String statuses)
	{
		String sQ1 = "AND (rp.status_id IN (SELECT cvterm_id from cvterm where name IN (";
		String sQ2 = "";
		String sQ3 = "))) ";
		String reviewStatusQuery = "";
		String[] statusArray = statuses.split(",");
		for (int i=0; i<statusArray.length; i++) {
			String status = statusArray[i];			
			if (CANDIDATE.equals(status)) {
				if (i==(statusArray.length-1)) {
					sQ2 = sQ2 + "'" + CANDIDATE + "'";
				} else {
					sQ2 = sQ2 + "'" + CANDIDATE + "',";
				}
			} else if (PROVISIONAL.equals(status)) {
				if (i==(statusArray.length-1)) {
					sQ2 = sQ2 + "'" + PROVISIONAL + "'";
				} else {
					sQ2 = sQ2 + "'" + PROVISIONAL + "',";
				}
			} else if (REJECTED.equals(status)) {
				if (i==(statusArray.length-1)) {
					sQ2 = sQ2 + "'" + REJECTED + "'";
				} else {
					sQ2 = sQ2 + "'" + REJECTED + "',";
				}
			} else if (ACCEPTED.equals(status)) {
				if (i==(statusArray.length-1)) {
					sQ2 = sQ2 + "'" + ACCEPTED + "'";
				} else {
					sQ2 = sQ2 + "'" + ACCEPTED + "',";
				}
			} 			
		}
		reviewStatusQuery = sQ1 + sQ2 + sQ3;
		return reviewStatusQuery;
	}

	private String addReviewStatusQuery(String statuses)
	{
		String sQ1 = "AND (rp.review_status_id IN (SELECT cvterm_id from cvterm where name IN (";
		String sQ2 = "";
		String sQ3 = "))) ";
		String reviewStatusQuery = "";
		String[] statusArray = statuses.split(",");
		for (int i=0; i<statusArray.length; i++) {
			String status = statusArray[i];			
			if (REVIEWED.equals(status)) {
				if (i==(statusArray.length-1)) {
					sQ2 = sQ2 + "'" + REVIEWED + "'";
				} else {
					sQ2 = sQ2 + "'" + REVIEWED + "',";
				}
			} else if (NEEDS_REVIEW.equals(status)) {
				if (i==(statusArray.length-1)) {
					sQ2 = sQ2 + "'" + NEEDS_REVIEW + "'";
				} else {
					sQ2 = sQ2 + "'" + NEEDS_REVIEW + "',";
				}
			} else if (PATIENT_NEVER_REVIEWED.equals(status)) {
				if (i==(statusArray.length-1)) {
					sQ2 = sQ2 + "'" + PATIENT_NEVER_REVIEWED + "'";
				} else {
					sQ2 = sQ2 + "'" + PATIENT_NEVER_REVIEWED + "',";
				}
			} else if (UNDER_REVIEW.equals(status)) {
				if (i==(statusArray.length-1)) {
					sQ2 = sQ2 + "'" + UNDER_REVIEW + "'";
				} else {
					sQ2 = sQ2 + "'" + UNDER_REVIEW + "',";
				}
			} 			
		}
		reviewStatusQuery = sQ1 + sQ2 + sQ3;
		return reviewStatusQuery;
	}

	private List<String> getPatientAttributes(String patientAttributes)
	{
		List<String> patientAttributeList = new ArrayList<String>();
		String[] patientAttributesArray = patientAttributes.split(",");
		patientAttributeList = Arrays.asList(patientAttributesArray);
		return patientAttributeList;
	}

	private List<String> getEncounterAttributes(String encounterAttributes)
	{
		List<String> encounterAttributeList = new ArrayList<String>();
		String[] encounterAttributesArray = encounterAttributes.split(",");
		encounterAttributeList = Arrays.asList(encounterAttributesArray);
		return encounterAttributeList;
	}

	private List<String> getDetectionEvents(String detectionEvents)
	{
		List<String> detectionEventsList = new ArrayList<String>();
		String[] detectionEventsArray = detectionEvents.split(",");
		detectionEventsList = Arrays.asList(detectionEventsArray);
		return detectionEventsList;
	}


	private String selectRegistryPatients_SQL = 
			"SELECT DISTINCT " + 
				"rp.uab_mrn, rp.formatted_uab_mrn, rp.registry_patient_id, rp.status_id, rp.last_review_date, rp.assigner_id, rp.review_status_id, " + 
				"rpd.source_firstname, rpd.source_lastname, " + 
				"cv.name, " +
				"re.update_date " + 
				"FROM registry_patient rp " +
					"LEFT JOIN registry_encounter re ON (rp.FORMATTED_UAB_MRN = re.formatted_medical_record_nbr) " +
					"LEFT JOIN registry_patient_demographics rpd ON (rp.uab_mrn = rpd.source_mrn) " +
					"LEFT JOIN cvterm cv ON (rp.status_id = cv.cvterm_id) " + 
					"LEFT JOIN registry_actor ra ON (rp.owner_id = ra.registry_actor_id) " + 					
				"WHERE " + 
					"rp.person_sk IS NOT NULL AND " + 
					"rp.registry_id = ? AND "																 	
			;		

	private static final String CANDIDATE   			= "Candidate";
	private static final String PROVISIONAL 			= "Provisional";
	private static final String REJECTED 				= "Rejected";
	private static final String ACCEPTED 				= "Accepted";

	private static final String REVIEWED				= "Reviewed";
	private static final String NEEDS_REVIEW			= "Needs Review";
	private static final String PATIENT_NEVER_REVIEWED	= "Patient Never Reviewed";
	private static final String UNDER_REVIEW			= "Under Review";
	 */


}
