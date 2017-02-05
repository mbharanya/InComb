package com.incomb.server.model;

import java.io.Serializable;

/**
 * This class isn't in use at the moment.
 */
public class ProviderExclusion implements Serializable {

	private static final long serialVersionUID = -672525244;

	private long userId;
	private int providerId;
	private int categoryId;

	public ProviderExclusion() {
	}

	public ProviderExclusion(final long userId, final int providerId, final int categoryId) {
		this.userId = userId;
		this.providerId = providerId;
		this.categoryId = categoryId;
	}

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public int getProviderId() {
		return this.providerId;
	}

	public void setProviderId(final int providerId) {
		this.providerId = providerId;
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(final int categoryId) {
		this.categoryId = categoryId;
	}
}
