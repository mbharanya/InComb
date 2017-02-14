package com.incomb.server.content.loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.content.parsing.ContentParser;
import com.incomb.server.content.parsing.rss.RssContentParserFactory;
import com.incomb.server.content.parsing.rss.RssDocument;
import com.incomb.server.model.Content;
import com.incomb.server.model.ContentSource;
import com.incomb.server.model.FetchHistory;

public class RssContentReader implements IContentReader {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RssContentReader.class);

	/**
	 * {@link ContentSource} of the the content will be read.
	 */
	private final ContentSource contentSource;

	/**
	 * Creates a new {@link RssContentReader}.
	 * @param contentSource of the the content will be read.
	 */
	public RssContentReader(final ContentSource contentSource) {
		this.contentSource = contentSource;
	}

	/**
	 * Reads, parses and returns the content form the content-source if it has changed since the given Date.
	 * @return read content.
	 */
	@Override
	public Content[] read(final FetchHistory lastFetch) {
		RssDocument doc = null;

		final Date lastFetchDate = lastFetch == null ? null : lastFetch.getFetchTime();

		try {
			LOGGER.debug("Reading rss feed from '{}' of content source '{}'.", contentSource.getUrl(), contentSource.getId());

			final RssContentParserFactory factory = RssContentParserFactory.getInstance();

			final URLConnection con = new URL(contentSource.getUrl()).openConnection();
			con.setConnectTimeout(2000);
			con.setReadTimeout(30000);

			final ContentParser<RssDocument> parser = factory.get(contentSource, con.getInputStream());

			if (parser.parse()) {
				doc = parser.getParsedObject();

				if(lastFetch != null && !doc.hasChangedSince(lastFetchDate)) {
					LOGGER.debug("RSS content of content source {} hasn't changed since {}.",
							contentSource.getId(), lastFetch.getFetchTime());

					return new Content[0];
				}
			}

		} catch (final IOException e) {
			LOGGER.warn("Cannot connect to: {}", contentSource.getUrl(), e);
		}
		return doc != null ? doc.getContent(lastFetchDate) : new Content[0];
	}

}
