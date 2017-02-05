package com.incomb.server.services.users.model;

import java.util.List;

import com.incomb.server.model.User;

/**
 * More details about {@link UserModel}.
 * Contains fly withs and with flyings.
 */
public class DetailedUserModel extends UserModel {

	/**
	 * {@link User}s who the {@link User} is flying with.
	 */
	private List<UserModel> flyWiths;

	/**
	 * {@link User}s who are flying with the {@link User}.
	 */
	private List<UserModel> withFlyings;

	/**
	 * Creates a new instance without any properties preset.
	 */
	public DetailedUserModel() {
		super();
	}

	/**
	 * Creates a new instance with the properties from the given {@link User} and the given fly withs and with flyings.
	 * @param user the {@link User} to copy.
	 * @param flyWiths {@link User}s who the {@link User} is flying with.
	 * @param withFlyings {@link User}s who are flying with the {@link User}.
	 */
	public DetailedUserModel(final User user, final List<UserModel> flyWiths, final List<UserModel> withFlyings) {
		super(user);
		this.flyWiths = flyWiths;
		this.withFlyings = withFlyings;
	}

	/**
	 * @return {@link User}s who the {@link User} is flying with.
	 */
	public List<UserModel> getFlyWiths() {
		return flyWiths;
	}

	/**
	 * Sets the {@link User}s who the {@link User} is flying with.
	 * @param flyWiths the {@link User}s who the {@link User} is flying with.
	 */
	public void setFlyWiths(final List<UserModel> flyWiths) {
		this.flyWiths = flyWiths;
	}

	/**
	 * @return {@link User}s who are flying with the {@link User}.
	 */
	public List<UserModel> getWithFlyings() {
		return withFlyings;
	}

	/**
	 * Sets the {@link User}s who are flying with the {@link User}.
	 * @param withFlyings the {@link User}s who are flying with the {@link User}.
	 */
	public void setWithFlyings(final List<UserModel> withFlyings) {
		this.withFlyings = withFlyings;
	}
}
