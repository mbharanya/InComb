package com.incomb.server.utils.passwords;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.incomb.server.utils.passwords.exceptions.HashingException;

/**
 * A utility class to hash passwords and check passwords vs hashed values. It
 * uses a combination of hashing and unique salt. The algorithm used is
 * PBKDF2WithHmacSHA1 which, although not the best for hashing password (vs.
 * bcrypt) is still considered robust and <a
 * href="http://security.stackexchange.com/a/6415/12614"> recommended by NIST
 * </a>. The hashed value has 256 bits.
 */
public final class PasswordUtil {

	private static final Random RANDOM = new SecureRandom();
	private static final int ITERATIONS = 10000;
	private static final int KEY_LENGTH = 256;

	/**
	 * static utility class
	 */
	private PasswordUtil() {
	}

	/**
	 * Returns a random salt to be used to hash a password.
	 *
	 * @return a 16 bytes random salt
	 */
	public static byte[] getNextSalt() {
		final byte[] salt = new byte[16];
		RANDOM.nextBytes(salt);
		return salt;
	}

	public static byte[] hash(final String password, final byte[] salt) {
		return hash(password.toCharArray(), salt);
	}

	/**
	 * Returns a salted and hashed password using the provided hash.<br>
	 * Note - side effect: the password is destroyed (the char[] is filled with
	 * zeros)
	 *
	 * @param password
	 *            the password to be hashed
	 * @param salt
	 *            a 16 bytes salt, ideally obtained with the getNextSalt method
	 *
	 * @return the hashed password with a pinch of salt
	 */
	public static byte[] hash(final char[] password, final byte[] salt) {
		final PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
		Arrays.fill(password, Character.MIN_VALUE);
		try {
			final SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			return skf.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new HashingException("Error while hashing a password: " + e.getMessage(), e);
		} finally {
			spec.clearPassword();
		}
	}

	public static boolean isExpectedPassword(final String password, final String salt, final String expectedHash) throws DecoderException {
		return isExpectedPassword(password.toCharArray(), Hex.decodeHex(salt.toCharArray()), Hex.decodeHex(expectedHash.toCharArray()));
	}
	/**
	 * Returns true if the given password and salt match the hashed value, false
	 * otherwise.<br>
	 * Note - side effect: the password is destroyed (the char[] is filled with
	 * zeros)
	 *
	 * @param password
	 *            the password to check
	 * @param salt
	 *            the salt used to hash the password
	 * @param expectedHash
	 *            the expected hashed value of the password
	 *
	 * @return true if the given password and salt match the hashed value, false
	 *         otherwise
	 */
	public static boolean isExpectedPassword(final char[] password, final byte[] salt, final byte[] expectedHash) {
		final byte[] pwdHash = hash(password, salt);
		Arrays.fill(password, Character.MIN_VALUE);
		if (pwdHash.length != expectedHash.length)
			return false;
		for (int i = 0; i < pwdHash.length; i++) {
			if (pwdHash[i] != expectedHash[i])
				return false;
		}
		return true;
	}

	/**
	 * Generates a random password of a given length, using letters and digits.
	 *
	 * @param length
	 *            the length of the password
	 *
	 * @return a random password
	 */
	public static String generateRandomPassword(final int length) {
		final StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			final int c = RANDOM.nextInt(62);
			if (c <= 9) {
				sb.append(String.valueOf(c));
			} else if (c < 36) {
				sb.append((char) ('a' + c - 10));
			} else {
				sb.append((char) ('A' + c - 36));
			}
		}
		return sb.toString();
	}
}