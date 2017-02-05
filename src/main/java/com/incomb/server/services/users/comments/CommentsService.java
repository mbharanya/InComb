package com.incomb.server.services.users.comments;

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
import com.incomb.server.model.ContentComment;
import com.incomb.server.model.dao.ContentCommentDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.news.model.CommentModel;

/**
 * This class is used for request {@link ContentComment} of a specific user as a REST service.
 *
 */
@Path("/users/{userId}/comments")
@Produces(MediaType.APPLICATION_JSON)
public class CommentsService extends AService {


	/**
	 * Returns a {@link SimpleSearchResult} of all (see <code>offset</code> and <code>count</code>) comments the user have made.<br>
	 * Returns a 200 Response.
	 *
	 * @param userId - Id for which the comments are loaded
	 * @param offset - Start Index for search
	 * @param count - Result count
	 * @param con - Connection to DB
	 * @return
	 */
	@GET
	public Response getContentComments(@PathParam("userId") final long userId, @QueryParam("offset") final int offset, @QueryParam("count") final int count, @Context final Connection con) {
		getAccessChecker().checkPrivateProfile(userId);

		final ContentCommentDao contentCommentDao = new ContentCommentDao(con);
		final List<ContentComment> items = contentCommentDao.getCommentsOfUser(userId, offset, count);

		final SimpleSearchResult<CommentModel> ssr = new SimpleSearchResult<>();
		for (final ContentComment contentComment : items) {
			ssr.addResult(new CommentModel(contentComment, con));
		}
		ssr.setTotalHits(contentCommentDao.getCommentsAmountForUser(userId)); //Used for paging.
		return ok(ssr);
	}

}
