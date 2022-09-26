package com.shizu.linktree.services.exception;

public class AlreadyExistsException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public AlreadyExistsException(String msg) {
		super(msg);
	}
}
