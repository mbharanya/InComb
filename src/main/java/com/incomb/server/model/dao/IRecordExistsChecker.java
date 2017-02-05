package com.incomb.server.model.dao;

/**
 * An implementation of this interface checks if a data record
 * exists already in its data.
 */
public interface IRecordExistsChecker {

	/**
	 * Checks if the given value exists already in the field with
	 * the given field name in the data.
	 *
	 * The value has to be in the right type for this {@link IRecordExistsChecker}.
	 * The value must be compared with another using {@link Object#equals(Object)}.
	 *
	 * @param fieldName the field in which the value should exist.
	 * @param value the value to check.
	 * @return <code>true</code> if a record exists already.
	 */
	boolean recordExists(String fieldName, Object value);
}
