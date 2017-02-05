package com.incomb.server.services.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.incomb.server.ATest;
import com.incomb.server.TestHttpServletRequest;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.UserDao;
import com.incomb.server.services.users.model.UserModel;

public class UsersServiceTest extends ATest {

	private final UsersService service = new UsersService();
	private UserDao dao;


	@Before
	public void setUp() {
		dao = new UserDao(con);
	}

	@Test
	public void testRegister() {
		final UserModel userModel = getTestUserModel();

		service.setRequest(new TestHttpServletRequest());
		service.register(userModel, con);
		final User user = dao.findByUsername(USERNAME);

		assertNotNull(user);
		assertEquals(userModel.getDisplayName(), user.getDisplayName());
		assertEquals(userModel.getEmail(), user.getEmail());
		assertEquals(userModel.getUsername(), user.getUsername());
	}
}
