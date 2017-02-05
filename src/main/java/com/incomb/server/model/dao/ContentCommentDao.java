package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import com.incomb.server.content.indexing.NewsIndexElement;
import com.incomb.server.content.indexing.NewsIndexType;
import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.indexing.IndexManager;
import com.incomb.server.indexing.SimpleIndexData;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.model.ContentComment;
import com.incomb.server.model.dao.internal.InternalContentCommentDao;
import com.incomb.server.model.tables.ContentCommentTable;

/**
 * DAO class for read and write stuff for {@link ContentComment}s.
 *
 */
public class ContentCommentDao extends ADao {

	/**
	 * The table to build SQL queries.
	 */
	private static final ContentCommentTable COMMENT_TABLE = new ContentCommentTable();

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalContentCommentDao dao;

	/**
	 * Creates a new instance and acquires for each query a new {@link Connection}
	 * from the {@link DBConnectionProvider}.
	 * If you want to do write operations than please use {@link #ContentCommentDao(Connection)}.
	 */
	public ContentCommentDao() {
		this(null);
	}

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public ContentCommentDao(final Connection connection) {
		super(connection);
		this.dao = new InternalContentCommentDao(jooqConfig);
	}

	/**
	 * Returns the {@link ContentComment} for the given id.
	 */
	public List<ContentComment> getCommentsByContentId(final long contentId) {
		return dao.fetchByContentId(contentId);
	}

	/**
	 * Counts all {@link ContentComment} made by the given user.
	 */
	public int getCommentsAmountForUser(final long userId) {
		return getCommentsAmount(COMMENT_TABLE.USER_ID.eq(userId));
	}

	/**
	 * get number of comments of a given contentId
	 * @param contentId
	 * @return
	 */
	public int getCommentsAmountForContentId(final long contentId) {
		return getCommentsAmount(COMMENT_TABLE.CONTENT_ID.eq(contentId));
	}

	/**
	 * Returns all {@link ContentComment} for the given user which match the offset and count criteria.
	 */
	public List<ContentComment> getCommentsOfUser(final long userId, final int offset, final int count) {
		return DSL.using(jooqConfig).
				select(COMMENT_TABLE.fields()).
				from(COMMENT_TABLE).
				where(COMMENT_TABLE.USER_ID.eq(userId)).
				orderBy(COMMENT_TABLE.COMMENT_DATE.desc()).
				limit(offset, count).
				fetchInto(ContentComment.class);
	}

	/**
	 * Inserts a new {@link ContentComment} into the database.
	 */
	public void insertComment(final ContentComment comment) {
		dao.insert(comment);
		comment.setId(getContentIdFromUserAndContentId(comment));

		final SimpleIndexData indexData = new SimpleIndexData(NewsIndexType.getInstance());
		indexData.addElement(new NewsIndexElement(new NewsDao().getNews(comment.getContentId()), EOperation.UPDATE));
		IndexManager.getInstance().index(indexData);
	}

	/**
	 * Returns the comment id of the given {@link ContentComment}.
	 * This is useful after the {@link ContentComment} was inserted.
	 * @param comment the {@link ContentComment} to get the comment id of it.
	 * @return the comment id.
	 */
	private long getContentIdFromUserAndContentId(final ContentComment comment) {
		return DSL.using(jooqConfig).
			select(COMMENT_TABLE.ID).
			from(COMMENT_TABLE).
			where(COMMENT_TABLE.USER_ID.eq(comment.getUserId())).
			and(COMMENT_TABLE.CONTENT_ID.eq(comment.getContentId())).
			orderBy(COMMENT_TABLE.COMMENT_DATE.desc()).
			limit(0, 1).
			fetchOne(COMMENT_TABLE.ID);
	}

	/**
	 * Updates the given {@link ContentComment} in the database.
	 */
	public void updateComment(final ContentComment comment) {
		dao.update(comment);
		final SimpleIndexData indexData = new SimpleIndexData(NewsIndexType.getInstance());
		indexData.addElement(new NewsIndexElement(new NewsDao().getNews(comment.getContentId()), EOperation.UPDATE));
		IndexManager.getInstance().index(indexData);
	}


	/**
	 * Returns the amount of of comments for the given condition
	 */
	private int getCommentsAmount(final Condition cond) {
		return DSL.using(jooqConfig).
				select(DSL.count()).
				from(COMMENT_TABLE).
				where(cond).fetchOne(DSL.count());
	}
}
