package com.incomb.server.model;

import java.io.Serializable;

import com.incomb.server.model.dao.TagPreferenceDao;

/**
 * This class represents a tag which a {@link User} likes.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link TagPreferenceDao}.</p>
 */
public class TagPreference implements Serializable {

	private static final long serialVersionUID = 1554014800;

	/**
	 * The id of the {@link User} who likes the tag.
	 */
	private long userId;

	/**
	 * The tag which the {@link User} likes.
	 */
	private String tag;

	/**
	 * Constructs a new object with the default properties.
	 */
	public TagPreference() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param userId the id of the {@link User} who likes the tag.
	 * @param tag the tag which the {@link User} likes.
	 */
	public TagPreference(final long userId, final String tag) {
		this.userId = userId;
		this.tag = tag;
	}

	/**
	 * @return the id of the {@link User} who likes the tag.
	 */
	public long getUserId() {
		return this.userId;
	}

	/**
	 * Sets the id of the {@link User} who likes the tag.
	 * @param userId the id of the {@link User} who likes the tag.
	 */
	public void setUserId(final long userId) {
		this.userId = userId;
	}

	/**
	 * @return the tag which the {@link User} likes.
	 */
	public String getTag() {
		return this.tag;
	}

	/**
	 * Sets the tag which the {@link User} likes.
	 * @param tag the tag which the {@link User} likes.
	 */
	public void setTag(final String tag) {
		this.tag = tag;
	}
}
