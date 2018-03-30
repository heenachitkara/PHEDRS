package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.NlpPatientDao;
import edu.uab.registry.domain.NlpPatientHitsDocs;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class NlpPatientDaoImpl implements NlpPatientDao
{
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	public List<NlpPatientHitsDocs> getNlpPatients(String mrn) throws DaoException
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		List <NlpPatientHitsDocs> nlpPatientHitList = new ArrayList<NlpPatientHitsDocs>();
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			pstmt = conn.prepareStatement(selectNlpDocsHits_SQL);
			logger.debug("sql query :" + selectNlpDocsHits_SQL);
			pstmt.setString(1, mrn);
			logger.debug("sql parameters, mrn:" +  mrn);
			rs = pstmt.executeQuery();			
			while(rs.next()) {
				NlpPatientHitsDocs nlpPatient = new NlpPatientHitsDocs();
				nlpPatient.setMrn(mrn);
				nlpPatient.setNc_dos(rs.getDate("nc_dos"));								
				nlpPatient.setDocument_id(rs.getInt("nc_reportid"));								
				nlpPatient.setNc_type(rs.getString("nc_type"));
				nlpPatient.setNc_subtype(rs.getString("nc_subtype"));				
				nlpPatientHitList.add(nlpPatient);				
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
		logger.debug("Number of NLP Patients :" + nlpPatientHitList.size());
		logger.info("Log.ElapsedTime.dao.getNlpPatients=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return nlpPatientHitList;
	}

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
		try {			
			NlpPatientDao nlpDao = (NlpPatientDao) context.getBean("nlpPatientDao");
			String mrn = "625310";mrn = "1375470";
			List<NlpPatientHitsDocs> nlpPatients = nlpDao.getNlpPatients(mrn);		
			for (int i=0; i<nlpPatients.size(); i++) {
				NlpPatientHitsDocs nlp = nlpPatients.get(i);
				System.out.println("nc_dos:"+nlp.getNc_dos()+								   
								   ",document_id:"+nlp.getDocument_id()+								   
								   ",nc_type:"+nlp.getNc_type()+
								   ",nc_subtype:"+nlp.getNc_subtype()								   
								   );
			}
			System.out.println("Number of NLP Patients :" + nlpPatients.size());
		} catch(Throwable th) {
			th.printStackTrace();
		} finally {
			context.close();
		}
	}
	
	private static String formatDate(Date date)
	{
		String formattedDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		formattedDate = sdf.format(date);
		return formattedDate;
	}
	
	private String selectNlpDocsHits_SQL = 
			"SELECT DISTINCT " + 
				"nc_dos, nc_type, nc_subtype, nc_reportid " +  				 			
			"FROM " + 				 
				"medics.nlp_docs " +				
			"WHERE " +								
				"nc_dos   		>= '01-JAN-15' " + 
				"AND NC_PT_MRN  = ? " +
			"ORDER BY nc_dos DESC, nc_type " 
			;		
	

	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(NlpPatientDaoImpl.class);
}
