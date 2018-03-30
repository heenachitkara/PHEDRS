package edu.uab.registry.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.sun.org.omg.CORBA.ExcDescriptionSeqHelper;
import edu.uab.registry.util.Utilities;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uab.registry.dao.RegistryPatientAttributesDao;
import edu.uab.registry.domain.RegistryPatientAttribute;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.Constants;
import edu.uab.registry.util.StopWatch;

public class RegistryPatientAttributesDaoImpl implements RegistryPatientAttributesDao
{
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}
	
	public List <RegistryPatientAttribute> getRegistryPatientAttributes(String mrn_, int registryID_) throws DaoException
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		DataSource ds = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List <RegistryPatientAttribute> registryPatientAttrList = new ArrayList<RegistryPatientAttribute>();
		try {
			// Validate the input parameters.
			if (StringUtils.isNullOrEmpty(mrn_)) { throw new Exception("Invalid MRN"); }
			if (registryID_ < 1) {throw new Exception("Invalid registry ID"); }

			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();			
			String sql = buildSQLStatement(registryID_);
			ps = conn.prepareStatement(sql);
			ps.setString(1, mrn_);
			ps.setInt(2, registryID_);
			

			rs = ps.executeQuery();
			while(rs.next()) {
				RegistryPatientAttribute rpa = new RegistryPatientAttribute();
				rpa.setName(rs.getString("name"));
				rpa.setMrn(mrn_);
				rpa.setRegsitry_id(rs.getInt("registry_id"));
				rpa.setRegistry_patient_cvterm_id(rs.getInt("registry_patient_cvterm_id"));
				rpa.setRegistry_patient_id(rs.getInt("registry_patient_id"));
				rpa.setStatus_assignment_date(rs.getString("status_assignment_date"));
				rpa.setAnnotator_id(rs.getInt("annotator_id"));
				rpa.setAnnotation_date(rs.getString("annotation_date"));
				rpa.setAnnotator_comment(rs.getString("annotator_comment"));
				rpa.setIs_valid(rs.getString("is_valid"));
				rpa.setStart_assignment_date(rs.getString("start_assignment_date"));
				rpa.setEnd_assignment_date(rs.getString("end_assignment_date"));
				registryPatientAttrList.add(rpa);
			}

		} catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			Utilities.closeDbResources(conn, ps, rs);
		}

		logger.debug("Number of Registry Patient Attrs :" + registryPatientAttrList.size());
		logger.info("Log.ElapsedTime.dao.getRegistryPatientAttributes=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");

		return registryPatientAttrList;
	}
	
	public static void main(String[] args) 
	{
	}
	
	private String buildSQLStatement(int registryId)
	{
		String sql = Constants.select_RegistryPatientAtributes_SQL;
		if (registryId == 0) {
			// do nothing
		} else {
			sql = sql + "AND rp.registry_id = ? ";
		}		
		return sql;
	}
	 
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(RegistryPatientAttributesDaoImpl.class);
}
