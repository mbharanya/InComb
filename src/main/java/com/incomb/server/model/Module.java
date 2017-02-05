package com.incomb.server.model;

import java.io.Serializable;

import com.incomb.server.i18n.Translator;

/**
 * This class represents a {@link Module} which has assigned {@link Category}s.
 *
 * <p>This structure is in the database too.</p>
 */
public class Module implements Serializable {

	private static final long serialVersionUID = 908718307;

	/**
	 * The id of the {@link Module}.
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
	 * The absolute path to the template.
	 */
	private String templatePath;

	/**
	 * Constructs a new object with the default properties.
	 */
	public Module() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param id the id of the {@link Module}.
	 * @param nameKey the key to get the localized name with a {@link Translator}.
	 * @param imagePath the absolute path to the image.
	 * @param templatePath the absolute path to the template.
	 */
	public Module(final int id, final String nameKey, final String imagePath, final String templatePath) {
		this.id = id;
		this.nameKey = nameKey;
		this.imagePath = imagePath;
		this.templatePath = templatePath;
	}

	/**
	 * @return the id of the {@link Module}.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id of the {@link Module}.
	 * @param id the id of the {@link Module}.
	 */
	public void setId(final int id) {
		this.id = id;
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
	 * @return the absolute path to the template.
	 */
	public String getTemplatePath() {
		return this.templatePath;
	}

	/**
	 * Sets the absolute path to the template.
	 * @param templatePath the absolute path to the template.
	 */
	public void setTemplatePath(final String templatePath) {
		this.templatePath = templatePath;
	}
}
