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

import edu.uab.registry.dao.GetRoleNamesDao;
import edu.uab.registry.domain.GetRoleNamesList;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class GetRoleNamesDaoImpl implements GetRoleNamesDao {

	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	
	
	@Override
	public List<GetRoleNamesList> getRoleNames(Integer cv_id)throws DaoException {
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<GetRoleNamesList> roleNamesList = new ArrayList<GetRoleNamesList>();
		
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectGetRoleName_SQL);
			logger.debug("sql query :" + selectGetRoleName_SQL);
			System.out.println(selectGetRoleName_SQL);
			pstmt.setInt(1, cv_id);
			logger.debug("sql parameters, cvtermID_:" +  cv_id);
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				
				GetRoleNamesList    roleNList  = new GetRoleNamesList();
				roleNList.setRoleId(rs.getInt("cvterm_id"));
				roleNList.setRoleName(rs.getString("name"));
				roleNamesList.add(roleNList);
						
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
		return roleNamesList;
	}
	
    private String selectGetRoleName_SQL = "SELECT CVTERM_ID, NAME FROM CVTERM WHERE CV_ID = ?";
	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(GetRoleNamesDaoImpl.class);

}
