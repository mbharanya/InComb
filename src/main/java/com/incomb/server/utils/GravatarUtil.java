package com.incomb.server.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.model.User;

/**
 * Utility to create the gravatar profile image URL.
 * 
 * @see <a href="https://de.gravatar.com/">https://de.gravatar.com/</a>
 * @see <a
 *      href="https://de.gravatar.com/site/implement/">https://de.gravatar.com/site/implement/</a>
 */
public class GravatarUtil {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(GravatarUtil.class);

	/**
	 * The URL to the gravatar server with automatching protocol.
	 */
	private static final String URL_PREFIX = "//www.gravatar.com/avatar/";

	/**
	 * The query params for the image.
	 */
	private static final String URL_POSTFIX = "?d=mm";

	/**
	 * Private constructor because the class contains only static helper methods.
	 * Does nothing.
	 */
	private GravatarUtil() {
	}

	/**
	 * Generates the gravatar image URL for the given {@link User}.
	 * It generates a MD5 hash of the user's email and prefixes and postfixes it with
	 * {@link #URL_PREFIX} and {@link #URL_POSTFIX}.
	 * @param user the {@link User} to generate the image.
	 * @return the generated gravatar image URL
	 */
	public static String getGravatarImg(final User user) {
		ObjectUtil.assertNotNull(user, "user may not be null");
		ObjectUtil.assertNotNull(user.getEmail(), "email of user may not be null");

		try {
			final MessageDigest md5 = MessageDigest.getInstance("MD5");
			final byte[] mailHash = md5.digest(user.getEmail().toLowerCase().getBytes());
			return URL_PREFIX + Hex.encodeHexString(mailHash) + URL_POSTFIX;
		} catch (final NoSuchAlgorithmException e) {
			LOGGER.error("Can't generate gravatar image url because md5 algorithm was not found.", e);
		}

		return null;
	}
}
