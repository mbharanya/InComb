package com.incomb.server.services.validators;

import org.apache.commons.lang3.StringUtils;


public class DisplayNameValidator extends AValidator<String> {
	/**
	 * Checks if a Displayname is valid according to the configuration
	 * <br /><br /><b>Note: </b> Field is also valid if it is null or blank
	 * <br />Also set {@link RequiredValidator} for to validate mandantory fields
	 */
	@Override
	public boolean isValid(final String displayName) {
		if (StringUtils.isBlank(displayName)) {
			return true;
		}

		if (displayName.length() < c.getIntProperty("validation.displayname.minLength") || displayName.length() > c.getIntProperty("validation.displayname.maxLength")) {
			return false;
		}
		if (!c.getBooleanProperty("validation.displayname.allowSpecial")) {
			if (displayName.matches(CONTAINS_SPECIALCHARS_REGEX)) {
				return false;
			}
		}
		return true;
	}

}
