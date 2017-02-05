package com.incomb.server.content.loader;

import com.incomb.server.model.ContentSource;
import com.incomb.server.model.RssFeedContentSource;

/**
 * {@link ContentReaderFactory} is a Singleton for creating the specific {@link IContentReader}
 * for {@link ContentSource}s.
 */
public class ContentReaderFactory {

	/**
	 * Single instance.
	 */
	private static ContentReaderFactory instance;

	/**
	 * No access for you. Muhahahaha
	 */
	private ContentReaderFactory() { }

	/**
	 * Returns a new {@link IContentReader} for the specific {@link ContentSource} object.
	 * @param {@link ContentSource} contentSource
	 * @return {@link IContentReader}
	 */
	protected IContentReader get(final ContentSource contentSource) {
		IContentReader contentReader = null;

		if (contentSource instanceof RssFeedContentSource) {
			contentReader = new RssContentReader(contentSource);

		} else {
			throw new IllegalArgumentException("No IContentReader found for: " + contentSource.getClass().toString());
		}

		return contentReader;
	}

	/**
	 * Creates a new {@link ContentReaderFactory} instance if
	 * no object has already been created.
	 *
	 * @return the single {@link ContentReaderFactory}
	 */
	public static synchronized ContentReaderFactory getInstance() {
		if (instance == null) {
			instance = new ContentReaderFactory();
		}
		return instance;
	}

}
