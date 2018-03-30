package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;

import edu.uab.registry.util.Utilities;
import org.springframework.jdbc.core.JdbcTemplate;

import org.h2.util.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uab.registry.controller.RegistryPatientCvtermController;
import edu.uab.registry.dao.RegistryPatientStatusDao;
import edu.uab.registry.domain.RegistryPatientStatus;
import edu.uab.registry.exception.DaoException;

import edu.uab.registry.orm.RegistryPatient;
import edu.uab.registry.orm.RegistryPatientHistory;
import edu.uab.registry.orm.dao.RegistryPatientDao;
import edu.uab.registry.orm.dao.impl.RegistryPatientDaoImpl;
import edu.uab.registry.util.Constants.RegistryStatusType;

//private static final Logger logger = LoggerFactory.getLogger(RegistryPatientStatusDaoImpl.class);

public class RegistryPatientStatusDaoImpl implements RegistryPatientStatusDao {

	private JdbcTemplate jdbcTemplate;	
	
	
	// Create a new REGISTRY_PATIENT_HISTORY record for the updated registry/workflow status.
	private void createNewHistory(RegistryPatientHistory history_) throws DaoException
	{
		if (history_ == null) { throw new DaoException("Invalid history"); }
		
		DataSource ds;
		Connection conn = null;
		PreparedStatement ps = null;
		
		// Generate the current timestamp for use in the query.
		java.util.Date now = new java.util.Date();
		java.sql.Date sqlNow = new java.sql.Date(now.getTime());
		
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();	
	
			// Generate the query
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO REGISTRY_PATIENT_HISTORY (");
			sql.append("REGISTRY_PATIENT_ID, ");
			sql.append("REGISTRY_ID, ");
			sql.append("PREVIOUS_STATUS_ID, ");
			sql.append("CURRENT_STATUS_ID, ");
			sql.append("CHANGE_DATE, ");
			sql.append("CHANGER_ID, ");
			sql.append("REG_STATUS_CHANGE_COMMENT, ");
			sql.append("PREV_REVIEW_STATUS_ID, ");
			sql.append("CUR_REVIEW_STATUS_ID, ");
			sql.append("WORKFLOW_STATUS_CHANGE_COMMENT ");
			sql.append(") VALUES (");
			sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
			
			// Create the prepared statement and add the parameter.
		    ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, history_.getRegistryPatientId());
			ps.setInt(2, history_.getRegistryId());
			ps.setInt(3, history_.getPreviousStatusId());
			ps.setInt(4, history_.getCurrentStatusId());
			ps.setDate(5, sqlNow);
			ps.setInt(6, history_.getChangerId());
			ps.setString(7, history_.getRegStatusChangeComment());
			ps.setInt(8, history_.getPrevReviewStatusId());
			ps.setInt(9, history_.getCurReviewStatusId());
			ps.setString(10, history_.getWorkflowStatusChangeComment());
			
			System.out.println("*******************************************************************************");
			System.out.println("*******************************************************************************");
			System.out.println("*******************************************************************************");
			System.out.println("Registry Status : PREVIOUS_STATUS_ID: "+history_.getPreviousStatusId());
			System.out.println("Registry Status : CURRENT_STATUS_ID: "+history_.getCurrentStatusId());
			System.out.println("WorkFlow Status : PREV_REVIEW_STATUS_ID: "+history_.getPrevReviewStatusId());
			System.out.println("WorkFlow Status : CUR_REVIEW_STATUS_ID: "+history_.getCurReviewStatusId());
			System.out.println("*******************************************************************************");
			System.out.println("*******************************************************************************");
			System.out.println("*******************************************************************************");
			System.out.println("*******************************************************************************");

			ps.executeQuery();
		}
		catch (Throwable th_) {
			throw new DaoException(th_.getMessage(), th_);
		}
		finally {
			Utilities.closeDbResources(conn, ps, null);
		}
		//private static final Logger logger = LoggerFactory.getLogger(RegistryPatientStatusDaoImpl.class);
	}


	// Get the most recent REGISTRY_PATIENT_HISTORY for the specified registry patient ID.
	private RegistryPatientHistory getMostRecentHistory(Integer registryPatientID_) throws DaoException
	{
		
		DataSource ds;
		Connection conn = null;
		RegistryPatientHistory mostRecent = new RegistryPatientHistory();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();	
			
			// Generate the query. Note that the strange syntax (rownum = 1) is intended to get the top 1 record
			// from the nested query.
			StringBuilder sql = new StringBuilder();
			sql.append("select * from (");
			sql.append("  select "); 
			sql.append("  current_status_id, "); 
			sql.append("  previous_status_id, "); 
			sql.append("  reg_status_change_comment as status_comment, "); 
			sql.append("  cur_review_status_id as current_review_status_id, "); 
			sql.append("  prev_review_status_id as previous_review_status_id, "); 
			sql.append("  workflow_status_change_comment as review_comment "); 
			sql.append("  from registry_patient_history ");  
			sql.append("  where registry_patient_id = ? ");
			sql.append("  order by change_date desc, registry_patient_history_id desc "); 
			sql.append(") where rownum = 1 "); 

			// Create the prepared statement and add the parameter.
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, registryPatientID_);
			
			// Execute the query and validate the results.
			rs = ps.executeQuery();
			if (rs == null || !rs.next()) { 

				// Create a "fake" history item since there are no real history records for this registry patient.
				// TODO: are there actual valid defaults we should use, instead?
				mostRecent.setCurrentStatusId(0);
				mostRecent.setPreviousStatusId(0);
				mostRecent.setRegStatusChangeComment("");
				
				mostRecent.setCurReviewStatusId(0);
				mostRecent.setPrevReviewStatusId(0);
				mostRecent.setWorkflowStatusChangeComment("");
				
			} else {
				mostRecent.setCurrentStatusId(rs.getInt("current_status_id"));
				mostRecent.setPreviousStatusId(rs.getInt("previous_status_id"));
				mostRecent.setRegStatusChangeComment(rs.getString("status_comment"));
				
				mostRecent.setCurReviewStatusId(rs.getInt("current_review_status_id"));
				mostRecent.setPrevReviewStatusId(rs.getInt("previous_review_status_id"));
				mostRecent.setWorkflowStatusChangeComment(rs.getString("review_comment"));
			}
		}
		catch (Throwable th_) {
			throw new DaoException(th_.getMessage(), th_);
		}
		finally {
			Utilities.closeDbResources(conn, ps, rs);
		}	
		
		return mostRecent;
	}
	
	
	// Get a patient's registry or workflow status history.
	public List<RegistryPatientStatus> getStatusHistory(RegistryPatient registryPatient_,
														RegistryStatusType statusType_) throws DaoException
	{
		// Validate input parameters.
		if (registryPatient_ == null) { throw new DaoException("Invalid registry patient"); }
		
		DataSource ds;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		// Initialize the collection to be returned.
		ArrayList<RegistryPatientStatus> statuses = new ArrayList<RegistryPatientStatus>();
				
		try {			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();	
			
			// Generate the query
			StringBuilder sql = new StringBuilder();
			sql.append("select ");
			sql.append("changer_id as annotator_id, ");
			sql.append("registry_actor.login as annotator_name, ");
			sql.append("change_date, ");
			
			if (statusType_ == RegistryStatusType.registry) {
				sql.append("reg_status_change_comment as annotator_comment, ");
				sql.append("current_status_id as status_id, ");
				sql.append("regstatus.name as status_text, ");
				//START: Adding SQL Statements
				sql.append("registry_patient_history_id as status_history_id ");
				sql.append("from registry_patient_history ");
				sql.append("left join cvterm regstatus on regstatus.cvterm_id = current_status_id ");
				//sql.append("left join cvterm wflowstatus on wflowstatus.cvterm_id = cur_review_status_id ");
				sql.append("join registry_actor on registry_actor_id = changer_id ");
				sql.append("where registry_patient_id = ? ");
				sql.append("order by change_date desc, registry_patient_history_id desc ");  
				
				
				
				//END : Adding SQL Statements
				
			} else if (statusType_ == RegistryStatusType.workflow) {
				sql.append("workflow_status_change_comment as annotator_comment, "); 
				sql.append("cur_review_status_id as status_id, ");
				sql.append("wflowstatus.name as status_text, "); 
				//START: Adding SQL Statements
				sql.append("registry_patient_history_id as status_history_id ");
				sql.append("from registry_patient_history ");
				//sql.append("left join cvterm regstatus on regstatus.cvterm_id = current_status_id ");
				sql.append("left join cvterm wflowstatus on wflowstatus.cvterm_id = cur_review_status_id ");
				sql.append("join registry_actor on registry_actor_id = changer_id ");
				sql.append("where registry_patient_id = ? ");
				sql.append("order by change_date desc, registry_patient_history_id desc ");  
				
				
				
				//END : Adding SQL Statements
				
			} else {
				throw new DaoException("Invalid registry status type");
			}
			
			/*sql.append("registry_patient_history_id as status_history_id ");
			
			sql.append("from registry_patient_history ");
			sql.append("left join cvterm regstatus on regstatus.cvterm_id = current_status_id ");
			sql.append("left join cvterm wflowstatus on wflowstatus.cvterm_id = cur_review_status_id ");
			sql.append("join registry_actor on registry_actor_id = changer_id ");
			sql.append("where registry_patient_id = ? ");
			sql.append("order by change_date desc, registry_patient_history_id desc ");*/  

			// Create the prepared statement and add the parameter.
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, registryPatient_.getRegistryPatientId());
			
			// Execute the query and validate the results.
			rs = ps.executeQuery();
			if (rs != null) {

				String previousComment = null;
				int previousStatusID = -1;
				
				while (rs.next()) { 

					// We are interested in changes to status and/or comment.
					int status_id = rs.getInt("status_id");
					String annotator_comment = rs.getString("annotator_comment");

					// If the status and comment are the same as the previous version, skip ahead.
					if (status_id == previousStatusID && annotator_comment == previousComment) { continue; }

					int annotator_id = rs.getInt("annotator_id");
					String annotator_name = rs.getString("annotator_name");
					Date change_date = rs.getDate("change_date");
					int status_history_id = rs.getInt("status_history_id");
					String status_text = rs.getString("status_text");
					
					// Populate the status object.
					RegistryPatientStatus status = new RegistryPatientStatus();
					status.setAnnotatorComment(annotator_comment);
					status.setAnnotatorID(annotator_id);
					status.setAnnotatorName(annotator_name);
					status.setChangeDate(change_date);
					status.setStatusHistoryID(status_history_id);
					status.setStatusID(status_id);
					status.setStatusText(status_text);
					status.setStatusType(statusType_);
					
					// Update the result collection.
					statuses.add(status);
					
					// This status and comment now becomes the "previous" version for future comparisons.
					previousComment = annotator_comment;
					previousStatusID = status_id;
				}
			}
		}
		catch (Throwable th_) {
			th_.printStackTrace();	
		}
		finally {
			Utilities.closeDbResources(conn, ps, rs);
		}	
		
		return statuses;
	}
	
	
	public void setDataSource(DataSource dataSource) {		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	
	// Update the registry or workflow status (including comment(s)) for the registry patient provided.
	public void updateStatus(ClassPathXmlApplicationContext context_,
							 Integer registrarID_,
							 RegistryPatient registryPatient_,
							 String registryStatusComment_,
							 Integer registryStatusID_,
							 String workflowStatusComment_,
							 Integer workflowStatusID_) throws DaoException
	{
		
		// Validate input parameters.
		if (context_ == null) { throw new DaoException("Invalid context"); }
		if (registryPatient_ == null) { throw new DaoException("Invalid registry patient"); }
		// TODO: validate registrar ID as belonging to the correct registry?
		
		// Get the most-recent history record for the registry patient provided.
		RegistryPatientHistory mostRecentHistory = getMostRecentHistory(registryPatient_.getRegistryPatientId());
		if (mostRecentHistory == null) { throw new DaoException("Invalid most-recent history"); }
		
		
		// Replace null comments with an empty string, otherwise trim the comment.
		String registryStatusComment = (StringUtils.isNullOrEmpty(registryStatusComment_) ? "" : registryStatusComment_.trim());
		String workflowStatusComment = (StringUtils.isNullOrEmpty(workflowStatusComment_) ? "" : workflowStatusComment_.trim());
		
		// The current date.
		java.util.Date now = new java.util.Date();
		
		// Create a new registry patient history and begin populating it.
		RegistryPatientHistory newHistory = new RegistryPatientHistory();
		newHistory.setChangeDate(now);
		newHistory.setChangerId(registrarID_);
		newHistory.setRegistryId(registryPatient_.getRegistryId());
		newHistory.setRegistryPatientId(registryPatient_.getRegistryPatientId());
					
		// Was the registry status updated?
		if (registryStatusID_ > 0) {
			
			// Use the current status provided and set the previous "current status" as the new previous.
			newHistory.setCurrentStatusId(registryStatusID_);
			newHistory.setPreviousStatusId(mostRecentHistory.getCurrentStatusId());
			
			// Set the comment provided (possibly an empty string).
			newHistory.setRegStatusChangeComment(registryStatusComment);
			
			// Update the registry patient.
			registryPatient_.setAssignerId(registrarID_);
			registryPatient_.setStatusId(registryStatusID_);
			registryPatient_.setStatusAssignmentDate(now);
			registryPatient_.setRegistrarComment(registryStatusComment);
			
		} else {
			// Use the current and previous from the most recent history record.
			newHistory.setCurrentStatusId(mostRecentHistory.getCurrentStatusId());
			newHistory.setPreviousStatusId(mostRecentHistory.getPreviousStatusId());
			newHistory.setRegStatusChangeComment(mostRecentHistory.getRegStatusChangeComment());
		}
		
		// Was the workflow status updated?
		if (workflowStatusID_ > 0) {
			
			// Use the current workflow status provided and set the previous "current workflow status" as the new previous.
			newHistory.setCurReviewStatusId(workflowStatusID_);
			newHistory.setPrevReviewStatusId(mostRecentHistory.getCurReviewStatusId());
			
			// Set the comment provided (possibly an empty string).
			newHistory.setWorkflowStatusChangeComment(workflowStatusComment);
			
			// Update the registry patient.
			registryPatient_.setLastReviewDate(now);
			registryPatient_.setReviewerId(registrarID_);
			registryPatient_.setReviewStatusId(workflowStatusID_);
			registryPatient_.setWorkflowComment(workflowStatusComment_);
						
		} else {
			// Use the current and previous from the most recent history record.
			newHistory.setCurReviewStatusId(mostRecentHistory.getCurReviewStatusId());
			newHistory.setPrevReviewStatusId(mostRecentHistory.getPrevReviewStatusId());
			newHistory.setWorkflowStatusChangeComment(mostRecentHistory.getWorkflowStatusChangeComment());
		}
		
		
		try {
			// Create the new history record.
			createNewHistory(newHistory);
			
			// Save the updated registry patient.
			RegistryPatientDao regPatientDao = context_.getBean("registryPatientDao", RegistryPatientDaoImpl.class);
			if (regPatientDao == null) { throw new DaoException("Invalid RegistryPatientDao"); }
			regPatientDao.insertOrUpdate(registryPatient_);
		}
		catch (Throwable th_) {
			th_.printStackTrace();	
		}
	}
}
