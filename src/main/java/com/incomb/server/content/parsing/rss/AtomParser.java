package com.incomb.server.content.parsing.rss;

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.model.ContentSource;
import com.incomb.server.utils.ObjectUtil;

public class AtomParser extends AXMLParser<RssDocument> {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AtomParser.class);

	protected static final String TAG_FEED = "feed";
	protected static final String TAG_ENTRY = "entry";
	protected static final String TAG_UPDATED = "updated";
	protected static final String TAG_TITLE = "title";
	protected static final String TAG_CONTENT = "content";
	protected static final String TAG_PUBLISHED = "published";
	protected static final String TAG_LINK = "link";

	protected static final String ATTR_REL = "rel";
	protected static final String ATTR_HREF = "href";

	protected static final String REL_ALTERNATE = "alternate";

	public AtomParser(final ContentSource contentSource, final InputStream inputStream) {
		super(contentSource, inputStream);

		availableSetters = new String [] {"setTitle", "setUpdated", "setLink", "setContent", "setPublished"};

		try {
			startElementMethods.put(TAG_FEED,        ObjectUtil.assertNotNull(getDeclaredMethod("createParsingElement", String.class, XMLEvent.class, XMLEventReader.class), TAG_FEED));
			startElementMethods.put(TAG_ENTRY,       ObjectUtil.assertNotNull(getDeclaredMethod("pushElementIntoMap", String.class, XMLEvent.class, XMLEventReader.class), TAG_ENTRY));

			// used for the feed and entry
			startElementMethods.put(TAG_UPDATED,	 ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), TAG_UPDATED));

			// used only in entry
			startElementMethods.put(TAG_TITLE,       ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), TAG_TITLE));
			startElementMethods.put(TAG_LINK,        ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), TAG_LINK));
			startElementMethods.put(TAG_CONTENT, 	 ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), TAG_CONTENT));
			startElementMethods.put(TAG_PUBLISHED,   ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), TAG_PUBLISHED));

			endElementMehods.put(TAG_ENTRY,          ObjectUtil.assertNotNull(getDeclaredMethod("popElementFromStack"), TAG_ENTRY));

			specialMethods.put(TAG_ENTRY,            ObjectUtil.assertNotNull(getDeclaredMethod("createItemObject"), TAG_ENTRY));

		} catch (final SecurityException e) {
			LOGGER.error("Failed creating {} because", getClass(), e );
		}
	}

	// *************************************************************
	// ---------  Methods to set properties on objects -------------
	// *************************************************************

	/**
	 * Basic setter
	 */
	protected void setTitle(final RssElement element) {
		if (!actualElement.isEmpty() && actualElement.lastElement().equals(TAG_ENTRY)) {
			parsingElement.getLastItem().setTitle((String) element.body);
		}
	}

	/**
	 * Basic setter
	 */
	protected void setContent(final RssElement element) {
		// TODO check content type
		if (!actualElement.isEmpty() && actualElement.lastElement().equals(TAG_ENTRY)) {
			parsingElement.getLastItem().setDescription((String) element.body);
		}
	}

	/**
	 * Basic setter
	 */
	protected void setPublished(final RssElement element) {
		if (!actualElement.isEmpty() && actualElement.lastElement().equals(TAG_ENTRY)) {
			parsingElement.getLastItem().setPubDate(RssUtil.get3339Date((String) element.body));
		}
	}

	/**
	 * Basic setter
	 */
	protected void setUpdated(final RssElement element) {
		if(!actualElement.isEmpty()) {
			if (actualElement.lastElement().equals(TAG_ENTRY)) {
				parsingElement.getLastItem().setUpdated(RssUtil.get3339Date((String) element.body));
			}
			else if(actualElement.lastElement().equals(TAG_FEED)) {
				parsingElement.setLastBuildDate(RssUtil.get3339Date((String) element.body));
			}
		}
	}

	/**
	 * Basic setter
	 */
	protected void setLink(final RssElement element) {
		if (!actualElement.isEmpty() && actualElement.lastElement().equals(TAG_ENTRY)) {
			final Attribute relAttr = element.attributes.get(ATTR_REL);
			final Attribute hrefAttr = element.attributes.get(ATTR_HREF);
			if(relAttr != null && hrefAttr != null) {
				switch (relAttr.getValue()) {
					case REL_ALTERNATE:
						parsingElement.getLastItem().setLink(hrefAttr.getValue());
						break;

					// TODO get image
				}
			}
		}
	}
}
