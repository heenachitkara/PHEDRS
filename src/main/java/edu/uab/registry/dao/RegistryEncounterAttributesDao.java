package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.RegistryEncounterAttribute;
import edu.uab.registry.domain.RegistryPatientCode;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.util.Constants.SimpleEncounterAttribute;

public interface RegistryEncounterAttributesDao 
{

	// Get encounter attributes associated with a particular encounter (possibly constrained by registry).
	List<RegistryEncounterAttribute> get(Integer encounterKey_,
										 Integer registryID_) throws DaoException;

	// NOTE: this doesn't really belong here but it can be moved at some point in the future.
	List<RegistryPatientCode> getPatientDiagnoses(String mrn, String type, int registryId) throws DaoException;

}
