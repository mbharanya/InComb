package com.incomb.server.services.users.exceptions;

import javax.ws.rs.NotFoundException;

import com.incomb.server.services.ResponseBean;
import com.incomb.server.utils.JsonUtil;
/**
 * Simple Wrapper for {@link RuntimeException} to throw a JSON serialized {@link ResponseBean}
 */
public class NotFoundExceptionResponseBean extends NotFoundException{
	/**
	 * Generated
	 */
	private static final long serialVersionUID = -9078749964107102097L;

	public NotFoundExceptionResponseBean(final String message) {
		super(JsonUtil.getJson(new ResponseBean(message, false)));
	}
	
}
