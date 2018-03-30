package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.RegistryPatientHistoryList;
import edu.uab.registry.exception.DaoException;

public interface RegistryPatientHistoryDao 
{		
	List<edu.uab.registry.domain.RegistryPatientHistory> getRegistryPatientHistory(int registryPatientId, int registryId) throws DaoException;
}
