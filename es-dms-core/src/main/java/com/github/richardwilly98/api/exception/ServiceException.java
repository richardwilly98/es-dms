package com.github.richardwilly98.api.exception;

@SuppressWarnings("serial")
public class ServiceException extends Exception {
	public ServiceException() {}
	public ServiceException(String message) {
		super(message);
	}

}
