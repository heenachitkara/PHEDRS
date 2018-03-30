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

import edu.uab.registry.dao.CvtermNameDao;
import edu.uab.registry.domain.CvtermNameList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class CvtermNameDaoImpl implements CvtermNameDao {
   
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	
	
	
	
	public List<CvtermNameList> getCvTermName(Integer cvtermID_) throws DaoException {
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
List<CvtermNameList> cvTermNameList = new ArrayList<CvtermNameList>();
		
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectNameDefinition_SQL);
			logger.debug("sql query :" +selectNameDefinition_SQL);
			System.out.println(selectNameDefinition_SQL);
			pstmt.setInt(1, cvtermID_);
			logger.debug("sql parameters, cvtermID_:" +  cvtermID_);
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				
				CvtermNameList    cvtermNList  = new CvtermNameList();
				cvtermNList.setCvtermId(cvtermID_);
				cvtermNList.setCvtermName(rs.getString("name"));
				cvtermNList.setCvtermDefinition(rs.getString("definition"));
				cvtermNList.setCvId(rs.getInt("cv_id"));
				cvTermNameList.add(cvtermNList);
						
			}
		} catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
		}finally {
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
		
		
			
		return cvTermNameList;
	}
	
	private String selectNameDefinition_SQL = "SELECT name , definition ,CV_ID FROM CVTERM WHERE CVTERM_ID = ?";
	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(CvtermNameDaoImpl.class);
	

}
