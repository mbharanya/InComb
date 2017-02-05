package com.incomb.server.content.parsing.rss;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.incomb.server.model.Content;
import com.incomb.server.model.ContentSource;

/**
 * This {@link RssDocument} represents a rss document in real live.
 */
public class RssDocument {

	/**
	 * {@link ContentSource} to which this document references.
	 */
	private final ContentSource contentSource;

	/**
	 * All {@link RssItem} containing in the Rss Feed.
	 */
	private final List<RssItem> items = new ArrayList<RssItem>();

	/**
	 * The last build date of the rss feed.
	 */
	private Date lastBuildDate = null;

	/**
	 * Creates a new {@link RssDocument}
	 * @param contentSource - {@link ContentSource}
	 */
	public RssDocument (final ContentSource contentSource) {
		this.contentSource = contentSource;
	}

	public ContentSource getContentSource() { return contentSource; }

	/**
	 * Adds a new {@link RssItem}
	 */
	public void addItem() { addItem(new RssItem(this)); }
	protected void addItem(final RssItem rssItem) { items.add(rssItem); }

	/**
	 * Removes the given item
	 * @param item - {@link RssItem} to remove
	 */
	public void removeItem(final RssItem item) { items.remove(item); }

	/**
	 * Returns the last {@link RssItem}
	 * @return the last {@link RssItem}
	 */
	public RssItem getLastItem() { return items.get(items.size()-1); }
	public RssItem[] getItems() { return items.toArray(new RssItem[items.size()]); }

	/**
	 * Sets the last build date of the rss feed.
	 * @param lastBuildDate the last build date of the rss feed.
	 */
	public void setLastBuildDate(final Date lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}

	/**
	 * Checks if the {@link Content}s in the rss feed have changed since the given {@link Date}.
	 * @param date the {@link Date} to compare.
	 * @return true if last change date is unknown or the {@link Content}s have changed.
	 */
	public boolean hasChangedSince(final Date date) {
		return lastBuildDate == null || lastBuildDate.after(date);
	}

	/**
	 * Returns all {@link RssItem}s in as a {@link Content}[].
	 * @param lastFetch the last fetch time or null if this is the first fetch.
	 * @return the {@link Content}s in the rss feed.
	 */
	public Content[] getContent(final Date lastFetch) {
		final List<Content> list = new ArrayList<Content>();
		for (final RssItem item : items) {
			if(item.getUpdated() == null || lastFetch == null || item.getUpdated().after(lastFetch)) {
				list.add(item.createContentObj());
			}
		}
		return list.toArray(new Content[list.size()]);
	}
}
