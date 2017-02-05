package com.incomb.server.services.validators;

import com.incomb.server.config.Config;
/**
 * Abstract class with configuration and handy Regexes for all validators to use
 */
public abstract class AValidator<T> implements IValidator<T> {
	/**
	 * the configuration to use for all child classes
	 */
	protected final Config c = Config.getDefault();

	/**
	 * Matches if a String contains upper and lowercase letters
	 * It must at least contain one upper- and one lowercase letter
	 */
	protected static final String CONTAINS_UPPER_AND_LOWERCASE_REGEX = "^(?=.*[a-z])(?=.*[A-Z]).+$";

	/**
	 * Matches if the String is a number
	 */
	protected static final String CONTAINS_NUMBERS_REGEX = "(.*)\\d(.*)";

	/**
	 * Matches if a String contains non-alphanumerical characters
	 */
	protected static final String CONTAINS_SPECIALCHARS_REGEX = "(.*)[^A-Za-z0-9](.*)";
}
