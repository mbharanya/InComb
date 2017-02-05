package com.incomb.server.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.incomb.server.model.dao.ContentCommentDao;

/**
 * This class represents an In or Comb for a {@link Content} given
 * by a {@link User}.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link ContentCommentDao}.</p>
 */
public class ContentVote implements Serializable {

	private static final long serialVersionUID = 1873415846;

	/**
	 * The id of the {@link User} who gave the {@link ContentVote}.
	 */
	private long userId;

	/**
	 * The id of the {@link Content} for which the {@link ContentVote} was given.
	 */
	private long contentId;

	/**
	 * The time when the {@link ContentVote} was given.
	 */
	private Timestamp voteDate;

	/**
	 * The type of the {@link ContentVote}. <code>true</code> means an In and <code>false</code> means a Comb.
	 */
	private boolean up;

	/**
	 * Constructs a new object with the default properties.
	 */
	public ContentVote() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param userId the id of the {@link User} who gave the {@link ContentVote}.
	 * @param contentId the id of the {@link Content} for which the {@link ContentVote} was given.
	 * @param voteDate the time when the {@link ContentVote} was given.
	 * @param up the type of the {@link ContentVote}. <code>true</code> means an In and <code>false</code> means a Comb.
	 */
	public ContentVote(final long userId, final long contentId, final Timestamp voteDate, final boolean up) {
		this.userId = userId;
		this.contentId = contentId;
		this.voteDate = voteDate;
		this.up = up;
	}

	/**
	 * @return the id of the {@link User} who gave the {@link ContentVote}.
	 */
	public long getUserId() {
		return this.userId;
	}

	/**
	 * Sets the id of the {@link User} who gave the {@link ContentVote}.
	 * @param userId the id of the {@link User} who gave the {@link ContentVote}.
	 */
	public void setUserId(final long userId) {
		this.userId = userId;
	}

	/**
	 * @return the id of the {@link Content} for which the {@link ContentVote} was given.
	 */
	public long getContentId() {
		return this.contentId;
	}

	/**
	 * Sets the id of the {@link Content} for which the {@link ContentVote} was given.
	 * @param contentId the id of the {@link Content} for which the {@link ContentVote} was given.
	 */
	public void setContentId(final long contentId) {
		this.contentId = contentId;
	}

	/**
	 * @return the time when the {@link ContentVote} was given.
	 */
	public Timestamp getVoteDate() {
		return this.voteDate;
	}

	/**
	 * Sets the time when the {@link ContentVote} was given.
	 * @param voteDate the time when the {@link ContentVote} was given.
	 */
	public void setVoteDate(final Timestamp voteDate) {
		this.voteDate = voteDate;
	}

	/**
	 * @return the type of the {@link ContentVote}.
	 * 		<code>true</code> means an In and <code>false</code> means a Comb.
	 */
	public boolean getUp() {
		return this.up;
	}

	/**
	 * Sets the type of the {@link ContentVote}.
	 * <code>true</code> means an In and <code>false</code> means a Comb.
	 * @param up the type of the {@link ContentVote}.
	 */
	public void setUp(final boolean up) {
		this.up = up;
	}
}
