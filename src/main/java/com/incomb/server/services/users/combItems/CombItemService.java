package com.incomb.server.services.users.combItems;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.model.CombItem;
import com.incomb.server.model.dao.CombItemDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.users.combItems.model.CombItemModel;
/**
 * Actions for single CombItems of a user 
 */
@Path("/users/{userId}/combItems/{contentId}")
@Produces(MediaType.APPLICATION_JSON)
public class CombItemService extends AService {

	/**
	 * Inserts a new Comb Item for the given user and content
	 * @param userId of the desired user
	 * @param contentId of the desired content
	 * @param con Connection to use - is injected automatically
	 * @return the created element
	 */
	@POST
	public Response insertCombItem(@PathParam("userId") final long userId, @PathParam("contentId") final long contentId,
			@Context final Connection con) {
		getAccessChecker().checkLoggedInUser(userId);

		final CombItem item = new CombItem(userId, contentId, new Timestamp(System.currentTimeMillis()), null);
		new CombItemDao(con).insertCombItem(item);
		return ok(item);
	}

	/**
	 * Updates an existing content element in a comb of a user
	 * @param userId of the user to update the comb element
	 * @param contentId of the element in the comb of the user
	 * @param item Data to change
	 * @param con Connection to use - is injected automatically
	 * @return the updated {@link CombItemModel}
	 */
	@PUT
	public Response updateCombItem(@PathParam("userId") final long userId, @PathParam("contentId") final long contentId,
			final CombItemModel item, @Context final Connection con) {
		if(userId != item.getUserId()) {
			throw new BadRequestException("Different userIds.");
		}

		if(contentId != item.getContentId()) {
			throw new BadRequestException("Different contentIds.");
		}

		getAccessChecker().checkLoggedInUser(userId);

		new CombItemDao(con).updateCombItem(item);
		return ok(item);
	}
	
	/**
	 * Removes an content element from the comb of the user
	 * @param userId of the user to remove the content element from
	 * @param contentId of the content element to remove
	 * @param con Connection to use - is injected automatically
	 * @return success-message
	 */
	@DELETE
	public Response removeCombItem (@PathParam("userId") final long userId, @PathParam("contentId") final long contentId,
			@Context final Connection con) {
		getAccessChecker().checkLoggedInUser(userId);

		new CombItemDao(con).removeCombItem(userId, contentId);
		return ok("CombItem removed.");
	}
}
