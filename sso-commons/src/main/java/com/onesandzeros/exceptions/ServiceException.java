package com.onesandzeros.exceptions;

public class ServiceException extends Exception {

	private static final long serialVersionUID = -4082077641031102020L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable t) {
		super(message, t);
	}
}
