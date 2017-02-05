package com.incomb.server.users.indexing;

import java.util.Locale;

import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.model.User;

/**
 * Represents a {@link User} which will be indexed.
 */
public class UserIndexElement implements IIndexElement {

	/**
	 * The {@link User} to index.
	 */
	private final User user;

	/**
	 * The operation of the element.
	 */
	private final EOperation operation;

	/**
	 * Creates a new instance with all needed properties.
	 * @param provider the {@link User} to index.
	 * @param operation the operation of the element
	 */
	public UserIndexElement(final User user, final EOperation operation) {
		this.user = user;
		this.operation = operation;
	}

	/**
	 * @return the operation of the element.
	 */
	@Override
	public EOperation getOperation() {
		return operation;
	}

	/**
	 * Important: always returns null because users are locale independent.
	 */
	@Override
	public Locale getLocale() {
		return null;
	}

	/**
	 * @return {@link User#getId()}
	 */
	@Override
	public String getId() {
		return String.valueOf(user.getId());
	}

	/**
	 * Returns the content of the field.
	 *
	 * Available fields:
	 * <ul>
	 * 	<li>{@link UserIndexType#FIELD_USERNAME}</li>
	 * 	<li>{@link UserIndexType#FIELD_DISPLAY_NAME}</li>
	 * </ul>
	 */
	@Override
	public Object getContent(final String fieldName) {
		switch (fieldName) {
			case UserIndexType.FIELD_USERNAME:
				return user.getUsername();
			case UserIndexType.FIELD_DISPLAY_NAME:
				return user.getDisplayName();
			default:
				throw new IllegalArgumentException("Field name " + fieldName + " is unknown.");
		}
	}
}
