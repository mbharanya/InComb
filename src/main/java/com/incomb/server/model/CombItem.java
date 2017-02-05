package com.incomb.server.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.incomb.server.model.dao.CombItemDao;

/**
 * This class references to a {@link Content} which a {@link User}
 * has put in his comb with some meta information.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link CombItemDao}.</p>
 */
public class CombItem implements Serializable {

	private static final long serialVersionUID = -1875946955;

	/**
	 * The id of the {@link User} who has put the {@link Content} in his comb.
	 */
	private long userId;

	/**
	 * The id of the {@link Content} which has put in the {@link User}s comb.
	 */
	private long contentId;

	/**
	 * The time when the {@link Content} was put in the {@link User}s comb.
	 */
	private Timestamp addDate;

	/**
	 * The first time when the {@link Content} was read from the {@link User} when it was in his comb.
	 */
	private Timestamp readDate;

	/**
	 * Constructs a new object with the default properties.
	 */
	public CombItem() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param userId the id of the {@link User} who has put the {@link Content} in his comb.
	 * @param contentId the id of the {@link Content} which has put in the {@link User}s comb.
	 * @param addDate the time when the {@link Content} was put in the {@link User}s comb.
	 * @param readDate the first time when the {@link Content} was read from the {@link User} when it was in his comb.
	 */
	public CombItem(final long userId, final long contentId, final Timestamp addDate, final Timestamp readDate) {
		this.userId = userId;
		this.contentId = contentId;
		this.addDate = addDate;
		this.readDate = readDate;
	}

	/**
	 * @return the id of the {@link User} who has put the {@link Content} in his comb.
	 */
	public long getUserId() {
		return this.userId;
	}

	/**
	 * Sets the id of the {@link User} who has put the {@link Content} in his comb.
	 * @param userId the id of the {@link User} who has put the {@link Content} in his comb.
	 */
	public void setUserId(final long userId) {
		this.userId = userId;
	}

	/**
	 * @return the id of the {@link Content} which has put in the {@link User}s comb.
	 */
	public long getContentId() {
		return this.contentId;
	}

	/**
	 * Sets the id of the {@link Content} which has put in the {@link User}s comb.
	 * @param contentId the id of the {@link Content} which has put in the {@link User}s comb.
	 */
	public void setContentId(final long contentId) {
		this.contentId = contentId;
	}

	/**
	 * @return the time when the {@link Content} was put in the {@link User}s comb.
	 */
	public Timestamp getAddDate() {
		return this.addDate;
	}

	/**
	 * Sets the time when the {@link Content} was put in the {@link User}s comb.
	 * @param addDate the time when the {@link Content} was put in the {@link User}s comb.
	 */
	public void setAddDate(final Timestamp addDate) {
		this.addDate = addDate;
	}

	/**
	 * @return the first time when the {@link Content} was read from the {@link User} when it was in his comb.
	 */
	public Timestamp getReadDate() {
		return this.readDate;
	}

	/**
	 * Sets the first time when the {@link Content} was read from the {@link User} when it was in his comb.
	 * @param readDate the first time when the {@link Content} was read from the {@link User} when it was in his comb.
	 */
	public void setReadDate(final Timestamp readDate) {
		this.readDate = readDate;
	}
}
