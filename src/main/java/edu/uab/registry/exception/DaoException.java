package edu.uab.registry.exception;

public class DaoException extends Throwable
{
	public DaoException() {
		super();
	}
	
	public DaoException(String msg) {
		super(msg);
	}
	
	public DaoException(String msg, Throwable th) {
		super(msg, th);
	}
}
