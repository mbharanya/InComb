package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.List;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.ProviderExclusion;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.internal.InternalProviderExclusionDao;

/**
 * This DAO must be used to get the {@link ProviderExclusion}s.
 */
public class ProviderExclusionDao extends ADao {

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalProviderExclusionDao dao;

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public ProviderExclusionDao(final Connection connection) {
		super(connection);
		this.dao = new InternalProviderExclusionDao(jooqConfig);
	}

	/**
	 * Returns all {@link ProviderExclusion} for the given {@link User}.
	 * @param userId the id of the {@link User}.
	 * @return {@link List} containing {@link ProviderExclusion}s.
	 */
	public List<ProviderExclusion> getProviderExclusions(final long userId) {
		return dao.fetchByUserId(userId);
	}
}
