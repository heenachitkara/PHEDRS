package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import edu.uab.registry.dao.CvTermsDao;
import edu.uab.registry.domain.ControlledVocabulary;
import edu.uab.registry.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.exception.DaoException;
import edu.uab.registry.orm.Cvterm;
import edu.uab.registry.domain.CvTerms;
import edu.uab.registry.util.StopWatch;

public class CvTermsDaoImpl implements CvTermsDao 
{

	public List<ControlledVocabulary> getRegistryCVs(int registryID_) throws DaoException
	{
		DataSource ds;
		Connection conn = null;
		List<ControlledVocabulary> cvs = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			if (registryID_ < 1) { throw new Exception("Invalid registry ID"); }

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			sql.append("cv.cv_id as cv_id, ");
			sql.append("cv.name as cv_name, ");
			sql.append("cv.definition as cv_definition, ");
			//sql.append("cv.registry_id as registry_id, ");
			sql.append("cvterm.CVTERM_ID as cvterm_id, ");
			sql.append("cvterm.DBXREF_ID as dbxref_id, ");
			sql.append("cvterm.DEFINITION as cvterm_definition, ");
			sql.append("cvterm.name as cvterm_name ");

			sql.append("FROM cv ");
			sql.append("JOIN cvterm on cvterm.cv_id = cv.cv_id ");

			// TODO: this example assumes a cv.registry_id column that can be set to a specific registry ID or be NULL if it's valid for all registries.
			//sql.append(String.format("WHERE (cv.registry_id = %d OR cv.registry_id IS NULL) ", registryID_));
			sql.append("ORDER BY cv.name ASC, cvterm.name ASC ");

			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			if (rs != null) {

				int previousCvID = -1;
				ControlledVocabulary cv = null;

				while (rs.next()) {

					// Get the CV ID. If it's different from the previously encountered CV ID, create a new object.
					int cv_id = rs.getInt("cv_id");

					if (cv_id != previousCvID) {

						// If it isn't NULL, add the previous cv object to the collection.
						if (cv != null) { cvs.add(cv); }

						cv = new ControlledVocabulary();
						cv.setDefinition(rs.getString("cv_definition"));
						cv.setId(cv_id);
						cv.setName(rs.getString("cv_name"));
					}

					// Always get a cv term and add it to the most-recent cv.
					Cvterm term = new Cvterm();
					term.setCvId(cv_id);
					term.setDbxrefId(rs.getInt("dbxref_id"));
					term.setDefinition(rs.getString("cvterm_definition"));
					term.setCvtermId(rs.getInt("cvterm_id"));
					term.setName(rs.getString("cvterm_name"));

					cv.addTerm(term);

					previousCvID = cv_id;
				}

				if (cv != null) { cvs.add(cv); }
			}

		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			Utilities.closeDbResources(conn, ps, rs);
		}

		return cvs;
	}


	/**
	 * This gets cvterms based on an input string which is
	 * the name of the CV. A database constraint enforces
	 * a unique string name.
	 */
	public List<CvTerms> getCvterms(String cvs) throws DaoException
	{	  
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		List<CvTerms> cvtermList = new ArrayList<CvTerms>();
		try {			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();	
			String sql = buildSQLStatement(cvs);
			pstmt = conn.prepareStatement(sql);		
			
			logger.debug("sql query :" + sql);	
			logger.debug("sql parameters, cvs:" + cvs);
			
			rs = pstmt.executeQuery();									
			while(rs.next()) {
				CvTerms cv = new CvTerms();							
				cv.setCvId(rs.getInt("cv_id"));
				cv.setCvName(rs.getString("cv_name"));
				cv.setCvtermId(rs.getInt("cvterm_id"));
				cv.setCvtermName(rs.getString("cvterm_name"));				
				cvtermList.add(cv);
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
		logger.info("Log.ElapsedTime.dao.getCvterms=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return cvtermList;
	}

	public static void main(String[] args) 
	{
	}	
	
	private String buildSQLStatement(String cvs)
	{
		String sql = cvs_SQL;
		sql = sql + addCvsQuery(cvs);
		sql = sql + 
			  "AND (cvterm.IS_OBSOLETE = 0) " + 
			  "ORDER BY cv.NAME ASC, cvterm.NAME ASC ";
		return sql;
	}
	
	private String addCvsQuery(String cvs)
	{
		String sQ1 = "cv.name IN (";
		StringBuilder sQ2 = new StringBuilder();
		String sQ3 = ") ";
		String cvsQuery = "";
		String[] cvsArray = cvs.split(",");
		for (int i=0; i<cvsArray.length; i++) {
			String cv = cvsArray[i];	
			if (i==(cvsArray.length-1)) {
				sQ2.append("'").append(cv).append("'");
			} else {
				sQ2.append("'").append(cv).append("',");
			}
		}
		cvsQuery = sQ1 + sQ2 + sQ3;
		return cvsQuery;
	}
	
	private String cvs_SQL = 
			"SELECT " + 
				"cv.CV_ID 			AS cv_id " +
				", cv.NAME 			AS cv_name " +
				", cvterm.CVTERM_ID AS cvterm_id " +
				", cvterm.NAME 		AS cvterm_name " + 				
			"FROM cvterm " +
				"JOIN cv ON (cv.CV_ID = cvterm.CV_ID) " + 							
			"WHERE "							
			;


	public void setDataSource(DataSource dataSource)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(CvTermsDaoImpl.class);		
}
