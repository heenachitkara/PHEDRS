package edu.uab.registry.dao;

import edu.uab.registry.domain.RegistryEncounter;
import edu.uab.registry.exception.DaoException;

import java.util.List;

public interface RegistryEncountersDao 
{
	RegistryEncounter get(Integer encounterKey_, String mrn_, Integer registryID_) throws DaoException;
	List<RegistryEncounter> get(String mrn_, Integer registryID_) throws DaoException;
}
