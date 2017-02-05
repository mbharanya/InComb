package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.List;

import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.CombItem;
import com.incomb.server.model.Content;
import com.incomb.server.model.News;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.internal.InternalCombItemDao;
import com.incomb.server.model.tables.CombItemTable;
import com.incomb.server.model.tables.ContentTable;
import com.incomb.server.model.tables.NewsTable;

/**
 * This DAO should be used to retrieve and save {@link CombItem}s.
 * (At the moment,) it operates only with the database.
 */
public class CombItemDao extends ADao {

	/**
	 * The table to build SQL queries.
	 */
	private static final CombItemTable COMB_ITEM_TABLE = new CombItemTable();

	/**
	 * The table to build SQL queries.
	 */
	private static final ContentTable CONTENT_TABLE = new ContentTable();

	/**
	 * The table to build SQL queries.
	 */
	private static final NewsTable NEWS_TABLE = new NewsTable();

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalCombItemDao dao;

	/**
	 * Creates a new instance and acquires for each query a new {@link Connection}
	 * from the {@link DBConnectionProvider}.
	 * If you want to do write operations than please use {@link #CombItemDao(Connection)}.
	 */
	public CombItemDao() {
		this(null);
	}

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public CombItemDao(final Connection connection) {
		super(connection);
		this.dao = new InternalCombItemDao(jooqConfig);
	}

	/**
	 * Returns the {@link CombItem} of the given {@link User} and {@link Content}
	 * @param contentId the {@link Content} to find the {@link CombItem}.
	 * @param userId the {@link User} to find the {@link CombItem}.
	 * @return {@link CombItem} or <code>null</code> if it doesn't exist.
	 */
	public CombItem getCombItem(final long contentId, final long userId) {
		return DSL.using(jooqConfig).
				select().
				from(COMB_ITEM_TABLE).
				where(COMB_ITEM_TABLE.CONTENT_ID.eq(contentId)).
				and(COMB_ITEM_TABLE.USER_ID.eq(userId)).
				fetchOneInto(CombItem.class);
	}

	/**
	 * Returns all {@link News} where a {@link CombItem} of the given {@link User} exists.
	 * The News are sorted by the add_date descending.
	 * @param userId the {@link User} to get the {@link CombItem}s.
	 * @param onlyUnread if <code>true</code> only {@link CombItem} with no read_date
	 * 			will be returned.
	 * @return {@link List} containing {@link News}.
	 */
	public List<News> getCombItems(final long userId, final boolean onlyUnread) {
		final SelectConditionStep<Record> sql = DSL.using(jooqConfig).
				select().
				from(COMB_ITEM_TABLE.
					join(CONTENT_TABLE).
						on(COMB_ITEM_TABLE.CONTENT_ID.eq(CONTENT_TABLE.ID)).
					join(NEWS_TABLE).
						on(CONTENT_TABLE.ID.eq(NEWS_TABLE.CONTENT_ID))).
				where(COMB_ITEM_TABLE.USER_ID.eq(userId));

		if(onlyUnread) {
			sql.and(COMB_ITEM_TABLE.READ_DATE.isNull());
		}

		return sql.orderBy(COMB_ITEM_TABLE.ADD_DATE.desc()).
				fetchInto(News.class);
	}

	/**
	 * Saves the given non existing {@link CombItem}.
	 * @param item the {@link CombItem} to insert.
	 */
	public void insertCombItem(final CombItem item) {
		dao.insert(item);
	}

	/**
	 * Saves the given already existing {@link CombItem}.
	 * @param item the {@link CombItem} to update.
	 */
	public void updateCombItem(final CombItem item) {
		dao.update(item);
	}

	/**
	 * Removes the {@link CombItem} with the given key.
	 * @param userId the owner of the {@link CombItem}.
	 * @param contentId the {@link Content} referenced of the {@link CombItem}.
	 */
	public void removeCombItem(final long userId, final long contentId) {
		dao.delete(new CombItem(userId, contentId, null, null));
	}
}
