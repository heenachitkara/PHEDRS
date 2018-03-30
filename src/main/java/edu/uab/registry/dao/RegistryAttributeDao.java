package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.RegistryAttributeList;
import edu.uab.registry.exception.DaoException;

public interface RegistryAttributeDao {
	
	List<RegistryAttributeList> getRegistryAttribute(int registryId) throws DaoException;

}
