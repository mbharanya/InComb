package com.incomb.server.services.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.incomb.server.ATest;

public class RequiredValidatorTest extends ATest {
	private static final Object[] COMPLIANT_VALUES = {"sdpkfsdpfjksdp", new Integer(12), new Double(12.13), new Float(23232.232), true, false, 0};
	private static final Object[] INVALID_VALUES = {"", null};
	private final RequiredValidator validator = new RequiredValidator();

	@Test
	public void positiveTest() {
		for (final Object value : COMPLIANT_VALUES) {
			assertTrue(validator.isValid(value));
		}
	}

	@Test
	public void negativeTest() {
		for (final Object value : INVALID_VALUES) {
			assertFalse(validator.isValid(value));
		}
	}
}
