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

import edu.uab.registry.dao.PingDao;
import edu.uab.registry.domain.Registry;
import edu.uab.registry.domain.RegistryRole;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class PingDaoImpl implements PingDao 
{
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	public String ping() throws DaoException
	{	  
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		String pingResult = null;		
		try {			
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();					
			pstmt = conn.prepareStatement(ping_SQL);												
			logger.debug("sql query :" + ping_SQL);				
			rs = pstmt.executeQuery();						
			
			while(rs.next()) {				
				pingResult = rs.getString("dummy");				
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
		logger.info("Log.ElapsedTime.dao.ping=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return pingResult;
	}

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
			PingDao pingDao = (PingDao) context.getBean("pingDao");						 					
			String pingResult = pingDao.ping();			
			System.out.println("pingResult :" + pingResult);
		} catch(Throwable th) {
			th.printStackTrace();
		}
	}		
	
	private String ping_SQL = "SELECT dummy FROM DUAL ";		
									 	
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(PingDaoImpl.class);		
}
