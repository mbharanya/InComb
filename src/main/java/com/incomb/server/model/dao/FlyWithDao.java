package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.List;

import org.jooq.impl.DSL;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.FlyWith;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.internal.InternalFlyWithDao;
import com.incomb.server.model.tables.FlyWithTable;
import com.incomb.server.model.tables.UserTable;

/**
 * This DAO can be used to read and write {@link FlyWith}s.
 */
public class FlyWithDao extends ADao {

	/**
	 * The table to build SQL queries.
	 */
	private static final FlyWithTable FLY_WITH_TABLE = new FlyWithTable();

	/**
	 * The table to build SQL queries.
	 */
	private static final UserTable USER_TABLE = new UserTable();

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalFlyWithDao dao;

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public FlyWithDao(final Connection connection) {
		super(connection);
		this.dao = new InternalFlyWithDao(jooqConfig);
	}

	/**
	 * Returns all {@link User}s who are flying with the given {@link User}.
	 */
	public List<User> getWithFlyingsOf(final long userId) {
		return DSL.using(jooqConfig).
			select(USER_TABLE.fields()).
			from(USER_TABLE).
			join(FLY_WITH_TABLE).
				on(USER_TABLE.ID.eq(FLY_WITH_TABLE.USER_ID)).
			where(FLY_WITH_TABLE.FLY_WITH_ID.eq(userId)).
			fetchInto(User.class);
	}

	/**
	 * Returns all {@link User} which the given {@link User} is flying with.
	 */
	public List<User> getFlyWiths(final long userId) {
		return DSL.using(jooqConfig).
				select(USER_TABLE.fields()).
				from(USER_TABLE).
				join(FLY_WITH_TABLE).
					on(USER_TABLE.ID.eq(FLY_WITH_TABLE.FLY_WITH_ID)).
				where(FLY_WITH_TABLE.USER_ID.eq(userId)).
				fetchInto(User.class);
	}

	/**
	 * Saves a new {@link FlyWith}.
	 * @param flyWith the {@link FlyWith} to save.
	 */
	public void insertFlyWith(final FlyWith flyWith) {
		dao.insert(flyWith);
	}

	/**
	 * Stops the given userId from flying with given flyWithId.
	 */
	public void deleteFlyWith(final long userId, final long flyWithId) {
		dao.delete(new FlyWith(userId, flyWithId, null));
	}
}
