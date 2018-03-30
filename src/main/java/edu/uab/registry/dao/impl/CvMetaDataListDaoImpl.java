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
import edu.uab.registry.dao.CvMetaDataListDao;
import edu.uab.registry.domain.CvMetaDataList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class CvMetaDataListDaoImpl implements CvMetaDataListDao {

	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	public List<CvMetaDataList> getCvMetaData(int registryId,int attributeCvtermID) throws DaoException
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		List<CvMetaDataList> cvMetaDataList = new ArrayList<CvMetaDataList>();
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectCvMetaData_SQL);
			logger.debug("sql query :" + selectCvMetaData_SQL);
			System.out.println("September 07 Test");
			System.out.println(selectCvMetaData_SQL);
			pstmt.setInt(1, registryId);
			pstmt.setInt(2, attributeCvtermID);
			logger.debug("sql parameters, registryId:" +  registryId);
			logger.debug("sql parameters, attributeCvtermID:" +  attributeCvtermID);
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				
				CvMetaDataList    cvList  = new CvMetaDataList();
				cvList.setRegistryId(registryId);
				cvList.setAttributeCvtermId(attributeCvtermID);
				cvList.setValueDisplayName(rs.getString("value_display_name"));
				cvList.setValueCvId(rs.getInt("value_cv_id"));
				cvList.setCvName(rs.getString("name"));
				cvMetaDataList.add(cvList);
						
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
		logger.debug("Number of CV MetaData List :" +cvMetaDataList.size());
		logger.info("Log.ElapsedTime.dao.getcvMetaData=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return cvMetaDataList;
	}
	
	
	
	private String selectCvMetaData_SQL = 
			"SELECT  " 
	             + "c.name,"
				 + "r.registry_id, "
				 + "r.attribute_cvterm_id,"
				 + "r.value_display_name,"
				 + "r.value_cv_id " +  				 			
			"FROM " + 				 
				"CV c,REGISTRY_CV_METADATA r " +				
			"WHERE c.CV_ID = r.VALUE_CV_ID" +								
				" AND registry_id = ? " + 
				" AND attribute_cvterm_id  = ? "
			
			;	
	
	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(CvMetaDataListDaoImpl.class);

}
