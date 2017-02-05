package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.List;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.TagPreference;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.internal.InternalTagPreferenceDao;

/**
 * This DAO is to retrieve and write {@link TagPreference}s.
 */
public class TagPreferenceDao extends ADao {

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalTagPreferenceDao dao;

	/**
	 * Creates a new instance and acquires for each query a new {@link Connection}
	 * from the {@link DBConnectionProvider}.
	 * If you want to do write operations than please use {@link #TagPreferenceDao(Connection)}.
	 */
	public TagPreferenceDao() {
		this(null);
	}

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public TagPreferenceDao(final Connection connection) {
		super(connection);
		dao = new InternalTagPreferenceDao(jooqConfig);
	}

	/**
	 * Returns all {@link TagPreference}s for the given {@link User}.
	 * @param userId the id of the {@link User}.
	 * @return {@link List} containing {@link TagPreference}s.
	 */
	public List<TagPreference> getTagPreferences(final long userId) {
		return dao.fetchByUserId(userId);
	}

	/**
	 * Saves the given not existing {@link TagPreference}.
	 * @param tagPreference the {@link TagPreference} to insert.
	 */
	public void addTagPreference(final TagPreference tagPreference) {
		dao.insert(tagPreference);
	}

	/**
	 * Removes the existing {@link TagPreference}.
	 * @param userId the user id of the {@link TagPreference}.
	 * @param tag the tag of the {@link TagPreference}.
	 */
	public void deleteTagPreference(final long userId, final String tag) {
		dao.delete(new TagPreference(userId, tag));
	}
}
