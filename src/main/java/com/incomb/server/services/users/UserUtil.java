package com.incomb.server.services.users;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.incomb.server.model.User;
import com.incomb.server.model.dao.FlyWithDao;
import com.incomb.server.services.users.model.DetailedUserModel;
import com.incomb.server.services.users.model.UserModel;

/**
 * Little static helpers which are useful for the user services.
 */
public class UserUtil {
	/**
	 * Returns a new {@link UserModel} from a {@link User}
	 * @param details whether to show flyWiths, withFlyings
	 * @param user to create the {@link UserModel} from
	 * @param con the {@link Connection} to use
	 * @return a {@link UserModel} with the same data as the given parameter (where applicable)
	 * Note: Passwordhashes will not be inserted in the {@link User} Password field
	 */
	public static UserModel getModel(final boolean details, final User user, final Connection con) {
		final UserModel userModel;

		if(details) {
			final FlyWithDao flyWithDao = new FlyWithDao(con);
			final List<User> flyWiths = flyWithDao.getFlyWiths(user.getId());
			final List<User> withFlyings = flyWithDao.getWithFlyingsOf(user.getId());
			userModel = new DetailedUserModel(user, toModels(flyWiths), toModels(withFlyings));
		}
		else {
			userModel = new UserModel(user);
		}

		return userModel;
	}

	/**
	 * Converts a List of {@link UserModel} to Models
	 * Note: This  is used for backwards compatibility and will be changed in the future
	 * @param users to parse
	 * @return the new List
	 */
	@Deprecated
	public static List<UserModel> toModels(final List<User> users) {
		return toModels(users, false);
	}
	
	/**
	 * Converts a List of {@link UserModel} to Models with optionally the email removed
	 * @param users to parse
	 * @param hideEmail remove email or not
	 * @return the new List
	 */
	public static List<UserModel> toModels(final List<User> users, final boolean hideEmail) {
		final List<UserModel> returnValue = new ArrayList<>();

		for (final User user : users) {
			final UserModel userModel = new UserModel(user);
			if (hideEmail){
				userModel.setEmail(null);
			}
			returnValue.add(userModel);
		}

		return returnValue;
	}
}
