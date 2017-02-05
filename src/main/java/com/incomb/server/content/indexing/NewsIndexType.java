package com.incomb.server.content.indexing;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.content.NewsGrouperThread;
import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.indexing.DocumentsSearchResult;
import com.incomb.server.indexing.IIndexData;
import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.ISearchResult;
import com.incomb.server.indexing.SimpleSearchResult;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.indexing.conf.IIndexTypeConf;
import com.incomb.server.indexing.conf.fields.DateIndexFieldConf;
import com.incomb.server.indexing.conf.fields.IIndexFieldConf;
import com.incomb.server.indexing.conf.fields.IntegerIndexFieldConf;
import com.incomb.server.indexing.conf.fields.LongIndexFieldConf;
import com.incomb.server.indexing.conf.fields.NotIndexedIndexFieldConf;
import com.incomb.server.indexing.conf.fields.StringIndexFieldConf;
import com.incomb.server.indexing.conf.fields.TextIndexFieldConf;
import com.incomb.server.model.News;
import com.incomb.server.model.dao.NewsDao;

/**
 * This is the {@link IIndexTypeConf} for {@link News}.
 * It returns all fields which should be indexed and returns
 * all elements for a complete reindex.
 */
public class NewsIndexType implements IIndexTypeConf {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsIndexType.class);

	/**
	 * This class uses the singleton pattern.
	 * This constant holds the single instance of this class and
	 * can be accessed by calling {@link #getInstance()}.
	 */
	private static final NewsIndexType INSTANCE = new NewsIndexType();

	/**
	 * The name of this index type.
	 */
	public static final String INDEX_TYPE_NAME = "news";

	/**
	 * Contains the title of the {@link News}.
	 */
	public static final String FIELD_TITLE = "title";

	/**
	 * Same as {@link #FIELD_TITLE} but this one can be used for sorting.
	 */
	public static final String FIELD_TITLE_SORT = "titleSort";

	/**
	 * Contains the description of the {@link News}.
	 */
	public static final String FIELD_DESCRIPTION = "description";

	/**
	 * Contains the link of the {@link News}.
	 */
	public static final String FIELD_LINK = "link";

	/**
	 * Contains the id of the provider of the {@link News}.
	 */
	public static final String FIELD_PROVIDERID = "providerId";

	/**
	 * Contains the id of the category of the {@link News}.
	 */
	public static final String FIELD_CATEGORYID = "categoryId";

	/**
	 * Contains the name of the provider of the {@link News}.
	 */
	public static final String FIELD_PROVIDER = "provider";

	/**
	 * Contains the name of the category of the {@link News}.
	 */
	public static final String FIELD_CATEGORY = "category";

	/**
	 * Contains the url of the image of the {@link News}.
	 */
	public static final String FIELD_IMAGE_URL = "imageUrl";

	/**
	 * Contains the width of the image of the {@link News}.
	 */
	public static final String FIELD_IMAGE_WIDTH = "imageWidth";

	/**
	 * Contains the height of the image of the {@link News}.
	 */
	public static final String FIELD_IMAGE_HEIGHT = "imageHeight";

	/**
	 * Contains the news group id of the {@link News}.
	 */
	public static final String FIELD_NEWSGROUPID = "newsGroupId";

	/**
	 * Contains the publish date of the {@link News}.
	 */
	public static final String FIELD_PUBLISH_DATE = "publishDate";

	/**
	 * This is a multivalued field.
	 * Contains all user ids of all users who gave an "in" to the {@link News}.
	 */
	public static final String FIELD_IN = "in";

	/**
	 * The amount of users who gave an "in" to the {@link News}.
	 */
	public static final String FIELD_INS_AMOUNT = "insAmount";

	/**
	 * This is a multivalued field.
	 * Contains all user ids of all users who gave a "comb" to the {@link News}.
	 */
	public static final String FIELD_COMB = "comb";

	/**
	 * This is a multivalued field.
	 * Contains all user ids of all users who wrote a comment to the {@link News}.
	 */
	public static final String FIELD_COMMENT_OF = "commentOf";

	/**
	 * The amount of users who gave wrote a comment to the {@link News}.
	 */
	public static final String FIELD_COMMENTS_AMOUNT = "commentsAmount";

	/**
	 * The constructor is only accessible for this class and subclasses.
	 */
	protected NewsIndexType() {
		// at the moment nothing to do.
	}

	/**
	 * This class uses the singleton pattern.
	 * This method returns the single instance of this class.
	 */
	public static NewsIndexType getInstance() {
		return INSTANCE;
	}

	/**
	 * Returns the name.
	 */
	@Override
	public String getName() {
		return INDEX_TYPE_NAME;
	}

	/**
	 * Returns all configured fields of this type.
	 *
	 * Available fields:
	 * <ul>
	 * 	<li>{@link NewsIndexType#FIELD_TITLE}</li>
	 *  <li>{@link NewsIndexType#FIELD_TITLE_SORT}</li>
	 *  <li>{@link NewsIndexType#FIELD_DESCRIPTION}</li>
	 *  <li>{@link NewsIndexType#FIELD_LINK}</li>
	 *  <li>{@link NewsIndexType#FIELD_PROVIDERID}</li>
	 *  <li>{@link NewsIndexType#FIELD_CATEGORYID}</li>
	 *  <li>{@link NewsIndexType#FIELD_PROVIDER}</li>
	 *  <li>{@link NewsIndexType#FIELD_CATEGORY}</li>
	 *  <li>{@link NewsIndexType#FIELD_IMAGE_URL}</li>
	 *  <li>{@link NewsIndexType#FIELD_IMAGE_WIDTH}</li>
	 *  <li>{@link NewsIndexType#FIELD_IMAGE_HEIGHT}</li>
	 *  <li>{@link NewsIndexType#FIELD_NEWSGROUPID}</li>
	 *  <li>{@link NewsIndexType#FIELD_PUBLISH_DATE}</li>
	 *  <li>{@link NewsIndexType#FIELD_IN}</li>
	 *  <li>{@link NewsIndexType#FIELD_INS_AMOUNT}</li>
	 *  <li>{@link NewsIndexType#FIELD_COMB}</li>
	 *  <li>{@link NewsIndexType#FIELD_COMMENT_OF}</li>
	 *  <li>{@link NewsIndexType#FIELD_COMMENTS_AMOUNT}</li>
	 * </ul>
	 */
	@Override
	public List<IIndexFieldConf<?>> getFields() {
		final List<IIndexFieldConf<?>> fields = new ArrayList<>();

		fields.add(new TextIndexFieldConf(FIELD_TITLE, true));
		fields.add(new StringIndexFieldConf(FIELD_TITLE_SORT, false));
		fields.add(new TextIndexFieldConf(FIELD_DESCRIPTION, true));
		fields.add(new StringIndexFieldConf(FIELD_LINK, true));
		fields.add(new IntegerIndexFieldConf(FIELD_PROVIDERID, true));
		fields.add(new IntegerIndexFieldConf(FIELD_CATEGORYID, true));
		fields.add(new TextIndexFieldConf(FIELD_PROVIDER, false));
		fields.add(new TextIndexFieldConf(FIELD_CATEGORY, false));
		fields.add(new StringIndexFieldConf(FIELD_IMAGE_URL, true));
		fields.add(new NotIndexedIndexFieldConf(FIELD_IMAGE_WIDTH));
		fields.add(new NotIndexedIndexFieldConf(FIELD_IMAGE_HEIGHT));
		fields.add(new LongIndexFieldConf(FIELD_NEWSGROUPID, true));
		fields.add(new DateIndexFieldConf(FIELD_PUBLISH_DATE, true));
		fields.add(new LongIndexFieldConf(FIELD_IN, false)); // multiple values
		fields.add(new IntegerIndexFieldConf(FIELD_INS_AMOUNT, false));
		fields.add(new LongIndexFieldConf(FIELD_COMB, false)); // multiple values
		fields.add(new LongIndexFieldConf(FIELD_COMMENT_OF, false)); // multiple values
		fields.add(new IntegerIndexFieldConf(FIELD_COMMENTS_AMOUNT, false));

		return fields;
	}

	/**
	 * Returns all {@link News}s. For each {@link News} a {@link NewsIndexElement} is returned.
	 * {@link NewsIndexElement#getOperation()} returns {@link EOperation#INSERT}.
	 */
	@Override
	public List<? extends IIndexElement> getElements(final Connection con, final int start, final int amount) {
		final List<NewsIndexElement> elements = new ArrayList<>();
		final List<News> records = new NewsDao(con).getNews(start, amount);

		for (final News record : records) {
			elements.add(new NewsIndexElement(record, EOperation.INSERT));
		}

		return elements;
	}

	/**
	 * Converts a {@link DocumentsSearchResult} to a {@link ISearchResult} containing {@link News}.
	 * The total hits will be taken from the given {@link DocumentsSearchResult}.
	 * @param documentsResult the {@link DocumentsSearchResult} to convert.
	 * @return {@link ISearchResult} containing {@link News}
	 */
	public static ISearchResult<News> docsToNewsSearchResult(final ISearchResult<Document> documentsResult) {
		return new SimpleSearchResult<>(docsToNews(documentsResult.getResults()), documentsResult.getTotalHits());
	}

	/**
	 * Converts the given {@link Document}s to {@link News} by using the fields in the {@link Document}.
	 * @param docs the {@link Document} to convert.
	 * @return {@link List} (never null) containing the converted {@link News}.
	 */
	public static List<News> docsToNews(final List<Document> docs) {
		final List<News> results = new ArrayList<>();

		for (final Document doc : docs) {
			try {
				final News news = new News();

				news.setId(Long.valueOf(doc.get(IIndexElement.FIELD_ID)));
				news.setTitle(doc.get(NewsIndexType.FIELD_TITLE));
				news.setText(doc.get(NewsIndexType.FIELD_DESCRIPTION));
				news.setPublishDate(new Timestamp(Long.parseLong(doc.get(NewsIndexType.FIELD_PUBLISH_DATE))));
				news.setProviderId(Integer.valueOf(doc.get(NewsIndexType.FIELD_PROVIDERID)));
				news.setCategoryId(Integer.valueOf(doc.get(NewsIndexType.FIELD_CATEGORYID)));
				news.setLocale(doc.get(IIndexElement.FIELD_LOCALE));
				news.setLink(doc.get(NewsIndexType.FIELD_LINK));
				news.setImageUrl(doc.get(NewsIndexType.FIELD_IMAGE_URL));
				news.setImageWidth(Integer.valueOf(doc.get(NewsIndexType.FIELD_IMAGE_WIDTH)));
				news.setImageHeight(Integer.valueOf(doc.get(NewsIndexType.FIELD_IMAGE_HEIGHT)));
				news.setNewsGroupId(Integer.valueOf(doc.get(NewsIndexType.FIELD_NEWSGROUPID)));

				results.add(news);
			}
			catch(final Exception e) {
				LOGGER.error("Can't convert news document to News object because of an Exception.", e);
			}
		}

		return results;
	}

	/**
	 * Groups all inserted {@link News} if they weren't indexed yet.
	 */
	@Override
	public void afterIndexing(final IIndexData indexData) {
		for (final IIndexElement element : indexData.getElements()) {
			if(element instanceof NewsIndexElement) {
				final NewsIndexElement newsElement = (NewsIndexElement) element;
				final News news = newsElement.getNews();

				final Connection con = DBConnectionProvider.getInstance().acquire();
				try {
					new NewsDao(con).setIndexed(news);
					con.commit();
				}
				catch(final Exception e) {
					LOGGER.error("Couldn't save indexed flag on news {}.", news.getId(), e);

					try {
						con.rollback();
					} catch (final SQLException e1) {
						LOGGER.error("Couldn't rollback changes for news {}.", news.getId(), e);
					}
				}
				finally {
					DBConnectionProvider.getInstance().release(con);
				}

				if(element.getOperation() == EOperation.INSERT &&
						news.getNewsGroupId() == News.NEWSGROUPID_NOT_GROUPED_YET) {

					// group the new news with others.
					NewsGrouperThread.getInstance().addNews(news);
				}
			}
		}
	}
}
