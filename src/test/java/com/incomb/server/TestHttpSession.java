package com.incomb.server;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * Simple implementation of {@link HttpSession} for testing purposes.
 */
public class TestHttpSession implements HttpSession {

	private final Map<String, Object> attributes = new HashMap<>();

	@Override
	public Object getAttribute(final String arg0) {
		return attributes.get(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return null;
	}

	@Override
	public long getCreationTime() {
		return 0;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public long getLastAccessedTime() {
		return 0;
	}

	@Override
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public Object getValue(final String arg0) {
		return null;
	}

	@Override
	public String[] getValueNames() {
		return null;
	}

	@Override
	public void invalidate() {

	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public void putValue(final String arg0, final Object arg1) {

	}

	@Override
	public void removeAttribute(final String arg0) {
		attributes.remove(arg0);
	}

	@Override
	public void removeValue(final String arg0) {

	}

	@Override
	public void setAttribute(final String arg0, final Object arg1) {
		attributes.put(arg0, arg1);

	}

	@Override
	public void setMaxInactiveInterval(final int arg0) {

	}
}
