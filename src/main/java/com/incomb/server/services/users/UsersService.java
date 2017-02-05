package com.incomb.server.services.users;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.indexing.ISearchResult;
import com.incomb.server.indexing.SimpleSearchResult;
import com.incomb.server.model.CategoryPreference;
import com.incomb.server.model.User;
import com.incomb.server.model.UserLocale;
import com.incomb.server.model.dao.CategoryPreferenceDao;
import com.incomb.server.model.dao.UserDao;
import com.incomb.server.model.dao.UserLocaleDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.ResponseBean;
import com.incomb.server.services.users.exceptions.BadRequestExceptionResponseBean;
import com.incomb.server.services.users.model.UserModel;
import com.incomb.server.services.validators.ValidationManager;
import com.incomb.server.services.validators.ValidationResponseBean;
import com.incomb.server.services.validators.rulesets.IRuleSet;
import com.incomb.server.services.validators.rulesets.UserRegisterRuleSet;
import com.incomb.server.utils.JsonUtil;
import com.incomb.server.utils.LocaleUtil;
import com.incomb.server.utils.passwords.PasswordUtil;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersService extends AService {
	/**
	 * <p>
	 * The {@link Logger} for this class.
	 * </p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UsersService.class);

	/**
	 * Registers a new user by
	 * <ol>
	 * <li>Validating the {@link UserModel} with {@link UserRegisterRuleSet}</li>
	 * <li>If it is not valid sending a {@link ValidationResponseBean} to the frontend (with a {@link BadRequestExceptionResponseBean})</li>
	 * <li>Creating a new {@link User} object and inserting the information from {@link UserModel}</li>
	 * <li>Hashing the plaintext password and generating a new random salt</li>
	 * <li>Inserting the user into the database</li>
	 * <li>Create default {@link CategoryPreference}</li>
	 * <li>Set language of the user based on his browser</li>
	 * <li>Log in the user</li>
	 * <li>Responding with the newly created {@link UserModel} in a {@link ResponseBean}</li>
	 * </ol>
	 * @param userModel from the client
	 * @param con connection to use
	 * @return a new ResponseBean
	 */
	@POST
	@PermitAll
	public Response register(final UserModel userModel, @Context final Connection con) {
		final ValidationManager validationManager = new ValidationManager(userModel);
		final UserDao dao = new UserDao(con);
		final IRuleSet ruleSet = new UserRegisterRuleSet(dao);
		validationManager.addRuleSet(ruleSet);

		final ValidationResponseBean validationResponseBean = validationManager.getValidatedResponse();

		LOGGER.debug(JsonUtil.getJson(validationResponseBean, true));

		if (!validationResponseBean.getSuccess()) {
			throw new BadRequestExceptionResponseBean(validationResponseBean);
		}

		final User user = new User();

		user.setEmail(userModel.getEmail());
		user.setUsername(userModel.getUsername());
		user.setDisplayName(userModel.getDisplayName());
		user.setRegisterDate(new Timestamp(new Date().getTime()));

		final byte[] salt = PasswordUtil.getNextSalt();
		user.setSalt(Hex.encodeHexString(salt));
		user.setPasswordHash(Hex.encodeHexString(PasswordUtil.hash(userModel.getPassword(), salt)));


		dao.insert(user);
		LOGGER.info("Created user {}", user.getUsername());

		new CategoryPreferenceDao(con).createCategoryPreferences(user.getId());
		
		final String userModelLocale = userModel.getLocale() == null ? LocaleUtil.DEFAULT_LOCALE.getLanguage() : userModel.getLocale();

		final UserLocale userLocale = new UserLocale(user.getId(), LocaleUtil.toLocale(userModelLocale).getLanguage());
		new UserLocaleDao(con).addUserLocale(userLocale);

		// log in user.
		setSessionAttribute(LoggedInUserService.SESSIONATTR_LOGGEDIN, user);

		return ok(new ResponseBean("Created user " + user.getUsername(), true, new UserModel(user)));
	}
	
	
	/**
	 * returns a response with a list of found users with the given searchterms
	 * @param search text to search in the index
	 * @param amount of users to return
	 * @param username optional: username to find
	 * @param details show more details
	 * @param con is injected automatically
	 * @return {@link BadRequestExceptionResponseBean} or {@link ResponseBean} with {@link SimpleSearchResult} if successful
	 */
	@GET
	public Response getUsers(@QueryParam("search") final String search,
			@QueryParam("amount") final int amount, @QueryParam("username") final String username,
			@QueryParam("details") final boolean details, @Context final Connection con) {

		if(username != null) {
			final List<UserModel> users = new ArrayList<>();

			final User user = new UserDao(con).findByUsername(username);
			if(user != null) {
				users.add(UserUtil.getModel(details, user, con));
			}

			return ok(new SimpleSearchResult<>(users));
		}

		if(StringUtils.isBlank(search)) {
			throw new BadRequestExceptionResponseBean("only searches and username lookups supported");
		}

		final SimpleSearchResult<UserModel> result = new SimpleSearchResult<>();
		final ISearchResult<User> foundUsers = new UserDao(con).findUsers(search, amount);

		for (final User user : foundUsers.getResults()) {
			result.addResult(UserUtil.getModel(details, user, con));
		}

		result.setTotalHits(foundUsers.getTotalHits());

		return ok(result);
	}
}