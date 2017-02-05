package com.incomb.server.indexing;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;

/**
 * This contains some optional parameters for a Lucene search in {@link IndexSearch}.
 */
public class SearchOptions {

	/**
	 * If all results should be returned than use this at {@link #setMaxResults(int)}.
	 */
	public static final int ALL_RESULTS = -1;

	/**
	 * The amount of results to return.
	 * @see SearchOptions#ALL_RESULTS
	 */
	private int maxResults = ALL_RESULTS;

	/**
	 * The sort order in which the result should be returned.
	 * If it's <code>null</code> than the result is sorted by relevance.
	 */
	private Sort sort = null;

	/**
	 * The {@link ScoreDoc} after the search should start.
	 * If it's <code>null</code> than the search will start at the beginning.
	 */
	private ScoreDoc afterScoreDoc = null;

	/**
	 * @return The amount of results to return.
	 */
	public int getMaxResults() {
		return maxResults;
	}

	/**
	 * Sets the amount of results to return.
	 * Use {@link #ALL_RESULTS} if you want all results.
	 * @param maxResults the amount of results to return
	 */
	public void setMaxResults(final int maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * @return The sort order in which the result should be returned.
	 * If it's <code>null</code> than the result is sorted by relevance.
	 */
	public Sort getSort() {
		return sort;
	}

	/**
	 * Sets the sort order in which the result should be returned.
	 * If it's <code>null</code> than the result is sorted by relevance.
	 */
	public void setSort(final Sort sort) {
		this.sort = sort;
	}

	/**
	 * Returns the {@link ScoreDoc} after the search should start.
	 * If it returns <code>null</code> than the search will start at the beginning.
	 */
	public ScoreDoc getAfterScoreDoc() {
		return afterScoreDoc;
	}

	/**
	 * Sets the {@link ScoreDoc} after the search should start.
	 */
	public void setAfterScoreDoc(final ScoreDoc afterScoreDoc) {
		this.afterScoreDoc = afterScoreDoc;
	}
}
