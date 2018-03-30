package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.GetConfigAddAttributesList;
import edu.uab.registry.exception.DaoException;



public interface GetConfigAddAttributesDao {

	//List<GetConfigAddAttributesList> getConfigAddAttribute(Integer cv_id) throws DaoException;

	List<GetConfigAddAttributesList> getConfigAddAttribute(Integer cv_id, String attributeName) throws DaoException;

	

}
