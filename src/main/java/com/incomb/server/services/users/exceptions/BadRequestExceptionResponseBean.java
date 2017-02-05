package com.incomb.server.services.users.exceptions;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.incomb.server.services.ResponseBean;
import com.incomb.server.utils.JsonUtil;
/**
 * Simple Wrapper for {@link RuntimeException} to throw a JSON serialized {@link ResponseBean}
 */
public class BadRequestExceptionResponseBean extends BadRequestException{
	/**
	 * Generated
	 */
	private static final long serialVersionUID = 244858427013205124L;

	public BadRequestExceptionResponseBean(final String message) {
		super(JsonUtil.getJson(new ResponseBean(message, false)));
	}

	public BadRequestExceptionResponseBean(final Object bean) {
		super(JsonUtil.getJson(bean), Response.status(Status.BAD_REQUEST).entity(bean).build());
	}

}
