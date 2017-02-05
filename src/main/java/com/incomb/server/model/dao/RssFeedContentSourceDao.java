package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.List;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.ContentSource;
import com.incomb.server.model.RssFeedContentSource;
import com.incomb.server.model.dao.internal.InternalRssFeedContentSourceDao;

/**
 * This class can be used to retrieve {@link RssFeedContentSource}s.
 */
public class RssFeedContentSourceDao extends ADao implements IFinder<RssFeedContentSource> {

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalRssFeedContentSourceDao dao;

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public RssFeedContentSourceDao(final Connection connection) {
		super(connection);
		dao = new InternalRssFeedContentSourceDao(jooqConfig);
	}

	/**
	 * @return RssFeedContentSource.class
	 */
	@Override
	public Class<RssFeedContentSource> findForClass() {
		return RssFeedContentSource.class;
	}

	/**
	 * @see #getAllRssFeedContentSources()
	 */
	@Override
	public List<RssFeedContentSource> findAll() {
		return getAllRssFeedContentSources();
	}

	/**
	 * Returns all existing {@link ContentSource}s.
	 * @return {@link List} containing {@link ContentSource}s.
	 */
	public List<RssFeedContentSource> getAllRssFeedContentSources() {
		final List<RssFeedContentSource> list = dao.findAll();

		final Connection connection = jooqConfig.connectionProvider().acquire();

		try {
			final ContentSourceDao csdao = new ContentSourceDao(connection);
			for (final RssFeedContentSource rssFeedContentSource : list) {
				rssFeedContentSource.setSuperObject(csdao.findContentSourceById(rssFeedContentSource.getContentSourceId()));
			}
		}
		finally {
			jooqConfig.connectionProvider().release(connection);
		}

		return list;
	}
}
