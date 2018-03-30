package edu.uab.registry.dao;

import edu.uab.registry.domain.RegistryEncounterInsuranceList;
import edu.uab.registry.exception.DaoException;

import java.util.List;

public interface EncounterInsuranceListDao {
	
	List<RegistryEncounterInsuranceList> getEncounterInsurance(String hqFinNum)  throws DaoException;

}
