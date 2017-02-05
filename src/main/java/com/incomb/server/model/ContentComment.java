package com.incomb.server.model;

import java.sql.Timestamp;

import com.incomb.server.model.dao.ContentCommentDao;

/**
 * This class represents a comment of a {@link Content} written
 * by a {@link User}.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link ContentCommentDao}.</p>
 */
public class ContentComment implements java.io.Serializable {

	private static final long serialVersionUID = 141752003;

	/**
	 * The id of this {@link ContentComment}.
	 */
	private long id;

	/**
	 * The id of the {@link User} who has written this {@link ContentComment}.
	 */
	private long userId;

	/**
	 * The id of the {@link Content} to which this {@link ContentComment} was written.
	 */
	private long contentId;

	/**
	 * The time when this {@link ContentComment} was written.
	 */
	private Timestamp commentDate;

	/**
	 * The text of this {@link ContentComment}.
	 */
	private String comment;

	/**
	 * Constructs a new object with the default properties.
	 */
	public ContentComment() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param id the id of this {@link ContentComment}.
	 * @param userId the id of the {@link User} who has written this {@link ContentComment}.
	 * @param contentId the id of the {@link Content} to which this {@link ContentComment} was written.
	 * @param commentDate the time when this {@link ContentComment} was written.
	 * @param comment the text of this {@link ContentComment}.
	 */
	public ContentComment(final long id, final long userId, final long contentId, final Timestamp commentDate, final String comment) {
		this.id = id;
		this.userId = userId;
		this.contentId = contentId;
		this.commentDate = commentDate;
		this.comment = comment;
	}

	/**
	 * @return the id of this {@link ContentComment}.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id of this {@link ContentComment}.
	 * @param id the id of this {@link ContentComment}.
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * @return the id of the {@link User} who has written this {@link ContentComment}.
	 */
	public long getUserId() {
		return this.userId;
	}

	/**
	 * Sets the id of the {@link User} who has written this {@link ContentComment}.
	 * @param userId the id of the {@link User} who has written this {@link ContentComment}.
	 */
	public void setUserId(final long userId) {
		this.userId = userId;
	}

	/**
	 * @return the id of the {@link Content} to which this {@link ContentComment} was written.
	 */
	public long getContentId() {
		return this.contentId;
	}

	/**
	 * Sets the id of the {@link Content} to which this {@link ContentComment} was written.
	 * @param contentId the id of the {@link Content} to which this {@link ContentComment} was written.
	 */
	public void setContentId(final long contentId) {
		this.contentId = contentId;
	}

	/**
	 * @return the time when this {@link ContentComment} was written.
	 */
	public Timestamp getCommentDate() {
		return this.commentDate;
	}

	/**
	 * Sets the time when this {@link ContentComment} was written.
	 * @param commentDate the time when this {@link ContentComment} was written.
	 */
	public void setCommentDate(final Timestamp commentDate) {
		this.commentDate = commentDate;
	}

	/**
	 * @return the text of this {@link ContentComment}.
	 */
	public String getComment() {
		return this.comment;
	}

	/**
	 * Sets the text of this {@link ContentComment}.
	 * @param comment the text of this {@link ContentComment}.
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}

	/**
	 * Checks if all properties of the given {@link ContentComment} are equal with this {@link ContentComment}.
	 * @param toCompare the {@link ContentComment} to compare.
	 * @return true if all properties are equal.
	 */
	public boolean equals(final ContentComment toCompare){
		return (
			getUserId() == toCompare.getUserId() &&
			getContentId() == toCompare.getContentId() &&
			// mysql dates are not as precise as java dates
			getCommentDate().getTime() / 1000 == toCompare.getCommentDate().getTime() / 1000 &&
			getComment().equals(toCompare.getComment())
		);
	}
}
