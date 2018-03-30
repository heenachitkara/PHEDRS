package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.RegistryPatient;
import edu.uab.registry.domain.RegistryPatientHistoryList;
import edu.uab.registry.exception.DaoException;

public interface RegistryPatientDao 
{	
	List<RegistryPatient> getRegistryPatients(int mrn, String name, String ownerId, String statuses, String beginDate, String endDate) throws DaoException;
}
