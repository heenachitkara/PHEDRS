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

import edu.uab.registry.dao.RegistryPatientEncounterAttributesDao;
import edu.uab.registry.domain.RegistryPatientAttribute;
import edu.uab.registry.domain.RegistryPatientCode;
import edu.uab.registry.domain.RegistryPatientEncounterAttributes;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;

public class RegistryPatientEncounterAttributesDaoImpl implements RegistryPatientEncounterAttributesDao
{
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	public RegistryPatientEncounterAttributes getRegistryPatientEncounterAttributes(String mrn) throws DaoException
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt_codes = null, pstmt_attrs = null;
		ResultSet rs_codes = null, rs_attrs = null;
		RegistryPatientEncounterAttributes registryPatientEncounterAttributes = new RegistryPatientEncounterAttributes();
		List <RegistryPatientCode> registryPatientCodeList = new ArrayList<RegistryPatientCode>();	
		List <RegistryPatientAttribute> registryPatientAttrList = new ArrayList<RegistryPatientAttribute>();
		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			pstmt_codes = conn.prepareStatement(Constants.select_RegistryPatientCodes_SQL);
			logger.debug("codes sql query :" + Constants.select_RegistryPatientCodes_SQL);
			pstmt_codes.setString(1, mrn);
			logger.debug("codes sql parameters, mrn:" +  mrn);
			rs_codes = pstmt_codes.executeQuery();				
			while(rs_codes.next()) { 
				RegistryPatientCode rpc = new RegistryPatientCode();				
				rpc.setMrn(mrn);
				rpc.setCode_value(rs_codes.getString("accession"));
				rpc.setCode_type(rs_codes.getInt("db_id"));					
				rpc.setCode_assignment_date(rs_codes.getString("code_assignment_date"));
				rpc.setCode_description(rs_codes.getString("description"));		
				rpc.setName(rs_codes.getString("name"));
				registryPatientCodeList.add(rpc);
			}
			if (rs_codes != null) {
				rs_codes.close();				
				rs_codes = null;
			}
			if (pstmt_codes != null) {				
				pstmt_codes.close();
				pstmt_codes = null;
			}
			
			pstmt_attrs = conn.prepareStatement(Constants.select_RegistryPatientAtributes_SQL);
			pstmt_attrs.setString(1, mrn);
			logger.debug("attrs sql query " + Constants.select_RegistryPatientAtributes_SQL);
			logger.debug("attrs sql query params, mrn:" + mrn);
			rs_attrs = pstmt_attrs.executeQuery();
			while(rs_attrs.next()) {
				RegistryPatientAttribute rpa = new RegistryPatientAttribute();
				rpa.setName(rs_attrs.getString("name"));
				rpa.setMrn(mrn);					
				rpa.setStatus_assignment_date(rs_attrs.getString("status_assignment_date"));
				rpa.setAnnotator_id(rs_attrs.getInt("annotator_id"));
				rpa.setAnnotation_date(rs_attrs.getString("annotation_date"));
				rpa.setAnnotator_comment(rs_attrs.getString("annotator_comment"));
				rpa.setIs_valid(rs_attrs.getString("is_valid"));
				rpa.setStart_assignment_date(rs_attrs.getString("start_assignment_date"));
				rpa.setEnd_assignment_date(rs_attrs.getString("end_assignment_date"));
				registryPatientAttrList.add(rpa);
			}
			if (rs_attrs != null) {				
				rs_attrs.close();
				rs_attrs = null;
			}
			if (pstmt_attrs != null) {				
				pstmt_attrs.close();
				pstmt_attrs = null;
			}
			registryPatientEncounterAttributes.setRegistry_patient_code_list(registryPatientCodeList);
			registryPatientEncounterAttributes.setRegistry_patient_attribute_list(registryPatientAttrList);			
		} catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			if (rs_codes != null) {
				try {
					rs_codes.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
				rs_codes = null;
			}
			if (pstmt_codes != null) {
				try {
					pstmt_codes.close();
				} catch(SQLException sqe) {
					sqe.printStackTrace();
				}
				pstmt_codes = null;
			}
			if (rs_attrs != null) {
				try {
					rs_attrs.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
				rs_attrs = null;
			}
			if (pstmt_attrs != null) {
				try {
					pstmt_attrs.close();
				} catch(SQLException sqe) {
					sqe.printStackTrace();
				}
				pstmt_attrs = null;
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
		logger.debug("Number of Registry Patient Codes :" + registryPatientCodeList.size());
		logger.debug("Number of Registry Patient Attrs :" + registryPatientAttrList.size());
		logger.info("Log.ElapsedTime.dao.getRegistryPatientEncounterAttributes=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return registryPatientEncounterAttributes;
	}
	
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
		try {			
			RegistryPatientEncounterAttributesDao rpeaDao = (RegistryPatientEncounterAttributesDao) context.getBean("registryPatientEncounterAttributesDao");
			String mrn = "3053717";mrn = "3014973";mrn = "1387275";
			RegistryPatientEncounterAttributes registryPatientEncounterAttributes = rpeaDao.getRegistryPatientEncounterAttributes(mrn);
			List <RegistryPatientCode> registryPatientCodeList = registryPatientEncounterAttributes.getRegistry_patient_code_list();
			List <RegistryPatientAttribute> registryPatientAttrList = registryPatientEncounterAttributes.getRegistry_patient_attribute_list();
			for (int i=0; i<registryPatientCodeList.size(); i++) {
				RegistryPatientCode rpc = registryPatientCodeList.get(i);
				System.out.println("mrn:" + rpc.getMrn() + 
						   ", code_value:" + rpc.getCode_value() + 
						   ", code_type:" + rpc.getCode_type() +
						   ", code_date:" + rpc.getCode_assignment_date() +
						   ", code_desc:" + rpc.getCode_description()
						   );
			}
			System.out.println("Number of RegistryPatientCodes :" + registryPatientCodeList.size());
			
			for (int i=0; i<registryPatientAttrList.size(); i++) {
				RegistryPatientAttribute rpa = registryPatientAttrList.get(i);
				System.out.println("mrn:" + rpa.getMrn() +
								   ", name:" + rpa.getName() +
								   ", status_assignment_date:" + rpa.getStatus_assignment_date()
				);
			}
			System.out.println("Number of RegistryPatientCodes :" + registryPatientCodeList.size());
			System.out.println("Number of RegistryPatientAttrs :" + registryPatientAttrList.size());
		} catch (Throwable th) {
			th.printStackTrace();			
		} finally {
			context.close();
		}
	}
	 
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(RegistryPatientEncounterAttributesDaoImpl.class);
}
