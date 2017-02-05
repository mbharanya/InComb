package com.incomb.server.model.dao;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.jooq.impl.DSL;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.ContentSource;
import com.incomb.server.model.FetchHistory;
import com.incomb.server.model.dao.internal.InternalFetchHistoryDao;
import com.incomb.server.model.tables.FetchHistoryTable;

/**
 * This DAO can be used to read and write {@link FetchHistory}s.
 */
public class FetchHistoryDao extends ADao {

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalFetchHistoryDao dao;

	/**
	 * Creates a new instance and acquires for each query a new {@link Connection}
	 * from the {@link DBConnectionProvider}.
	 * If you want to do write operations than please use {@link #ContentVoteDao(Connection)}.
	 */
	public FetchHistoryDao() {
		this(null);
	}

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public FetchHistoryDao(final Connection connection) {
		super(connection);
		dao = new InternalFetchHistoryDao(jooqConfig);
	}

	/**
	 * This {@link Map} contains the latest {@link FetchHistory} for each {@link ContentSource}.
	 * The key of the {@link Map} is the content source id. If an entry for a content source id
	 * doesn't exist then it wasn't queried from the database yet. Use {@link #getLastFetchHistoryFor(ContentSource)}.
	 */
	private static final Map<Integer, FetchHistory> lastFetches = new HashMap<Integer, FetchHistory>();

	/**
	 * Returns the latest {@link FetchHistory} from the given {@link ContentSource}.
	 * @param contentSource the {@link ContentSource} for which the last {@link FetchHistory} will be returned.
	 * @return {@link FetchHistory} or <code>null</code> if no fetch was made yet.
	 */
	public FetchHistory getLastFetchHistoryFor(final ContentSource contentSource) {
		if(!lastFetches.containsKey(contentSource.getId())) {
			final FetchHistoryTable table = new FetchHistoryTable();

			final FetchHistory item = DSL.using(jooqConfig).
					selectFrom(table).
					where(table.CONTENT_SOURCE_ID.equal(contentSource.getId())).
					orderBy(table.FETCH_TIME.desc()).
					limit(1).
					fetchOneInto(FetchHistory.class);

			lastFetches.put(contentSource.getId(), item);
		}

		return lastFetches.get(contentSource.getId());
	}

	/**
	 * Saves the given not existing {@link FetchHistory} to the database.
	 * @param history the {@link FetchHistory} to save.
	 */
	public void addFetchHistory(final FetchHistory history) {
		dao.insert(history);
		lastFetches.put(history.getContentSourceId(), history);
	}
}
