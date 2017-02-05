package com.incomb.server.content.parsing.rss.providerSpecific;

import java.util.regex.Pattern;

import com.incomb.server.content.parsing.rss.RssItem;
import com.incomb.server.i18n.TranslatorManager;

/**
 * A provider specific rss item because guardian includes live tickers.
 */
public class GuardianRssItem extends RssItem {
	
	/** 
	 * Regex for finding a live ticker
	 */
	private final static Pattern URL_PATTERN = Pattern.compile("\\/live\\/");
	
	/**
	 * Max Characters of an an article. If the length is bigger it could be a live ticker
	 */
	private final static int TICKER_MIN_CHARACTERS = 2500;
	
	/**
	 * Default text which replaces the description. If its a live ticker
	 */
	private final String LIVE_TICKER;

	/**
	 * Creates a new {@link GuardianRssItem} with the {@link GuardianRssDocument} as parent object.
	 */
	public GuardianRssItem(final GuardianRssDocument doc) {
		super(doc);
		LIVE_TICKER = TranslatorManager.translate(getDocument().getContentSource().getLocale(), "guardianParser.ticker");
	}
	
	@Override
	public void setLink(final String link) {
		super.setLink(link);
		if (URL_PATTERN.matcher(link).find()) {
			setTickerDescription();
		}
	}
	
	@Override
	public void setDescription(final String description) {
		if (description.length() >= TICKER_MIN_CHARACTERS || (getLink() != null && URL_PATTERN.matcher(getLink()).find())) {
			setTickerDescription();

		} else {
			super.setDescription(description);
		}
	}
	
	/**
	 * Sets the <code>LIVE_TICKER</code> as description.
	 */
	private void setTickerDescription() { 
		super.setDescription(LIVE_TICKER);
	}
	
}
