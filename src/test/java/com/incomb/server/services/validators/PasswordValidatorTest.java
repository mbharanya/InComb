package com.incomb.server.services.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.incomb.server.ATest;

public class PasswordValidatorTest extends ATest {
	private static final String[] COMPLIANT_PASSWORDS = {"Abc123--K$k", "Ծ [disapA1rove] ಠ~ಠ [hrm…] ఠ_ఠ [o rly?] ಠ_ರೃ [", null, ""};
	private static final String[] INVALID_PASSWORDS = {"a", "abc", "abc12", "Lorem_ipsum_dolor_sit_amet,_nonummy_ligula_volutpat_hac_integer_nonummy._Suspendisse_ultricies,_congue_etiam_tellus,_erat_libero,_nulla_eleifend,_mauris_pellentesque._Suspendisse_integer_praesent_vel,_integer_gravida_mauris,_fringilla_vehicula_lacinia_non1"};
	private final PasswordValidator validator = new PasswordValidator();

	@Test
	public void positiveTest() {
		for (final String pw : COMPLIANT_PASSWORDS) {
			assertTrue(validator.isValid(pw));
		}
	}

	@Test
	public void negativeTest() {
		for (final String pw : INVALID_PASSWORDS) {
			assertFalse("Password: "+pw,validator.isValid(pw));
		}
	}
}
