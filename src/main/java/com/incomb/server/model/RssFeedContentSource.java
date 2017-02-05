package com.incomb.server.model;

import java.io.Serializable;

import com.incomb.server.model.dao.RssFeedContentSourceDao;

/**
 * This class represents a {@link ContentSource} where the {@link Content}s will
 * be parsed from an RSS feed.
 * At the moment there aren't any properties but in future there could be some
 * RSS feed specific.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link RssFeedContentSourceDao}.</p>
 */
public class RssFeedContentSource extends ContentSource implements Serializable {

	private static final long serialVersionUID = 1730500955;

	/**
	 * Constructs a new object with the default properties.
	 */
	public RssFeedContentSource() {
	}

	/**
	 * Constructs a new object with the given {@link ContentSource}.
	 * @param contentSource the properties to set
	 */
	public RssFeedContentSource(final ContentSource contentSource) {
		super(contentSource.getId(), contentSource.getProviderId(), contentSource.getCategoryId(), contentSource
				.getInterval(), contentSource.getUrl(), contentSource.getLocale().getLanguage());
	}

	/**
	 * Sets the properties of the given {@link ContentSource} to this {@link RssFeedContentSource}.
	 * @param contentSource the properties to set
	 */
	public void setSuperObject(final ContentSource contentSource) {
		setId(contentSource.getId());
		setProviderId(contentSource.getProviderId());
		setCategoryId(contentSource.getCategoryId());
		setInterval(contentSource.getInterval());
		setUrl(contentSource.getUrl());
		setLocaleObj(contentSource.getLocale());
	}

	/**
	 * Used by jooq.
	 * @return the id of this {@link ContentSource}.
	 * @see #getId()
	 */
	public int getContentSourceId() {
		return getId();
	}

	/**
	 * Sets the id of this {@link ContentSource}.
	 * Used by jooq.
	 * @param id the id of this {@link ContentSource}.
	 * @see #setId(int)
	 */
	public void setContentSourceId(final int contentSourceId) {
		setId(contentSourceId);
	}
}
