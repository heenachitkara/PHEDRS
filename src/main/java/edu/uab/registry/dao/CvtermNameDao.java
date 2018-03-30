package edu.uab.registry.dao;

import java.util.List;

import edu.uab.registry.domain.CvtermNameList;
import edu.uab.registry.exception.DaoException;

public interface CvtermNameDao {

	List<CvtermNameList> getCvTermName(Integer cvtermID_) throws DaoException;

}
