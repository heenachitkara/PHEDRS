package edu.uab.registry.dao;

import edu.uab.registry.domain.RegistryDetectionCodeList;
import edu.uab.registry.exception.DaoException;

public interface DetectionCodesDao 
{	
	RegistryDetectionCodeList getDetectionCodes(String mrn, int registryId) throws DaoException;
}
