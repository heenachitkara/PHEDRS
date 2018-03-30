package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.exception.DaoException;

public interface GenericRegistryPatientDao 
{	
	List<GenericRegistryPatient> getGenericRegistryPatients(int registryId, String assignerId, String status) throws DaoException;
	List<GenericRegistryPatient> getGenericRegistryPatient(String mrn_, int registryID_) throws DaoException;
}
