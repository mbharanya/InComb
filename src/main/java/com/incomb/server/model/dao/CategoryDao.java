package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.document.Document;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.Module;
import com.incomb.server.categories.indexing.CategoryIndexType;
import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.indexing.DocumentsSearchResult;
import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.ISearchResult;
import com.incomb.server.indexing.IndexSearch;
import com.incomb.server.indexing.SearchOptions;
import com.incomb.server.indexing.SimpleSearchResult;
import com.incomb.server.model.Category;
import com.incomb.server.model.dao.internal.InternalCategoryDao;
import com.incomb.server.model.tables.CategoryTable;

/**
 * This DAO should be used to retrieve {@link Category}s.
 * It handles the database and the index of {@link Category}s.
 */
public class CategoryDao extends ADao {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryDao.class);

	/**
	 * Max age in milliseconds of {@link #CATEGORIES}.
	 */
	private static final long MAX_AGE_CACHE = 10*60*1000; // 10 minutes

	/**
	 * This is the cache of all {@link Category}s. The {@link Integer} is the category id.
	 */
	private static Map<Integer, Category> CATEGORIES = new HashMap<>();

	/**
	 * Time in milliseconds since {@link #CATEGORIES} was last loaded.
	 */
	private static long lastLoadTime = 0;

	/**
	 * The table to build SQL queries.
	 */
	private static final CategoryTable TABLE = new CategoryTable();

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalCategoryDao dao;

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public CategoryDao(final Connection connection) {
		super(connection);
		dao = new InternalCategoryDao(jooqConfig);
	}

	/**
	 * Internal getter for the {@link #CATEGORIES}.
	 * If the {@link Map} wasn't loaded yet or if {value #MAX_AGE_CACHE} was reached than it will be loaded now.
	 *
	 * @return {@link #CATEGORIES} after all checks are done.
	 */
	private Map<Integer, Category> getCategoriesMap() {
		// lastLoadTime == 0 means it wasn't loaded yet.
		if(lastLoadTime == 0 || System.currentTimeMillis() - lastLoadTime > MAX_AGE_CACHE) {
			synchronized (CategoryDao.class) {
				// check again because synchronized
				if(lastLoadTime == 0 || System.currentTimeMillis() - lastLoadTime > MAX_AGE_CACHE) {
					// we don't clear the old Map so we don't have to synchronize read access to the Map.
					final Map<Integer, Category> newCache = new HashMap<>();

					for (final Category category : dao.findAll()) {
						newCache.put(Integer.valueOf(category.getId()), category);
					}

					CATEGORIES = newCache;
				}
			}
		}

		return CATEGORIES;
	}

	/**
	 * Returns all existing {@link Category}s.
	 * The result isn't sorted.
	 * @return {@link List} containing {@link Category}.
	 */
	public List<Category> getCategories() {
		return new ArrayList<>(getCategoriesMap().values());
	}

	/**
	 * Returns the {@link Category}s for the given {@link Module}.
	 * The result isn't sorted.
	 * @param moduleId the {@link Module} to filter.
	 * @return {@link List} containing {@link Category}.
	 */
	public List<Category> getCategories(final int moduleId) {
		final List<Category> categories = new ArrayList<>();

		for (final Category category : getCategories()) {
			if(category.getModuleId() == moduleId) {
				categories.add(category);
			}
		}

		return categories;
	}

	/**
	 * Returns the {@link Category}s from the database.
	 * The result isn't sorted. This method can be used for reindexing.
	 * @param start start index of the results
	 * @param amount the amount of {@link Category}s to return after the start index.
	 * @return {@link List} containing {@link Category}.
	 */
	public List<Category> getCategories(final int start, final int amount) {
		return DSL.using(jooqConfig).select().from(TABLE).limit(start, amount).fetchInto(Category.class);
	}

	/**
	 * Returns the {@link Category} for the given id.
	 * @param categoryId the id to find the {@link Category}.
	 * @return {@link Category} or <code>null</code> if not found.
	 */
	public Category getCategory(final int categoryId) {
		return getCategoriesMap().get(Integer.valueOf(categoryId));
	}

	/**
	 * Searches all {@link Category}s with the given {@link Locale} for the given search text
	 * and returns at best the given amount.
	 * @param searchText the text to search for.
	 * @param locale the {@link Locale} to filter the {@link Category}s.
	 * 			If <code>null</code> then all {@link Category}s will be searched through.
	 * @param amount the maximum amount of results
	 * @return {@link ISearchResult} containing the found {@link Category}s.
	 */
	public ISearchResult<Category> findCategories(final String searchText, final Locale locale, final int amount) {
		final SearchOptions options = new SearchOptions();
		options.setMaxResults(amount);

		return docsToCategories(IndexSearch.getInstance().search(searchText,
				CategoryIndexType.getInstance(), locale, options));
	}

	/**
	 * Converts the given {@link DocumentsSearchResult} to a {@link ISearchResult} containing
	 * the found {@link Category}s. It extracts all category ids from the {@link Document}s
	 * and queries the database for them.
	 * @param docsResult the {@link DocumentsSearchResult} to convert.
	 * @return {@link ISearchResult} containing the {@link Category}s.
	 */
	private ISearchResult<Category> docsToCategories(final DocumentsSearchResult docsResult) {
		final List<Category> categories = new ArrayList<>();

		for (final Document doc : docsResult.getResults()) {
			final String categoryId = doc.get(IIndexElement.FIELD_ID);
			if(NumberUtils.isNumber(categoryId)) {
				categories.add(getCategory(Integer.parseInt(categoryId)));
			}
			else {
				LOGGER.error("Not numeric category id from index {}.", categoryId);
			}
		}

		return new SimpleSearchResult<>(categories, docsResult.getTotalHits());
	}
}
