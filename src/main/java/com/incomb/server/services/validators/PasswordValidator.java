package com.incomb.server.services.validators;

import org.apache.commons.lang3.StringUtils;

/**
 * Validate unhashed passwords if it is valid according to the rules in the Configuration
 */
public class PasswordValidator extends AValidator<String> {

	@Override
	/**
	 * Checks an unhashed password if it is valid according to the rules in the Configuration
	 * <br /><br /><b>Note: </b> Field is also valid if it is null or blank
	 * <br />Also set {@link RequiredValidator} for to validate mandantory fields
	 */
	public boolean isValid(final String password) {
		if (StringUtils.isBlank(password)) {
			return true;
		}

		if (password.length() < c.getIntProperty("validation.password.minLength") || password.length() > c.getIntProperty("validation.password.maxLength")){
			return false;
		}

		if (c.getBooleanProperty("validation.password.mixedCaseLetters")){
			if (!(password.matches(CONTAINS_UPPER_AND_LOWERCASE_REGEX))){
				return false;
			}
		}

		if (c.getBooleanProperty("validation.password.numbers")){
			if (!password.matches(CONTAINS_NUMBERS_REGEX)){
				return false;
			}
		}

		if (c.getBooleanProperty("validation.password.special")){
			if (!password.matches(CONTAINS_SPECIALCHARS_REGEX)){
				return false;
			}
		}
		return true;
	}

}
