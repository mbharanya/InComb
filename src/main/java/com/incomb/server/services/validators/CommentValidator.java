package com.incomb.server.services.validators;

import org.apache.commons.lang3.StringUtils;
/**
 * Checks if a comment is valid according to the configuration
 */
public class CommentValidator extends AValidator<String>{

	/**
	 * Checks if a comment is valid according to the configuration
	 * <br /><br /><b>Note: </b> Field is also valid if it is null or blank
	 * <br />Also set {@link RequiredValidator} for to validate mandantory fields
	 */
	@Override
	public boolean isValid(final String comment) {
		if (StringUtils.isBlank(comment)) {
			return true;
		}

		if (comment.length() < c.getIntProperty("validation.comment.minLength") || comment.length() > c.getIntProperty("validation.comment.maxLength")) {
			return false;
		}
		return true;
	}
}
