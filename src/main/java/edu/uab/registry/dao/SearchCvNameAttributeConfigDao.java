package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.domain.SearchCvNameList;
import edu.uab.registry.exception.DaoException;

public interface SearchCvNameAttributeConfigDao {

	List<SearchCvNameList> searchByCVName(String cvName_) throws DaoException;
	
	
	 

}
