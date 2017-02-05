package com.incomb.server.services.validators;

import com.incomb.server.model.dao.IRecordExistsChecker;
/**
 * Checks if a Record already exists in the database
 */
public class DuplicateValidator extends AValidator<Object> {
	private final IRecordExistsChecker dao;
	private final String fieldName;

	/**
	 * @param dao to use for the duplicate check
	 * @param fieldName to check
	 */
	public DuplicateValidator(final IRecordExistsChecker dao, final String fieldName) {
		this.dao = dao;
		this.fieldName = fieldName;
	}

	@Override
	/**
	 * Checks if the record already exists in the Database
	 * Executes the recordsExists method on the DAO with the given value
	 */
	public boolean isValid(final Object value) {
		return !dao.recordExists(fieldName, value);
	}

}
