package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.GetRoleNamesList;
import edu.uab.registry.exception.DaoException;

public interface GetRoleNamesDao {

	List<GetRoleNamesList> getRoleNames(Integer cv_id) throws DaoException;

}
