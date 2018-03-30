package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.ControlledVocabulary;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.domain.CvTerms;

public interface CvTermsDao
{	
	List<CvTerms> getCvterms(String cvs) throws DaoException;

	List<ControlledVocabulary> getRegistryCVs(int registryID_) throws DaoException;
}
