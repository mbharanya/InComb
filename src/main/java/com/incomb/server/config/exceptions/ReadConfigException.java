package com.incomb.server.config.exceptions;

/**
 * Simple Wrapper for {@link RuntimeException} to show that the has been an Exception reading the Configuration
 */
public class ReadConfigException extends RuntimeException {
	/**
	 * Generated
	 */
	private static final long serialVersionUID = 2079202266624176481L;

	public ReadConfigException(final Exception e) {
		super(e);
	}

	public ReadConfigException(final Exception e, final String message) {
		super(message, e);
	}
}
