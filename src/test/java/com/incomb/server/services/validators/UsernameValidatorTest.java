package com.incomb.server.services.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.incomb.server.ATest;

public class UsernameValidatorTest extends ATest {
	private static final String[] COMPLIANT_VALUES = {"Hello", null, ""};
	private static final String[] INVALID_VALUES = {"a", "Lorem_ipsum_dolor_sit_amet,_nonummy_ligula_volutpat_hac_integer_nonummy._Suspendisse_ultricies,_congue_etiam_tellus,_erat_libero,_nulla_eleifend,_mauris_pellentesque._Suspendisse_integer_praesent_vel,_integer_gravida_mauris,_fringilla_vehicula_lacinia_non1"};
	private final UsernameValidator validator = new UsernameValidator();

	@Test
	public void positiveTest() {
		for (final String value : COMPLIANT_VALUES) {
			assertTrue("Value: "+value, validator.isValid(value));
		}
	}

	@Test
	public void negativeTest() {
		for (final String value : INVALID_VALUES) {
			assertFalse("Value: "+value, validator.isValid(value));
		}
	}
}
