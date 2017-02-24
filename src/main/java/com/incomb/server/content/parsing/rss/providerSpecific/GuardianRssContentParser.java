package com.incomb.server.content.parsing.rss.providerSpecific;

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;

import com.incomb.server.content.parsing.rss.DefaultRssParser;
import com.incomb.server.model.ContentSource;

/**
 * A provider specific rss parser because guardian includes live tickers.
 */
public class GuardianRssContentParser extends DefaultRssParser<GuardianRssDocument> {
	private final String[] garbageStrings = {"Continue reading..."};

	/**
	 * Creates a new {@link GuardianRssContentParser}
	 */
	public GuardianRssContentParser(final ContentSource contentSource, final InputStream inputStream) {
		super(contentSource, inputStream);
	}
	
	@Override
	protected void createParsingElement(final String localPart, final XMLEvent event, final XMLEventReader eventReader) {
		parsingElement = new GuardianRssDocument(contentSource);
	}
	
	@Override
	protected void setDescription(final RssElement element) {
		if (actualElement.size() != 0 && actualElement.lastElement().equals(ITEM)) {
			String description = (String) element.body;
			for (final String garbageString : garbageStrings){
				description = description.replaceAll(garbageString, "");
			}

			parsingElement.getLastItem().setDescription(description);
		}
	}

	@Override
	protected void setLink(final RssElement element) {
		if (actualElement.size() != 0 && actualElement.lastElement().equals(ITEM)) {
			parsingElement.getLastItem().setLink((String) element.body);
		}
	}
	
}
