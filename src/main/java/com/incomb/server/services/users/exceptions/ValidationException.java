package com.incomb.server.services.users.exceptions;

/**
 * Simple Wrapper for {@link RuntimeException} to show that there has been an exception during validation
 */
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = -5095313197035357700L;

	public ValidationException(final String message, final Throwable t) {
		super(message, t);
	}
}
