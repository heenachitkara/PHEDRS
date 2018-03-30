package edu.uab.registry.dao;

import edu.uab.registry.domain.CvMetaDataList;
import edu.uab.registry.exception.DaoException;

import java.util.List;

public interface CvMetaDataListDao 
{
	List<CvMetaDataList> getCvMetaData(int registryId,int attributeCvtermID) throws DaoException;
	
}
