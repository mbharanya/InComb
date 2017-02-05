package com.incomb.server.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.incomb.server.model.dao.FlyWithDao;

/**
 * This class means that a {@link User} is flying with another {@link User}.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link FlyWithDao}.</p>
 */
public class FlyWith implements Serializable {

	private static final long serialVersionUID = -2108684406;

	/**
	 * The id of the {@link User} who is flying with another {@link User}.
	 */
	private long userId;

	/**
	 * The id of the {@link User} which another {@link User} is flying with.
	 */
	private long flyWithId;

	/**
	 * The time when the {@link User} started flying with the other {@link User}.
	 */
	private Timestamp flyWithStartDate;

	/**
	 * Constructs a new object with the default properties.
	 */
	public FlyWith() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param userId the id of the {@link User} who is flying with another {@link User}.
	 * @param flyWithId the id of the {@link User} which another {@link User} is flying with.
	 * @param flyWithStartDate the time when the {@link User} started flying with the other {@link User}.
	 */
	public FlyWith(final long userId, final long flyWithId, final Timestamp flyWithStartDate) {
		this.userId = userId;
		this.flyWithId = flyWithId;
		this.flyWithStartDate = flyWithStartDate;
	}

	/**
	 * @return the id of the {@link User} who is flying with another {@link User}.
	 */
	public long getUserId() {
		return this.userId;
	}

	/**
	 * Sets the id of the {@link User} who is flying with another {@link User}.
	 * @param userId the id of the {@link User} who is flying with another {@link User}.
	 */
	public void setUserId(final long userId) {
		this.userId = userId;
	}

	/**
	 * @return the id of the {@link User} which another {@link User} is flying with.
	 */
	public long getFlyWithId() {
		return this.flyWithId;
	}

	/**
	 * Sets the id of the {@link User} which another {@link User} is flying with.
	 * @param flyWithId the id of the {@link User} which another {@link User} is flying with.
	 */
	public void setFlyWithId(final long flyWithId) {
		this.flyWithId = flyWithId;
	}

	/**
	 * @return the time when the {@link User} started flying with the other {@link User}.
	 */
	public Timestamp getFlyWithStartDate() {
		return this.flyWithStartDate;
	}

	/**
	 * Sets the time when the {@link User} started flying with the other {@link User}.
	 * @param flyWithStartDate the time when the {@link User} started flying with the other {@link User}.
	 */
	public void setFlyWithStartDate(final Timestamp flyWithStartDate) {
		this.flyWithStartDate = flyWithStartDate;
	}
}
