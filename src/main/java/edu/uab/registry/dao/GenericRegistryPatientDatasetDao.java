package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.exception.DaoException;

public interface GenericRegistryPatientDatasetDao 
{	
	List<GenericRegistryPatient> getGenericRegistryPatientsDataset(int registryId, String datasetName, String datasetStatus) throws DaoException;
}
