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

import edu.uab.registry.dao.GetConfigAddAttributesDao;
import edu.uab.registry.domain.GetConfigAddAttributesList;
import edu.uab.registry.domain.GetConfigAddAttributesList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class GetConfigAddAttributesDaoImpl implements GetConfigAddAttributesDao  {
	
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	
	@Override
	public List<GetConfigAddAttributesList> getConfigAddAttribute(Integer cv_id,String attributeName) throws DaoException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;	
		
		List<GetConfigAddAttributesList> configAddAttrList = new ArrayList<GetConfigAddAttributesList>();
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectGetConfigAddAttribute);
			logger.debug("sql query :" + selectGetConfigAddAttribute);
			System.out.println(selectGetConfigAddAttribute);
			pstmt.setInt(1, cv_id);
			pstmt.setString(2, "%"+attributeName+"%");
			logger.debug("sql parameters, cv_id:" +  cv_id + "Attribute Name: "+attributeName);
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				
				GetConfigAddAttributesList    configAddAList  = new GetConfigAddAttributesList();
				configAddAList.setConfigAddAttributeCvID(cv_id);
				configAddAList.setConfigAddAttributeCvID(rs.getInt("cv_id"));
				configAddAList.setConfigAddAttributeCvTermID(rs.getInt("cvterm_id"));
				configAddAList.setConfigAddAttributeName(rs.getString("name"));
				configAddAttrList.add(configAddAList);
										
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
		
		
		
		return configAddAttrList;
	}
			
	
	
	private String selectGetConfigAddAttribute = "SELECT CVTERM_ID, "
														 + "LOWER(NAME) AS NAME, CV_ID "
												 + "FROM "
												 		+ "cvterm "
												 + "WHERE cv_id = ? "
												 + "AND  LOWER(NAME) LIKE ? AND rownum < 1001 "
												 + "ORDER BY cvterm.name";

	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(GetConfigAddAttributesDaoImpl.class);
}
