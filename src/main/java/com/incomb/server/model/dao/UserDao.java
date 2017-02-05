package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.document.Document;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.incomb.server.model.User;
import com.incomb.server.model.dao.internal.InternalUserDao;
import com.incomb.server.model.tables.UserTable;
import com.incomb.server.users.indexing.UserIndexElement;
import com.incomb.server.users.indexing.UserIndexType;

/**
 * This DAO can be used to retrieve and save {@link User}s.
 * It stores the {@link User}s in the database and index.
 */
public class UserDao extends ADao implements IRecordExistsChecker {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

	/**
	 * The table to build SQL queries.
	 */
	private static final UserTable TABLE = new UserTable();

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalUserDao userDao;

	/**
	 * Creates a new instance and acquires for each query a new {@link Connection}
	 * from the {@link DBConnectionProvider}.
	 * If you want to do write operations than please use {@link #UserDao(Connection)}.
	 */
	public UserDao() {
		this(null);
	}

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public UserDao(final Connection connection) {
		super(connection);
		userDao = new InternalUserDao(jooqConfig);
	}

	/**
	 * Returns the {@link User} with the given id.
	 * @param id the id of the {@link User} to return.
	 * @return {@link User} or <code>null</code> if not found.
	 */
	public User findById(final long id) {
		return userDao.findById(id);
	}

	/**
	 * Returns the {@link User} with the given username.
	 * @param username the username of the {@link User} to return.
	 * @return {@link User} or <code>null</code> if not found.
	 */
	public User findByUsername(final String username) {
		return userDao.fetchOneByUsername(username);
	}

	/**
	 * Sets the deleted flag on the given {@link User}.
	 * @param id the id of the {@link User} to delete.
	 */
	public void deleteUser(final long id) {
		final User user = userDao.findById(id);
		user.setDeleted(true);
		userDao.update(user);
	}

	/**
	 * Saves the given non existing {@link User}.
	 * @param user the {@link User} to insert.
	 */
	public void insert(final User user) {
		final SimpleIndexData indexData = new SimpleIndexData(UserIndexType.getInstance());

		userDao.insert(user);

		// set generated user id
		user.setId(getIdForUsername(user.getUsername()));

		// update index
		indexData.addElement(new UserIndexElement(user, EOperation.INSERT));
		IndexManager.getInstance().index(indexData);
	}

	/**
	 * Returns the user id for the given username.
	 * @param username the username to find the {@link User}.
	 * @return the id of the {@link User}.
	 */
	private long getIdForUsername(final String username) {
		final Record1<Long> idRecord = DSL.using(jooqConfig).
				select(TABLE.ID).
				from(TABLE).
				where(TABLE.USERNAME.eq(username)).
				fetchOne();

		if(idRecord == null) {
			return 0;
		}

		return idRecord.getValue(TABLE.ID);
	}

	/**
	 * Saves the given existing {@link User}.
	 * @param user the {@link User} to update.
	 */
	public void update(final User user) {
		userDao.update(user);

		// update index
		final SimpleIndexData indexData = new SimpleIndexData(UserIndexType.getInstance());
		indexData.addElement(new UserIndexElement(user, EOperation.UPDATE));
		IndexManager.getInstance().index(indexData);
	}

	/**
	 * Returns the {@link User}s from the database.
	 * The result isn't sorted. This method can be used for reindexing.
	 * @param start start index of the results
	 * @param amount the amount of {@link User}s to return after the start index.
	 * @return {@link List} containing {@link User}
	 */
	public List<User> getUsers(final int start, final int amount) {
		return DSL.using(jooqConfig).select().from(TABLE).limit(start, amount).fetchInto(User.class);
	}

	/**
	 * Checks if a {@link User} exists with the given value in the given field.
	 * @return <code>true</code> if a {@link User} exists.
	 */
	@Override
	public boolean recordExists(final String fieldName, final Object value) {
		final Record1<Integer> count = DSL.using(jooqConfig).
				select(DSL.count()).
				from(TABLE).
				where(DSL.fieldByName(fieldName).eq(value)).
				fetchOne();

		return count.getValue(0, int.class) > 0;
	}

	/**
	 * Searches all {@link User}s with the given search text.
	 * @param searchText the text to search
	 * @param amount the maximum of results.
	 * @return an {@link ISearchResult} containing {@link User}s with the results and the total hits.
	 */
	public ISearchResult<User> findUsers(final String searchText, final int amount) {
		final SearchOptions options = new SearchOptions();
		options.setMaxResults(amount);

		return docsToUsers(IndexSearch.getInstance().search(searchText,
				UserIndexType.getInstance(), null, options)); // search isn't language dependent.
	}

	/**
	 * Converts the given {@link DocumentsSearchResult} to a {@link ISearchResult} containing
	 * the found {@link User}s. It extracts all user ids from the {@link Document}s
	 * and queries the database for them.
	 * @param docsResult the {@link DocumentsSearchResult} to convert.
	 * @return {@link ISearchResult} containing the {@link User}s.
	 */
	private ISearchResult<User> docsToUsers(final DocumentsSearchResult docsResult) {
		final List<Long> userIds = new ArrayList<>();

		for (final Document doc : docsResult.getResults()) {
			final String userId = doc.get(IIndexElement.FIELD_ID);
			if(NumberUtils.isNumber(userId)) {
				userIds.add(Long.valueOf(userId));
			}
			else {
				LOGGER.error("Not numeric user id from index {}.", userId);
			}
		}

		// no results -> return empty ISearchResult
		if(userIds.isEmpty()) {
			return new SimpleSearchResult<User>(new ArrayList<User>(), docsResult.getTotalHits());
		}

		return new SimpleSearchResult<>(DSL.using(jooqConfig).
				select().
				from(TABLE).
				where(TABLE.ID.in(userIds)).
				orderBy(DSL.field("FIELD(id, " + StringUtils.join(userIds, ", ") + ")", Long.class)).
				fetchInto(User.class), docsResult.getTotalHits());
	}
}
