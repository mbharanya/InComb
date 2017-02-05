package com.incomb.server.content.loader;

import com.incomb.server.model.Content;
import com.incomb.server.model.ContentSource;
import com.incomb.server.model.FetchHistory;


/**
 * {@link IContentReader} is an interface which allows you to read specific
 * content from a defined source.
 */
public interface IContentReader {

	/**
	 * Returns the content read form content-source if it has changed since the given last fetch.
	 * @param lastFetch the last fetch of this {@link ContentSource} or <code>null</code> if it
	 * 			wasn't fetched yet.
	 * @return read {@link Content}
	 */
	public Content[] read(FetchHistory lastFetch);

}
