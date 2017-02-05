package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.User;
import com.incomb.server.model.UserLocale;
import com.incomb.server.model.dao.internal.InternalUserLocaleDao;

/**
 * Single class for getting {@link UserLocale} and write them to the database.
 */
public class UserLocaleDao extends ADao {

	/**
	 * Jooqs DAO for helpers to execute statements in the database.
	 */
	private final InternalUserLocaleDao dao;

	/**
	 * Creates a new instance and acquires for each query a new {@link Connection}
	 * from the {@link DBConnectionProvider}.
	 * If you want to do write operations than please use {@link #UserLocaleDao(Connection)}.
	 */
	public UserLocaleDao() {
		this(null);
	}

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public UserLocaleDao(final Connection connection) {
		super(connection);
		this.dao = new InternalUserLocaleDao(jooqConfig);
	}

	/**
	 * Returns all {@link Locale} for the given {@link User}.
	 * @param userId the {@link User} to filter the {@link UserLocale}s.
	 * @return a {@link List} with users {@link Locale}.
	 */
	public List<Locale> getLocalesForUser(final long userId) {
		final List<Locale> locales = new ArrayList<>();

		for (final UserLocale userLocale : dao.fetchByUserId(userId)) {
			locales.add(userLocale.getLocale());
		}

		return locales;
	}

	/**
	 * Adds the given {@link UserLocale} to the database.
	 * @param userLocale the {@link UserLocale} to add.
	 */
	public void addUserLocale(final UserLocale userLocale) {
		dao.insert(userLocale);
	}

	/**
	 * Removes the given {@link UserLocale} from the database.
	 * @param userLocale {@link UserLocale} to remove.
	 */
	public void removeUserLocale(final UserLocale userLocale) {
		dao.delete(userLocale);
	}
}
