package com.incomb.server.services.users.flyWiths;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.model.FlyWith;
import com.incomb.server.model.dao.FlyWithDao;
import com.incomb.server.model.dao.UserDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.users.UserUtil;

/**
 * Actions to handle single FlyWiths of a user
 */
@Path("/users/{userId}/flyWiths/{flyWithId}")
@Produces(MediaType.APPLICATION_JSON)
public class FlyWithService extends AService {
	/**
	 * Adds a new FlyWith to a given user
	 * @param userId of the user to add the flyWith to
	 * @param flyWithId to add
	 * @param con Connection to use - is injected automatically
	 * @return
	 */
	@POST
	public Response addFlyWith(@PathParam ("userId") final long userId, @PathParam ("flyWithId") final long flyWithId, @Context final Connection con ) {
		getAccessChecker().checkLoggedInUser(userId);
		final FlyWithDao dao = new FlyWithDao(con);
		final FlyWith flyWith = new FlyWith(userId, flyWithId, new Timestamp(System.currentTimeMillis()));
		dao.insertFlyWith(flyWith);
		return ok(UserUtil.getModel(false, new UserDao(con).findById(flyWithId), con));
	}
	
	/**
	 * Deletes a FlyWith from a given user
	 * @param userId of the user to remove the flywith from
	 * @param flyWithId to remove
	 * @param con Connection to use - is injected automatically
	 * @return
	 */
	@DELETE
	public Response deleteFlyWith(@PathParam ("userId") final long userId, @PathParam ("flyWithId") final long flyWithId, @Context final Connection con ) {
		getAccessChecker().checkLoggedInUser(userId);
		final FlyWithDao dao = new FlyWithDao(con);
		dao.deleteFlyWith(userId, flyWithId);
		return ok(null);
	}
}
