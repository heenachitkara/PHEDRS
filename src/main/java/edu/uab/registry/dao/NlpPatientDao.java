package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.NlpPatientHitsDocs;
import edu.uab.registry.exception.DaoException;

public interface NlpPatientDao 
{
	List<NlpPatientHitsDocs> getNlpPatients(String mrn) throws DaoException;
}
