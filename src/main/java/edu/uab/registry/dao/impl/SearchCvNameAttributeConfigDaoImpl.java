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

import edu.uab.registry.dao.SearchCvNameAttributeConfigDao;
import edu.uab.registry.domain.SearchCvNameList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class SearchCvNameAttributeConfigDaoImpl implements SearchCvNameAttributeConfigDao {

	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	
	public List<SearchCvNameList> searchByCVName(String cvName_) throws DaoException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<SearchCvNameList> searchCvNameList = new ArrayList<SearchCvNameList>();
try{
			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			
			pstmt = conn.prepareStatement(selectCvName_SQL);
			//logger.debug("sql query :" + selectCvName_SQL);
			System.out.println("LOWER Sql Clause Test");
			System.out.println(selectCvName_SQL);	
			pstmt.setString(1, cvName_ + "%");
			rs = pstmt.executeQuery();
									
					
			while(rs.next()) {
				
				SearchCvNameList    searchCVList  = new SearchCvNameList();
				 searchCVList.setCVName(rs.getString("name"));
				 searchCVList.setCVId(rs.getInt("cv_id"));
				 searchCvNameList.add(searchCVList);
						
			}
			
							
		}
		catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
			
		}
		finally {
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



       return searchCvNameList;
	}
	//Original SQL, commenting for testing with lowercase below
	/*String selectCvName_SQL = "SELECT "
			+ "cv.name,"
			+ "cv_id "
   +"FROM"
   			+" CV"
   +" WHERE "
   		   + "cv.name "
   		   + "LIKE ? ";*/
	
	String selectCvName_SQL = "SELECT "
			+ "LOWER(cv.name),"
			+ "cv_id "
   +"FROM"
   			+" CV"
   +" WHERE "
   		   + "LOWER(cv.name) "
   		   + "LIKE ? ";
	
	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(SearchCvNameAttributeConfigDaoImpl.class);
}
