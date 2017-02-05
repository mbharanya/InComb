package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.Category;
import com.incomb.server.model.CategoryPreference;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.internal.InternalCategoryPreferenceDao;
import com.incomb.server.model.tables.CategoryPreferenceTable;

/**
 * This DAO should be used to retrieve and save {@link CategoryPreference}s.
 * (At the moment,) it operates only with the database.
 */
public class CategoryPreferenceDao extends ADao {

	/**
	 * The table to build SQL queries.
	 */
	private static final CategoryPreferenceTable TABLE = new CategoryPreferenceTable();

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalCategoryPreferenceDao dao;

	/**
	 * Creates a new instance and acquires for each query a new {@link Connection}
	 * from the {@link DBConnectionProvider}.
	 * If you want to do write operations than please use {@link #CategoryPreferenceDao(Connection)}.
	 */
	public CategoryPreferenceDao() {
		this(null);
	}

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public CategoryPreferenceDao(final Connection connection) {
		super(connection);
		dao = new InternalCategoryPreferenceDao(jooqConfig);
	}

	/**
	 * Returns all {@link CategoryPreference}s for the given {@link User}.
	 * @param userId the {@link User} to return its {@link CategoryPreference}s.
	 * @return {@link List} containing the {@link CategoryPreference}s.
	 */
	public List<CategoryPreference> getCategoryPreferences(final long userId) {
		return dao.fetchByUserId(userId);
	}

	/**
	 * Saves the given already existing {@link CategoryPreference} to the database.
	 * @param preference the {@link CategoryPreference} to update.
	 */
	public void updateCategoryPreference(final CategoryPreference preference) {
		dao.update(preference);
	}

	/**
	 * Creates {@link CategoryPreference}s for the given {@link User} and every existing {@link Category}
	 * with the factor <code>1.0</code>.
	 * @param userId the {@link User} for which the {@link CategoryPreference}s should be created for.
	 */
	public void createCategoryPreferences(final long userId) {
		final Connection con = jooqConfig.connectionProvider().acquire();
		try {
			final CategoryDao categoryDao = new CategoryDao(con);
			final List<Category> categories = categoryDao.getCategories();

			final List<CategoryPreference> preferences = new ArrayList<>();
			for (final Category category : categories) {
				preferences.add(new CategoryPreference(userId, category.getId(), 1.0));
			}

			dao.insert(preferences);
		}
		finally {
			jooqConfig.connectionProvider().release(con);
		}
	}
}
