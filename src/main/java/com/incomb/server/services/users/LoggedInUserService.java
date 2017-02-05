package com.incomb.server.services.users;

import java.sql.Connection;

import javax.annotation.security.PermitAll;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.DecoderException;

import com.incomb.server.model.User;
import com.incomb.server.model.dao.UserDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.ResponseBean;
import com.incomb.server.services.users.exceptions.ValidationException;
import com.incomb.server.services.users.model.UserModel;
import com.incomb.server.services.validators.RequiredValidator;
import com.incomb.server.services.validators.ValidationManager;
import com.incomb.server.services.validators.ValidationResponseBean;
import com.incomb.server.utils.passwords.PasswordUtil;

@Path("/users/loggedIn")
@Produces(MediaType.APPLICATION_JSON)
public class LoggedInUserService extends AService {

	/**
	 * The attribute to store the logged in user in
	 */
	public static final String SESSIONATTR_LOGGEDIN = "loggedIn";

	/**
	 * Returns the currently logged in user from the current session
	 * @param con is injected automatically
	 * @return {@link UserModel}
	 */
	@GET
	@PermitAll
	public Response getLoggedInUser(@Context final Connection con) {
		final User loggedIn = (User) getSessionAttribute(SESSIONATTR_LOGGEDIN);
		if(loggedIn == null) {
			throw new NotFoundException();
		}

		return ok(UserUtil.getModel(true, loggedIn, con));
	}


	/**
	 * Login a user
	 * Notes:<br />
	 * <ul>
	 * <li>userModel is validated by password and username {@link RequiredValidator}</li>
	 * </ul>
	 * @param userModel userdata to log in with (username, password etc..)
	 * @param con 
	 * @return Success or {@link NotAuthorizedException} if invalid login
	 */
	@POST
	@PermitAll
	public Response login(final UserModel userModel, @Context final Connection con) {
		final UserDao dao = new UserDao(con);
		try {
			final ValidationManager userValidationManager = new ValidationManager(userModel);

			userValidationManager.addRule("username", new RequiredValidator(), "validation.user.username.required");
			userValidationManager.addRule("password", new RequiredValidator(), "validation.user.password.required");

			final ValidationResponseBean validationResponseBean = userValidationManager.getValidatedResponse();

			if (!validationResponseBean.getSuccess()) {
				throw new NotAuthorizedException(validationResponseBean);
			}

			final User userInDb = dao.findByUsername(userModel.getUsername());

			if (userInDb != null) {
				final boolean success = PasswordUtil.isExpectedPassword(userModel.getPassword(), userInDb.getSalt(), userInDb.getPasswordHash());

				if (success) {
					setSessionAttribute(SESSIONATTR_LOGGEDIN, userInDb);
					return ok(new ResponseBean("User id: " + userModel.getId() + ", username: " + userModel.getUsername() + 
							"Logged in", true, UserUtil.getModel(true, userInDb, con)));
				} else {
					throw new NotAuthorizedException(new ResponseBean("Invalid Login", false));
				}
			} else {
				throw new NotAuthorizedException(new ResponseBean("User does not exist", false));
			}
		} catch (final DecoderException e) {
			throw new InternalServerErrorException(Response.serverError().entity(new ResponseBean("Error comparing password", false)).build(), e);
		} catch (final ValidationException e) {
			throw new InternalServerErrorException(Response.serverError().entity(new ResponseBean("Error in validation", false)).build(), e);
		}
	}
	
	/**
	 * Deletes the session attribute with the logged in user
	 * @return empty HTTP 200
	 */
	@DELETE
	public Response logout() {
		removeSessionAttribute(SESSIONATTR_LOGGEDIN);
		return ok(null);
	}
}
