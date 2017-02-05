package com.incomb.server.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Locale;

import com.incomb.server.model.dao.NewsDao;

/**
 * This class represents a basic content which is assigned to a {@link Category}
 * and a {@link Provider}. There can be sub classes of this to provide more
 * type specific data.
 *
 * <p>This structure is in the database too.
 * To operate with the database use (at the moment) {@link NewsDao}.</p>
 */
public class Content implements Serializable {

	private static final long serialVersionUID = -1214154860;

	/**
	 * The id of this {@link Content}.
	 */
	private long id;

	/**
	 * The id of the assigned {@link Provider}.
	 */
	private int providerId;

	/**
	 * The id of the assigned {@link Category}.
	 */
	private int categoryId;

	/**
	 * The title of this {@link Content}.
	 */
	private String title;

	/**
	 * The {@link Locale} in which the texts of this {@link Content} were written.
	 */
	private String locale;

	/**
	 * The text of this {@link Content}.
	 */
	private String text;

	/**
	 * The time when this {@link Content} was published.
	 */
	private Timestamp publishDate;

	/**
	 * This is <code>true</code> when this {@link Content} was successfully indexed.
	 */
	private boolean indexed = false;

	/**
	 * Constructs a new object with the default properties.
	 */
	public Content() { }

	/**
	 * Constructs a new object with the given properties.
	 * @param id the id of this {@link Content}.
	 * @param providerId the id of the assigned {@link Provider}.
	 * @param categoryId the id of the assigned {@link Category}.
	 * @param title the title of this {@link Content}.
	 * @param locale the {@link Locale} in which the texts of this {@link Content} were written.
	 * @param text the text of this {@link Content}.
	 * @param publishDate the time when this {@link Content} was published.
	 */
	public Content(final long id, final int providerId, final int categoryId,
			final String title, final Locale locale, final String text, final Timestamp publishDate) {
		this.id = id;
		this.providerId = providerId;
		this.categoryId = categoryId;
		this.title = title;
		setLocaleObj(locale);
		this.text = text;
		this.publishDate = publishDate;
	}

	/**
	 * @return the id of this {@link Content}.
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Alias of {@link #getId()}. Used for jooq.
	 * @return the id of this {@link Content}.
	 */
	public long getContentId() {
		return getId();
	}

	/**
	 * Sets the id of this {@link Content}.
	 * @param id the id of this {@link Content}.
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * @return the id of the assigned {@link Provider}.
	 */
	public int getProviderId() {
		return this.providerId;
	}

	/**
	 * Sets the id of the assigned {@link Provider}.
	 * @param providerId the id of the assigned {@link Provider}.
	 */
	public void setProviderId(final int providerId) {
		this.providerId = providerId;
	}

	/**
	 * @return the id of the assigned {@link Category}.
	 */
	public int getCategoryId() {
		return this.categoryId;
	}

	/**
	 * Sets the id of the assigned {@link Category}.
	 * @param categoryId the id of the assigned {@link Category}.
	 */
	public void setCategoryId(final int categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the title of this {@link Content}.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the title of this {@link Content}.
	 * @param title the title of this {@link Content}.
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return the {@link Locale} in which the texts of this {@link Content} were written.
	 */
	public Locale getLocale() {
		return new Locale(locale);
	}

	/**
	 * Sets the {@link Locale} in which the texts of this {@link Content} were written.
	 * @param locale the {@link Locale} in which the texts of this {@link Content} were written.
	 */
	public void setLocale(final String locale) {
		this.locale = locale;
	}

	/**
	 * Sets the {@link Locale} in which the texts of this {@link Content} were written.
	 * @param locale the {@link Locale} in which the texts of this {@link Content} were written.
	 */
	public void setLocaleObj(final Locale locale) {
		if (locale != null) {
			this.locale = locale.getLanguage();

		} /*else {
			this.locale = LocaleUtil.DEFAULT_LOCALE.getLanguage();
		}*/
	}

	/**
	 * @return the text of this {@link Content}.
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Sets the text of this {@link Content}.
	 * @param text the text of this {@link Content}.
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * @return the time when this {@link Content} was published.
	 */
	public Timestamp getPublishDate() {
		return publishDate;
	}

	/**
	 * Sets the time when this {@link Content} was published.
	 * @param publishDate the time when this {@link Content} was published.
	 */
	public void setPublishDate(final Timestamp publishDate) {
		this.publishDate = publishDate;
	}

	/**
	 * @return this is <code>true</code> when this {@link Content} was successfully indexed.
	 */
	public boolean isIndexed() {
		return indexed;
	}

	/**
	 * Sets if the {@link Content} was successfully indexed or not.
	 * @param indexed is <code>true</code> when this {@link Content} was successfully indexed.
	 */
	public void setIndexed(final boolean indexed) {
		this.indexed = indexed;
	}
}
