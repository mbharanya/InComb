package com.incomb.server.services.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.incomb.server.ATest;

public class CommentValidatorTest extends ATest {
	private static final String[] COMPLIANT_VALUES = {"Hello", "", null};
	private static final String[] INVALID_VALUES = {"a", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy ei"};
	private final CommentValidator validator = new CommentValidator();

	@Test
	public void positiveTest() {
		for (final String value : COMPLIANT_VALUES) {
			assertTrue(validator.isValid(value));
		}
	}

	@Test
	public void negativeTest() {
		for (final String value : INVALID_VALUES) {
			assertFalse(validator.isValid(value));
		}
	}
}
