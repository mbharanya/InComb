package com.incomb.server.services.users.combItems;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.model.News;
import com.incomb.server.model.dao.CombItemDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.news.model.NewsModel;
/**
 * Actions with CombItems of a user with id userId
 */
@Path("/users/{userId}/combItems")
@Produces(MediaType.APPLICATION_JSON)
public class CombItemsService extends AService {
	/**
	 * Returns all combitems of a given user
	 * @param userId of the user to get the comb items from
	 * @param con Connection to use - is injected automatically
	 * @return a List of {@link NewsModel}s which are in the comb of the given user
	 */
	@GET
	public Response getCombItems(@PathParam("userId") final long userId, @Context final Connection con) {
		getAccessChecker().checkLoggedInUser(userId);

		final List<News> items = new CombItemDao(con).getCombItems(userId, true);

		final List<NewsModel> models = new ArrayList<>();
		for (final News news : items) {
			models.add(new NewsModel(news, userId, con));
		}

		return ok(models);
	}
}
