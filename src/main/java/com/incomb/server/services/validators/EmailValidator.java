package com.incomb.server.services.validators;

import org.apache.commons.lang3.StringUtils;
/**
 * Checks if an Email is valid
 */
public class EmailValidator extends AValidator<String> {
	/**
	 * Checks if an Email is valid with the {@link org.apache.commons.validator.routines.EmailValidator}
	 * <br /><br /><b>Note: </b> Field is also valid if it is null or blank
	 * <br />Also set {@link RequiredValidator} for to validate mandatory fields
	 */
	@Override
	public boolean isValid(final String email) {
		if (StringUtils.isBlank(email)) {
			return true;
		}
		return org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email);
	}

}
