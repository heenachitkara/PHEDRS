package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.RegConfigEncounterAttributesList;

import edu.uab.registry.exception.DaoException;

public interface RegConfigEncounterAttributesDao {
	
	
	List<RegConfigEncounterAttributesList> getRegConfigEncounterAttr(int registryID_)  throws DaoException;
	
	
	

}
