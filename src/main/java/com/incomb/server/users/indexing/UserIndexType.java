package com.incomb.server.users.indexing;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.incomb.server.indexing.IIndexData;
import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.indexing.conf.IIndexTypeConf;
import com.incomb.server.indexing.conf.fields.IIndexFieldConf;
import com.incomb.server.indexing.conf.fields.TextIndexFieldConf;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.UserDao;

/**
 * This is the {@link IIndexTypeConf} for {@link User}s.
 * It returns all fields which should be indexed and returns
 * all elements for a complete reindex.
 */
public class UserIndexType implements IIndexTypeConf {

	/**
	 * This class uses the singleton pattern.
	 * This constant holds the single instance of this class and
	 * can be accessed by calling {@link #getInstance()}.
	 */
	private static final UserIndexType INSTANCE = new UserIndexType();

	/**
	 * The name of this index type.
	 */
	public static final String INDEX_TYPE_NAME = "user";

	/**
	 * Contains the username of the {@link User}.
	 */
	public static final String FIELD_USERNAME = "username";

	/**
	 * Contains the display name of the {@link User}.
	 */
	public static final String FIELD_DISPLAY_NAME = "displayname";

	/**
	 * The constructor is only accessible for this class and subclasses.
	 */
	protected UserIndexType() {

	}

	/**
	 * This class uses the singleton pattern.
	 * This method returns the single instance of this class.
	 */
	public static UserIndexType getInstance() {
		return INSTANCE;
	}

	/**
	 * Returns the name.
	 */
	@Override
	public String getName() {
		return INDEX_TYPE_NAME;
	}

	/**
	 * Returns all configured fields of this type.
	 *
	 * Available fields:
	 * <ul>
	 * 	<li>{@link UserIndexType#FIELD_USERNAME}</li>
	 * 	<li>{@link UserIndexType#FIELD_DISPLAY_NAME}</li>
	 * </ul>
	 */
	@Override
	public List<IIndexFieldConf<?>> getFields() {
		final List<IIndexFieldConf<?>> fields = new ArrayList<>();

		fields.add(new TextIndexFieldConf(FIELD_USERNAME, true));
		fields.add(new TextIndexFieldConf(FIELD_DISPLAY_NAME, true));

		return fields;
	}

	/**
	 * Returns all {@link User}s. For {@link User} a {@link UserIndexElement} is returned.
	 * {@link UserIndexElement#getOperation()} returns {@link EOperation#INSERT}.
	 */
	@Override
	public List<? extends IIndexElement> getElements(final Connection con, final int start, final int amount) {
		final List<UserIndexElement> elements = new ArrayList<>();
		final List<User> records = new UserDao(con).getUsers(start, amount);

		for (final User record : records) {
			elements.add(new UserIndexElement(record, EOperation.INSERT));
		}

		return elements;
	}

	@Override
	public void afterIndexing(final IIndexData indexData) {
		// do nothing at the moment
	}
}
