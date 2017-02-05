package com.incomb.server.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.content.indexing.NewsIndexType;
import com.incomb.server.indexing.DocumentsSearchResult;
import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.IndexSearch;
import com.incomb.server.indexing.SearchOptions;
import com.incomb.server.indexing.util.QueryUtil;
import com.incomb.server.model.News;
import com.incomb.server.utils.ObjectUtil;

/**
 * Finds similar news to the given {@link News}.
 * <p>A {@link News} is similar if it's in the same category and
 * was published most {@value #PUBLISH_DATE_DELTA} hours before or later.
 * It must have similar words in the title and description to it too.</p>
 */
public class SimilarNewsFinder {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SimilarNewsFinder.class);

	/**
	 * Maximum of hours which the publish date may differ from the publish date of the given {@link News}.
	 */
	public static final int PUBLISH_DATE_DELTA = 3;

	/**
	 * Minimal score of a found {@link News} that it is a similar {@link News}.
	 */
	public static final float MIN_SCORE = 1.2f;

	/**
	 * The {@link News} to find similar ones.
	 */
	private final News news;

	/**
	 * Creates a new {@link SimilarNewsFinder} with the {@link News} to group.
	 * @param news the {@link News} to group.
	 */
	public SimilarNewsFinder(final News news) {
		ObjectUtil.assertNotNull(news, "News may not be null.");
		this.news = news;
	}

	/**
	 * Searches for similar {@link News}.
	 * @return a {@link List} with the similar {@link News}. Can be empty.
	 * 		The more similar the more top is the {@link News} in the {@link List}.
	 */
	public List<News> getSimilar() {
		final int docId = IndexSearch.getInstance().getDocIdForId(
				NewsIndexType.getInstance(), String.valueOf(news.getId()));

		// configure "more like this"
		final MoreLikeThis moreLikeThis = IndexSearch.getInstance().newMoreLikeThis(news.getLocale());
		moreLikeThis.setMinWordLen(3);
		moreLikeThis.setBoost(true);
		moreLikeThis.setBoostFactor(10);
		moreLikeThis.setFieldNames(new String[] {
				NewsIndexType.FIELD_TITLE,
				NewsIndexType.FIELD_DESCRIPTION
		});

		try {
			final BooleanQuery query = new BooleanQuery();

			// it must have the same locale
			QueryUtil.addLocale(query, news.getLocale());

			// filter with publish date
			final NumericRangeQuery<Long> dateQuery = NumericRangeQuery.newLongRange(
					NewsIndexType.FIELD_PUBLISH_DATE, getDate(-PUBLISH_DATE_DELTA),
					getDate(PUBLISH_DATE_DELTA), true, true);
			query.add(dateQuery, Occur.MUST);

			// it must be in the same category.
			final NumericRangeQuery<Integer> categoryQuery = NumericRangeQuery.newIntRange(
					NewsIndexType.FIELD_CATEGORYID, news.getCategoryId(), news.getCategoryId(), true, true);
			query.add(categoryQuery, Occur.MUST);

			final Query moreQuery = moreLikeThis.like(docId);
			query.add(moreQuery, Occur.MUST);

			// not the same news
			query.add(new TermQuery(new Term(IIndexElement.FIELD_ID, String.valueOf(news.getId()))), Occur.MUST_NOT);

			// execute query
			final DocumentsSearchResult result = IndexSearch.getInstance().search(query, new SearchOptions());

			final List<Document> resultDocs = new ArrayList<>();
			for (final Document doc : result.getResults()) {
				final float score = result.getScore(doc);

				// use only news with a sufficient score.
				if(score >= MIN_SCORE) {
					resultDocs.add(doc);
					LOGGER.debug("News {} is similar to news {}.", doc.get(IIndexElement.FIELD_ID), news.getId());
				}
				else {
					LOGGER.debug("News {} has not a sufficient score to be similar to news {}.",
							doc.get(IIndexElement.FIELD_ID), news.getId());
				}
			}

			// convert the Documents to News.
			return NewsIndexType.docsToNews(resultDocs);
		}
		catch (final IOException e) {
			LOGGER.error("Can't build query for news with id '{}'.", news.getId());
		}

		return null;
	}

	/**
	 * Builds the timestamp by adding the given hours to the publish date.
	 */
	private long getDate(final int hours) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(news.getPublishDate());

		cal.add(Calendar.HOUR, hours);

		return cal.getTimeInMillis();
	}
}
