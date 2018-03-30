package edu.uab.registry.dao.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import edu.uab.registry.dao.NlpDocumentsDao;
import edu.uab.registry.domain.NlpShowDocument;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.StopWatch;

public class NlpDocumentsDaoImpl implements NlpDocumentsDao
{
	public void setDataSource(DataSource dataSource) 
	{		
		jdbcTemplate = new JdbcTemplate(dataSource);									
	}

	@Override
	public List<NlpShowDocument> getNlpShowDocument(String docId, String cvTermPropValue) throws DaoException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		List<NlpShowDocument> nlpShowDocs = new ArrayList<NlpShowDocument>();			
		ds = jdbcTemplate.getDataSource();
		String selectNlpDocsInfo_SQL = "SELECT * FROM "+
				"("+
				" SELECT"+
				" na.analysis_id, "+
				" rank() over (order by na.analysis_id desc) as rnk, "+
				" na.analysis_type, "+
				" docs.nc_clcont, "+
				" docs.nc_dos, "+
				" nhe.hit_text, "+
				" nhe.hit_start_char_index, "+
				" nhe.hit_stop_char_index, "+
				" nhe.concept_text, "+
				" nhe.is_negated "+
				" FROM medics.NLP_ANALYSIS na "+
				" JOIN medics.NLP_DOCSET nd ON (na.analysis_dataset = nd.docset_id AND analysis_pipeline_id="+cvTermPropValue+")"+
				" JOIN medics.NLP_DOCSET_MAP ndm ON (nd.docset_id=ndm.docset_id AND ndm.document_id = ?) "+
				" JOIN medics.NLP_DOCS docs ON (docs.nc_reportid=ndm.document_id) "+
				" LEFT JOIN medics.NLP_HITS_EXTENDED nhe ON (na.analysis_id=nhe.analysis_id AND nhe.document_id=ndm.document_id) "+
				" ORDER BY na.analysis_id DESC "+
				")"+
				" WHERE rnk=1";
		try (Connection conn = ds.getConnection();
				PreparedStatement pstmt1 = conn.prepareStatement(selectNlpDocsInfo_SQL)){
			pstmt1.setString(1, docId);	
			logger.info("Getting cvTermPropValue/document pipeline type "+cvTermPropValue+
					"in getNlpShowDocument for nc_reportid "+docId+" on url "+conn.getMetaData().getURL());
			try ( ResultSet rs1 = pstmt1.executeQuery()){	
				while(rs1.next()) {				
					NlpShowDocument nlpShowDocRecord = new NlpShowDocument();		
					nlpShowDocRecord.setNote_content(inputStreamAsString(rs1.getAsciiStream("nc_clcont")));	
					nlpShowDocRecord.setConcept_text(rs1.getString("concept_text"));
					nlpShowDocRecord.setNc_dos(rs1.getDate("nc_dos"));
					nlpShowDocRecord.setStart(rs1.getInt("hit_start_char_index"));
					nlpShowDocRecord.setStop(rs1.getInt("hit_stop_char_index"));
					nlpShowDocRecord.setIs_negated(rs1.getString("is_negated"));
					nlpShowDocRecord.setHitText(rs1.getString("hit_text"));
					nlpShowDocs.add(nlpShowDocRecord);
				}
			} catch(Throwable th) {
				throw new DaoException(th.getMessage(), th);
			} 
		} catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} 
		logger.info("Log.ElapsedTime.dao.getNlpDocsWithHits=" + stopWatch.getElapsedTimeInMilliSeconds() + "ms");
		return nlpShowDocs;
	}


	//Using string approach
	public String  getCvtermPropValue(String regID) throws DaoException
	{

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();						
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;

		String returnedValue = null;

		try {
			ds = jdbcTemplate.getDataSource();
			conn = ds.getConnection();
			pstmt2 = conn.prepareStatement(selectValueSQL);
			pstmt2.setString(1, regID);					

			rs2 = pstmt2.executeQuery();	
			//May be change the while loop to if(rs2.next())
			while(rs2.next()) {				
				returnedValue = rs2.getString("value");
				System.out.println("I am inside getCvtermPropValue method to get the cvtermPropValue");
				System.out.println(returnedValue);
			}

			System.out.println("INSIDE getCvtermPropValue before catch checking cvTermPropValue");
			System.out.println(returnedValue);
		} catch(Throwable th) {
			throw new DaoException(th.getMessage(), th);
		} finally {
			if (rs2 != null) {
				try {
					rs2.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
				rs2 = null;
			}
			if (pstmt2 != null) {
				try {
					pstmt2.close();
				} catch(SQLException sqe) {
					sqe.printStackTrace();
				}
				pstmt2 = null;
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


		return returnedValue;
	}



	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
			NlpDocumentsDao docDao = (NlpDocumentsDao) context.getBean("nlpDocumentsDao");			
			String docId = "12771596";
			/*List<NlpPatientHitsDocs> nlpDocRecs = docDao.getNlpDocsWithHits(docId);

			for (int i=0; i<nlpDocRecs.size(); i++) {
				NlpPatientHitsDocs nlpRecord = nlpDocRecs.get(i);
				System.out.println("Document:\n"+nlpRecord.getNote_content().substring(0, 100));
			}	*/													
		} catch(Throwable th) {
			th.printStackTrace();
		} finally {
			context.close();
		}
	}

	public String inputStreamAsString(InputStream stream) throws Exception 
	{
		BufferedReader quoteReader = new BufferedReader(new InputStreamReader(stream));
		char[] temp = new char[2048];
		int numberOfCharsRead = -1;
		StringBuffer sb = new StringBuffer();		
		while ((numberOfCharsRead = quoteReader.read(temp, 0, 2048)) > 0)
			sb.append(String.valueOf(temp, 0, numberOfCharsRead));		
		return sb.toString();
	}


	// The following SQL refers PHEDRS schema
	private String selectValueSQL = "SELECT "
			+ "value " +
			"FROM "
			+ "phedrs.cvtermprop "
			+ "WHERE "
			+ "cvterm_id = ? "
			+ "AND "
			+ "type_id = 3321";



	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger = LoggerFactory.getLogger(NlpDocumentsDaoImpl.class);



}













