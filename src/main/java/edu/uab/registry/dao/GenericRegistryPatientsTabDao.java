package edu.uab.registry.dao;

import java.util.List;
import org.slf4j.Logger;
import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.exception.DaoException;

public interface GenericRegistryPatientsTabDao 
{	
	List<GenericRegistryPatient> get(Logger logger_, Integer registryID_, String tabKey_) throws DaoException;
}
