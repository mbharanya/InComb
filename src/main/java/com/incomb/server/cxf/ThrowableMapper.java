package com.incomb.server.cxf;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Catches all {@link Throwable}s so that no information will be sent to the client.
 * Per default the exception with the stack trace will be written to the response.
 * This class creates a new {@link Response} with {@value Status#INTERNAL_SERVER_ERROR}.
 */
public class ThrowableMapper implements ExceptionMapper<Throwable> {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ThrowableMapper.class);

	/**
	 * Handles the {@link Throwable}.
	 * If it's an {@link OutOfMemoryError} it starts the garbage collector explicitly.
	 */
	@Override
	public Response toResponse(final Throwable t) {
		if(t instanceof OutOfMemoryError) {
			System.gc(); // try it.
		}
		else if(t instanceof WebApplicationException) {
			return ((WebApplicationException) t).getResponse();
		}

		LOGGER.error("An error during dispatching occured. Response with status code 500 sent.", t);

		return Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
	}
}
