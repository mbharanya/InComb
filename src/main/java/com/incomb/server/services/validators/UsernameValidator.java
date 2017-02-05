package com.incomb.server.services.validators;

import org.apache.commons.lang3.StringUtils;

/**
 * Validates Usernames
 */
public class UsernameValidator extends AValidator<String> {

	/**
	 * Checks if a the given username is valid (rules are configurable in the conf-json)
	 * <br /><br /><b>Note: </b> Field is also valid if it is null or blank
	 * <br />Also set {@link RequiredValidator} for to validate mandantory fields
	 */
	@Override
	public boolean isValid(final String username) {
		if (StringUtils.isBlank(username)) {
			return true;
		}

		if (username.length() < c.getIntProperty("validation.username.minLength") || username.length() > c.getIntProperty("validation.username.maxLength")){
			return false;
		}
		if (!c.getBooleanProperty("validation.username.allowSpecial")){
			if (username.matches(CONTAINS_SPECIALCHARS_REGEX)){
				return false;
			}
		}
		return true;
	}

}
