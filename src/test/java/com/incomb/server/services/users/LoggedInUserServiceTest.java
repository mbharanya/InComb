package com.incomb.server.services.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.incomb.server.ATest;
import com.incomb.server.TestHttpServletRequest;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.UserDao;
import com.incomb.server.services.users.model.UserModel;

public class LoggedInUserServiceTest extends ATest {

	private final LoggedInUserService service = new LoggedInUserService();
	private UserDao dao;

	@Before
	public void setUp() {
		dao = new UserDao(con);
	}

	@Test
	public void testLogin() {
		final User user = createUser();

		final UserModel userModel = new UserModel();
		userModel.setUsername(user.getUsername());
		userModel.setPassword(PASSWORD);

		final HttpServletRequest request = new TestHttpServletRequest();
		service.setRequest(request);
		final Response response = service.login(userModel, con);

		assertNotNull(response);
		assertEquals(200, response.getStatus());

		final User userToCheck = (User) request.getSession().getAttribute(LoggedInUserService.SESSIONATTR_LOGGEDIN);
		assertNotNull(userToCheck);
		assertEquals(user.getId(), userToCheck.getId());
	}

	@Test
	public void testLoginFailed() {
		final User user = createUser();

		final UserModel userModel = new UserModel();
		userModel.setUsername(user.getUsername());
		userModel.setPassword("wrongPassword");

		final HttpServletRequest request = new TestHttpServletRequest();

		try {
			service.setRequest(request);
			service.login(userModel, con);
			fail();
		}
		catch(final NotAuthorizedException e) {
			// should happen
		}

		final User userToCheck = (User) request.getSession().getAttribute(LoggedInUserService.SESSIONATTR_LOGGEDIN);
		assertNull(userToCheck);
	}

	@Test
	public void testLoginUnknownUser() {
		createUser();

		final UserModel userModel = new UserModel();
		userModel.setUsername("usssserrrr");
		userModel.setPassword(PASSWORD);

		final HttpServletRequest request = new TestHttpServletRequest();

		try {
			service.setRequest(request);
			service.login(userModel, con);
			fail();
		}
		catch(final NotAuthorizedException e) {
			// should happen
		}

		final User userToCheck = (User) request.getSession().getAttribute(LoggedInUserService.SESSIONATTR_LOGGEDIN);
		assertNull(userToCheck);
	}

	@Test
	public void testLogout() {
		final HttpServletRequest request = new TestHttpServletRequest();
		request.getSession().setAttribute(LoggedInUserService.SESSIONATTR_LOGGEDIN, createUser());

		service.setRequest(request);
		final Response response = service.logout();
		assertNotNull(response);
		assertEquals(200, response.getStatus());

		final User userToCheck = (User) request.getSession().getAttribute(LoggedInUserService.SESSIONATTR_LOGGEDIN);
		assertNull(userToCheck);
	}
}
