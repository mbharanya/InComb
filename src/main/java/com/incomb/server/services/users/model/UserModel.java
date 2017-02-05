package com.incomb.server.services.users.model;

import java.util.Locale;

import com.incomb.server.model.User;
import com.incomb.server.utils.GravatarUtil;
/**
 * Usermodel for Communication with the frontend
 */
public class UserModel {

	/**
	 * The id of the {@link User}.
	 */
	private long id;

	/**
	 * The e-mail address of the {@link User}.
	 */
	private String email;

	/**
	 * The unique username of the {@link User}.
	 */
	private String username;

	/**
	 * The name of the {@link User} which will be shown to other users.
	 */
	private String displayName;

	/**
	 * The not encrypted password of the {@link User}.
	 */
	private String password;

	/**
	 * The state if the {@link User} was deleted.
	 */
	private boolean deleted = false;

	/**
	 * The state if the {@link User} wants that only himself can view his profile.
	 */
	private boolean privateProfile;

	/**
	 * The current {@link Locale} of the browser.
	 */
	private String locale;

	/**
	 * The URL of an image of the {@link User}.
	 */
	private String userImg;

	/**
	 * Creates a new instance without any properties preset.
	 */
	public UserModel() {

	}

	/**
	 * Creates a new instance with the properties from the given {@link User}.
	 * @param user the {@link User} to copy.
	 */
	public UserModel(final User user) {
		setId(user.getId());
		setEmail(user.getEmail());
		setUsername(user.getUsername());
		setDisplayName(user.getDisplayName());
		setDeleted(user.isDeleted());
		setUserImg(GravatarUtil.getGravatarImg(user));
		setPrivateProfile(user.isPrivateProfile());
	}

	/**
	 * @return the state if the {@link User} was deleted.
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * Sets the state if the {@link User} was deleted.
	 * @param deleted the state if the {@link User} was deleted.
	 */
	public void setDeleted(final boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the id of the {@link User}.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id of the {@link User}.
	 * @param id the id of the {@link User}.
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * @return the e-mail address of the {@link User}.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the e-mail address of the {@link User}.
	 * @param email the e-mail address of the {@link User}.
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * @return the unique username of the {@link User}.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the unique username of the {@link User}.
	 * @param username the unique username of the {@link User}.
	 */
	public void setUsername(final String username) {
		this.username = username;
	}

	/**
	 * @return the name of the {@link User} which will be shown to other users.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Sets the name of the {@link User} which will be shown to other users.
	 * @param displayName the name of the {@link User} which will be shown to other users.
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the not encrypted password of the {@link User}.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the not encrypted password of the {@link User}.
	 * @param password the not encrypted password of the {@link User}.
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return he URL of an image of the {@link User}.
	 */
	public String getUserImg() {
		return userImg;
	}

	/**
	 * Sets he URL of an image of the {@link User}.
	 * @param userImg he URL of an image of the {@link User}.
	 */
	public void setUserImg(final String userImg) {
		this.userImg = userImg;
	}

	/**
	 * @return the current {@link Locale} of the browser.
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Sets the current {@link Locale} of the browser.
	 * @param locale the current {@link Locale} of the browser.
	 */
	public void setLocale(final String locale) {
		this.locale = locale;
	}

	/**
	 * @return the state if the {@link User} wants that only himself can view his profile.
	 */
	public boolean isPrivateProfile() {
		return privateProfile;
	}

	/**
	 * Sets the state if the {@link User} wants that only himself can view his profile.
	 * @param privateProfile the state if the {@link User} wants that only himself can view his profile.
	 */
	public void setPrivateProfile(final boolean privateProfile) {
		this.privateProfile = privateProfile;
	}
}
