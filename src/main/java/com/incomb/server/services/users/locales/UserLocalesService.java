package com.incomb.server.services.users.locales;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
 * Interactions with Locales of users
 */
@Path("/users/{userId}/locales")
@Produces(MediaType.APPLICATION_JSON)
public class UserLocalesService extends AService {
	/**
	 * Returns a List of {@link Locale}s for the given user
	 * @param userId of the user to get the locales from
	 * @param con Connection to use - is injected automatically
	 * @return a list of {@link Locale}s
	 */
	@GET
	public Response getLocales(@PathParam("userId") final long userId, @Context final Connection con) {
		getAccessChecker().checkLoggedInUser(userId);
		final List<Locale> locales = new UserLocaleDao(con).getLocalesForUser(userId);
		return ok(locales);
	}

	/**
	 * Adds a new Locale (Language) to a given user
	 * @param userId of the user to add the locale
	 * @param userLocale to add
	 * @param con Connection to use - is injected automatically
	 * @return the added {@link UserLocale}
	 */
	@POST
	public Response addLocale(@PathParam("userId") final long userId, final UserLocale userLocale,
			@Context final Connection con) {
		getAccessChecker().checkLoggedInUser(userId);

		userLocale.setUserId(userId);
		new UserLocaleDao(con).addUserLocale(userLocale);
		return ok(userLocale);
	}
}
