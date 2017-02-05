package com.incomb.server.services.users.categorypreferences;

import java.sql.Connection;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.model.CategoryPreference;
import com.incomb.server.model.dao.CategoryPreferenceDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.users.categorypreferences.model.CategoryPreferenceModel;
/**
 * Actions for a single user and its category with the id categoryId
 */
@Path("/users/{userId}/categories/{categoryId}")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryPreferenceService extends AService {
	/**
	 * Updates a {@link CategoryPreference} of a User 
	 * @param preferenceModel the new values
	 * @param userId of the user to update
	 * @param categoryId of the {@link CategoryPreference}
	 * @param con Connection to use - is injected automatically
	 * @return the updated {@link CategoryPreferenceModel}
	 */
	@PUT
	public Response putPreference(final CategoryPreferenceModel preferenceModel,
			@PathParam("userId") final long userId, @PathParam("categoryId") final int categoryId,
			@Context final Connection con) {
		getAccessChecker().checkLoggedInUser(userId);

		final CategoryPreference preference = new CategoryPreference();
		preference.setUserId(userId);
		preference.setCategoryId(categoryId);
		preference.setFactor(preferenceModel.getFactor());

		new CategoryPreferenceDao(con).updateCategoryPreference(preference);
		return ok(preferenceModel);
	}
}
