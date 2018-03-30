package edu.uab.registry.dao;

import edu.uab.registry.exception.DaoException;

public interface PingDao 
{	
	String ping() throws DaoException;
}
