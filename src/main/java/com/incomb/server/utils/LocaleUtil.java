package com.incomb.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

/**
 * Little helper for internationalization things.
 */
public final class LocaleUtil {

	/**
	 * The default locale of the system is {@value Locale#ENGLISH}.
	 */
	public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	/**
	 * All supported {@link Locale}s of the system.
	 */
	private static final List<Locale> LOCALES = new ArrayList<Locale>() {

		private static final long serialVersionUID = -8536981042196947795L;

		{
			add(Locale.ENGLISH);
			add(Locale.GERMAN);
		}
	};

	/**
	 * Private constructor because the class contains only static helper methods.
	 * Does nothing.
	 */
	private LocaleUtil() {
	}

	/**
	 * Returns all supported {@link Locale}s of the system.
	 */
	public static List<Locale> getAllLocales() {
		return new ArrayList<>(LOCALES);
	}

	/**
	 * Converts the given locale {@link String} to a {@link Locale}.
	 * The case of the {@link String} is irrelevant unlike {@link LocaleUtils#toLocale(String)}.
	 *
	 * @param locale the {@link String} to convert.
	 * @return the converted {@link Locale}.
	 */
	public static Locale toLocale(String locale) {
		final String[] parts = locale.split("_");
		for(int i = 0; i < parts.length; i++) {
			switch (i) {
				case 0:
					locale = parts[0].toLowerCase();
					break;
				case 1:
					locale += "_" + parts[1].toUpperCase();
					break;
				case 2:
					locale += "_" + parts[2];
			}
		}

		return LocaleUtils.toLocale(locale);
	}
}
