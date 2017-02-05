package com.incomb.server.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.incomb.server.model.dao.TagPreferenceDao;

/**
 * This class represents a registered user.
 *
 * <p>This structure is in the database too.
 * To operate with the database use {@link TagPreferenceDao}.</p>
 */
public class User implements Serializable, Cloneable {

	private static final long serialVersionUID = 1911413508;

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
	 * The with the {@link #salt} encrypted password of the {@link User}.
	 */
	private String passwordHash;

	/**
	 * The salt to encrypt the password of the {@link User}.
	 */
	private String salt;

	/**
	 * The time when the {@link User} has registered himself.
	 */
	private Timestamp registerDate;

	/**
	 * The state if the {@link User} was deleted.
	 */
	private boolean deleted = false;

	/**
	 * The state if the {@link User} wants that only himself can view his profile.
	 */
	private boolean privateProfile;

	/**
	 * Constructs a new object with the default properties.
	 */
	public User() {
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param id the id of the {@link User}.
	 * @param email the e-mail address of the {@link User}.
	 * @param username the unique username of the {@link User}.
	 * @param displayName the name of the {@link User} which will be shown to other users.
	 * @param passwordHash the with the {@link #salt} encrypted password of the {@link User}.
	 * @param salt the salt to encrypt the password of the {@link User}.
	 * @param registerDate the time when the {@link User} has registered himself.
	 * @param privateProfile the state if the {@link User} wants that only himself can view his profile.
	 */
	public User(final long id, final String email, final String username, final String displayName, final String passwordHash, final String salt, final Timestamp registerDate, final boolean privateProfile) {
		this.id = id;
		this.email = email;
		this.username = username;
		this.displayName = displayName;
		this.passwordHash = passwordHash;
		this.salt = salt;
		this.registerDate = registerDate;
		this.privateProfile = privateProfile;
	}

	/**
	 * Constructs a new object with the given properties.
	 * @param id the id of the {@link User}.
	 * @param email the e-mail address of the {@link User}.
	 * @param username the unique username of the {@link User}.
	 * @param displayName the name of the {@link User} which will be shown to other users.
	 * @param passwordHash the with the {@link #salt} encrypted password of the {@link User}.
	 * @param salt the salt to encrypt the password of the {@link User}.
	 * @param registerDate the time when the {@link User} has registered himself.
	 * @param deleted the state if the {@link User} was deleted.
	 * @param privateProfile the state if the {@link User} wants that only himself can view his profile.
	 */
	public User(final long id, final String email, final String username, final String displayName, final String passwordHash, final String salt, final Timestamp registerDate, final boolean deleted, final boolean privateProfile) {
		this(id, email, username, displayName, passwordHash, salt, registerDate, privateProfile);
		this.deleted = deleted;
	}

	/**
	 * Clones the {@link User} with all its properties.
	 * @return the cloned {@link User}.
	 */
	@Override
	public User clone() throws CloneNotSupportedException {
		return (User) super.clone();
	}

	/**
	 * Checks if the properties of the given {@link User} are equal with them of this.
	 * @param toCheck the {@link User} to check.
	 * @return true if all properties are equal.
	 */
	public boolean equals(final User toCheck){
		return (
			getId() == toCheck.getId() &&
			getEmail().equals(toCheck.getEmail()) &&
			getUsername().equals(toCheck.getUsername()) &&
			getDisplayName().equals(toCheck.getDisplayName()) &&
			getPasswordHash().equals(toCheck.getPasswordHash()) &&
			getSalt().equals(toCheck.getSalt()) &&
			getRegisterDate().equals(toCheck.getRegisterDate()) &&
			isDeleted() == toCheck.isDeleted()
		);
	}

	/**
	 * @return the id of the {@link User}.
	 */
	public long getId() {
		return this.id;
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
		return this.email;
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
		return this.username;
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
		return this.displayName;
	}

	/**
	 * Sets the name of the {@link User} which will be shown to other users.
	 * @param displayName the name of the {@link User} which will be shown to other users.
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the with the {@link #salt} encrypted password of the {@link User}.
	 */
	public String getPasswordHash() {
		return this.passwordHash;
	}

	/**
	 * Sets the with the {@link #salt} encrypted password of the {@link User}.
	 * @param passwordHash the with the {@link #salt} encrypted password of the {@link User}.
	 */
	public void setPasswordHash(final String passwordHash) {
		this.passwordHash = passwordHash;
	}

	/**
	 * @return the salt to encrypt the password of the {@link User}.
	 */
	public String getSalt() {
		return this.salt;
	}

	/**
	 * Sets the salt to encrypt the password of the {@link User}.
	 * @param salt the salt to encrypt the password of the {@link User}.
	 */
	public void setSalt(final String salt) {
		this.salt = salt;
	}

	/**
	 * @return the time when the {@link User} has registered himself.
	 */
	public Timestamp getRegisterDate() {
		return this.registerDate;
	}

	/**
	 * Sets the time when the {@link User} has registered himself.
	 * @param registerDate the time when the {@link User} has registered himself.
	 */
	public void setRegisterDate(final Timestamp registerDate) {
		this.registerDate = registerDate;
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
