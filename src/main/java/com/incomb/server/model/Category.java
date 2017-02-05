package com.incomb.server.model;

import java.io.Serializable;
import java.util.Locale;

import com.incomb.server.i18n.Translator;
import com.incomb.server.i18n.TranslatorManager;
import com.incomb.server.model.dao.CategoryDao;

/**
 * This represents a category assigned to a {@link Module}.
 * A category has assigned {@link Content}s.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link CategoryDao}.</p>
 */
public class Category implements Serializable {

	private static final long serialVersionUID = 1627078719;

	/**
	 * The id of this {@link Category}.
	 */
	private int id;

	/**
	 * The key to get the localized name with a {@link Translator}.
	 */
	private String nameKey;

	/**
	 * The absolute path to the image.
	 */
	private String imagePath;

	/**
	 * The id of the assigned {@link Module}.
	 */
	private int moduleId;

	/**
	 * Constructs a new object with the default properties.
	 */
	public Category() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param id the id of this {@link Category}.
	 * @param nameKey the key to get the localized name with a {@link Translator}.
	 * @param imagePath the absolute path to the image.
	 * @param moduleId the id of the assigned {@link Module}.
	 */
	public Category(final int id, final String nameKey, final String imagePath, final int moduleId) {
		this.id = id;
		this.nameKey = nameKey;
		this.imagePath = imagePath;
		this.moduleId = moduleId;
	}

	/**
	 * @return the id of this {@link Category}.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id of this {@link Category}.
	 * @param id the id of this {@link Category}.
	 */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * Returns the localized name of this {@link Category} for the given locale.
	 * @param locale the {@link Locale} to get the translated name.
	 * @return the localized name.
	 */
	public String getName(final Locale locale) {
		return TranslatorManager.translate(locale, this.nameKey);
	}

	/**
	 * @return the key to get the localized name with a {@link Translator}.
	 */
	public String getNameKey() {
		return this.nameKey;
	}

	/**
	 * Sets the key to get the localized name with a {@link Translator}.
	 * @param nameKey the key to get the localized name with a {@link Translator}.
	 */
	public void setNameKey(final String nameKey) {
		this.nameKey = nameKey;
	}

	/**
	 * @return the absolute path to the image.
	 */
	public String getImagePath() {
		return this.imagePath;
	}

	/**
	 * Sets the absolute path to the image.
	 * @param imagePath the absolute path to the image.
	 */
	public void setImagePath(final String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * @return the id of the assigned {@link Module}.
	 */
	public int getModuleId() {
		return moduleId;
	}

	/**
	 * Sets the id of the assigned {@link Module}.
	 * @param moduleId the id of the assigned {@link Module}.
	 */
	public void setModuleId(final int moduleId) {
		this.moduleId = moduleId;
	}
}
