package com.incomb.server.services.news;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.model.ContentComment;
import com.incomb.server.model.dao.ContentCommentDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.ResponseBean;
import com.incomb.server.services.news.model.CommentModel;
import com.incomb.server.services.users.exceptions.BadRequestExceptionResponseBean;
import com.incomb.server.services.validators.ValidationManager;
import com.incomb.server.services.validators.ValidationResponseBean;
import com.incomb.server.services.validators.rulesets.CommentRuleSet;
import com.incomb.server.services.validators.rulesets.IRuleSet;
/**
 * Actions for comments of a specific content with id contentId
 */
@Path("/news/{contentId}/comments")
@Produces(MediaType.APPLICATION_JSON)
public class CommentsService extends AService {
	/**
	 * Returns a list of {@link ContentComment}s of the given contentid
	 * @param contentId of the content element
	 * @param con Connection to use - is injected automatically
	 * @return the list of {@link ContentComment}s
	 */
	@GET
	public Response getCommentsOfContentId(@PathParam("contentId") final long contentId, @Context final Connection con) {
		final ContentCommentDao dao = new ContentCommentDao(con);
		final List<ContentComment> comments = dao.getCommentsByContentId(contentId);

		return ok(comments);
	}
	/**
	 * Adds a new Comment to a content with id contentId
	 * @param con Connection to use - is injected automatically
	 * @param comment the comment to add 
	 * @param contentId the id of the content to add the comment to 
	 * @return successmessage, the created comment
	 */
	@POST
	public Response addComment(@Context final Connection con, final ContentComment comment, @PathParam("contentId") final long contentId) {
		if (comment.getContentId() != contentId) {
			throw new BadRequestExceptionResponseBean("responses.comments.errors.contentId.invalid");
		}

		// check if user is allowed to comment
		getAccessChecker().checkLoggedInUser(comment.getUserId());

		final IRuleSet ruleSet = new CommentRuleSet();
		final ValidationManager manager = new ValidationManager(comment);
		manager.addRuleSet(ruleSet);

		final ValidationResponseBean validationResponseBean = manager.getValidatedResponse();

		if (!validationResponseBean.getSuccess()) {
			throw new BadRequestExceptionResponseBean(validationResponseBean);
		}
		final ContentCommentDao dao = new ContentCommentDao(con);
		comment.setCommentDate(new Timestamp(new Date().getTime()));
		comment.setContentId(contentId);
		dao.insertComment(comment);

		final CommentModel commentModel = new CommentModel(comment, con);
		return ok(new ResponseBean("responses.comments.addedComment", true, commentModel));
	}
}
