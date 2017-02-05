package com.incomb.server.utils.passwords.exceptions;
import com.incomb.server.utils.passwords.PasswordUtil;

/**
 * RuntimeException Wrapper to show that there has been a HashingException
 * @see PasswordUtil
 */
public class HashingException extends RuntimeException {
	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = -7978923351476336426L;
	/**
	 * RuntimeException Wrapper to show that there has been a HashingException
	 * @param message
	 * @param cause
	 */
	public HashingException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
