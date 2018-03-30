package edu.uab.registry.dao;

import edu.uab.registry.domain.RegistryPatientEncounterAttributes;
import edu.uab.registry.exception.DaoException;

public interface RegistryPatientEncounterAttributesDao 
{	
	RegistryPatientEncounterAttributes getRegistryPatientEncounterAttributes(String mrn) throws DaoException;
}
