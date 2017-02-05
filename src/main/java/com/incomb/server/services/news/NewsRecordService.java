package com.incomb.server.services.news;

import java.sql.Connection;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.model.News;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.NewsDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.news.model.NewsModel;

/**
 * Service for a single {@link News} record.
 */
@Path("/news/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class NewsRecordService extends AService {

	/**
	 * Returns the {@link News} with the given id.
	 * @param id the content id of the {@link News}.
	 * @param userId the {@link User} which wants to see the {@link News}.
	 * @param con the {@link Connection} to get the {@link News}.
	 * @return {@link News}
	 * @throws NotFoundException if {@link News} doesn't exists.
	 */
	@GET
	public Response getNews(@PathParam("id") final long id, @QueryParam("userId") final long userId,
			@Context final Connection con) {
		final News news = new NewsDao(con).getNews(id);

		if(news == null) {
			throw new NotFoundException("News with id " + id + " not found.");
		}

		return ok(new NewsModel(news, userId, con));
	}
}
