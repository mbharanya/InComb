package com.incomb.server.services.news;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.ATest;
import com.incomb.server.TestHttpServletRequest;
import com.incomb.server.model.ContentComment;
import com.incomb.server.services.ResponseBean;
import com.incomb.server.services.users.LoggedInUserService;
import com.incomb.server.utils.JsonUtil;

public class CommentsServiceTest extends ATest{
	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CommentsServiceTest.class);
	
	private final HttpServletRequest request = new TestHttpServletRequest();

	private final CommentsService service = new CommentsService();
	final ContentComment testComment = getTestComment();

	@Before
	public void testAddComment(){
		logInUser();
		service.setRequest(request);
		addComment();
	}
	
	private void logInUser(){
		request.getSession().setAttribute(LoggedInUserService.SESSIONATTR_LOGGEDIN, createUser());
	}
	
	private void addComment(){
		final Response response = service.addComment(con, testComment, 1);
		final ResponseBean bean = (ResponseBean)response.getEntity();
		LOGGER.info("Got response {}", JsonUtil.getJson(bean));
		assertTrue(bean.getSuccess());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetCommentsOfContentId(){
		final Response comments = service.getCommentsOfContentId(1, con);
		final List<ContentComment> commentList = (List<ContentComment>) comments.getEntity();
		assertNotNull(commentList);
		assertTrue(testComment.equals(commentList.get(0)));
	}
}
