package edu.uab.registry.dao;

import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.domain.RegistryPatientDemographics;
import edu.uab.registry.exception.DaoException;

public interface RegistryPatientDemographicsDao 
{
	void get(RegistryPatientDemographics demographics_, String mrn_, int registryID_, GenericRegistryPatient registryPatient_) throws DaoException;
}
