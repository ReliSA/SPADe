package com.assembla.exception;

public class AssemblaAPIException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AssemblaAPIException(String message) {
		super(message);
	}

	public AssemblaAPIException(String string, Throwable e) {
		super(string, e);
	}

}
