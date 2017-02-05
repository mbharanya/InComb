package com.incomb.server.model;

import java.io.Serializable;

import com.incomb.server.model.dao.CategoryPreferenceDao;

/**
 * This class holds the information about how much a {@link User}
 * wants to see {@link Content}s of a {@link Category}.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link CategoryPreferenceDao}.</p>
 */
public class CategoryPreference implements Serializable {

	private static final long serialVersionUID = -671474376;

	/**
	 * The id of the {@link User} who preferences the {@link Category}.
	 */
	private long userId;

	/**
	 * The id of the {@link Category} which the {@link User} likes.
	 */
	private int categoryId;

	/**
	 * The factor how much the {@link User} likes the {@link Category}.
	 * Value: <code>0.0</code> (doesn't like it), <code>0.0</code> (likes it really much).
	 */
	private double factor;

	/**
	 * Constructs a new object with the default properties.
	 */
	public CategoryPreference() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param userId the id of the {@link User} who preferences the {@link Category}.
	 * @param categoryId the id of the {@link Category} which the {@link User} likes.
	 * @param factor the factor how much the {@link User} likes the {@link Category}.
	 */
	public CategoryPreference(final long userId, final int categoryId, final double factor) {
		this.userId = userId;
		this.categoryId = categoryId;
		this.factor = factor;
	}

	/**
	 * @return the id of the {@link User} who preferences the {@link Category}.
	 */
	public long getUserId() {
		return this.userId;
	}

	/**
	 * Sets the id of the {@link User} who preferences the {@link Category}.
	 * @param userId the id of the {@link User} who preferences the {@link Category}.
	 */
	public void setUserId(final long userId) {
		this.userId = userId;
	}

	/**
	 * @return the id of the {@link Category} which the {@link User} likes.
	 */
	public int getCategoryId() {
		return this.categoryId;
	}

	/**
	 * Sets the id of the {@link Category} which the {@link User} likes.
	 * @param categoryId the id of the {@link Category} which the {@link User} likes.
	 */
	public void setCategoryId(final int categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the factor how much the {@link User} likes the {@link Category}.
	 */
	public double getFactor() {
		return this.factor;
	}

	/**
	 * Sets the factor how much the {@link User} likes the {@link Category}.
	 * @param factor the factor how much the {@link User} likes the {@link Category}.
	 */
	public void setFactor(final double factor) {
		this.factor = factor;
	}
}
