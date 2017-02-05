package com.incomb.server.services.users.categorypreferences.model;

import com.incomb.server.model.Category;
import com.incomb.server.model.CategoryPreference;
import com.incomb.server.model.User;

/**
 * {@link CategoryPreference} which has to be used to send the information to the client.
 */
public class CategoryPreferenceModel {

	/**
	 * The id of the {@link User} who preferences the {@link Category}.
	 */
	private long userId;

	/**
	 * The factor how much the {@link User} likes the {@link Category}.
	 * Value: <code>0.0</code> (doesn't like it), <code>0.0</code> (likes it really much).
	 */
	private double factor;

	/**
	 * The {@link Category} which the {@link User} likes.
	 */
	private Category category;

	/**
	 * Constructs a new object with the default properties.
	 */
	public CategoryPreferenceModel() {

	}

	/**
	 * Constructs a new object with the given properties.
	 * @param userId the id of the {@link User} who preferences the {@link Category}.
	 * @param categoryId the {@link Category} which the {@link User} likes.
	 * @param factor the factor how much the {@link User} likes the {@link Category}.
	 */
	public CategoryPreferenceModel(final CategoryPreference preference, final Category category) {
		this();
		this.userId = preference.getUserId();
		this.factor = preference.getFactor();
		this.category = category;
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
	 * @return the {@link Category} which the {@link User} likes.
	 */
	public Category getCategory() {
		return this.category;
	}

	/**
	 * Sets the {@link Category} which the {@link User} likes.
	 * @param categoryId the {@link Category} which the {@link User} likes.
	 */
	public void setCategory(final Category category) {
		this.category = category;
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
