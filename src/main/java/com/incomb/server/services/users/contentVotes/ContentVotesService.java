package com.incomb.server.services.users.contentVotes;

import java.sql.Connection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.indexing.SimpleSearchResult;
import com.incomb.server.model.News;
import com.incomb.server.model.dao.ContentVoteDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.news.model.NewsModel;


/**
 * This class is used for request ins and combs of a specific user as a REST service.
 *
 */
@Path("/users/{userId}")
@Produces(MediaType.APPLICATION_JSON)
public class ContentVotesService extends AService {

	/**
	 * Returns a {@link SimpleSearchResult} of all (see <code>offset</code> and <code>count</code>) news on which the user gave a "in".<br>
	 * Returns a 200 Response.
	 *
	 * @param userId - Id for which the ins are loaded
	 * @param loggedInUserId - Logged in user
	 * @param offset - Start Index for search
	 * @param count - Result count
	 * @param con - Connection to DB
	 * @return
	 */
	@GET
	@Path("/ins")
	public Response getIns(@PathParam("userId") final long userId, @QueryParam("loggedInUserId") final long loggedInUserId,
			@QueryParam("offset") final int offset, @QueryParam("count") final int count, @Context final Connection con) {
		getAccessChecker().checkPrivateProfile(userId);

		final ContentVoteDao contentVoteDao = new ContentVoteDao(con);
		final List<News> items = contentVoteDao.getNewsWithInsOfUser(userId, offset, count);

		final SimpleSearchResult<News> ssr = new SimpleSearchResult<>();
		for (final News news : items) {
			ssr.addResult(new NewsModel(news, loggedInUserId, con));
		}
		ssr.setTotalHits(contentVoteDao.getInsAmountForUser(userId)); //Used for paging
		return ok(ssr);
	}

	/**
	 * Returns a {@link SimpleSearchResult} of all (see <code>offset</code> and <code>count</code>) news on which the user gave a "comb".<br>
	 * Returns a 200 Response.
	 *
	 * @param userId - Id for which the ins are loaded
	 * @param loggedInUserId - Logged in user
	 * @param offset - Start Index for search
	 * @param count - Result count
	 * @param con - Connection to DB
	 * @return
	 */
	@GET
	@Path("/combs")
	public Response getCombs(@PathParam("userId") final long userId, @QueryParam("loggedInUserId") final long loggedInUserId,
			@QueryParam("offset") final int offset, @QueryParam("count") final int count, @Context final Connection con) {
		getAccessChecker().checkPrivateProfile(userId);

		final ContentVoteDao contentVoteDao = new ContentVoteDao(con);
		final List<News> items = contentVoteDao.getNewsCombsOfUser(userId, offset, count);

		final SimpleSearchResult<News> ssr = new SimpleSearchResult<>();
		for (final News news : items) {
			ssr.addResult(new NewsModel(news, loggedInUserId, con));
		}
		ssr.setTotalHits(contentVoteDao.getCombsAmountForUser(userId)); //Used for paging
		return ok(ssr);
	}

}
