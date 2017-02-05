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

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.indexing.DocumentsSearchResult;
import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.ISearchResult;
import com.incomb.server.indexing.IndexSearch;
import com.incomb.server.indexing.SearchOptions;
import com.incomb.server.indexing.SimpleSearchResult;
import com.incomb.server.model.ContentSource;
import com.incomb.server.model.Provider;
import com.incomb.server.model.dao.internal.InternalProviderDao;
import com.incomb.server.model.tables.ProviderTable;
import com.incomb.server.providers.indexing.ProviderIndexType;

/**
 * The single point to get {@link Provider}s.
 * The {@link Provider}s will be cached and reloaded after {@link #MAX_AGE_CACHE}.
 */
public class ProviderDao extends ADao {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderDao.class);

	/**
	 * The {@link ProviderTable} to access the columns to build SQL.
	 */
	private static final ProviderTable TABLE = new ProviderTable();

	/**
	 * Max age in milliseconds of {@link #PROVIDERS}.
	 */
	private static final long MAX_AGE_CACHE = 10*60*1000; // 10 minutes

	/**
	 * Cache of {@link Provider}s. Key is {@link Provider#getId()}.
	 */
	private static Map<Integer, Provider> PROVIDERS = new HashMap<>();

	/**
	 * Time in milliseconds since {@link #PROVIDERS} was last loaded.
	 */
	private static long lastLoadTime = 0;

	/**
	 * Jooqs {@link InternalProviderDao} for simple queries.
	 */
	private final InternalProviderDao dao;

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public ProviderDao(final Connection connection) {
		super(connection);
		dao = new InternalProviderDao(jooqConfig);
	}

	/**
	 * Returns the {@link Provider} for the given key.
	 * @param providerId the provider id of the {@link Provider} to return
	 * @return {@link Provider} or null if not found.
	 */
	public Provider getProvider(final int providerId) {
		return getProvidersMap().get(Integer.valueOf(providerId));
	}

	/**
	 * Returns all {@link Provider}s.
	 */
	public List<Provider> getProviders() {
		return new ArrayList<>(getProvidersMap().values());
	}

	/**
	 * Returns all {@link Provider}s with the given name (case insensitive).
	 * @param name the name to find {@link Provider}s.
	 * @return {@link Provider} or null if not found.
	 */
	public List<Provider> getProvidersByName(final String name) {
		final List<Provider> returnValue = new ArrayList<>();

		for (final Provider provider : getProviders()) {
			if(provider.getName().equalsIgnoreCase(name)) {
				returnValue.add(provider);
			}
		}

		return returnValue;
	}

	/**
	 * Internal getter for the {@link #PROVIDERS}.
	 * If the {@link Map} wasn't loaded yet or if {value #MAX_AGE_CACHE} was reached than it will be loaded now.
	 *
	 * @return {@link #PROVIDERS} after all checks are done.
	 */
	private Map<Integer, Provider> getProvidersMap() {
		// lastLoadTime == 0 means it wasn't loaded yet.
		if(lastLoadTime == 0 || System.currentTimeMillis() - lastLoadTime > MAX_AGE_CACHE) {
			synchronized (ProviderDao.class) {
				// check again because synchronized
				if(lastLoadTime == 0 || System.currentTimeMillis() - lastLoadTime > MAX_AGE_CACHE) {
					// we don't clear the old Map so we don't have to synchronize read access to the Map.
					final Map<Integer, Provider> newCache = new HashMap<>();

					for (final Provider provider : dao.findAll()) {
						newCache.put(Integer.valueOf(provider.getId()), provider);
					}

					PROVIDERS = newCache;
				}
			}
		}

		return PROVIDERS;
	}

	/**
	 * Returns {@link Provider}s directly from the database. Can be used for building the index.
	 * @param start start position to return the {@link Provider}s.
	 * @param amount the maximum of {@link Provider}s.
	 * @return a {@link List} with {@link Provider}s.
	 */
	public List<Provider> getProviders(final int start, final int amount) {
		return DSL.using(jooqConfig).select().from(TABLE).limit(start, amount).fetchInto(Provider.class);
	}

	/**
	 * Searches in the lucene index for the given search text and the locale and returns not more than the given amount.
	 * @param searchText the text to search with
	 * @param locale return only {@link Provider} with {@link ContentSource}s in the given {@link Locale}.
	 * 			If {@link Locale} is null than no locale check will made.
	 * @param amount the maximum of results.
	 * @return an {@link ISearchResult} containing {@link Provider} with the results and the total hits.
	 */
	public ISearchResult<Provider> findProviders(final String searchText, final Locale locale, final int amount) {
		final SearchOptions options = new SearchOptions();
		options.setMaxResults(amount);

		return docsToProviders(IndexSearch.getInstance().search(searchText,
				ProviderIndexType.getInstance(), locale, options));
	}

	/**
	 * Builds {@link Provider}s for the given {@link Document}s.
	 * @param docsResult the {@link Document}s to convert.
	 * @return a new {@link ISearchResult} containing the {@link Provider}s and the
	 * 			totalHits from the given {@link DocumentsSearchResult}.
	 */
	private ISearchResult<Provider> docsToProviders(final DocumentsSearchResult docsResult) {
		final List<Provider> providers = new ArrayList<>();

		for (final Document doc : docsResult.getResults()) {
			final String providerId = doc.get(IIndexElement.FIELD_ID);
			if(NumberUtils.isNumber(providerId)) {
				providers.add(getProvider(Integer.parseInt(providerId)));
			}
			else {
				LOGGER.error("Not numeric user id from index {}.", providerId);
			}
		}

		return new SimpleSearchResult<>(providers, docsResult.getTotalHits());
	}
}
