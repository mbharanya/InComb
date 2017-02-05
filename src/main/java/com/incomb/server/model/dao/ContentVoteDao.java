package com.incomb.server.model.dao;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import com.incomb.server.content.indexing.NewsIndexElement;
import com.incomb.server.content.indexing.NewsIndexType;
import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.indexing.IndexManager;
import com.incomb.server.indexing.SimpleIndexData;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.model.ContentVote;
import com.incomb.server.model.News;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.internal.InternalContentVoteDao;
import com.incomb.server.model.tables.ContentTable;
import com.incomb.server.model.tables.ContentVoteTable;
import com.incomb.server.model.tables.NewsTable;
import com.incomb.server.model.tables.UserTable;

/**
 * DAO class for read and write stuff for {@link ContentVote}s.
 */
public class ContentVoteDao extends ADao {

	/**
	 * The table to build SQL queries.
	 */
	private static final UserTable        USER_TABLE    = new UserTable();

	/**
	 * The table to build SQL queries.
	 */
	private static final ContentVoteTable VOTE_TABLE    = new ContentVoteTable();

	/**
	 * The table to build SQL queries.
	 */
	private static final ContentTable     CONTENT_TABLE = new ContentTable();

	/**
	 * The table to build SQL queries.
	 */
	private static final NewsTable        NEWS_TABLE    = new NewsTable();

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalContentVoteDao dao;

	/**
	 * Creates a new instance and acquires for each query a new {@link Connection}
	 * from the {@link DBConnectionProvider}.
	 * If you want to do write operations than please use {@link #ContentVoteDao(Connection)}.
	 */
	public ContentVoteDao() {
		this(null);
	}

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public ContentVoteDao(final Connection connection) {
		super(connection);
		dao = new InternalContentVoteDao(jooqConfig);
	}

	/**
	 * Saves the given {@link ContentVote} to the database.
	 * @param vote the {@link ContentVote} to insert.
	 */
	public void addVote(final ContentVote vote){
		vote.setVoteDate(new Timestamp(new Date().getTime()));

		if (getVoteAmount(VOTE_TABLE.CONTENT_ID.eq(vote.getContentId()).and(VOTE_TABLE.USER_ID.eq(vote.getUserId()))) <= 0){
			dao.insert(vote);
		}else{
			dao.update(vote);
		}

		// update the news element because of the comment amount field.
		final SimpleIndexData indexData = new SimpleIndexData(NewsIndexType.getInstance());
		indexData.addElement(new NewsIndexElement(new NewsDao().getNews(vote.getContentId()), EOperation.UPDATE));
		IndexManager.getInstance().index(indexData);
	}

	/**
	 * Returns the amount of ins on a content object.
	 */
	public int getInsAmountByContentId(final long contentId) {
		return getVoteAmount(VOTE_TABLE.CONTENT_ID.eq(contentId), true);
	}

	/**
	 * Returns the amount of combs on a content object.
	 */
	public int getCombsAmountByContentId(final long contentId) {
		return getVoteAmount(VOTE_TABLE.CONTENT_ID.eq(contentId), false);
	}

	/**
	 * Returns the amount of ins which the user made in his career.
	 */
	public int getInsAmountForUser(final long userId) {
		return getVoteAmount(VOTE_TABLE.USER_ID.eq(userId), true);
	}

	/**
	 * Returns the amount of combs which the user made in his career.
	 */
	public int getCombsAmountForUser(final long userId) {
		return getVoteAmount(VOTE_TABLE.USER_ID.eq(userId), false);
	}

	/**
	 * Returns the amount of ins or combs for the given condition.
	 * in == true returns all "ins" / false all "combs"
	 */
	private int getVoteAmount(final Condition cond) {
		return DSL.using(jooqConfig).
				select(DSL.count()).
				from(VOTE_TABLE).
				where(cond).fetchOne(DSL.count());
	}

	/**
	 * Returns the amount of ins or combs for the given condition.
	 * in == true returns all "ins" / false all "combs"
	 */
	private int getVoteAmount(final Condition cond, final boolean in) {
		return DSL.using(jooqConfig).
				select(DSL.count()).
				from(VOTE_TABLE).
				where(cond).
				and(VOTE_TABLE.UP.eq(in)).fetchOne(DSL.count());
	}

	/**
	 * Returns a list of users which gave a "in" to the given contentId.
	 */
	public List<User> getUsersThatVotedInOfContentId(final long contentId) {
		return getUsersThatVotedOnContentId(contentId, true);
	}

	/**
	 * Returns a list of users which gave a "comb" to the given contentId.
	 */
	public List<User> getUsersThatVotedCombOfContentId(final long contentId) {
		return getUsersThatVotedOnContentId(contentId, false);
	}

	/**
	 * Returns a list of all users which gave a "in" or a "comb" to the given news article.
	 */
	private List<User> getUsersThatVotedOnContentId(final long contentId, final boolean up) {
		return DSL.using(jooqConfig).
				select(USER_TABLE.fields()).
				from(USER_TABLE.join(VOTE_TABLE).
					on(USER_TABLE.ID.eq(VOTE_TABLE.USER_ID))).
				where(VOTE_TABLE.CONTENT_ID.eq(contentId)).
				and(VOTE_TABLE.UP.eq(up)).fetchInto(User.class);
	}

	/**
	 * Returns a list of news on which the given user has gave an "in".
	 */
	public List<News> getNewsWithInsOfUser(final long userId, final int offset, final int count) {
		return getNewsVotesForUser(userId, true, offset, count);
	}

	/**
	 * Returns a list of news on which the given user has gave an "comb".
	 */
	public List<News> getNewsCombsOfUser(final long userId, final int offset, final int count) {
		return getNewsVotesForUser(userId, false, offset, count);
	}

	/**
	 * Returns a list of all news on the given user has gave a "in" or a "comb".
	 */
	private List<News> getNewsVotesForUser(final long userId, final boolean up, final int offset, final int count) {
		return DSL.using(jooqConfig).
				select().
				from(NEWS_TABLE.join(VOTE_TABLE).
					on(NEWS_TABLE.CONTENT_ID.eq(VOTE_TABLE.CONTENT_ID)).
				join(CONTENT_TABLE).
					on(CONTENT_TABLE.ID.eq(VOTE_TABLE.CONTENT_ID))).
				where(VOTE_TABLE.USER_ID.eq(userId)).
					and(VOTE_TABLE.UP.eq(up)).
				orderBy(VOTE_TABLE.VOTE_DATE).
				limit(offset, count).
				fetchInto(News.class);
	}

	/**
	 * Returns all positive (ins) content-votes for the given news article.
	 */
	public List<ContentVote> getInVotes(final long newsId) {
		return getContentVotes(newsId, true);
	}

	/**
	 * Returns all negative (combs) content-votes for the given news article.
	 */
	public List<ContentVote> getCombVotes(final long contentId) {
		return getContentVotes(contentId, false);
	}

	/**
	 * Returns all content-votes for the given news article.
	 */
	private List<ContentVote> getContentVotes(final long contentId, final boolean up) {
		return DSL.using(jooqConfig).
				select().
				from(VOTE_TABLE).
				where(VOTE_TABLE.CONTENT_ID.eq(contentId)).
				and(VOTE_TABLE.UP.eq(up)).fetchInto(ContentVote.class);
	}
}
