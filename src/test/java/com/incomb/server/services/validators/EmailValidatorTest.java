package com.incomb.server.services.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.ATest;

public class EmailValidatorTest extends ATest {
	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailValidatorTest.class);
	
	private static final String[] COMPLIANT_VALUES = {"spam@trash.li", null, "", "incomb@mmc.codes"};
	private static final String[] INVALID_VALUES = {"a", "bla@foobar"};
	private final EmailValidator validator = new EmailValidator();

	@Test
	public void positiveTest() {
		for (final String value : COMPLIANT_VALUES) {
			assertTrue(validator.isValid(value));
		}
	}

	@Test
	public void negativeTest() {
		for (final String value : INVALID_VALUES) {
			LOGGER.debug("checking {} and is valid: {}", value, validator.isValid(value));
			assertFalse(validator.isValid(value));
		}
	}
}
