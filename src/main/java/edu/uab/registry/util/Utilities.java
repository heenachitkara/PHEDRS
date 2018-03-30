package edu.uab.registry.util;


import edu.uab.registry.exception.DaoException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.h2.util.StringUtils;
import org.slf4j.Logger;


public class Utilities {


	//FIXME Use try-with-resources not closeDbResources
    // Close the result set, prepared statement, and connection often used by a db query.
    public static void closeDbResources(Connection conn_, PreparedStatement ps_, ResultSet rs_) {
        if (rs_ != null) { try { rs_.close(); } catch (SQLException e) { e.printStackTrace(); }}
        if (ps_ != null) { try { ps_.close(); } catch(SQLException sqe) { sqe.printStackTrace(); }}
        if (conn_ != null) { try { conn_.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }}
    }


    // A query to return patient attributes.
    public static String generatePatientAttributesQuery(String mrn_, Integer registryID_) throws DaoException {

        // Validate the input parameters.
        String mrn = validateNumericString(mrn_);
        if (registryID_ < 1) { throw new DaoException("Invalid registry ID parameter"); }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("cv.name AS cv_name, ");
        sql.append("rp.status_assignment_date, ");
        sql.append("rpc.registry_patient_cvterm_id, ");
        sql.append("rpc.registry_patient_id, ");
        sql.append("rpc.registry_id, ");
        sql.append("rpc.annotator_id, ");
        sql.append("rpc.annotation_date, ");
        sql.append("rpc.annotator_comment, ");
        sql.append("rpc.is_valid, ");
        sql.append("rpc.start_assignment_date, ");
        sql.append("rpc.end_assignment_date, ");
        sql.append("ra.login as annotator_name ");

        sql.append("FROM registry_patient rp ");
        sql.append("JOIN registry_patient_cvterm rpc ON (rp.registry_patient_id = rpc.registry_patient_id) ");
        sql.append("JOIN cvterm cvt ON (rpc.cvterm_id = cvt.cvterm_id) ");
        sql.append("JOIN registry_actor ra ON (rpc.annotator_id = ra.registry_actor_id) ");
        sql.append("WHERE rpc.is_valid = 'Y' ");
        sql.append(String.format("AND rpc.registry_id = %d ", registryID_));
        sql.append(String.format("AND rp.uab_mrn =  '%s' ", mrn));

        return sql.toString();
    }

    /*------------------------------------------------------------------------------------------------------------------
    This is a partial SQL query used to retrieve registry patients. Since this sub-query is used in several places,
    we're maintaining it here as a central location.

    The SELECT returns the following values:

    // Demographics and registry patient info
    first_name
    last_contact_date
    last_name
    mrn
    registry_patient_id

    // Detection event(s)
    detection_event_abbr
    detection_event_id
    detection_event_name

    // Registry status
    status_id
    status_name
    status_assigner
    status_assignment_date
    status_comment

    // Workflow/review status
    review_status_id
    review_status_name
    reviewed_by
    last_review_date
    review_comment

    ------------------------------------------------------------------------------------------------------------------*/
    public static String generateRegistryPatientPartialQuery(Integer registryID_) {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");

        // Demographics and registry patient info
        sql.append("d.source_firstname AS first_name, ");
        sql.append("d.source_lastname AS last_name, ");
        sql.append("rp.uab_mrn AS mrn, ");
        sql.append("rp.registry_patient_id, ");
        sql.append("( ");
        sql.append("	SELECT MAX(update_date) AS last_contact_date ");
        sql.append("	FROM registry_encounter ");
        sql.append("	WHERE formatted_medical_record_nbr = rp.formatted_uab_mrn ");
        sql.append(") AS last_contact_date, ");

        // Detection event(s)
        sql.append("evidenceprop.value as detection_event_abbr, ");
        sql.append("rp.evidence_code AS detection_event_id, ");
        sql.append("evidence.name AS detection_event_name, ");

        // Registry status
        sql.append("rp.status_id AS status_id, ");
        sql.append("registrystatus.name AS status_name, ");
        sql.append("assigner.login AS status_assigner, ");
        sql.append("rp.status_assignment_date as status_assignment_date, ");
        sql.append("rp.registrar_comment as status_comment, ");

        // Workflow/review status
        sql.append("rp.review_status_id AS review_status_id, ");
        sql.append("workflowstatus.name AS review_status_name, ");
        sql.append("reviewer.login AS reviewed_by, ");
        sql.append("rp.last_review_date as last_review_date, ");
        sql.append("rp.workflow_comment as review_comment ");

        sql.append("FROM registry_patient rp ");
        sql.append("LEFT JOIN registry_actor assigner ON (assigner.registry_actor_id = rp.assigner_id) ");
        sql.append("LEFT JOIN registry_actor reviewer ON (reviewer.registry_actor_id = rp.reviewer_id) ");

        //Since there can be multiple demographics for each registry patient, make sure only one is returned.
        sql.append("JOIN (");
        sql.append("	SELECT source_lastname, source_firstname, source_mrn, ");
        sql.append("	row_number() OVER (partition by source_mrn ORDER BY source_max_enc_date desc) AS d_rownum ");
        sql.append("	FROM registry_patient_demographics d ");
        sql.append(") d ON d.source_mrn = rp.uab_mrn ");
        sql.append("JOIN cvterm registrystatus ON registrystatus.cvterm_id = rp.status_id ");
        sql.append("JOIN cvterm workflowstatus ON workflowstatus.cvterm_id = rp.review_status_id ");
        sql.append("LEFT JOIN cvterm evidence ON evidence.cvterm_id = rp.evidence_code ");
        sql.append("LEFT JOIN cvtermprop evidenceprop ON evidenceprop.cvterm_id = evidence.cvterm_id ");
        sql.append(String.format("WHERE rp.registry_id = %d ", registryID_));
        sql.append("AND d_rownum = 1 ");

        return sql.toString();
    }


    // Convert a Date string formatted as "MM/dd/yyyy" into the Oracle-friendly format "dd-MMM-yy".
    public static String parseToOracleDateString(String dateString_) throws ParseException {

        if (StringUtils.isNullOrEmpty(dateString_)) { return null; }
        dateString_ = dateString_.trim();

        SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Date date = parser.parse(dateString_);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
        return formatter.format(date);
    }

    // Convert a list of integers to a delimited list.
    public static String toDelimitedList(String delimiter_, List<Integer> intList_) throws Exception {

        if (StringUtils.isNullOrEmpty(delimiter_)) { throw new Exception("Invalid delimiter"); }
        if (intList_ == null || intList_.size() < 1) { return ""; }

        // Trim the delimiter.
        String delimiter = delimiter_.trim();

        StringBuilder result = new StringBuilder();

        for (Integer listInt: intList_) {
            if (result.length() > 0) { result.append(delimiter); }
            result.append(listInt.toString());
        }

        return result.toString();
    }

    // Convert a list of integers to a comma-delimited list.
    public static String toDelimitedList(List<Integer> intList_) throws Exception {
        return toDelimitedList(",", intList_);
    }

    // Validate a String that should only contain numeric characters (possibly comma-delimited), ultimately returning a
    // trimmed version (if successful) or raising an exception (if not successful).
    public static String validateNumericString(String numericString_) throws NumberFormatException {

        if (StringUtils.isNullOrEmpty(numericString_)) {
            throw new NumberFormatException("Invalid String parameter (empty)");
        } else if (numericString_.matches("[^\\d\\s,]+")) {
            throw new NumberFormatException("Invalid characters");
        }

        String result = numericString_.trim();

        // Remove a trailing comma.
        if (result.endsWith(",")) { result = result.substring(0, result.length() - 1); }

        return result;
    }
    
	public static String getFileSQL(String path, Logger logger) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		StringBuffer sql = new StringBuffer();
		//try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(path)))){
		try (BufferedReader br = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(path)))){
			while(br.ready()){
				sql.append(br.readLine());
				sql.append(System.getProperty("line.separator"));
			}
		} catch (NullPointerException | IOException io) {
			logger.warn("Failed to get SQL for "+path);
			io.printStackTrace(); 
			if(path==null) logger.warn("Specified NULL path to retrieve SQL!");
			logger.warn(path+" is not found in path");
		}
		return sql.toString();
	}


}


