package com.incomb.server.model.dao;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Test;

import com.incomb.server.ATest;
import com.incomb.server.model.CategoryPreference;
import com.incomb.server.model.User;

public class CategoryPreferenceDaoTest extends ATest {

	private static final int USERID = 23478932;

	@Test
	public void testCreateOnRegister() {
		final User dummyUser = new User(USERID, "", "", "", "", "", new Timestamp(System.currentTimeMillis()), false);
		new UserDao(con).insert(dummyUser);

		final CategoryPreferenceDao dao = new CategoryPreferenceDao(con);
		dao.createCategoryPreferences(USERID);

		final List<CategoryPreference> preferences = dao.getCategoryPreferences(USERID);
		assertEquals(6, preferences.size());

		for (final CategoryPreference preference : preferences) {
			assertEquals(USERID, preference.getUserId());
		}
	}
}
