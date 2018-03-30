package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.RegistryPatientHistoryDao;
import edu.uab.registry.domain.RegistryPatientHistoryList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class RegistryPatientHistoryDaoImpl implements RegistryPatientHistoryDao 
{

	public List<edu.uab.registry.domain.RegistryPatientHistory> getRegistryPatientHistory(int registryPatientID_, int registryID_) throws DaoException
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		DataSource ds = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		
		List<edu.uab.registry.domain.RegistryPatientHistory> historyList = new ArrayList<edu.uab.registry.domain.RegistryPatientHistory>();

		try {
			if (registryPatientID_ < 1) {throw new Exception("Invalid registry patient ID"); }
			if (registryID_ < 1) {throw new Exception("Invalid registry ID"); }

			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();

			// Generate the SQL.
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			sql.append("rph.previous_status_id, ");
			sql.append("rph.current_status_id, ");
			sql.append("rph.prev_review_status_id, ");
			sql.append("rph.cur_review_status_id, ");
			sql.append("rph.changer_id, ");
			sql.append("rph.change_date, ");
			sql.append("rph.reg_status_change_comment, ");
			sql.append("rph.workflow_status_change_comment, ");
			sql.append("previousstatus.name AS prev_status, ");
			sql.append("currentstatus.name AS curr_status, ");
			sql.append("prevreviewstatus.name AS prev_review_status, ");
			sql.append("currentreviewstatus.name AS curr_review_status, ");
			sql.append("ra.login ");

			sql.append("FROM registry_patient_history rph ");
			sql.append("LEFT JOIN cvterm previousstatus ON previousstatus.cvterm_id = rph.previous_status_id ");
			sql.append("LEFT JOIN cvterm currentstatus ON currentstatus.cvterm_id = rph.current_status_id ");
			sql.append("LEFT JOIN cvterm prevreviewstatus ON prevreviewstatus.cvterm_id = rph.prev_review_status_id ");
			sql.append("LEFT JOIN cvterm currentreviewstatus ON currentreviewstatus.cvterm_id = rph.cur_review_status_id ");
			sql.append("LEFT JOIN registry_actor ra ON ra.registry_actor_id = rph.changer_id ");
			sql.append("WHERE rph.registry_patient_id = ? ");
			sql.append("AND rph.registry_id = ? ");
			sql.append("ORDER BY rph.change_date DESC ");

			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, registryPatientID_);
			ps.setInt(2, registryID_);

			rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					edu.uab.registry.domain.RegistryPatientHistory rph = new edu.uab.registry.domain.RegistryPatientHistory();
					rph.setChanger_id(rs.getInt("changer_id"));
					rph.setChanger(rs.getString("login"));
					rph.setChange_date(rs.getDate("change_date"));
					rph.setPrevious_status_id(rs.getInt("previous_status_id"));
					rph.setPrevious_status(rs.getString("prev_status"));
					rph.setCurrent_status_id(rs.getInt("current_status_id"));
					rph.setCurrent_status(rs.getString("curr_status"));
					rph.setPrev_review_status_id(rs.getInt("prev_review_status_id"));
					rph.setPrev_review_status(rs.getString("prev_review_status"));
					rph.setCurr_review_status_id(rs.getInt("cur_review_status_id"));
					rph.setCurr_review_status(rs.getString("curr_review_status"));
					rph.setReg_status_change_comment(rs.getString("reg_status_change_comment"));
					rph.setWorkflow_status_change_comment(rs.getString("workflow_status_change_comment"));

					historyList.add(rph);
				}
			}
		} catch (Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }}
			if (ps != null) { try { ps.close(); } catch(SQLException sqe) { sqe.printStackTrace(); }}
			if (conn != null) { try { conn.close(); } catch (SQLException sqle) { sqle.printStackTrace(); }}
		}

		stopWatch.stop();
		logger.debug("Number of RegistryPatientHistory entries :" + historyList.size());
		logger.info("Log.ElapsedTime.dao.getRegistryPatientHistory=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");

		return historyList;
	}
	
	public static void main(String[] args) 
	{
	}		

	public void setDataSource(DataSource dataSource)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(RegistryPatientHistoryDaoImpl.class);	
			
}
