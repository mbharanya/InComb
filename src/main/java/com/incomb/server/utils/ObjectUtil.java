package com.incomb.server.utils;

/**
 * This class is used for basic object operations.
 *
 */
public class ObjectUtil {
	
	/**
	 * Checks if the given <code>object</code> is <code>null</code>.<br>
	 * If it is, a new {@link NullPointerException} is thrown with the given message.
	 * @param object - Object to check
	 * @param message - Message for the {@link NullPointerException}
	 * @return The given object if it isn't null.
	 */
	public static <T> T assertNotNull(final T object, final String message) {
		if (object == null) { throw new NullPointerException(message); }
		return object;
	}
	
}
