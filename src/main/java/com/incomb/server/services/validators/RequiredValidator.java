package com.incomb.server.services.validators;

import org.apache.commons.lang3.StringUtils;

/**
 * Is used on all fields that must be entered by the user
 */
public class RequiredValidator extends AValidator<Object> {
	/**
	 * Checks if the given Object is null, or if a String is blank
	 */
	@Override
	public boolean isValid(final Object field) {
		if (field instanceof String) {
			if (StringUtils.isBlank((String) field)) {
				return false;
			}
		}
		return field != null;
	}
}