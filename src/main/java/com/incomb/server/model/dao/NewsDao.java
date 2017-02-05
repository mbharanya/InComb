package com.incomb.server.model.dao;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.ConstValueSource;
import org.apache.lucene.queries.function.valuesource.LongFieldSource;
import org.apache.lucene.queries.function.valuesource.SumFloatFunction;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.jooq.JoinType;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.content.indexing.NewsIndexElement;
import com.incomb.server.content.indexing.NewsIndexType;
import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.indexing.DocumentsSearchResult;
import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.ISearchResult;
import com.incomb.server.indexing.IndexManager;
import com.incomb.server.indexing.IndexSearch;
import com.incomb.server.indexing.SearchOptions;
import com.incomb.server.indexing.SimpleIndexData;
import com.incomb.server.indexing.SimpleSearchResult;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.indexing.util.QueryUtil;
import com.incomb.server.model.CategoryPreference;
import com.incomb.server.model.News;
import com.incomb.server.model.NewsGroup;
import com.incomb.server.model.Provider;
import com.incomb.server.model.TagPreference;
import com.incomb.server.model.User;
import com.incomb.server.model.UserLocale;
import com.incomb.server.model.dao.NewsDao.SearchParams.ENewsSource;
import com.incomb.server.model.dao.NewsDao.SearchParams.ESortField;
import com.incomb.server.model.dao.NewsDao.SearchParams.ESortOrder;
import com.incomb.server.model.dao.internal.InternalContentDao;
import com.incomb.server.model.dao.internal.InternalNewsDao;
import com.incomb.server.model.tables.ContentTable;
import com.incomb.server.model.tables.NewsTable;
import com.incomb.server.utils.CollectionUtil;

/**
 * This is the DAO to retrieve and write {@link News}.
 * It uses the database or the index to get the data.
 * It guarantees that changes will always be made in the database and index.
 */
public class NewsDao extends ADao {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsDao.class);

	/**
	 * The table to build SQL queries.
	 */
	private static final ContentTable TABLE_CONTENT = new ContentTable();

	/**
	 * The table to build SQL queries.
	 */
	private static final NewsTable TABLE_NEWS = new NewsTable();

	/**
	 * If two {@link News} have the same link and were published before or after
	 * this minute amount than the news are the same.
	 */
	private static final int PUBLISHDATE_DELTA = 30;

	/**
	 * This is the value of the last generated news group id.
	 */
	private static AtomicLong lastNewsGroupId = null;

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalContentDao contentDao;

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalNewsDao newsDao;

	/**
	 * Initializes the {@link #lastNewsGroupId} with the highest value
	 * from the database.
	 */
	static {
		initNewsGroupId();
	}

	/**
	 * Creates a new instance and acquires for each query a new {@link Connection}
	 * from the {@link DBConnectionProvider}.
	 * If you want to do write operations than please use {@link #NewsDao(Connection)}.
	 */
	public NewsDao() {
		this(null);
	}

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public NewsDao(final Connection connection) {
		super(connection);
		contentDao = new InternalContentDao(jooqConfig);
		newsDao = new InternalNewsDao(jooqConfig);
	}

	/**
	 * Saves a {@link News}.
	 *
	 * This method checks based on the link and title if the {@link News} already exists
	 * and than updates the existing record. If it doesn't exist yet, it will inserted.
	 *
	 * {@link News#getId()} must not return a right value. It will automatically generated
	 * if the news is new or the old id will be used and set on the {@link News} if it already exists.
	 *
	 * If {@link News#getPublishDate()} returns null than the last publish date will be used if the
	 * record already exists. If it's a new news and {@link News#getPublishDate()} returns null
	 * than the current time will be saved.
	 *
	 * If the {@link News} was already saved and {@link News#getNewsGroupId()} returns
	 * {@link News#NEWSGROUPID_NOT_GROUPED_YET} than it uses the news group id which was last saved.
	 *
	 * @param news the {@link News} to save
	 */
	public void saveNews(final News news) {
		final SimpleIndexData indexData = new SimpleIndexData(NewsIndexType.getInstance());

		// set publish date to now if it wasn't set.
		final boolean hasOwnPublishDate = news.getPublishDate() != null;
		if(!hasOwnPublishDate) {
			news.setPublishDate(new Timestamp(System.currentTimeMillis()));
		}

		final Defaults defaults = getDefaultsForNews(news);

		if(defaults == null) {
			news.setNewsGroupId(News.NEWSGROUPID_NOT_GROUPED_YET);

			contentDao.insert(news);
			news.setId(getLastId());
			newsDao.insert(news);
			indexData.addElement(new NewsIndexElement(news, EOperation.INSERT));

			LOGGER.debug("News with id {} was inserted.", news.getId());
		}
		else {
			if(news.getNewsGroupId() == News.NEWSGROUPID_NOT_GROUPED_YET) {
				news.setNewsGroupId(defaults.getNewsGroupId());
			}

			final boolean equal = defaults.getImageWidth() == news.getImageWidth() &&
					defaults.getImageHeight() == news.getImageHeight() &&
					defaults.getNewsGroupId() == news.getNewsGroupId() &&
					StringUtils.equals(defaults.getTitle(), news.getTitle()) &&
					StringUtils.equals(defaults.getText(), news.getText()) &&
					StringUtils.equals(defaults.getLink(), news.getLink()) &&
					StringUtils.equals(defaults.getImageUrl(), news.getImageUrl());

			if(equal) {
				LOGGER.debug("News with id {} wasn't updated because of no changes.", defaults.getId());
			}
			else {
				news.setId(defaults.getId());

				// set last publish date if it wasn't set.
				if(!hasOwnPublishDate && defaults.getPublishDate() != null) {
					news.setPublishDate(defaults.getPublishDate());
				}

				contentDao.update(news);
				newsDao.update(news);
				indexData.addElement(new NewsIndexElement(news, EOperation.UPDATE));

				LOGGER.debug("News with id {} was updated.", news.getId());
			}
		}

		if(!indexData.getElements().isEmpty()) {
			IndexManager.getInstance().index(indexData);
		}
	}

	/**
	 * Sets the indexed flag and saves the {@link News} to the database without reindexing
	 * if the flag wasn't already set.
	 * @param news the {@link News} which was indexed.
	 */
	public void setIndexed(final News news) {
		if(!news.isIndexed()) {
			DSL.using(jooqConfig).
				update(TABLE_CONTENT).
				set(TABLE_CONTENT.INDEXED, true).
				where(TABLE_CONTENT.ID.eq(news.getId())).
				execute();

			news.setIndexed(true);
		}
	}

	/**
	 * Sets the given news group id on the given {@link News} and updates it on the database.
	 * Then a reindex of the {@link News} will be made.
	 * @param news the {@link News} with the new news group id.
	 * @param newsGroupId the new news group id
	 */
	public void setNewsGroup(final News news, final long newsGroupId) {
		final int affectedRecords = DSL.using(jooqConfig).
			update(TABLE_NEWS).
			set(TABLE_NEWS.NEWS_GROUP_ID, newsGroupId).
			where(TABLE_NEWS.CONTENT_ID.eq(news.getId())).
			execute();

		news.setNewsGroupId(newsGroupId);

		// update index if there are any changes
		if(affectedRecords > 0) {
			final SimpleIndexData indexData = new SimpleIndexData(NewsIndexType.getInstance());
			indexData.addElement(new NewsIndexElement(news, EOperation.UPDATE));
			IndexManager.getInstance().index(indexData);
		}
	}

	/**
	 * Returns the {@link News} with the given id.
	 * @param newsId the it of the {@link News} which will be returned.
	 * @return the found {@link News} or <code>null</code> if no {@link News} was found for the id.
	 */
	public News getNews(final long newsId) {
		return DSL.using(jooqConfig).
				select().
				from(TABLE_CONTENT.join(TABLE_NEWS, JoinType.JOIN).
					on(TABLE_CONTENT.ID.eq(TABLE_NEWS.CONTENT_ID))).
				where(TABLE_CONTENT.ID.eq(newsId)).
				fetchOneInto(News.class);
	}

	/**
	 * Returns the {@link News} from the database.
	 * The result isn't sorted. This method can be used for reindexing.
	 * @param start start index of the results
	 * @param amount the amount of {@link News}s to return after the start index.
	 * @return {@link List} containing {@link News}
	 */
	public List<News> getNews(final int start, final int amount) {
		return DSL.using(jooqConfig).
				select().
				from(TABLE_CONTENT.join(TABLE_NEWS, JoinType.JOIN).
						on(TABLE_CONTENT.ID.eq(TABLE_NEWS.CONTENT_ID))).
				limit(start, amount).
				fetchInto(News.class);
	}

	/**
	 * Searches for {@link News} which matches with the given {@link SearchParams}.
	 * @param conf the criteria to find the {@link News}.
	 * @return {@link ISearchResult} containing the matching {@link News}.
	 * @see #getNewsGroups(SearchParams)
	 */
	public ISearchResult<News> getNews(final SearchParams conf) {
		final DocumentsSearchResult result = IndexSearch.getInstance().search(
				buildQuery(conf), buildOptions(conf));

		return NewsIndexType.docsToNewsSearchResult(new SimpleSearchResult<>(result.getResults().subList(
				conf.offset, result.getResults().size()), result.getTotalHits()));
	}

	/**
	 * Searches for {@link News} which matches with the given {@link SearchParams} and
	 * creates {@link NewsGroup}s of these. The {@link NewsGroup}s will be completed with
	 * all {@link News} which are assigned to the {@link NewsGroup}.
	 *
	 * @param conf the criteria to find the {@link News}.
	 * @return {@link ISearchResult} containing the {@link NewsGroup}s.
	 * @see #getNews(SearchParams)
	 */
	public ISearchResult<NewsGroup> getNewsGroups(final SearchParams conf) {
		final List<NewsGroup> newsGroups = new ArrayList<>();

		// all already created NewsGroups.
		final Set<Long> addedNewsGroupIds = new HashSet<>();

		final Query query = buildQuery(conf);
		final SearchOptions options = buildOptions(conf);

		DocumentsSearchResult lastResult = null;

		int retryCounter = 0;
		do {
			if(lastResult != null) {
				options.setAfterScoreDoc(lastResult.getLastScoreDoc());
				options.setMaxResults((int) (options.getMaxResults()*0.5));
			}

			lastResult = IndexSearch.getInstance().search(query, options);
			if(lastResult.getResults().isEmpty()) {
				// no more results
				break;
			}

			final List<News> newsResult = NewsIndexType.docsToNews(lastResult.getResults());
			for (final News news : newsResult) {
				final long groupId = news.getNewsGroupId();

				// add it only if the news in a news group was not already added with another news in the same group
				if(groupId == 0 || !addedNewsGroupIds.contains(groupId)) {
					List<News> otherNews = new ArrayList<>();

					// if the news is in a group get the other ones of the group.
					if(groupId > 0) {
						otherNews = getNewsOfNewsGroup(groupId, news.getId());
						addedNewsGroupIds.add(groupId);
					}

					newsGroups.add(new NewsGroup(news, otherNews));
				}
			}

			// do it again if not enough news group could be build.
		} while(newsGroups.size() - conf.offset < conf.amount && retryCounter++ < 20);

		final int totalHits = lastResult == null ? 0 : (lastResult.getTotalHits() - getAmountOfNewsInNewsGroups(query));

		return new SimpleSearchResult<>(CollectionUtil.subList(newsGroups, conf.offset, conf.amount), totalHits);
	}

	/**
	 * Builds Lucene's {@link Query} for the given {@link SearchParams}.
	 * @param conf the {@link SearchParams} to build the {@link Query}.
	 * @return the {@link Query}
	 */
	private Query buildQuery(final SearchParams conf) {
		final BooleanQuery query = new BooleanQuery();

		final Connection con = jooqConfig.connectionProvider().acquire();

		try {
			QueryUtil.addTypeConf(query, NewsIndexType.getInstance());
			QueryUtil.addSearchText(query, conf.searchText, NewsIndexType.getInstance());

			// locales
			List<Locale> locales = new ArrayList<>();

			if(conf.userId > 0) {
				locales = new UserLocaleDao(con).getLocalesForUser(conf.userId);
			}

			// if user hasn't defined any locales or is not logged in, use the one of the SearchParams.
			if(locales.isEmpty()) {
				locales.add(conf.locale);
			}

			// filter user locales
			final BooleanQuery localeQuery = new BooleanQuery();
			localeQuery.setMinimumNumberShouldMatch(1); // at least one locale has to match
			localeQuery.setBoost(0f); // locale query is only for filtering -> no boost
			for (final Locale locale : locales) {
				final TermQuery termQuery = new TermQuery(new Term(IIndexElement.FIELD_LOCALE, locale.getLanguage()));
				termQuery.setBoost(0f); // locale query is only for filtering -> no boost
				localeQuery.add(termQuery, Occur.SHOULD);
			}
			query.add(localeQuery, Occur.MUST);

			// filter provider
			if(conf.providerId > 0) {
				final NumericRangeQuery<Integer> subQuery = NumericRangeQuery.newIntRange(
						NewsIndexType.FIELD_PROVIDERID, conf.providerId, conf.providerId, true, true);
				query.add(subQuery, Occur.MUST);
			}

			// filter category
			if(conf.categoryId > 0) {
				final NumericRangeQuery<Integer> subQuery = NumericRangeQuery.newIntRange(
						NewsIndexType.FIELD_CATEGORYID, conf.categoryId, conf.categoryId, true, true);
				query.add(subQuery, Occur.MUST);
			}

			// filter publish date
			if(conf.startPublishDate != null || conf.endPublishDate != null) {
				final Long startDate = conf.startPublishDate != null ? conf.startPublishDate.getTime() : null;
				final Long endDate = conf.endPublishDate != null ? conf.endPublishDate.getTime() : null;

				final NumericRangeQuery<Long> subQuery = NumericRangeQuery.newLongRange(
						NewsIndexType.FIELD_PUBLISH_DATE, startDate, endDate, true, true);
				query.add(subQuery, Occur.MUST);
			}

			// boost personalized news and bee activities.
			if(conf.userId > 0) {
				final BooleanQuery userQuery = new BooleanQuery();
				userQuery.setMinimumNumberShouldMatch(1);

				if(conf.newsSources.contains(ENewsSource.USER)) {
					final BooleanQuery subQuery = new BooleanQuery();

					// Tag Preferences
					final List<TagPreference> tags = new TagPreferenceDao(con).getTagPreferences(conf.userId);
					final BooleanQuery tagQuery = new BooleanQuery();
					for (final TagPreference tag : tags) {
						QueryUtil.addSearchText(tagQuery, tag.getTag(), NewsIndexType.getInstance(), 2f);
					}
					subQuery.add(tagQuery, Occur.SHOULD);

					// Category Preferences
					final List<CategoryPreference> categories = new CategoryPreferenceDao(con).
							getCategoryPreferences(conf.userId);
					for (final CategoryPreference category : categories) {
						final NumericRangeQuery<Integer> catQuery = NumericRangeQuery.newIntRange(
								NewsIndexType.FIELD_CATEGORYID, category.getCategoryId(),
								category.getCategoryId(), true, true);
						catQuery.setBoost((float) (category.getFactor()*0.5f));
						subQuery.add(catQuery, Occur.SHOULD);
					}

					// Provider/Category Exclusions - not implemented yet
					/*final List<ProviderExclusion> providerExclusions = new ProviderExclusionDao(con).
							getProviderExclusions(conf.userId);
					for (final ProviderExclusion exclusion : providerExclusions) {
						final BooleanQuery exclQuery = new BooleanQuery();

						final NumericRangeQuery<Integer> catQuery = NumericRangeQuery.newIntRange(
								NewsIndexType.FIELD_CATEGORYID, exclusion.getCategoryId(),
								exclusion.getCategoryId(), true, true);
						exclQuery.add(catQuery, Occur.MUST);

						final NumericRangeQuery<Integer> providerQuery = NumericRangeQuery.newIntRange(
								NewsIndexType.FIELD_PROVIDERID, exclusion.getProviderId(),
								exclusion.getProviderId(), true, true);
						exclQuery.add(providerQuery, Occur.MUST);

						subQuery.add(exclQuery, Occur.MUST_NOT);
					}*/

					userQuery.add(subQuery, Occur.SHOULD);
				}

				if(conf.newsSources.contains(ENewsSource.BEES)) {
					final BooleanQuery subQuery = new BooleanQuery();

					final List<User> flyWiths = new FlyWithDao(con).getFlyWiths(conf.userId);
					for (final User flyWith : flyWiths) {
						final NumericRangeQuery<Long> inQuery = NumericRangeQuery.newLongRange(
								NewsIndexType.FIELD_IN, flyWith.getId(), flyWith.getId(), true, true);
						final NumericRangeQuery<Long> combQuery = NumericRangeQuery.newLongRange(
								NewsIndexType.FIELD_COMB, flyWith.getId(), flyWith.getId(), true, true);
						final NumericRangeQuery<Long> commentQuery = NumericRangeQuery.newLongRange(
								NewsIndexType.FIELD_COMMENT_OF, flyWith.getId(), flyWith.getId(), true, true);

						inQuery.setBoost(1.0f);
						combQuery.setBoost(0.8f);
						commentQuery.setBoost(1.0f);

						subQuery.add(inQuery, Occur.SHOULD);
						subQuery.add(combQuery, Occur.SHOULD);
						subQuery.add(commentQuery, Occur.SHOULD);
					}

					userQuery.add(subQuery, Occur.SHOULD);
				}

				query.add(userQuery, Occur.SHOULD);
			}

			if(StringUtils.isBlank(conf.searchText)) {
				// boost new news
				final LongFieldSource dateFieldSource = new LongFieldSource(NewsIndexType.FIELD_PUBLISH_DATE);
				final ConstValueSource currentDateSource = new ConstValueSource(-System.currentTimeMillis());
				final SumFloatFunction dateSumFunction = new SumFloatFunction(new ValueSource[] {dateFieldSource, currentDateSource});
				final FunctionQuery dateQuery = new FunctionQuery(dateSumFunction);
				dateQuery.setBoost(1f/(1000f*60f*60f*48f)); // boost on two day base
				query.add(dateQuery, Occur.SHOULD);
			}
		}
		finally {
			jooqConfig.connectionProvider().release(con);
		}

		return query;
	}

	/**
	 * Builds the {@link SearchOptions} based on the given {@link SearchParams}.
	 * @param conf the {@link SearchParams} to build the {@link SearchOptions}.
	 * @return the {@link SearchOptions}.
	 */
	private SearchOptions buildOptions(final SearchParams conf) {
		final SearchOptions options = new SearchOptions();

		// amount of results
		options.setMaxResults(conf.offset + conf.amount);

		// sort order
		if(!conf.sortFields.isEmpty()) {
			final SortField[] sortFields = new SortField[conf.sortFields.size()+1];

			int i = 0;
			for (final Entry<ESortField, ESortOrder> sortConf : conf.sortFields.entrySet()) {
				sortFields[i++] = sortConf.getKey().getSortField(sortConf.getValue());
			}

			sortFields[i] = SortField.FIELD_SCORE;

			options.setSort(new Sort(sortFields));
		}

		return options;
	}

	/**
	 * Returns all ungrouped ({@link News#NEWSGROUPID_NOT_GROUPED_YET}) {@link News} which were already indexed.
	 * Not indexed {@link News} will be grouped after they were indexed.
	 * @return {@link List} containing ungrouped {@link News}.
	 */
	public List<News> getNewsToGroup() {
		return DSL.using(jooqConfig).
				select().
				from(TABLE_CONTENT.join(TABLE_NEWS, JoinType.JOIN).
						on(TABLE_CONTENT.ID.eq(TABLE_NEWS.CONTENT_ID))).
				where(TABLE_NEWS.NEWS_GROUP_ID.eq(News.NEWSGROUPID_NOT_GROUPED_YET)).
					and(TABLE_CONTENT.INDEXED.eq(Boolean.TRUE)).
				fetchInto(News.class);
	}

	/**
	 * Returns all {@link News} of the given news group except the {@link News} with the given id.
	 * @param newsGroupId the news group id
	 * @param exceptId the news which should not be returned
	 * @return a {@link List} with all {@link News} of the requested news group except the {@link News} with the exceptId.
	 */
	private List<News> getNewsOfNewsGroup(final long newsGroupId, final long exceptId) {
		final BooleanQuery query = new BooleanQuery();

		QueryUtil.addTypeConf(query, NewsIndexType.getInstance());

		final NumericRangeQuery<Long> groupQuery = NumericRangeQuery.newLongRange(
				NewsIndexType.FIELD_NEWSGROUPID, newsGroupId, newsGroupId, true, true);
		query.add(groupQuery, Occur.MUST);

		// exclude news
		query.add(new TermQuery(new Term(IIndexElement.FIELD_ID, String.valueOf(exceptId))), Occur.MUST_NOT);

		final SearchOptions options = new SearchOptions();
		options.setSort(new Sort(ESortField.PUBLISH_DATE.getSortField(ESortOrder.DESC)));

		final DocumentsSearchResult result = IndexSearch.getInstance().search(query, options);
		return NewsIndexType.docsToNews(result.getResults());
	}

	/**
	 * Returns the amount of {@link News} which are assigned to news groups (news group id > 0)
	 * for the given {@link Query}.
	 */
	private int getAmountOfNewsInNewsGroups(final Query filterQuery) {
		final BooleanQuery query = new BooleanQuery();
		query.add(filterQuery, Occur.MUST);

		// get only news that are in real groups (newsGroupId > 0)
		final NumericRangeQuery<Long> newsGroupFilterQuery = NumericRangeQuery.newLongRange(
				NewsIndexType.FIELD_NEWSGROUPID, 0l, null, false, true);
		query.add(newsGroupFilterQuery, Occur.MUST);

		final SearchOptions options = new SearchOptions();
		options.setMaxResults(0); // we only want the totalHits, not the results.

		final TopDocs topDocs = IndexSearch.getInstance().getTopDocs(query, options);

		return topDocs.totalHits;
	}

	/**
	 * Little data holder as return value of {@link NewsDao#getDefaultsForNews(String)}.
	 */
	private static class Defaults extends News {

		private static final long serialVersionUID = 1L;
	}

	/**
	 * Checks if a {@link News} already exists for the given link. If one exists
	 * then the {@link Defaults} of this are returned. If not then <code>null</code>
	 * will be returned.
	 *
	 * @param news the {@link News} to find a {@link News} with the same link or name.
	 * @return {@link Defaults} or <code>null</code>
	 */
	private Defaults getDefaultsForNews(final News news) {
		final Defaults result = DSL.using(jooqConfig).
				select(TABLE_CONTENT.ID, TABLE_CONTENT.PUBLISH_DATE, TABLE_NEWS.NEWS_GROUP_ID,
						TABLE_CONTENT.TITLE, TABLE_CONTENT.TEXT, TABLE_NEWS.IMAGE_URL,
						TABLE_NEWS.IMAGE_WIDTH, TABLE_NEWS.IMAGE_HEIGHT, TABLE_NEWS.LINK).
				from(TABLE_CONTENT.join(TABLE_NEWS, JoinType.JOIN).
						on(TABLE_CONTENT.ID.eq(TABLE_NEWS.CONTENT_ID))).
				where(TABLE_NEWS.LINK.eq(news.getLink())).
					or(TABLE_CONTENT.TITLE.eq(news.getTitle()).
						and(TABLE_CONTENT.PROVIDER_ID.eq(news.getProviderId())).
						and(TABLE_CONTENT.PUBLISH_DATE.between(getDate(news, PUBLISHDATE_DELTA),
								getDate(news, -PUBLISHDATE_DELTA)))).
				fetchOneInto(Defaults.class);

		// no news found -> exit with null.
/*		if(result == null) {
			return null;
		}

		final Defaults defaults = new Defaults();
		defaults.setId(id); = result.getValue(TABLE_NEWS.CONTENT_ID);
		defaults.publishDate = result.getValue(TABLE_CONTENT.PUBLISH_DATE);
		defaults.newsGroupId = result.getValue(TABLE_NEWS.NEWS_GROUP_ID);
*/
		return result;
	}

	/**
	 * Builds the timestamp by adding the given minutes to the publish date.
	 */
	private Timestamp getDate(final News news, final int minutes) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(news.getPublishDate());

		cal.add(Calendar.MINUTE, minutes);

		return new Timestamp(cal.getTimeInMillis());
	}

	/**
	 * Returns the greatest news id.
	 * Can be used to get the auto incremented value after a {@link News} was inserted.
	 */
	private long getLastId() {
		final Record1<Long> result = DSL.using(jooqConfig).
				select(TABLE_CONTENT.ID).
				from(TABLE_CONTENT).
				orderBy(TABLE_CONTENT.ID.desc()).
				limit(0, 1).
				fetchOne();

		return result == null ? 0 : result.getValue(TABLE_CONTENT.ID);
	}

	/**
	 * Returns a news group id which wasn't used before.
	 */
	public static long getNewNewsGroupId() {
		return lastNewsGroupId.incrementAndGet();
	}

	/**
	 * Queries the biggest news group id from the database and initializes the
	 * {@link #lastNewsGroupId} with it.
	 */
	private static void initNewsGroupId() {
		final Connection con = DBConnectionProvider.getInstance().acquire();
		try {
			final Record1<Long> record = DSL.using(con).
				select(DSL.max(TABLE_NEWS.NEWS_GROUP_ID)).
				from(TABLE_NEWS).
				fetchOne();

			if(record == null) {
				lastNewsGroupId = new AtomicLong(0);
			}
			else {
				final Long max = record.getValue(DSL.max(TABLE_NEWS.NEWS_GROUP_ID));
				lastNewsGroupId = new AtomicLong(max == null ? 0 : max);
			}
		}
		finally {
			DBConnectionProvider.getInstance().release(con);
		}
	}

	/**
	 * Queries all not indexed {@link News} from the database and puts them to
	 * the {@link IndexManager} queue.
	 */
	public void indexNotIndexedNews() {
		final List<News> newsToIndex = DSL.using(jooqConfig).
			select().
			from(TABLE_CONTENT.join(TABLE_NEWS, JoinType.JOIN).
				on(TABLE_CONTENT.ID.eq(TABLE_NEWS.CONTENT_ID))).
			where(TABLE_CONTENT.INDEXED.eq(Boolean.FALSE)).
			fetchInto(News.class);

		if(newsToIndex.isEmpty()) {
			LOGGER.info("No unindexed news found.");
			return; // nothing to index
		}

		final SimpleIndexData indexData = new SimpleIndexData(NewsIndexType.getInstance());
		for (final News news : newsToIndex) {
			LOGGER.info("Add unindexed news {} to index queue.", news.getId());
			indexData.addElement(new NewsIndexElement(news, EOperation.UPDATE));
					// update because it should delete a possible existing news.
		}

		IndexManager.getInstance().index(indexData);
	}

	/**
	 * This is a data holder which contains filter params for a {@link News} search with
	 * {@link NewsDao#getNews(SearchParams)} or {@link NewsDao#getNewsGroups(SearchParams)}.
	 */
	public static class SearchParams {

		/**
		 * This enum contains all possible sort fields and holds Lucene's {@link SortField}.
		 */
		public enum ESortField {
			PROVIDER(1, NewsIndexType.FIELD_PROVIDER, Type.STRING_VAL, ESortOrder.ASC),
			PUBLISH_DATE(2, NewsIndexType.FIELD_PUBLISH_DATE, Type.LONG, ESortOrder.DESC),
			TITLE(3, NewsIndexType.FIELD_TITLE_SORT, Type.STRING_VAL, ESortOrder.ASC),
			CATEGORY(4, NewsIndexType.FIELD_CATEGORY, Type.STRING_VAL, ESortOrder.ASC),
			COMMENTS_AMOUNT(5, NewsIndexType.FIELD_COMMENTS_AMOUNT, Type.INT, ESortOrder.DESC),
			INS_AMOUNT(6, NewsIndexType.FIELD_INS_AMOUNT, Type.INT, ESortOrder.DESC);

			/**
			 * The id of the {@link ESortField}.
			 */
			private final int id;

			/**
			 * The name of the {@link Field} in the {@link News} {@link Document} to which
			 * will be sorted.
			 */
			private final String name;

			/**
			 * The {@link Type} of the field set in {@link #name()}.
			 */
			private final Type type;

			/**
			 * The default {@link ESortOrder} for this field.
			 */
			private final ESortOrder defaultOrder;

			/**
			 * Creates a new instance with all properties.
			 * @param id the id of the {@link ESortField}.
			 * @param name the name of the {@link Field} in the {@link News} {@link Document} to which
			 * 			will be sorted.
			 * @param type the {@link Type} of the field set in {@link #name()}.
			 * @param defaultOrder the default {@link ESortOrder} for this field.
			 */
			private ESortField(final int id, final String name, final Type type, final ESortOrder defaultOrder) {
				this.id = id;
				this.name = name;
				this.type = type;
				this.defaultOrder = defaultOrder;
			}

			/**
			 * Returns the {@link SortField} for the {@link ESortField} and the given {@link ESortOrder}.
			 * @param sortOrder the sort order of the {@link SortField}.
			 * @return the configured {@link SortField}.
			 */
			SortField getSortField(final ESortOrder sortOrder) {
				return new SortField(name, type, sortOrder == ESortOrder.DESC);
			}

			/**
			 * @return the default {@link ESortOrder} for this field.
			 */
			public ESortOrder getDefaultOrder() {
				return defaultOrder;
			}

			/**
			 * Returns the {@link ESortField} with the given id.
			 * @throws IllegalArgumentException if the id is unknown.
			 */
			public static ESortField getById(final int id) {
				for (final ESortField field : values()) {
					if(id == field.id) {
						return field;
					}
				}

				throw new IllegalArgumentException("Unknown id " + id + ".");
			}
		}

		/**
		 * All possible sort orders.
		 */
		public enum ESortOrder {
			ASC,
			DESC
		}

		/**
		 * All possible news sources.
		 */
		public enum ENewsSource {

			/**
			 * Activities of the bees which the {@link User} is flying with.
			 */
			BEES,

			/**
			 * Personalized for the {@link User} with {@link TagPreference}s, {@link CategoryPreference}s, ...
			 */
			USER
		}

		/**
		 * The id of the {@link Provider} to filter.
		 * If <code>0</code> all {@link Provider}s may occur.
		 */
		public int providerId = 0;

		/**
		 * The id of the {@link Category} to filter.
		 * If <code>0</code> all {@link Category}s may occur.
		 */
		public int categoryId = 0;

		/**
		 * The id of the {@link User} to personalize the results.
		 * If <code>0</code> no personalization will be made.
		 */
		public long userId = 0;

		/**
		 * Filters the {@link News}' publish date.
		 * If <code>null</code> all {@link News} since start may occur.
		 */
		public Date startPublishDate = null;

		/**
		 * Filters the {@link News}' publish date.
		 * If <code>null</code> all {@link News} until now may occur.
		 */
		public Date endPublishDate = null;

		/**
		 * All wanted {@link ENewsSource}. Default are all.
		 */
		public Set<ENewsSource> newsSources = new HashSet<ENewsSource>(Arrays.asList(ENewsSource.values()));

		/**
		 * Text to search in the {@link News}. Default is <code>null</code>.
		 */
		public String searchText = null;

		/**
		 * Default locale if the no {@link #userId} was set or the {@link User} has no {@link UserLocale}s defined.
		 */
		public Locale locale = null;

		/**
		 * The start index of the results.
		 */
		public int offset = 0;

		/**
		 * The maximum amount of {@link News} to return.
		 */
		public int amount = 0;

		/**
		 * Sort fields with its sortorder.
		 */
		public Map<ESortField, ESortOrder> sortFields = new HashMap<>();
	}
}
