package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.PatientCVList;
import edu.uab.registry.exception.DaoException;

public interface PatientCVDao {

	List<PatientCVList> getPatientAttributeCV(int registryID_)throws DaoException;

}
