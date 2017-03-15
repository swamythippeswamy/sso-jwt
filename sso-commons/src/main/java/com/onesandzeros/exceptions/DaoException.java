package com.onesandzeros.exceptions;

public class DaoException extends Exception {

	private static final long serialVersionUID = -4209038045236054500L;

	public DaoException(String message) {
		super(message);
	}

	public DaoException(String message, Throwable t) {
		super(message, t);
	}

}
