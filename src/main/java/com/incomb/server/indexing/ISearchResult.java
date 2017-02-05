package com.incomb.server.indexing;

import java.util.List;

/**
 * This is a generally result of a search.
 * @param <T> the type of the results.
 */
public interface ISearchResult<T> {

	/**
	 * Returns all results for the query in the requested order.
	 */
	List<T> getResults();

	/**
	 * Returns the total amount of results if the query wouldn't be limited.
	 * If the query wasn't limited then <code>getResults().size() == getTotalHits()</code> should be true.
	 */
	int getTotalHits();
}
