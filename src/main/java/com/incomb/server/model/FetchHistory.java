package com.incomb.server.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.incomb.server.model.dao.FetchHistoryDao;

/**
 * This class represents a summary of a fetch of a {@link ContentSource}.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link FetchHistoryDao}.</p>
 */
public class FetchHistory implements Serializable {

	private static final long serialVersionUID = -2082006105;

	/**
	 * The id of the {@link ContentSource} which was fetched.
	 */
	private int contentSourceId;

	/**
	 * The time when the {@link ContentSource} was fetched.
	 */
	private Timestamp fetchTime;

	/**
	 * The result of the fetch. <code>true</code> means successful, <code>false</code> means not successful.
	 */
	private boolean result;

	/**
	 * Constructs a new object with the default properties.
	 */
	public FetchHistory() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param contentSourceId the id of the {@link ContentSource} which was fetched.
	 * @param fetchTime the time when the {@link ContentSource} was fetched.
	 * @param result the result of the fetch. <code>true</code> means successful, <code>false</code> means not successful.
	 */
	public FetchHistory(final int contentSourceId, final Timestamp fetchTime, final boolean result) {
		this.contentSourceId = contentSourceId;
		this.fetchTime = fetchTime;
		this.result = result;
	}

	/**
	 * @return a {@link String} containing all property values. Can be used for debugging.
	 */
	@Override
	public String toString() {
		return "ContentSoruceId: " + getContentSourceId() + " " + "FetchTime: " + getFetchTime() + " " + "Result: "
				+ getResult();
	}

	/**
	 * @return the id of the {@link ContentSource} which was fetched.
	 */
	public int getContentSourceId() {
		return this.contentSourceId;
	}

	/**
	 * Sets the id of the {@link ContentSource} which was fetched.
	 * @param contentSourceId the id of the {@link ContentSource} which was fetched.
	 */
	public void setContentSourceId(final int contentSourceId) {
		this.contentSourceId = contentSourceId;
	}

	/**
	 * @return the time when the {@link ContentSource} was fetched.
	 */
	public Timestamp getFetchTime() {
		return this.fetchTime;
	}

	/**
	 * Sets the time when the {@link ContentSource} was fetched.
	 * @param fetchTime the time when the {@link ContentSource} was fetched.
	 */
	public void setFetchTime(final Timestamp fetchTime) {
		this.fetchTime = fetchTime;
	}

	/**
	 * @return the result of the fetch.
	 * 		<code>true</code> means successful, <code>false</code> means not successful.
	 */
	public boolean getResult() {
		return this.result;
	}

	/**
	 * Sets the result of the fetch.
	 * <code>true</code> means successful, <code>false</code> means not successful.
	 * @param result the result of the fetch.
	 */
	public void setResult(final boolean result) {
		this.result = result;
	}
}
