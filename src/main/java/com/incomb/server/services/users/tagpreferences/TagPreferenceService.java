package com.incomb.server.services.users.tagpreferences;

import java.sql.Connection;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.model.dao.TagPreferenceDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.ResponseBean;

/**
 * Handles all Requests for a single tag
 */
@Path("/users/{userId}/tags/{tag}")
@Produces(MediaType.APPLICATION_JSON)
public class TagPreferenceService extends AService {

	/**
	 * Deletes a tag of a given user
	 * @param userId to delete the tag from
	 * @param tag the tag to delete
	 * @param con Connection to use - is injected automatically
	 * @return a success message in a {@link ResponseBean} 
	 */
	@DELETE
	public Response deleteTag(@PathParam("userId") final long userId,
			@PathParam("tag") final String tag, @Context final Connection con) {
		getAccessChecker().checkLoggedInUser(userId);

		new TagPreferenceDao(con).deleteTagPreference(userId, tag);
		return ok(new ResponseBean("Successfully deleted", true));
	}
}
