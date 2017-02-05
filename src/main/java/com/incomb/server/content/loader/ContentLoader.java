package com.incomb.server.content.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.model.ContentSource;
import com.incomb.server.model.FetchHistory;
import com.incomb.server.utils.ObjectUtil;

/**
 * This class loads content form a given {@link ContentSource} and hands over the read data to the {@link ContentLoaderManager}.
 */
public class ContentLoader implements Runnable {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentLoader.class);

	private final ContentLoaderManager manager;
	private final IContentReader contentReader;
	private final ContentSource contentSource;
	private final FetchHistory lastFetch;

	/**
	 * Creates a new {@link ContentLoader}. It asserts that no parameter could be null.
	 * @param manager - must not be null
	 * @param contentSource - must not be null
	 * @param lastFetch the last fetch of the {@link ContentSource} or <code>null</code> if it wasn't fetched yet.
	 */
	public ContentLoader(final ContentLoaderManager manager, final ContentSource contentSource, final FetchHistory lastFetch) {
		this.manager       = ObjectUtil.assertNotNull(manager, "manager");
		this.contentSource = ObjectUtil.assertNotNull(contentSource, "contentSource");
		this.contentReader = ObjectUtil.assertNotNull(manager.getContentFactory().get(contentSource), "factory");
		this.lastFetch 	   = lastFetch;
	}

	/**
	 * Reads the Content and joins the back the the {@link ContentLoaderManager}.
	 */
	@Override
	public void run() {
		try {
			manager.joinThread(this, contentReader.read(lastFetch));
		}
		catch(final Throwable t) {
			LOGGER.error("An error occured during reading new contents from content source {}.", contentSource.getId(), t);
		}
	}

	/**
	 * Returns its {@link ContentSource};
	 * @return contentSource
	 */
	public ContentSource getContentSource() { return contentSource; }
}
