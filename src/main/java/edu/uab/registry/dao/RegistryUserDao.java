package edu.uab.registry.dao;

import edu.uab.registry.domain.RegistryUserList;
import edu.uab.registry.exception.DaoException;

import java.util.List;

public interface RegistryUserDao 
{

	List<RegistryUserList> getRegistryUsers(Integer registryID_) throws DaoException;
	/*List<RegistryUser> getRegistryUsers(int registryID_) throws DaoException;*/
}
