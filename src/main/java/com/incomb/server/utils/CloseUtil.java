package com.incomb.server.utils;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util class for closing {@link Closeable}s.
 *
 */
public class CloseUtil {

	private static final Logger LOG = LoggerFactory.getLogger(CloseUtil.class);

	/**
	 * This Method closes the given {@link Closeable}.
	 * A thrown {@link IOException} will be logged.
	 * @param close object to close, may be null
	 */
	public static void close(final Closeable close){
		if (close == null) {
			return;
		}
		try {
			close.close();
		} catch (final IOException e) {
			LOG.warn("Tried to close {} and it fucked up", close, e);
		}
	}

	/**
	 * This Method closes the given {@link AutoCloseable}.
	 * A thrown {@link Exception} will be logged.
	 * @param close object to close, may be null
	 */
	public static void close(final AutoCloseable close){
		if (close == null) {
			return;
		}
		try {
			close.close();
		} catch (final Exception e) {
			LOG.warn("Tried to close {} and it fucked up", close, e);
		}
	}

}
