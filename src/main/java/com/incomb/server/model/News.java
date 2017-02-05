package com.incomb.server.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Locale;

import com.incomb.server.model.dao.CategoryDao;
import com.incomb.server.model.dao.NewsDao;
import com.incomb.server.model.dao.ProviderDao;

/**
 * This class represents a news which is a {@link Content}. It provides
 * news specific properties.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link NewsDao}.</p>
 */
public class News extends Content implements Serializable {

	/**
	 * {@link News#getNewsGroupId()} returns this value if the news wasn't grouped yet.
	 */
	public static final long NEWSGROUPID_NOT_GROUPED_YET = -1;

	/**
	 * {@link News#getNewsGroupId()} returns this value if the news
	 * matches at the moment with no other {@link News}.
	 */
	public static final long NEWSGROUPID_NO_GROUP = 0;

	private static final long serialVersionUID = -111604080;

	/**
	 * The URL to the article on news page of the {@link Provider}.
	 */
	private String link;

	/**
	 * The URL to an image of this {@link News}.
	 */
	private String imageUrl;

	/**
	 * The width in pixels of the {@link #imageUrl}.
	 */
	private int imageWidth;

	/**
	 * The height in pixels of the {@link #imageUrl}.
	 */
	private int imageHeight;

	/**
	 * The id of a {@link NewsGroup} if the {@link News} is in one.
	 *
	 * <p>Other possible values:</p>
	 * <ul>
	 * 	<li>{@link #NEWSGROUPID_NOT_GROUPED_YET}</li>
	 * 	<li>{@link #NEWSGROUPID_NO_GROUP}</li>
	 * </ul>
	 */
	private long newsGroupId;

	/**
	 * Constructs a new object with the default properties.
	 */
	public News() { }

	/**
	 * Constructs a new object with the given properties.
	 * @param id the id of this {@link Content}.
	 * @param providerId the id of the assigned {@link Provider}.
	 * @param categoryId the id of the assigned {@link Category}.
	 * @param title the title of this {@link Content}.
	 * @param locale the {@link Locale} in which the texts of this {@link Content} were written.
	 * @param text the text of this {@link Content}.
	 * @param publishDate the time when this {@link Content} was published.
	 * @param link the URL to the article on news page of the {@link Provider}.
	 * @param imageUrl the URL to an image of this {@link News}.
	 * @param imageWidth the width in pixels of the {@link #imageUrl}.
	 * @param imageHeight the height in pixels of the {@link #imageUrl}.
	 * @param newsGroupId the id of a {@link NewsGroup} if the {@link News} is in one.
	 */
	public News(final long id, final int providerId, final int categoryId,
			final String title, final Locale locale, final String text, final Timestamp publishDate,
			final String link, final String imageUrl, final int imageWidth, final int imageHeight,
			final long newsGroupId) {

		super(id, providerId, categoryId, title, locale, text, publishDate);
		this.link = link;
		this.imageUrl = imageUrl;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.newsGroupId = newsGroupId;
	}

	/**
	 * @return the URL to the article on news page of the {@link Provider}.
	 */
	public String getLink() {
		return this.link;
	}

	/**
	 * Sets the URL to the article on news page of the {@link Provider}.
	 * @param link the URL to the article on news page of the {@link Provider}.
	 */
	public void setLink(final String link) {
		this.link = link;
	}

	/**
	 * @return the URL to an image of this {@link News}.
	 */
	public String getImageUrl() {
		return this.imageUrl;
	}

	/**
	 * Sets the URL to an image of this {@link News}.
	 * @param imageUrl the URL to an image of this {@link News}.
	 */
	public void setImageUrl(final String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * @return the width in pixels of the {@link #imageUrl}.
	 */
	public int getImageWidth() {
		return this.imageWidth;
	}

	/**
	 * Sets the width in pixels of the {@link #imageUrl}.
	 * @param imageWidth the width in pixels of the {@link #imageUrl}.
	 */
	public void setImageWidth(final int imageWidth) {
		this.imageWidth = imageWidth;
	}

	/**
	 * @return the height in pixels of the {@link #imageUrl}.
	 */
	public int getImageHeight() {
		return this.imageHeight;
	}

	/**
	 * Sets the height in pixels of the {@link #imageUrl}.
	 * @param imageHeight the height in pixels of the {@link #imageUrl}.
	 */
	public void setImageHeight(final int imageHeight) {
		this.imageHeight = imageHeight;
	}

	/**
	 * @return the id of a {@link NewsGroup} if the {@link News} is in one.
	 */
	public long getNewsGroupId() {
		return this.newsGroupId;
	}

	/**
	 * Sets the id of a {@link NewsGroup} if the {@link News} is in one.
	 * @param newsGroupId the id of a {@link NewsGroup} if the {@link News} is in one.
	 */
	public void setNewsGroupId(final long newsGroupId) {
		this.newsGroupId = newsGroupId;
	}

	/**
	 * Returns the assigned {@link Provider}.
	 * @param con the {@link Connection} to fetch the database.
	 * @return the assigned {@link Provider}.
	 */
	public Provider getProvider(final Connection con) {
		return new ProviderDao(con).getProvider(getProviderId());
	}

	/**
	 * Returns the assigned {@link Category}.
	 * @param con the {@link Connection} to fetch the database.
	 * @return the assigned {@link Category}.
	 */
	public Category getCategory(final Connection con) {
		return new CategoryDao(con).getCategory(getCategoryId());
	}
}
