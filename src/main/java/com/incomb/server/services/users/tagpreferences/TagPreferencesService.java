package com.incomb.server.services.users.tagpreferences;

import java.sql.Connection;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.model.TagPreference;
import com.incomb.server.model.dao.TagPreferenceDao;
import com.incomb.server.services.AService;
/**
 * This class handles all the Tag preferences of users
 */
@Path("/users/{userId}/tags")
@Produces(MediaType.APPLICATION_JSON)
public class TagPreferencesService extends AService {

	/**
	 * Returns all tags for the given user
	 * @param userId of the user to get the tags from
	 * @param con Connection to use - is injected automatically
	 * @return a list of {@link TagPreference}s of the given userId
	 */
	@GET
	public Response getTags(@PathParam("userId") final long userId, @Context final Connection con) {
		getAccessChecker().checkLoggedInUser(userId);

		final TagPreferenceDao dao = new TagPreferenceDao(con);
		return ok(dao.getTagPreferences(userId));
	}
	
	/**
	 * Creates a new Tag on the server for the given userid
	 * @param tagPreference data to set for the user
	 * @param userId to create the tag for
	 * @param con Connection to use - is injected automatically
	 * @return if successful the created {@link TagPreference}
	 */
	@POST
	public Response postTag(final TagPreference tagPreference, @PathParam("userId") final long userId,
			@Context final Connection con) {
		if(userId != tagPreference.getUserId()) {
			throw new BadRequestException("Different userIds.");
		}

		getAccessChecker().checkLoggedInUser(userId);

		new TagPreferenceDao(con).addTagPreference(tagPreference);
		return ok(tagPreference);
	}
}
