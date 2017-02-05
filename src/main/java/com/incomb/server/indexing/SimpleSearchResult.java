package com.incomb.server.indexing;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of {@link ISearchResult} with properties for the values.
 */
public class SimpleSearchResult<T> implements ISearchResult<T> {

	/**
	 * All results for the query in the requested order.
	 */
	protected List<T> results;

	/**
	 * The total amount of results if the query wouldn't be limited.
	 */
	protected int totalHits;

	/**
	 * Creates a new instance without results and 0 total hits.
	 */
	public SimpleSearchResult() {
		this(new ArrayList<T>(), 0);
	}

	/**
	 * Uses the size of the given {@link List} to determine the totalHits.
	 * You can override {@link #totalHits} by calling {@link #setTotalHits(int)}.
	 */
	public SimpleSearchResult(final List<T> results) {
		this(results, results.size());
	}

	/**
	 * Creates the new instance with the {@link List} of results and the amount of total hits.
	 * @param results all results for the query in the requested order.
	 * @param totalHits the total amount of results if the query wouldn't be limited.
	 */
	public SimpleSearchResult(final List<T> results, final int totalHits) {
		this.results = results;
		this.totalHits = totalHits;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> getResults() {
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTotalHits() {
		return totalHits;
	}

	/**
	 * Adds a new result to the {@link List} of results.
	 * @param result the result to add.
	 */
	public void addResult(final T result) {
		results.add(result);
	}

	/**
	 * Sets the total hits.
	 * @param totalHits the total amount of results if the query wouldn't be limited.
	 */
	public void setTotalHits(final int totalHits) {
		this.totalHits = totalHits;
	}
}
