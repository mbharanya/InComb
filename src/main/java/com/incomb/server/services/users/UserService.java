package com.incomb.server.services.users;

import java.sql.Connection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import com.incomb.server.config.Config;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.UserDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.ResponseBean;
import com.incomb.server.services.users.exceptions.BadRequestExceptionResponseBean;
import com.incomb.server.services.users.exceptions.NotFoundExceptionResponseBean;
import com.incomb.server.services.users.model.DetailedUserModel;
import com.incomb.server.services.users.model.UserModel;
import com.incomb.server.services.validators.DuplicateValidator;
import com.incomb.server.services.validators.ValidationManager;
import com.incomb.server.services.validators.ValidationResponseBean;
import com.incomb.server.services.validators.rulesets.IRuleSet;
import com.incomb.server.services.validators.rulesets.UserUpdateRuleSet;
import com.incomb.server.utils.passwords.PasswordUtil;

@Path("/users/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class UserService extends AService {
	/**
	 * Updates information about a users
	 * Notes:<br />
	 * <ul>
	 * <li>The user querying must be logged in and trying to change himself</li>
	 * <li>The userid in the request url must be the same as in the parameter {@link DetailedUserModel}</li>
	 * <li>If noting has changed a HTTP 200 with a localized message will be returned</li>
	 * <li>userModel is validated by {@link UserUpdateRuleSet}</li>
	 * <li>If the user is updated, the session is updated too</li>
	 * </ul>
	 * @param id of the user to update
	 * @param userModel data to update
	 * @param con Connection to use - is injected automatically
	 * @return a Response if successful or not
	 * @throws CloneNotSupportedException
	 * @throws DecoderException
	 */
	@PUT
	public Response updateUser(@PathParam("id") final long id, final DetailedUserModel userModel, @Context final Connection con) throws CloneNotSupportedException, DecoderException {
		// check if user is allowed to update
		getAccessChecker().checkLoggedInUser(id);

		final UserDao dao = new UserDao(con);
		final User currentUserInDb = dao.findById(id);
		
		if (currentUserInDb == null || currentUserInDb.isDeleted()){
			throw new NotFoundExceptionResponseBean("responses.users.errors.notFound");
		}
		
		final User updatedUser = currentUserInDb.clone();


		if(id != userModel.getId()) {
			throw new BadRequestExceptionResponseBean("Different UserIds");
		}

		boolean updateNeeded = false;

		if (needsToBeUpdated(userModel.getUsername(), currentUserInDb.getUsername())){
			updatedUser.setUsername(userModel.getUsername());
			updateNeeded = true;
		}

		if (needsToBeUpdated(userModel.getDisplayName(), currentUserInDb.getDisplayName())){
			updatedUser.setDisplayName(userModel.getDisplayName());
			updateNeeded = true;
		}

		if (needsToBeUpdated(userModel.getEmail(), currentUserInDb.getEmail())){
			updatedUser.setEmail(userModel.getEmail());
			updateNeeded = true;
		}

		if(userModel.isPrivateProfile() != currentUserInDb.isPrivateProfile()) {
			updatedUser.setPrivateProfile(userModel.isPrivateProfile());
			updateNeeded = true;
		}

		// check if not the same password
		if (!StringUtils.isBlank(userModel.getPassword()) && !PasswordUtil.isExpectedPassword(userModel.getPassword(), currentUserInDb.getSalt(), currentUserInDb.getPasswordHash())){
			final byte[] salt = PasswordUtil.getNextSalt();
			updatedUser.setPasswordHash(Hex.encodeHexString(PasswordUtil.hash(userModel.getPassword(), salt)));
			updatedUser.setSalt(Hex.encodeHexString(salt));
			updateNeeded = true;
		}

		// check if something got updated
		if(!updateNeeded){
			return ok("responses.users.notModified");
		}

		// if something is modified validate the new things
		final ValidationManager validationManager = new ValidationManager(updatedUser);
		final IRuleSet ruleSet = new UserUpdateRuleSet();
		validationManager.addRuleSet(ruleSet);

		// if the new username is not the same as the last one, we need to check for duplicates
		if (!updatedUser.getUsername().equals(currentUserInDb.getUsername())){
			validationManager.addRule("username", new DuplicateValidator(dao, "username"), "validation.user.username.exists");
		}

		// if the new email is not the same as the last one, we need to check for duplicates
		if (!updatedUser.getEmail().equals(currentUserInDb.getEmail())){
			validationManager.addRule("email", new DuplicateValidator(dao, "email"), "validation.user.email.exists");
		}

		final ValidationResponseBean validationResponseBean = validationManager.getValidatedResponse();
		if (!validationResponseBean.getSuccess()) {
			throw new BadRequestExceptionResponseBean(validationResponseBean);
		}

		dao.update(updatedUser);
		setSessionAttribute(LoggedInUserService.SESSIONATTR_LOGGEDIN, updatedUser);

		if (Config.getDefault().getBooleanProperty("debug")){
			return ok(new ResponseBean("responses.users.updated", true, dao.findById(id)));
		}else{
			return ok("responses.users.updated");
		}
	}

	private boolean needsToBeUpdated(final String fieldToCheck, final String externalField){
		if (!StringUtils.isBlank(fieldToCheck) && !fieldToCheck.equals(externalField)){
			return true;
		}
		return false;
	}


	/**
	 * Return details about a user
	 * @param id of user to search
	 * @param details show more details
	 * @param con Connection to use (is injected automatically)
	 * @return a UserModel
	 */
	@GET
	public Response getUser(@PathParam("id") final long id, @QueryParam("details") final boolean details,
			@Context final Connection con) {
		final UserDao dao = new UserDao(con);
		final User user = dao.findById(id);

		if (user == null || user.isDeleted()) {
			throw new NotFoundExceptionResponseBean("responses.users.errors.notFound");
		}

		final UserModel userModel = UserUtil.getModel(details, user, con);

		// if user is no the same as the logged in user don't return the email
		if(!getAccessChecker().isLoggedInUser(id)){
			userModel.setEmail(null);
		}

		return ok(userModel);
	}


	/**
	 * Set the user to deleted
	 * @param id userId to set
	 * @param con Connection to use - is injected automatically
	 * @return localized success-message
	 */
	@DELETE
	public Response deleteUser(@PathParam("id") final long id, @Context final Connection con) {
		getAccessChecker().checkLoggedInUser(id);

		final UserDao dao = new UserDao(con);
		final User user = dao.findById(id);

		if (user == null || user.isDeleted()) {
			throw new NotFoundExceptionResponseBean("responses.users.errors.notFound");
		}
		user.setDeleted(true);
		dao.update(user);

		if (Config.getDefault().getBooleanProperty("debug")){
			return ok(new ResponseBean("responses.users.deleted", true, dao.findById(id)));
		}else{
			return ok("responses.users.deleted");
		}
	}
}
