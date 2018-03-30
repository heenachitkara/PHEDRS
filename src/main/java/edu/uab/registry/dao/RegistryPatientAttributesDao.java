package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.RegistryPatientAttribute;
import edu.uab.registry.exception.DaoException;

public interface RegistryPatientAttributesDao 
{	
	List<RegistryPatientAttribute> getRegistryPatientAttributes(String mrn, int registryId) throws DaoException;
}
