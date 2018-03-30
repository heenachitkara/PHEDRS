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

import edu.uab.registry.dao.GetAllCVDao;
import edu.uab.registry.domain.GetAllCVList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class GetAllCVDaoImpl implements GetAllCVDao {
	
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	
	
	public List<GetAllCVList> getAllCV() throws DaoException {
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<GetAllCVList> cvAllList = new ArrayList<GetAllCVList>();
		
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectAllCv_SQL);
			logger.debug("sql query :" + selectAllCv_SQL);
			System.out.println(selectAllCv_SQL);
			
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				
				GetAllCVList cvList = new GetAllCVList();
				cvList.setCvId(rs.getInt("cv_id"));
				cvList.setCvName(rs.getString("name"));
				cvAllList.add(cvList);
			}
						
		}catch(Throwable th){
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
		
				
		return cvAllList;
	}
	
	private String selectAllCv_SQL = " SELECT "
											+ " CV.name, CV.CV_ID "
								     + " FROM CV "
								     + " WHERE "
								     +        " CV_ID NOT IN (3,6,7,8,2,10,11,13,15,22) "
								     + " ORDER BY "
								     + " CV.NAME ";
	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(GetAllCVDaoImpl.class);

}
