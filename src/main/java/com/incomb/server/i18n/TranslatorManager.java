package com.incomb.server.i18n;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.incomb.server.config.Config;
import com.incomb.server.utils.ConfigUtil;
import com.incomb.server.utils.LocaleUtil;

/**
 * Manages the {@link Translator} instances.
 */
public class TranslatorManager {

	/**
	 * The location in the file system where the translation json files exists.
	 */
	private static final String FILE_LOCATION = ConfigUtil.getDocBase() + "WEB-INF/translations/";

	/**
	 * Contains all created {@link Translator} instances. The key is the language of the {@link Locale}.
	 */
	private static final Map<String, Translator> TRANSLATORS = new HashMap<String, Translator>();

	/**
	 * Returns the {@link Translator} for the given {@link Locale}.
	 * If no translation file exists the {@link Translator} for the default
	 * locale ({@value LocaleUtil#DEFAULT_LOCALE}) will be returned.
	 * @param locale the {@link Locale} to get the {@link Translator} for.
	 * @return the {@link Translator}.
	 */
	public static Translator getTranslator(final Locale locale) {
		final Translator translator = internalGetTranslator(locale);

		if(translator != null) {
			return translator;
		}

		return internalGetTranslator(LocaleUtil.DEFAULT_LOCALE);
	}

	/**
	 * Returns the {@link Translator} for the default locale ({@value LocaleUtil#DEFAULT_LOCALE}).
	 */
	static Translator getDefaultTranslator() {
		return getTranslator(LocaleUtil.DEFAULT_LOCALE);
	}

	/**
	 * Returns the {@link Translator} for the given {@link Locale}.
	 * If the instance doesn't exist yet than it will created from the json file.
	 * @param locale the {@link Locale} to get the {@link Translator} for.
	 * @return the {@link Translator} or null if no {@link Translator} for the {@link Locale} exists.
	 */
	private static Translator internalGetTranslator(final Locale locale) {
		Translator translator = TRANSLATORS.get(locale.getLanguage());
		if(translator != null) {
			return translator;
		}

		final File file = new File(FILE_LOCATION, locale.getLanguage() + ".json");
		if(!file.exists()) {
			return null;
		}

		final Config data = new Config(file);
		translator = new Translator(locale, data);
		TRANSLATORS.put(locale.getLanguage(), translator);
		return translator;
	}

	/**
	 * Returns the translation for the given key and fills the placeholders
	 * with the given values.
	 * @param locale the {@link Locale} to get the translation for.
	 * @param key the key to find the translations.
	 * @param values optional placeholder values.
	 * @return the translation.
	 */
	public static String translate(final Locale locale,
			final String key, final Object... values) {
		return getTranslator(locale).get(key, values);
	}
}
