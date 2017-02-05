package com.incomb.server.services.news;

import java.sql.Connection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.model.ContentVote;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.ContentVoteDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.ResponseBean;
import com.incomb.server.services.news.model.VoteAmountModel;
import com.incomb.server.services.users.UserUtil;
import com.incomb.server.services.users.exceptions.BadRequestExceptionResponseBean;
import com.incomb.server.services.validators.ValidationManager;
import com.incomb.server.services.validators.ValidationResponseBean;
import com.incomb.server.services.validators.rulesets.IRuleSet;
import com.incomb.server.services.validators.rulesets.VoteRuleSet;
/**
 * Actions for a specific news article with the given contentId
 */
@Path("/news/{contentId}")
@Produces(MediaType.APPLICATION_JSON)
public class VoteService extends AService{
	/**
	 * Returns all in votes of a given contentId Element
	 * @param contentId to get the in votes of
	 * @param con Connection to use - is injected automatically
	 * @return a list of Users that voted in for the given contentid
	 */
	@GET
	@Path("/ins")
	public Response getInsOfContentId(@PathParam("contentId") final long contentId, @Context final Connection con){
		final ContentVoteDao dao = new ContentVoteDao(con);
		final List<User> insOfUsers= dao.getUsersThatVotedInOfContentId(contentId);
		return ok(UserUtil.toModels(insOfUsers, true));
	}

	/**
	 * Return the amount of ins and combs for the given contentid
	 * @param contentId to count from
 	 * @param con Connection to use - is injected automatically
	 * @return {@link VoteAmountModel} in a HTTP OK
	 */
	@GET
	@Path("/votes")
	public Response getVoteAmount(@PathParam("contentId") final long contentId, @Context final Connection con){
		final ContentVoteDao dao = new ContentVoteDao(con);
		return ok(new VoteAmountModel(dao.getInsAmountByContentId(contentId), dao.getCombsAmountByContentId(contentId)));
	}
	
	/**
	 * adds a new Vote to a contentId
	 * @param con Connection to use - is injected automatically
	 * @param vote to give to the content
	 * @param contentId of the content you want to vote for
	 * @return successmessage and the new amount
	 */
	@POST
	@Path("/votes")
	public Response addVote(@Context final Connection con, final ContentVote vote, @PathParam("contentId") final long contentId){
		if (vote.getContentId() != contentId) {
			throw new BadRequestExceptionResponseBean("responses.votes.errors.contentId.invalid");
		}

		// check if user is allowed to vote
		getAccessChecker().checkLoggedInUser(vote.getUserId());

		final IRuleSet ruleSet = new VoteRuleSet();
		final ValidationManager manager = new ValidationManager(vote);
		manager.addRuleSet(ruleSet);

		final ValidationResponseBean validationResponseBean = manager.getValidatedResponse();

		if (!validationResponseBean.getSuccess()){
			throw new BadRequestExceptionResponseBean(validationResponseBean);
		}
		
		final ContentVoteDao dao = new ContentVoteDao(con);
		dao.addVote(vote);

		return ok(new ResponseBean("responses.Votes.addedVote", true, new VoteAmountModel(dao.getInsAmountByContentId(contentId), dao.getCombsAmountByContentId(contentId))));

	}
	
	/**
	 * Returns a list of users that voted comb on this content
	 * @param contentId of the content
	 * @param con Connection to use - is injected automatically
	 * @return a list of users that voted comb on this content
	 */
	@GET
	@Path("/combs")
	public Response getCombsOfContentId(@PathParam("contentId") final long contentId, @Context final Connection con){
		final ContentVoteDao dao = new ContentVoteDao(con);
		final List<User> combsOfUsers = dao.getUsersThatVotedCombOfContentId(contentId);
		return ok(UserUtil.toModels(combsOfUsers, true));
	}
}
