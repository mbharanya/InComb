package com.incomb.server.services.providers.model;

import com.incomb.server.model.Provider;

/**
 * Simple bean for Providers to communicate with the user
 */
public class ProviderModel {

	/**
	 * The id of the {@link Provider}.
	 */
	private int id;

	/**
	 * The name of the {@link Provider}.
	 */
	private String name;

	/**
	 * The absolute path to an image of the {@link Provider}.
	 */
	private String imagePath;

	/**
	 * The URL to the website of this {@link Provider}.
	 */
	private String website;

	/**
	 * Constructs a new object with the default properties.
	 */
	public ProviderModel() {

	}

	/**
	 * Constructs a new object with the properties of the given {@link Provider}.
	 * @param provider the {@link Provider} to copy the properties from.
	 */
	public ProviderModel(final Provider provider) {
		this(provider.getId(), provider.getName(), provider.getImagePath(), provider.getWebsite());
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param id the id of the {@link Provider}.
	 * @param name the name of the {@link Provider}.
	 * @param imagePath the absolute path to an image of the {@link Provider}.
	 * @param website the URL to the website of this {@link Provider}.
	 */
	public ProviderModel(final int id, final String name, final String imagePath, final String website) {
		this.id = id;
		this.name = name;
		this.imagePath = imagePath;
		this.website = website;
	}

	/**
	 * @return the id of the {@link Provider}.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id of the {@link Provider}.
	 * @param id the id of the {@link Provider}.
	 */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * @return the name of the {@link Provider}.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the {@link Provider}.
	 * @param name the name of the {@link Provider}.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the absolute path to an image of the {@link Provider}.
	 */
	public String getImagePath() {
		return this.imagePath;
	}

	/**
	 * Sets the absolute path to an image of the {@link Provider}.
	 * @param imagePath the absolute path to an image of the {@link Provider}.
	 */
	public void setImagePath(final String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * @return the URL to the website of this {@link Provider}.
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * Sets the URL to the website of this {@link Provider}.
	 * @param website the URL to the website of this {@link Provider}.
	 */
	public void setWebsite(final String website) {
		this.website = website;
	}
}
