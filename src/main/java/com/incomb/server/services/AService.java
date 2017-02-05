package com.incomb.server.services;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.incomb.server.model.User;
import com.incomb.server.services.users.LoggedInUserService;
/**
 * Contains methods for session manipulation and wrapper methods for common HTTP methods
 */
public abstract class AService {
	/**
	 * Charset to encode the Response in
	 */
	private static final String DEFAULT_CHARSET = "utf-8";

	@Context
	private HttpServletRequest request;

	@Context
	private Connection con;
	
	/**
	 * Sets an Attribute in the current session of the user
	 * @param key to set
	 * @param value to set
	 */
	protected void setSessionAttribute(final String key, final Object value){
		request.getSession(true).setAttribute(key, value);
	}

	/**
	 * Get a value from the current session of the user
	 * @param key to get
	 * @return the found object
	 */
	protected Object getSessionAttribute(final String key) {
		return request.getSession(true).getAttribute(key);
	}

	/**
	 * Deletes the value of key in the current session
	 * @param key to delete
	 */
	protected void removeSessionAttribute(final String key) {
		request.getSession(true).removeAttribute(key);
	}

	/**
	 * Returns a HTTP 200 Response object and serializes the bean as message body
	 * @param bean
	 * @return the created Response
	 */
	protected Response ok(final Object bean) {
		return Response.ok().entity(bean).encoding(DEFAULT_CHARSET).build();
	}
	
	/**
	 * Puts the number in a new {@link ResponseBean} converting it to a String
	 * Returns a HTTP 200 Response object and serializes the bean as message body
	 * @param number
	 * @return the created Response
	 */
	protected Response ok(final int number) {
		return Response.ok().entity(new ResponseBean(String.valueOf(number), true)).encoding(DEFAULT_CHARSET).build();
	}

	/**
	 * Puts the message in a new {@link ResponseBean}
	 * Returns a HTTP 200 Response object and serializes the bean as message body
	 * @param message
	 * @return the created Response
	 */
	protected Response ok(final String message) {
		return Response.ok().entity(new ResponseBean(message, true)).encoding(DEFAULT_CHARSET).build();
	}

	/**
	 * Returns a HTTP 500 Response object and serializes the bean as message body
	 * @param bean
	 * @return the created Response
	 */
	protected Response serverError(final Object bean) {
		return Response.serverError().entity(bean).encoding(DEFAULT_CHARSET).build();
	}

	/**
	 * Returns a HTTP Not Modified Response object and serializes the bean as message body
	 * @param bean
	 * @return the created Response
	 */
	protected Response notModified(final Object bean) {
		return Response.notModified().entity(bean).encoding(DEFAULT_CHARSET).build();
	}
	
	/**
	 * Puts the message in a new {@link ResponseBean} with success = true
	 * Returns a HTTP Not Modified Response object and serializes the bean as message body
	 * @param message
	 * @return the created Response
	 */
	protected Response notModified(final String message){
		return Response.notModified().entity(new ResponseBean(message, true)).encoding(DEFAULT_CHARSET).build();
	}

	/**
	 * Returns a HTTP Accepted Response object and serializes the bean as message body
	 * @param bean
	 * @return the created Response
	 */
	protected Response accepted(final Object bean) {
		return Response.accepted().entity(bean).encoding(DEFAULT_CHARSET).build();
	}

	/**
	 * Returns a new {@link AccessChecker} from the session attribute
	 * @return a new {@link AccessChecker}
	 */
	protected AccessChecker getAccessChecker() {
		return new AccessChecker(getLoggedInUser(), con);
	}

	/**
	 * Gets the currently logged in {@link User} in the session
	 * @return the found {@link User}
	 */
	private User getLoggedInUser() {
		return (User) getSessionAttribute(LoggedInUserService.SESSIONATTR_LOGGEDIN);
	}

	/**
	 * Gets the HTTP Request
	 * @return the HTTP Request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
	
	/**
	 * Sets the HTTP Request
	 * @param request the HTTP Request
	 */
	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}
}
