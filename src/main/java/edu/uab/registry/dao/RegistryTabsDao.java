package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.RegistryTabsList;
import edu.uab.registry.exception.DaoException;

public interface RegistryTabsDao {

	List<RegistryTabsList> getRegistryTabs(int registryID_) throws DaoException;

}
