package com.incomb.server.model;

import java.io.Serializable;
import java.util.Locale;

import com.incomb.server.utils.LocaleUtil;

/**
 * This class holds a {@link Locale} in which the {@link User}
 * wants to read {@link Content}s.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link UserLocale}.</p>
 */
public class UserLocale implements Serializable {

	private static final long serialVersionUID = 2059039880;

	/**
	 * The id of the {@link User} who wants to read in the {@link Locale}.
	 */
	private long userId;

	/**
	 * The {@link Locale} in which the {@link User} wants to read {@link Content}s.
	 */
	private String locale;

	/**
	 * Constructs a new object with the default properties.
	 */
	public UserLocale() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param userId the id of the {@link User} who wants to read in the {@link Locale}.
	 * @param locale the {@link Locale} in which the {@link User} wants to read {@link Content}s.
	 */
	public UserLocale(final long userId, final String locale) {
		this.userId = userId;
		this.locale = locale;
	}

	/**
	 * @return the id of the {@link User} who wants to read in the {@link Locale}.
	 */
	public long getUserId() {
		return this.userId;
	}

	/**
	 * Sets the id of the {@link User} who wants to read in the {@link Locale}.
	 * @param userId the id of the {@link User} who wants to read in the {@link Locale}.
	 */
	public void setUserId(final long userId) {
		this.userId = userId;
	}

	/**
	 * @return the {@link Locale} in which the {@link User} wants to read {@link Content}s.
	 */
	public Locale getLocale() {
		return LocaleUtil.toLocale(this.locale);
	}

	/**
	 * Sets the {@link Locale} in which the {@link User} wants to read {@link Content}s.
	 * @param locale the {@link Locale} in which the {@link User} wants to read {@link Content}s.
	 */
	public void setLocale(final String locale) {
		this.locale = locale;
	}

	/**
	 * Sets the {@link Locale} in which the {@link User} wants to read {@link Content}s.
	 * @param locale the {@link Locale} in which the {@link User} wants to read {@link Content}s.
	 */
	public void setLocaleObj(final Locale locale) {
		this.locale = locale.toString();
	}
}
