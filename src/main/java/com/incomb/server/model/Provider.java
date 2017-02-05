package com.incomb.server.model;

import java.io.Serializable;

import com.incomb.server.content.parsing.ContentParser;
import com.incomb.server.content.parsing.rss.DefaultRssParser;
import com.incomb.server.model.dao.ProviderDao;

/**
 * This represents a provider which provides {@link Content}s.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link ProviderDao}.</p>
 */
public class Provider implements Serializable {

	private static final long serialVersionUID = -329240166;

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
	 * The Java class which handles the parsing of the {@link Content}s for this {@link Provider}.
	 * Must be an implementation of {@link ContentParser}.
	 * If <code>null</code> the {@link DefaultRssParser} will be used.
	 */
	private String parserClass;

	/**
	 * Constructs a new object with the default properties.
	 */
	public Provider() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param id the id of the {@link Provider}.
	 * @param name the name of the {@link Provider}.
	 * @param imagePath the absolute path to an image of the {@link Provider}.
	 * @param website the URL to the website of this {@link Provider}.
	 * @param parserClass the Java class which handles the parsing of the {@link Content}s for this {@link Provider}.
	 * 			Must be an implementation of {@link ContentParser}.
	 * 			If <code>null</code> the {@link DefaultRssParser} will be used.
	 */
	public Provider(final int id, final String name, final String imagePath, final String website, final String parserClass) {
		this.id = id;
		this.name = name;
		this.imagePath = imagePath;
		this.website = website;
		this.parserClass = parserClass;
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

	/**
	 * @return the Java class which handles the parsing of the {@link Content}s for this {@link Provider}.
	 * 		Must be an implementation of {@link ContentParser}.
	 * 		If <code>null</code> the {@link DefaultRssParser} will be used.
	 */
	public String getParserClass() {
		return parserClass;
	}

	/**
	 * Sets the Java class which handles the parsing of the {@link Content}s for this {@link Provider}.
	 * Must be an implementation of {@link ContentParser}.
	 * If <code>null</code> the {@link DefaultRssParser} will be used.
	 * @param parserClass the Java class which handles the parsing of the {@link Content}s for this {@link Provider}.
	 */
	public void setParserClass(final String parserClass) {
		this.parserClass = parserClass;
	}
}
