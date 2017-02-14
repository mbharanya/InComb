package com.incomb.server.content.loader;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.model.Content;
import com.incomb.server.model.ContentSource;
import com.incomb.server.model.FetchHistory;
import com.incomb.server.utils.CloseUtil;

/**
 * This {@link TxtContentReader} can be used for reading a simple text document
 * from the Internet via <code>HTTP-GET</code>.
 */
public class TxtContentReader implements IContentReader {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TxtContentReader.class);

	/**
	 * {@link ContentSource} of the the content will be read.
	 */
	private final ContentSource contentSource;

	/**
	 * Creates a new {@link RssContentReader}.
	 * @param contentSource of the the content will be read.
	 */
	public TxtContentReader(final ContentSource contentSource) {
		this.contentSource = contentSource;
	}

	/**
	 * Returns the content read form content-source.
	 * @return read {@link Content}
	 */
	@Override
	public Content[] read(final FetchHistory lastFetch) {

		final CloseableHttpClient httpClient = HttpClients.createDefault();
		final HttpGet httpget = new HttpGet(contentSource.getUrl());
		final HttpContext context = new BasicHttpContext();

		CloseableHttpResponse response = null;
		String stringRead = null;

		try {
			try {
				LOGGER.info("Loading uri: " + httpget.getURI());
				response = httpClient.execute(httpget, context);
				final HttpEntity entity = response.getEntity();

				if (entity != null) {
					stringRead = IOUtils.toString(entity.getContent());
					LOGGER.info("Read {} bytes from: {}", stringRead.getBytes().length, httpget.getURI());
				}

			} finally {
				CloseUtil.close(response);
				CloseUtil.close(httpClient);
			}

		} catch (final Exception e) {
			LOGGER.warn("Error occurred while reading text document: " + contentSource.getUrl());
		}

		return new Content[] { createContentObject(stringRead) };
	}

	/**
	 * Creates the {@link Content} <code>object</code>.
	 * @param stringRead content.
	 * @return {@link Content}
	 */
	public Content createContentObject(final String stringRead) {
		return new Content(0, contentSource.getProviderId(), contentSource.getCategoryId(), null, null, stringRead, null);
	}

}
