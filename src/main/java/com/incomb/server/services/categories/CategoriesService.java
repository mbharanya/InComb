package com.incomb.server.services.categories;

import java.sql.Connection;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.indexing.SimpleSearchResult;
import com.incomb.server.model.Category;
import com.incomb.server.model.dao.CategoryDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.users.exceptions.BadRequestExceptionResponseBean;
import com.incomb.server.utils.LocaleUtil;
/**
 * Actions for categories
 */
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoriesService extends AService {
	/**
	 * Get categories for the given parameters
	 * @param moduleId of the module
	 * @param search string to search
	 * @param amount amount of results to return
	 * @param localeStr locale to use
	 * @param con Connection to use - is injected automatically
	 * @return {@link SimpleSearchResult} with categories
	 */
	@GET
	public Response getCategories(@QueryParam("moduleId") final int moduleId,
			@QueryParam("search") final String search, @QueryParam("amount") final int amount,
			@QueryParam("locale") final String localeStr, @Context final Connection con) {

		if(search != null) {
			return search(search, amount, localeStr, con);
		}

		List<Category> categories = null;

		final CategoryDao dao = new CategoryDao(con);

		if(moduleId > 0) {
			categories = dao.getCategories(moduleId);
		}
		else {
			categories = dao.getCategories();
		}

		return Response.ok().entity(new SimpleSearchResult<>(categories)).build();
	}

	/**
	 * Searches for categories
	 * @param search string to search
	 * @param amount amount of results to return
	 * @param localeStr locale to use
	 * @param con Connection to use - is injected automatically
	 * @return the found Categories
	 */
	private Response search(final String search, final int amount, final String localeStr, final Connection con) {
		final Locale locale = LocaleUtil.toLocale(localeStr);
		if(locale == null) {
			throw new BadRequestExceptionResponseBean("Please specify a locale as GET parameter.");
		}

		// TODO filter moduleId if needed.
		return ok(new CategoryDao(con).findCategories(search, locale, amount));
	}
}
