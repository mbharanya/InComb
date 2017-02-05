package com.incomb.server.services.users.locales;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.model.UserLocale;
import com.incomb.server.model.dao.UserLocaleDao;
import com.incomb.server.services.AService;
import com.mysql.jdbc.Connection;

/**
 * All Actions with a specific Locale
 */
@Path("/users/{userId}/locales/{locale}")
@Produces(MediaType.APPLICATION_JSON)
public class UserLocaleService extends AService {

	/**
	 * Deletes a Locale of given user
	 * @param userId of the user to delete the locale from
	 * @param locale the locale to delete
	 * @param con Connection to use - is injected automatically
	 * @return the deleted locale
	 */
	@DELETE
	public Response removeLocale(@PathParam("userId") final long userId, @PathParam("locale") final String locale,
			@Context final Connection con) {
		getAccessChecker().checkLoggedInUser(userId);

		final UserLocale userLocale = new UserLocale(userId, locale);
		new UserLocaleDao(con).removeUserLocale(userLocale);
		return ok(userLocale);
	}
}
