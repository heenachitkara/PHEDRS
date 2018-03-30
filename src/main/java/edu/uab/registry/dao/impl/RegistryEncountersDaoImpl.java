package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.RegistryEncountersDao;
import edu.uab.registry.domain.RegistryEncounter;
import edu.uab.registry.domain.RegistryEncounterAttribute;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.Utilities;

public class RegistryEncountersDaoImpl implements RegistryEncountersDao
{
	
	
		
	// Get this patient's encounters along with any associated encounter attributes.
	public List<RegistryEncounter> get(String mrn_, Integer registryID_) throws DaoException
	{
		// Validate the input parameters.
		if (mrn_==null || mrn_.trim().isEmpty()) { throw new DaoException("Invalid MRN"); }
		if (registryID_ < 1) { throw new DaoException("Invalid registry ID"); }

		// The encounter lookup is a collection of encounter objects keyed by encounter key.
		HashMap<Integer, RegistryEncounter> encounterLookup = new HashMap<>();

		// Encounter order is a list of encounter keys sorted by encounter start date (most recent first).
		List<Integer> encounterOrder = new ArrayList<>();

		// Get all of this patient's encounters.
		String fsql = Utilities.getFileSQL("sql/getEncounterByMrnRegistry.sql", logger);
		DataSource ds;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		logger.info(fsql.toString());

		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			ps = conn.prepareStatement(fsql.toString());
			ps.setInt(1, registryID_);
			ps.setString(2, mrn_);

			rs = ps.executeQuery();

			if (rs != null) {
				while (rs.next()) {

					Integer encounterKey = rs.getInt("encounter_key");

					// Create and populate an encounter object.
					RegistryEncounter encounter = new RegistryEncounter();
					encounter.setFinancialInfoNum(rs.getString("formatted_financial_nbr"));
					encounter.setPlandesc(rs.getString("billplan"));
					encounter.setAdmitLoc(rs.getString("admit_loc"));
					encounter.setAdmitLocDescription(rs.getString("loc_ambulatory_desc"));
					encounter.setEncounterKey(encounterKey);
					encounter.setEncounterTypeClassRef(rs.getString("encounter_type_class_ref"));
					encounter.setEncounterTypeDescription(rs.getString("encounter_type_description"));
					encounter.setEndDate(rs.getDate("end_date"));
					encounter.setMrn(mrn_);
					encounter.setReasonForVisit(rs.getString("reason_for_visit_txt"));
					encounter.setStartDate(rs.getDate("start_date"));
					

					// Update the list of ordered encounter keys.
					encounterOrder.add(encounterKey);

					// Update the collection of encounters.
					encounterLookup.put(encounterKey, encounter);
				}
			}
		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			Utilities.closeDbResources(conn, ps, rs);
		}

		logger.info("after first query");
		logger.info("encounterLookup size = " + encounterLookup.size());
		logger.info("encounterOrder size = " + encounterOrder.size());


		return associateEncounterAttributes(encounterLookup, encounterOrder);
	}


	// Get a single encounter and any associated encounter attributes.
	public RegistryEncounter get(Integer encounterKey_, String mrn_, Integer registryID_) throws DaoException {

		// Validate the input parameters.
		if (encounterKey_ < 1) { throw new DaoException("Invalid encounter key"); }
		if (StringUtils.isNullOrEmpty(mrn_)) { throw new DaoException("Invalid MRN"); }
		if (registryID_ < 1) { throw new DaoException("Invalid registry ID"); }

		String fsql = Utilities.getFileSQL("sql/getEncounterByMrnRegistry.sql", logger);
		fsql+=" AND re.encounter_key = ? ";

		DataSource ds;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		logger.info(fsql.toString());

		RegistryEncounter encounter = new RegistryEncounter();

		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			ps = conn.prepareStatement(fsql.toString());
			ps.setInt(1, registryID_);
			ps.setString(2, mrn_);
			ps.setInt(3, encounterKey_);
			rs = ps.executeQuery();

			if (rs != null && rs.next()) {

				// Populate the encounter object.
				encounter.setFinancialInfoNum(rs.getString("formatted_financial_nbr"));
				encounter.setPlandesc(rs.getString("billplan"));
				encounter.setAdmitLoc(rs.getString("admit_loc"));
				encounter.setAdmitLocDescription(rs.getString("loc_ambulatory_desc"));
				encounter.setEncounterKey(rs.getInt("encounter_key"));
				encounter.setEncounterTypeClassRef(rs.getString("encounter_type_class_ref"));
				encounter.setEncounterTypeDescription(rs.getString("encounter_type_description"));
				encounter.setEndDate(rs.getDate("end_date"));
				encounter.setMrn(mrn_);
				encounter.setReasonForVisit(rs.getString("reason_for_visit_txt"));
				encounter.setStartDate(rs.getDate("start_date"));
				
			}
		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			Utilities.closeDbResources(conn, ps, rs);
		}

		return associateEncounterAttributes(encounter);
	}



	// Retrieve encounter attributes associated with a single encounter.
	// TODO: note that a null "is_valid" is considered still valid as we transition to always specifying Y or N. In the
	// future, we probably don't want a null "is_valid" to be considered valid!
	public RegistryEncounter associateEncounterAttributes(RegistryEncounter encounter_) throws DaoException
	{
		// Validate the input parameters.
		if (encounter_ == null || encounter_.getEncounterKey() < 1) { throw new DaoException("Invalid encounter parameter"); }

		// Initialize the collection of encounter attributes.
		List<RegistryEncounterAttribute> encounterAttributes = new ArrayList<>();

		// Create the query.
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("assigner.login as actor_name, ");
		sql.append("rec.assigner_id, ");
		sql.append("rec.assignment_date, ");
		sql.append("rec.code_id, ");
		sql.append("cvt.cvterm_id as cvt_id, ");
		sql.append("cvt.name as cvt_name, ");
		sql.append("d.accession as dbxref_accession, ");
		sql.append("d.description as dbxref_description, ");
		sql.append("rec.encounter_key, ");
		sql.append("rec.event_cvterm_id, ");
		sql.append("rec.is_valid, ");
		sql.append("rec.registrar_comment, ");
		sql.append("rec.registry_id, ");
		sql.append("rec.value ");
        sql.append("FROM registry_encounter_cvterm rec ");
		sql.append("JOIN cvterm cvt on cvt.cvterm_id = rec.cvterm_id ");
		sql.append("JOIN registry_actor assigner on assigner.registry_actor_id = rec.assigner_id ");
		sql.append("LEFT JOIN registry_patient_codes rpc ON rpc.code_id = rec.code_id ");
		sql.append("LEFT JOIN dbxref d on d.dbxref_id = rpc.code_dbxref ");
		sql.append("WHERE rec.encounter_key = ? ");
		sql.append("AND (rec.is_valid = 'Y' or rec.is_valid is null) ");

		logger.debug(sql.toString());
		System.out.println("Test By Adarsh"+sql.toString().length());

		DataSource ds;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, encounter_.getEncounterKey());
			rs = ps.executeQuery();

			if (rs != null) {
				while (rs.next()) {

					// Create and populate an encounter attribute object.
					RegistryEncounterAttribute encounterAttribute = new RegistryEncounterAttribute();
					encounterAttribute.setActor_name(rs.getString("actor_name"));
					encounterAttribute.setAssigner_id(rs.getInt("assigner_id"));
					encounterAttribute.setAssignment_date(rs.getDate("assignment_date"));
					encounterAttribute.setCode_id(rs.getInt("code_id"));
					encounterAttribute.setCvterm_id(rs.getInt("cvt_id"));
					encounterAttribute.setCvterm_name(rs.getString("cvt_name"));
					encounterAttribute.setDbxref_accession(rs.getString("dbxref_accession"));
					encounterAttribute.setDbxref_description(rs.getString("dbxref_description"));
					encounterAttribute.setEncounter_key(rs.getInt("encounter_key"));
					encounterAttribute.setEvent_cvterm_id(rs.getInt("event_cvterm_id"));
					encounterAttribute.setIs_valid(rs.getString("is_valid"));
					encounterAttribute.setRegistrar_comment(rs.getString("registrar_comment"));
					encounterAttribute.setRegistry_id(rs.getInt("registry_id"));
					encounterAttribute.setValue(rs.getInt("value"));

					// Update the collection.
					encounterAttributes.add(encounterAttribute);
				}
			}
		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			Utilities.closeDbResources(conn, ps, rs);
		}

		// Associate the attributes with the encounter and return it.
		encounter_.setRegistry_encounter_attributes(encounterAttributes);

		return encounter_;
	}


	// Retrieve encounter attributes associated with any of the encounters provided as a parameter.
	public List<RegistryEncounter> associateEncounterAttributes(HashMap<Integer, RegistryEncounter> encounterLookup_,
																List<Integer> encounterOrder_) throws DaoException
	{
		// Validate the input parameters.
		if (encounterLookup_ == null || encounterLookup_.size() < 1) { return null; }
		if (encounterOrder_ == null || encounterOrder_.size() < 1) { return null; }

		DataSource ds;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			// Convert the collection of encounter keys into a delimited list.
			String encounterKeys = Utilities.toDelimitedList(encounterOrder_);

			// Get associated encounter attributes.
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			sql.append("assigner.login as actor_name, ");
			sql.append("rec.assigner_id, ");
			sql.append("rec.assignment_date, ");
			sql.append("rec.code_id, ");
			sql.append("cvt.cvterm_id as cvt_id, ");
			sql.append("cvt.name as cvt_name, ");
			sql.append("d.accession as dbxref_accession, ");
			sql.append("d.description as dbxref_description, ");
			sql.append("rec.encounter_key, ");
			sql.append("rec.event_cvterm_id, ");
			sql.append("rec.is_valid, ");
			sql.append("rec.registry_id, ");
			sql.append("rec.registrar_comment, ");
			sql.append("rec.registry_id, ");
			sql.append("rec.value ");

			sql.append("FROM registry_encounter_cvterm rec ");
			sql.append("JOIN cvterm cvt on cvt.cvterm_id = rec.cvterm_id ");
			sql.append("JOIN registry_actor assigner on assigner.registry_actor_id = rec.assigner_id ");
			sql.append("LEFT JOIN registry_patient_codes rpc ON rpc.code_id = rec.code_id ");
			sql.append("LEFT JOIN dbxref d on d.dbxref_id = rpc.code_dbxref ");
			sql.append(String.format("WHERE rec.encounter_key IN (%s) ", encounterKeys));
			sql.append("AND (rec.is_valid = 'Y' or rec.is_valid is null) ");

			logger.info(sql.toString());



			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			if (rs != null) {
				while (rs.next()) {

					Integer encounterKey = rs.getInt("encounter_key");

					// Create and populate an encounter attribute object.
					RegistryEncounterAttribute encounterAttribute = new RegistryEncounterAttribute();
					encounterAttribute.setActor_name(rs.getString("actor_name"));
					encounterAttribute.setAssigner_id(rs.getInt("assigner_id"));
					encounterAttribute.setAssignment_date(rs.getDate("assignment_date"));
					encounterAttribute.setCode_id(rs.getInt("code_id"));
					encounterAttribute.setCvterm_id(rs.getInt("cvt_id"));
					encounterAttribute.setCvterm_name(rs.getString("cvt_name"));
					encounterAttribute.setDbxref_accession(rs.getString("dbxref_accession"));
					encounterAttribute.setDbxref_description(rs.getString("dbxref_description"));
					encounterAttribute.setEncounter_key(encounterKey);
					encounterAttribute.setEvent_cvterm_id(rs.getInt("event_cvterm_id"));
					encounterAttribute.setIs_valid(rs.getString("is_valid"));
					encounterAttribute.setRegistrar_comment(rs.getString("registrar_comment"));
					encounterAttribute.setRegistry_id(rs.getInt("registry_id"));
					encounterAttribute.setValue(rs.getInt("value"));

					// Update the associated encounter.
					if (encounterLookup_.containsKey(encounterKey)) {
						RegistryEncounter encounter = encounterLookup_.get(encounterKey);
						encounter.addEncounterAttribute(encounterAttribute);

						// Replace the encounter in the collection.
						encounterLookup_.replace(encounterKey, encounter);
					}
				}
			}
		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			Utilities.closeDbResources(conn, ps, rs);
		}


		// Prepare the collection that will be returned.
		List<RegistryEncounter> encounters = new ArrayList<>();

		// Iterate thru the ordered list of encounter keys.
		for (Integer ek: encounterOrder_) {

			if (!encounterLookup_.containsKey(ek)) { throw new DaoException("Encounter with key " + ek.toString() + " is missing"); }

			// Get the encounter from the lookup and add it to the result collection.
			RegistryEncounter encounter = encounterLookup_.get(ek);
			encounters.add(encounter);
		}

		return encounters;
	}

	public static void main(String[] args)
	{
	}
	private JdbcTemplate jdbcTemplate;
	public void setDataSource(DataSource dataSource) { jdbcTemplate = new JdbcTemplate(dataSource); }
	private static final Logger logger = LoggerFactory.getLogger(RegistryEncountersDaoImpl.class);
}
