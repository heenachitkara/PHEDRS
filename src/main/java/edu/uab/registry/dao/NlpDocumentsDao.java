package edu.uab.registry.dao;

import java.util.List;
import edu.uab.registry.domain.NlpShowDocument;
import edu.uab.registry.exception.DaoException;

public interface NlpDocumentsDao 
{
	String getCvtermPropValue(String registryID ) throws DaoException;
    List<NlpShowDocument> getNlpShowDocument(String doc_id, String cvTermPropValue) throws DaoException;
	
	
}
