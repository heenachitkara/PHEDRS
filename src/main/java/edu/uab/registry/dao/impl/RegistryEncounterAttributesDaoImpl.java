package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import edu.uab.registry.domain.RegistryEncounterAttribute;
import edu.uab.registry.util.Utilities;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.RegistryEncounterAttributesDao;
import edu.uab.registry.domain.RegistryPatientCode;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.Constants.SimpleEncounterAttribute;


public class RegistryEncounterAttributesDaoImpl implements RegistryEncounterAttributesDao
{

	// Get encounter attributes associated with a particular encounter (possibly constrained by registry).
	public List<RegistryEncounterAttribute> get(Integer encounterKey_,
												Integer registryID_) throws DaoException
	{
		// Validate parameter(s).
		if (encounterKey_ < 1) { throw new DaoException("Invalid encounter key"); }

		DataSource ds;
		Connection conn = null;
		List<RegistryEncounterAttribute> encounterAttributes = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;


		// Generate the query.
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");

		// The parent encounter
		sql.append("rec.ENCOUNTER_KEY as encounter_key, ");
		
		// Get the Financial Information Number from Registry Encounter table
		//sql.append("re.FORMATTED_FINANCIAL_NBR as FIN,");

		// The encounter attribute's unique ID
		sql.append("rec.EVENT_CVTERM_ID as event_cvterm_id, ");

		// The assigned cvterm
		sql.append("rec.CVTERM_ID as cvterm_id, ");
		sql.append("attrCvt.name as cvterm_name, ");

		// Assignment info
		sql.append("rec.ASSIGNER_ID as assigner_id, ");
		sql.append("ra.login as assigner_name, ");
		sql.append("rec.ASSIGNMENT_DATE as assignment_date, ");
		sql.append("rec.REGISTRAR_COMMENT as registrar_comment, ");

		// Dbxref accession and description.
		sql.append("d.accession as dbxref_accession, ");
		sql.append("d.description as dbxref_description, ");

		// Other
		sql.append("rec.REGISTRY_ID as registry_id, ");
		sql.append("rec.VALUE as value "); // TODO: what is this?

		sql.append("FROM registry_encounter_cvterm rec ");
		sql.append("JOIN cvterm attrCvt ON attrCvt.cvterm_id = rec.cvterm_id ");
		sql.append("LEFT JOIN registry_patient_codes rpc ON rpc.code_id = rec.code_id ");
		sql.append("LEFT JOIN dbxref d on d.dbxref_id = rpc.code_dbxref ");
		sql.append("LEFT JOIN registry_actor ra ON ra.registry_actor_id = rec.ASSIGNER_ID ");
		//sql.append("LEFT JOIN registry_encounter re ON re.encounter_key = rec.ENCOUNTER_KEY");
		sql.append(String.format("WHERE rec.ENCOUNTER_KEY = %d ", encounterKey_));

		if (registryID_ > 0) { sql.append(String.format("AND rec.registry_id = %d ", registryID_)); }

		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			if (rs != null) {
				while (rs.next()) {

					RegistryEncounterAttribute ea = new RegistryEncounterAttribute();
					ea.setActor_name(rs.getString("assigner_name"));
					ea.setAssigner_id(rs.getInt("assigner_id"));
					ea.setAssignment_date(rs.getDate("assignment_date"));
					ea.setCode_id(rs.getInt("code_id"));
					ea.setCvterm_id(rs.getInt("cvterm_id"));
					ea.setCvterm_name(rs.getString("cvterm_name"));
					ea.setDbxref_accession(rs.getString("dbxref_accession"));
					ea.setDbxref_description(rs.getString("dbxref_description"));
					ea.setEncounter_key(rs.getInt("encounter_key"));
					ea.setEvent_cvterm_id(rs.getInt("event_cvterm_id"));
					ea.setRegistrar_comment(rs.getString("registrar_comment"));
					ea.setRegistry_id(rs.getInt("registry_id"));
					ea.setValue(rs.getInt("value"));
					//ea.setFin_info_num(rs.getInt("formatted_financial_nbr"));

					encounterAttributes.add(ea);
				}
			}

		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			Utilities.closeDbResources(conn, ps, rs);
		}

		return encounterAttributes;
	}


	public List<RegistryPatientCode> getPatientDiagnoses(String mrn_, String type_, int registryID_) throws DaoException
	{
		// Validate input parameters.
		if (StringUtils.isNullOrEmpty(mrn_)) { throw new DaoException("Invalid MRN"); }

		DataSource ds = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<RegistryPatientCode> registryPatientCodeList = new ArrayList<RegistryPatientCode>();

		try {
			// Dynamically build the query.
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			sql.append("dbx.accession, ");
			sql.append("rpc.code_assignment_date, ");
			sql.append("dbx.db_id, dbx.description, ");
			sql.append("db.name, ");
			sql.append("cvterm.cvterm_id, ");
			sql.append("cvp.value, ");
			sql.append("cvp.type_id AS registry_id ");

			sql.append("FROM registry_patient_codes rpc ");
			sql.append("JOIN dbxref dbx ON (dbx.dbxref_id = rpc.code_dbxref) ");
			sql.append("JOIN DB db ON (db.db_id = dbx.db_id) ");
			sql.append("JOIN cvterm ON cvterm.dbxref_id = dbx.dbxref_id ");
			sql.append("LEFT JOIN cvtermprop cvp ON cvp.cvterm_id = cvterm.cvterm_id ");
			sql.append("WHERE rpc.uab_mrn =  ? ");
			sql.append("AND (cvp.value IS NULL OR cvp.value LIKE '%criteria') ");

			if (!StringUtils.isNullOrEmpty(type_)) {
				// Format type as a comma-delimited list of single-quoted db names.
				StringBuilder dbNames = new StringBuilder();
				String[] typeArray = type_.split(",");
				if (typeArray.length > 0) {
					for (String dbName: typeArray) {
						if (dbNames.length() > 0) { dbNames.append(","); }
						dbNames.append(String.format("'%s'", dbName.trim()));
					}
				}

				if (dbNames.length() > 0) { sql.append(String.format("AND db.name IN (%s) ", dbNames.toString())); }
			}

			// dmd 04/11/17 - there don't appear to be cvp's with a registry ID.
			//if (registryID_ > 0) { sql.append(String.format("AND cvp.type_id = %d ", registryID_)); }

			sql.append("ORDER BY dbx.accession, rpc.code_assignment_date DESC ");

			logger.info(sql.toString());

			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, mrn_);
			rs = ps.executeQuery();

			if (rs != null) {
				while (rs.next()) {

					RegistryPatientCode rpc = new RegistryPatientCode();

					rpc.setMrn(mrn_);
					rpc.setCode_value(rs.getString("accession"));
					rpc.setCode_type(rs.getInt("db_id"));
					rpc.setCode_assignment_date(rs.getString("code_assignment_date"));
					rpc.setCode_description(rs.getString("description"));
					rpc.setName(rs.getString("name"));
					rpc.setDetection_criteria(rs.getString("value"));
					rpc.setRegistry_id(rs.getInt("registry_id"));
					rpc.setCvterm_id(rs.getInt("cvterm_id"));

					registryPatientCodeList.add(rpc);
				}
			}

		} catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			Utilities.closeDbResources(conn, ps, rs);
		}

		return registryPatientCodeList;
	}



	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}

	public static void main(String[] args) 
	{
	}
	 
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(RegistryEncounterAttributesDaoImpl.class);
}
