package edu.uab.registry.util;

public class Constants 
{

	// Keys for registry tab queries. The text should correspond to the 'PHEDRS Tab CV'
	// They are here for legacy/performance reasons
	public static final String TAB_REGISTRY_INPATIENT_REVIEW_TAB   	= "REGISTRY_INPATIENT_REVIEW";
	public static final String TAB_90_DAY_WINDOW 					= "90_DAY_WINDOW";
	public static final String TAB_90_DAY_WINDOW_MEDICARE			= "90_DAY_WINDOW_MEDICARE";
	public static final String TAB_REGISTRY_INPATIENT_TAB 			= "REGISTRY_INPATIENT";
	public static final String TAB_ALL_REGISTRY_PATIENTS			= "ALL_REGISTRY_PATIENTS";


	// Result statuses for web services?
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";
	public static final String EMPTY = "";

    
	//Values for Attribute CV Term ID in the REGISTRY_CV_METADATA table
	public static final int PATIENT_ATTRIBUTE_CVTERM_ID = 3187;
	public static final int ENCOUNTER_ATTRIBUTE_CVTERM_ID = 3281;
	
	//Value for the CV_ID that is used in the addcvterm webservice at the time of adding new registry
	public static final int CV_ID = 11;
	
	// Possible result statuses of an MRN lookup.
	public enum MrnLookupStatus {
		invalid_mrn,
		already_in_registry,
		valid_mrn
	}

	// The different types of status available for a registry patient.
	public enum RegistryStatusType {
		registry,
		workflow
	}
	
	

	// A temporary object used when converting Encounter Attribute JSON sent from UI.
	public static class SimpleEncounterAttribute {
		public String comment;
		public int cvterm_id;
		public int event_cvterm_id;

		// C-tor
		private SimpleEncounterAttribute() {}
	}


	// Query to get registry patient codes 
	public static String select_RegistryPatientCodes_SQL = 
		"SELECT " +			
			"rpc.uab_mrn " +			
			",rpc.code_assignment_date " +
			", dbx.accession, dbx.db_id, dbx.description " +
			", db.name " +
		"FROM " +
			"registry_patient_codes rpc " +			 
			"JOIN dbxref dbx ON (dbx.dbxref_id = rpc.code_dbxref) " +
			"JOIN DB db ON (db.db_id = dbx.db_id) " +
		"WHERE " +
			"rpc.uab_mrn =  ? " +
		"ORDER BY rpc.code_assignment_date DESC "
		;
	
	// Query to get registry patient codes and filtered by type, registry_id
	public static String select_RegistryPatientCodes__partial_filtered_SQL = 
			"SELECT " +			
				"dbx.accession " +			
				", rpc.code_assignment_date " +
				", dbx.db_id, dbx.description " +
				", db.name " +
				", cvterm.cvterm_id " +
				", cvp.value, cvp.type_id AS registry_id " +
			"FROM " +
				"registry_patient_codes rpc " +			 
				"JOIN dbxref dbx ON (dbx.dbxref_id = rpc.code_dbxref) " +
				"JOIN DB db ON (db.db_id = dbx.db_id) " +
				"JOIN cvterm ON cvterm.dbxref_id = dbx.dbxref_id " + 
				"LEFT JOIN cvtermprop cvp ON cvp.cvterm_id = cvterm.cvterm_id " +
			"WHERE " +
				"rpc.uab_mrn =  ? " +
				"AND (cvp.value IS NULL OR cvp.value LIKE '%criteria') ";
	
	// Query to get Patient level Attributes
	public static String select_RegistryPatientAtributes_SQL = 
		"SELECT " +
			"cv.name " +
			", rp.status_assignment_date " +
			", rpc.registry_patient_cvterm_id, rpc.registry_patient_id, rpc.registry_id, rpc.annotator_id, rpc.annotation_date, rpc.annotator_comment, rpc.is_valid, rpc.start_assignment_date, rpc.end_assignment_date " +
			", ra.name " +			
		"FROM registry_patient rp " +
			"JOIN registry_patient_cvterm rpc ON (rp.registry_patient_id=rpc.registry_patient_id) " +
			"JOIN cvterm cv ON (rpc.cvterm_id=cv.cvterm_id) " +
			"JOIN registry_actor ra ON (rpc.annotator_id=ra.registry_actor_id) " +
		"WHERE " +
			"rpc.is_valid = 'Y' " + 
			"AND rp.uab_mrn =  ? "
		;
	
	// Query to get Encounter level Attributes
	public static String selectEncounterAttrs_SQL =
			"SELECT " +
			   "cv.name AS cvterm_name " +
			   ", rec.event_cvterm_id, rec.encounter_key, rec.assigner_id, rec.registry_id, rec.cvterm_id, rec.value, rec.registrar_comment,  rec.assignment_date, rec.code_id " +
			   ", ra.name AS actor_name " + 			    
			"FROM " +
			   "registry_encounter re " +
			   "JOIN registry_encounter_cvterm rec ON (re.encounter_key=rec.encounter_key) " +
			   "LEFT JOIN cvterm cv ON (rec.cvterm_id=cv.cvterm_id) " +
			   "LEFT JOIN registry_actor ra ON (rec.assigner_id=ra.registry_actor_id) " +
			   "WHERE re.formatted_medical_record_nbr = to_char(?, 'FM000000000000') "			   
			;
		
	// Query to get last contact date with Health System 
	//public static String select_MaxUpdateDate_SQL = "SELECT MAX(update_date) AS last_contact_date FROM registry_encounter WHERE formatted_medical_record_nbr = ? ";
		
	// Query to get CV term names
	/*public static String select_CVTermName_SQL =
		"SELECT " +
			"name " +
		"FROM " +
			"cvterm " +
		"WHERE " +
			"cvterm_id = ? "
		;	*/
	
	/*public static String select_DetectionEvent_SQL =
		"SELECT " +
			"rp.evidence_code " +
			", cvt.name " + 
		"FROM " +
			"registry_patient rp " +
			"JOIN cvterm cvt ON (cvt.cvterm_id = rp.evidence_code) " +
		"WHERE " +
			"rp.registry_patient_id = ? "
		;*/
}
