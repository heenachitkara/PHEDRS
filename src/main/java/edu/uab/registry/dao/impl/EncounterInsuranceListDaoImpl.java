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
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.EncounterInsuranceListDao;
import edu.uab.registry.domain.RegistryEncounterInsuranceList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class EncounterInsuranceListDaoImpl implements EncounterInsuranceListDao {
	
	
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	//public List<CvMetaDataList> getCvMetaData(int registryId,int attributeCvtermID) throws DaoException
	public List<RegistryEncounterInsuranceList> getEncounterInsurance(String hqFinNum) throws DaoException
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		
		List<RegistryEncounterInsuranceList> encInsuranceList = new ArrayList<RegistryEncounterInsuranceList>();
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectEncInsurance_SQL);
			logger.debug("sql query :" + selectEncInsurance_SQL);
			System.out.println("September 15 Test");
			System.out.println(selectEncInsurance_SQL);
			pstmt.setString(1, hqFinNum);
			//pstmt.setInt(2, attributeCvtermID);
			logger.debug("sql parameters, hqFinNum:" +  hqFinNum);
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				
				
				RegistryEncounterInsuranceList eInsList = new RegistryEncounterInsuranceList();
				eInsList.setHealthQuestFIN(hqFinNum);
				eInsList.setInsurancePlanInfo(rs.getString("plnfscrptccat"));
				eInsList.setDateOfService(rs.getString("dos"));
				eInsList.setLocationDescription(rs.getString("locdesc"));
				encInsuranceList.add(eInsList);
				
			}
		} catch(Throwable th) {
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
		logger.debug("Number of Enclounter Insurance List :" +encInsuranceList.size());
		logger.info("Log.ElapsedTime.dao.getEncounterInsurance=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return encInsuranceList;
	}
	
	
	/*SELECT PLNFSCRPTCCAT,
    DOS,
    LOCDESC,
    HQFINNUM
FROM  REGISTRY_BILLED_CHARGES
WHERE HQFINNUM = 1856617215;*/
	
	private String selectEncInsurance_SQL = 
			"SELECT DISTINCT " 
	             + "PLNFSCRPTCCAT,"
				 + "DOS, "
				 + "LOCDESC,"
				 + "HQFINNUM " +  				 			
			"FROM " + 				 
				"REGISTRY_BILLED_CHARGES " +				
			"WHERE" +								
				" hqFinNum = ? " 
				;	
	
	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(EncounterInsuranceListDaoImpl.class);

}
