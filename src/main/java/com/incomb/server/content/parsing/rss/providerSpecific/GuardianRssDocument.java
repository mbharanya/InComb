package com.incomb.server.content.parsing.rss.providerSpecific;

import com.incomb.server.content.parsing.rss.RssDocument;

import com.incomb.server.model.ContentSource;

/**
 * A provider specific rss document because guardian includes live tickers.
 */
public class GuardianRssDocument extends RssDocument {

	/** 
	 * Creates a new {@link GuardianRssDocument}
	 * @param contentSource
	 */
	public GuardianRssDocument(final ContentSource contentSource) {
		super(contentSource);
	}
	
	@Override
	public void addItem() { super.addItem(new GuardianRssItem(this)); 	}

}
