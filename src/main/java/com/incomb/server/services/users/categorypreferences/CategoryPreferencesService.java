package com.incomb.server.services.users.categorypreferences;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.incomb.server.model.CategoryPreference;
import com.incomb.server.model.dao.CategoryDao;
import com.incomb.server.model.dao.CategoryPreferenceDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.users.categorypreferences.model.CategoryPreferenceModel;
/**
 * Actions for categories of a user
 */
@Path("/users/{userId}/categories")
@Produces("application/json")
public class CategoryPreferencesService extends AService {

	/**
	 * Returns all Categories of a given user
	 * @param userId of the user to get categories from
	 * @param con Connection to use - is injected automatically
	 * @return a List of matching {@link CategoryPreferenceModel}s
	 */
	@GET
	public Response getCategories(@PathParam("userId") final long userId, @Context final Connection con) {
		getAccessChecker().checkLoggedInUser(userId);

		final CategoryPreferenceDao dao = new CategoryPreferenceDao(con);
		final CategoryDao categoryDao = new CategoryDao(con);

		final List<CategoryPreferenceModel> preferences = new ArrayList<>();
		for (final CategoryPreference preference : dao.getCategoryPreferences(userId)) {
			preferences.add(new CategoryPreferenceModel(preference,
					categoryDao.getCategory(preference.getCategoryId())));
		}

		return ok(preferences);
	}
}
