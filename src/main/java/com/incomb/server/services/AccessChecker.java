package com.incomb.server.services;

import java.sql.Connection;

import javax.ws.rs.NotAuthorizedException;

import com.incomb.server.model.User;
import com.incomb.server.model.dao.UserDao;

/**
 * This class should be used to check access rights in services.
 * Currently only a login check exists but this class should be
 * the single access checker which will check roles and permissions
 * too.
 *
 * Method names starting with "check" throw an exception if the check
 * wasn't successful. Method names starting with "is" return a boolean
 * which says if the check was successful or not.
 */
public class AccessChecker {

	/**
	 * The current logged in {@link User}.
	 */
	private final User user;

	/**
	 * A database connection to get additional needed information.
	 * Don't close this {@link Connection} in the {@link AccessChecker}.
	 */
	private final Connection con;

	/**
	 * Constructs a new instance with the current logged in {@link User}.
	 * @param user the current logged in {@link User}.
	 * @param con a database connection to get additional needed information.
	 * 		This connection must be closed when this object won't be used anymore.
	 */
	public AccessChecker(final User user, final Connection con) {
		this.user = user;
		this.con = con;
	}

	/**
	 * Checks if a user is logged in and if it's the same as the given as param.
	 * @param userId the id of the {@link User} to check if it's logged in.
	 * @return true if a user is logged in and it's the same as requested.
	 */
	public boolean isLoggedInUser(final long userId) {
		return user != null && user.getId() == userId;
	}

	/**
	 * Checks if a user is logged in and if it's the same as the given as param.
	 * If not a {@link NotAuthorizedException} is thrown.
	 * @param userId the id of the {@link User} to check if it's logged in.
	 * @throws NotAuthorizedException if no user is logged in or it's not the same as requested.
	 * @see #isLoggedInUser(long)
	 */
	public void checkLoggedInUser(final long userId) {
		if(!isLoggedInUser(userId)) {
			throw new NotAuthorizedException(new RuntimeException("errors.notAuthorized")); // FIXME: XMBomb[03.02.2015] JOSEF: Do not throw RunTimeException :\
		}
	}

	/**
	 * Checks if the {@link User} with the given id has set his profile to preview and
	 * is currently not logged in.
	 * @param userId the id of the {@link User} to check if his profile is private.
	 * @throws NotAuthorizedException if no user is logged in or it's not the same as requested.
	 */
	public void checkPrivateProfile(final long userId) {
		// if the user is logged in we don't have to do anymore checks
		if(!isLoggedInUser(userId)) {
			final User user = new UserDao(con).findById(userId);
			if(user == null) {
				throw new IllegalArgumentException("User with id " + userId + " couldn't be found in the database.");
			}

			if(user.isPrivateProfile()) {
				throw new NotAuthorizedException(new RuntimeException("errors.notAuthorized"));// FIXME: XMBomb[03.02.2015] JOSEF: Do not throw RunTimeException :\
			}
		}
	}
}
