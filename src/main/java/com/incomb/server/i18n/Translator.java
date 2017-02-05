package com.incomb.server.i18n;

import java.io.File;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.config.Config;

/**
 * A translator for a specific locale.
 */
public class Translator {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Translator.class);

	/**
	 * The {@link Locale} for the translator.
	 */
	private final Locale locale;

	/**
	 * The {@link Config} to retrieve the translations.
	 */
	private final Config data;

	/**
	 * Creates a new instance for a specific {@link Locale}.
	 * @param locale the {@link Locale} for the translator.
	 * @param data the {@link Config} to retrieve the translations.
	 */
	Translator(final Locale locale, final Config data) {
		this.locale = locale;
		this.data = data;
	}

	/**
	 * Returns the translation for the given key and fills the placeholders
	 * with the given values.
	 * @param key the key to find the translations.
	 * @param values optional placeholder values.
	 * @return the translation.
	 */
	public String get(final String key, final Object... values) {
		final String value = data.getStringProperty(key);

		if(value == null) {
			LOGGER.error("Can't find translation of key {} in locale {}.", key, locale.getLanguage());

			if(Config.getDefault().getBooleanProperty("debug")) {
				return "-" + key + "-";
			}

			return TranslatorManager.getDefaultTranslator().get(key, values);
		}

		return String.format(value, values);
	}

	/**
	 * @return the {@link File} where the {@link Translator} retrieves its translations.
	 */
	public File getFile() {
		return data.getFile();
	}
}
