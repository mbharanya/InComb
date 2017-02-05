package com.incomb.server.indexing;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.indexing.conf.IIndexTypeConf;
import com.incomb.server.indexing.util.QueryUtil;

/**
 * This is the single point for searching in the lucene {@link Index}.
 * It can search for a specific search text in an {@link IIndexTypeConf} or
 * search directly with a lucene {@link Query}.
 */
public class IndexSearch {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexSearch.class);

	/**
	 * This class uses the singleton pattern.
	 * This constant holds the single instance of this class and
	 * can be accessed by calling {@link #getInstance()}.
	 */
	private static final IndexSearch INSTANCE = new IndexSearch();

	/**
	 * The constructor is only accessible for this class and subclasses.
	 */
	protected IndexSearch() {

	}

	/**
	 * This class uses the singleton pattern.
	 * This method returns the single instance of this class.
	 */
	public static IndexSearch getInstance() {
		return INSTANCE;
	}

	/**
	 * Searches in the lucene index for the given search text.
	 *
	 * @param searchText the text to find the results.
	 * @param indexType the type in which should be searched.
	 * @param locale optional locale to filter the results.
	 * @param options other options for this query.
	 * @return {@link DocumentsSearchResult} with the results.
	 */
	public DocumentsSearchResult search(final String searchText, final IIndexTypeConf indexType,
			final Locale locale, final SearchOptions options) {
		final BooleanQuery query = new BooleanQuery();

		QueryUtil.addTypeConf(query, indexType);
		QueryUtil.addLocale(query, locale);
		QueryUtil.addSearchText(query, searchText, indexType);

		return search(query, options);
	}

	/**
	 * Executes the given {@link Query} and returns a {@link DocumentsSearchResult} with
	 * the found documents and meta information about them.
	 *
	 * @param query the query to execute
	 * @param options the additional options to execute the query.
	 * @return {@link DocumentsSearchResult} with the found {@link Document}.
	 */
	public DocumentsSearchResult search(final Query query, final SearchOptions options) {
		final DocumentsSearchResult result = new DocumentsSearchResult();

		final TopDocs topDocs = getTopDocs(query, options);
		if(topDocs != null) {
			result.setTotalHits(topDocs.totalHits);

			final Index index = IndexManager.getInstance().getIndex();
			final IndexReader reader = index.getIndexReader();

			try {
				LOGGER.debug("Found these documents (total = {}) for query '{}':", topDocs.totalHits, query);

				int counter = 0;
				for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
					final Document document = reader.document(scoreDoc.doc);
					if(document != null) {
						LOGGER.debug("- Score: " + StringUtils.rightPad(Float.toString(scoreDoc.score), 8) +
								" Doc: " + document.get(IIndexElement.FIELD_ID));
						result.addDocument(document, scoreDoc.score);
					}

					// if it's the last document -> set ScoreDoc to result
					if(++counter == topDocs.scoreDocs.length) {
						result.setLastScoreDoc(scoreDoc);
					}
				}
			}
			catch(final IOException e) {
				LOGGER.error("Can't get documents for topdocs.", e);
			}
		}

		return result;
	}

	/**
	 * Executes the given {@link Query} but returns lucene's {@link TopDocs}.
	 * @param query the query to execute
	 * @param options the additional options to execute the query.
	 * @return {@link TopDocs} or null if an error occurred.
	 */
	public TopDocs getTopDocs(final Query query, final SearchOptions options) {
		TopDocs topDocs = null;

		final Index index = IndexManager.getInstance().getIndex();
		final IndexReader reader = index.getIndexReader();
		final IndexSearcher searcher = new IndexSearcher(reader);

		// stopwatch to check performance of search
		final StopWatch stopWatch = new StopWatch();

		try {
			int maxResults = options.getMaxResults();
			if(maxResults <= 0) {
				maxResults = reader.numDocs();
			}

			stopWatch.start();
			if(options.getSort() == null) {
				if(options.getAfterScoreDoc() == null) {
					topDocs = searcher.search(query, maxResults);
				}
				else {
					topDocs = searcher.searchAfter(options.getAfterScoreDoc(), query, maxResults);
				}
			}
			else {
				if(options.getAfterScoreDoc() == null) {
					topDocs = searcher.search(query, maxResults, options.getSort());
				}
				else {
					topDocs = searcher.searchAfter(options.getAfterScoreDoc(), query, maxResults, options.getSort());
				}
			}

			stopWatch.stop();
			LOGGER.info("Query execution used {}ms {}.", stopWatch.getTime(), query);
		}
		catch (final IOException e) {
			LOGGER.error("Can't execute search because of an IOException.", e);
		}

		return topDocs;
	}

	/**
	 * Executes the given {@link Query} and groups the found {@link Document}s by the given groupField.
	 * @param groupField the field to group the {@link Document}s with.
	 * @param query the query to execute
	 * @param options the additional options to execute the query.
	 * @return {@link TopGroups} or null if an error occurred.
	 */
	public TopGroups<BytesRef> getGroupedDocs(final String groupField, final Query query, final SearchOptions options) {
		final Index index = IndexManager.getInstance().getIndex();
		final IndexReader reader = index.getIndexReader();
		final IndexSearcher searcher = new IndexSearcher(reader);
		final GroupingSearch groupingSearch = new GroupingSearch(groupField);

		if(options.getSort() != null) {
			groupingSearch.setSortWithinGroup(options.getSort());
		}

		TopGroups<BytesRef> topGroups = null;

		try {
			topGroups = groupingSearch.search(searcher, query, 0, options.getMaxResults());
		} catch (final IOException e) {
			LOGGER.error("Can't execute group search because of an IOException.", e);
		}

		return topGroups;
	}

	/**
	 * Returns a new instance of lucene's {@link MoreLikeThis} with the
	 * right {@link IndexReader}.
	 */
	public MoreLikeThis newMoreLikeThis(final Locale locale) {
		final Index index = IndexManager.getInstance().getIndex();
		final MoreLikeThis mlt = new MoreLikeThis(index.getIndexReader());

		mlt.setAnalyzer(index.getAnalyzer(locale));

		return mlt;
	}

	/**
	 * Returns lucene's document id for the given id in the given {@link IIndexTypeConf}
	 * @param typeConf the 188.166.43.201 to fins.
	 * @param id the id to find.
	 * @return the id or 0 if document was not found.
	 */
	public int getDocIdForId(final IIndexTypeConf typeConf, final String id) {
		final SearchOptions params = new SearchOptions();
		params.setMaxResults(1);

		final BooleanQuery query = new BooleanQuery();
		QueryUtil.addTypeConf(query, typeConf);
		QueryUtil.addId(query, id);

		final TopDocs topDocs = IndexSearch.getInstance().getTopDocs(query, params);

		if(topDocs.totalHits == 0) {
			throw new IllegalStateException("Can't find news with id " + id + " in news index.");
		}
		else if(topDocs.totalHits > 1) {
			LOGGER.warn("Found more than one result for news with id " + id + " in news index. "
					+ "This is an invalid state. Using the first found document.");
		}

		return topDocs.scoreDocs[0].doc;
	}
}
