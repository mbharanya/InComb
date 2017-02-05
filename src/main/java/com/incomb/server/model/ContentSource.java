package com.incomb.server.model;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Locale;

import com.incomb.server.model.dao.ContentSourceDao;
import com.incomb.server.model.dao.FetchHistoryDao;

/**
 * This class represents a {@link Content} source. It's assigned
 * to a {@link Category} and a {@link Provider} and provides only
 * {@link Content}s in the defined {@link Locale}.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link ContentSourceDao}.</p>
 */
public class ContentSource implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The id of this {@link ContentSource}.
	 */
	private int id;

	/**
	 * The id of the assigned {@link Provider}.
	 */
	private int providerId;

	/**
	 * The id of the assigned {@link Category}.
	 */
	private int categoryId;

	/**
	 * The time in seconds after this {@link ContentSource} should be checked for new {@link Content}s.
	 */
	private int interval;

	/**
	 * The URL where the {@link Content}s can be fetched.
	 */
	private String url;

	/**
	 * The {@link Locale} in which the {@link Content}s of this {@link ContentSource} are written.
	 */
	private String locale;

	/**
	 * Constructs a new object with the default properties.
	 */
	public ContentSource() {}

	/**
	 * Constructs a new object with the given properties.
	 * @param id the id of this {@link ContentSource}.
	 * @param providerId the id of the assigned {@link Provider}.
	 * @param categoryId the id of the assigned {@link Category}.
	 * @param interval the time in seconds after this {@link ContentSource} should be checked for new {@link Content}s.
	 * @param url the URL where the {@link Content}s can be fetched.
	 * @param locale the {@link Locale} in which the {@link Content}s of this {@link ContentSource} are written.
	 */
	public ContentSource(final int id, final int providerId, final int categoryId, final int interval, final String url, final String locale) {
		this.id = id;
		this.providerId = providerId;
		this.categoryId = categoryId;
		this.interval = interval;
		this.url = url;
		this.locale = locale;
	}

	/**
	 * @return the id of this {@link ContentSource}.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id of this {@link ContentSource}.
	 * @param id the id of this {@link ContentSource}.
	 */
	public void setId(final int id) {
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
	 * @return the time in seconds after this {@link ContentSource} should be checked for new {@link Content}s.
	 */
	public int getInterval() {
		return this.interval;
	}

	/**
	 * Sets the time in seconds after this {@link ContentSource} should be checked for new {@link Content}s.
	 * @param interval the time in seconds after this {@link ContentSource} should be checked for new {@link Content}s.
	 */
	public void setInterval(final int interval) {
		this.interval = interval;
	}

	/**
	 * @return the URL where the {@link Content}s can be fetched.
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Sets the URL where the {@link Content}s can be fetched.
	 * @param url the URL where the {@link Content}s can be fetched.
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 * @return the {@link Locale} in which the {@link Content}s of this {@link ContentSource} are written.
	 */
	public Locale getLocale() {
		return new Locale(locale);
	}

	/**
	 * Sets the {@link Locale} in which the {@link Content}s of this {@link ContentSource} are written.
	 * @param locale the {@link Locale} in which the {@link Content}s of this {@link ContentSource} are written.
	 */
	public void setLocale(final String locale) {
		this.locale = locale;
	}

	/**
	 * Sets the {@link Locale} in which the {@link Content}s of this {@link ContentSource} are written.
	 * @param locale the {@link Locale} in which the {@link Content}s of this {@link ContentSource} are written.
	 */
	public void setLocaleObj(final Locale locale) {
		this.locale = locale.getLanguage();
	}

	/**
	 * Returns the last {@link FetchHistory} item of this {@link ContentSource}.
	 * @param connection the {@link Connection} to fetch the {@link FetchHistory} from the database. Can be null.
	 * @return the last {@link FetchHistory} or null if this {@link ContentSource} wasn't fetched yet.
	 */
	public FetchHistory getLastFetch(final Connection connection) {
		return new FetchHistoryDao(connection).getLastFetchHistoryFor(this);
	}
}
