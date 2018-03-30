package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.RegConfigPatientAttributesList;
import edu.uab.registry.exception.DaoException;

//This is for pulling registry patient attributes for configure registry section. 
//Not to be confused with the RegistryPatientAttributesDao which is used for different purpose
public interface RegConfigPatientAttributesDao {
	
	List<RegConfigPatientAttributesList> getRegConfigPatientAttr(int registryID_)  throws DaoException;

}
