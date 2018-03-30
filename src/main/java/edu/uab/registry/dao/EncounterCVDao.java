package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.EncounterCVList;
import edu.uab.registry.exception.DaoException;

public interface EncounterCVDao {

	List<EncounterCVList> getEncounterAttributeCV(int registryID_) throws DaoException;

}
