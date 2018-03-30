package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.GetAllCVList;
import edu.uab.registry.exception.DaoException;

public interface GetAllCVDao {

	

	List<GetAllCVList> getAllCV() throws DaoException;

}
