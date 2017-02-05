package com.incomb.server.content.parsing.rss;

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.model.ContentSource;
import com.incomb.server.utils.ObjectUtil;

/**
 * A {@link DefaultRssParser} is used for parsing rss documents. It has the basic
 * operations for parsing rss documents. If you have some specials operations
 * you must write your own parser.
 *
 *
 * @param <T> Parsing Element
 */
public class DefaultRssParser<T extends RssDocument> extends AXMLParser<T> {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRssParser.class);


	//Rssfeed elements
	/**
	 * The RSS tag in the rss document.
	 */
	protected static final String RSS					   = "rss";
	/**
	 * The CHANNEL tag in the rss document.
	 */
	protected static final String CHANNEL                  = "channel";
	/**
	 * The ITEM tag in the rss document.
	 */
	protected static final String ITEM                     = "item";

	/**
	 * The TITLE tag in the rss document.
	 */
	protected static final String TITLE                    = "title";

	/**
	 * The LINK tag in the rss document.
	 */
	protected static final String LINK                     = "link";

	/**
	 * The DESCRIPTION tag in the rss document.
	 */
	protected static final String DESCRIPTION              = "description";

	/**
	 * The LANGUAGE tag in the rss document.
	 */
	protected static final String LANGUAGE                 = "language";

	/**
	 * <code>image</code> is not in the RSS specification but many providers use it.
	 * In future we will push it down to a provider specific parsing implementation.
	 */
	protected static final String IMAGE                    = "image";

	/**
	 * The ENCLOSURE tag in the rss document.
	 */
	protected static final String ENCLOSURE                = "enclosure";

	/**
	 * The PUB_DATE tag in the rss document.
	 */
	protected static final String PUB_DATE                 = "pubDate";

	/**
	 * The lastBuildDate tag in the rss document.
	 */
	protected static final String LAST_BUILD_DATE          = "lastBuildDate";

	/**
	 * An alternative url.
	 */
	protected static final String ATTR_URL 				   = "url";

	/**
	 * An alternative url. mime typet.
	 */
	protected static final String ATTR_MIME_TYPE		   = "mime-type";

	/**
	 * Creates a new Parser Object.
	 * @param contentSource - {@link ContentSource} to parse
	 * @param inputStream - Created {@link InputStream} providing the RSS feed to parse.
	 */
	public DefaultRssParser(final ContentSource contentSource, final InputStream inputStream) {
		super(contentSource, inputStream);

		availableSetters = new String [] {"setTitle", "setDescription", "setLink", "setImage", "setWidth", "setHeight", "setPubDate", "setEnclosure", "setLastBuildDate"};

		try {
			startElementMethods.put(RSS,         	ObjectUtil.assertNotNull(getDeclaredMethod("createParsingElement", String.class, XMLEvent.class, XMLEventReader.class), RSS));
			startElementMethods.put(CHANNEL,     	ObjectUtil.assertNotNull(getDeclaredMethod("pushElementIntoMap",   String.class, XMLEvent.class, XMLEventReader.class), CHANNEL));
			startElementMethods.put(ITEM,        	ObjectUtil.assertNotNull(getDeclaredMethod("pushElementIntoMap",   String.class, XMLEvent.class, XMLEventReader.class), ITEM));

			// used for the channel
			startElementMethods.put(LAST_BUILD_DATE,	ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), LAST_BUILD_DATE));

			// used only in item
			startElementMethods.put(TITLE,       	ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), TITLE));
			startElementMethods.put(LINK,        	ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), LINK));
			startElementMethods.put(DESCRIPTION, 	ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), DESCRIPTION));
			startElementMethods.put(LANGUAGE,    	ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), LANGUAGE));
			startElementMethods.put(PUB_DATE,    	ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), PUB_DATE));
			startElementMethods.put(IMAGE,       	ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), IMAGE));
			startElementMethods.put(ENCLOSURE,   	ObjectUtil.assertNotNull(getDeclaredMethod("parseStandardElement", String.class, XMLEvent.class, XMLEventReader.class), ENCLOSURE));

			endElementMehods.put(CHANNEL,       	ObjectUtil.assertNotNull(getDeclaredMethod("popElementFromStack"), CHANNEL));
			endElementMehods.put(ITEM,          	ObjectUtil.assertNotNull(getDeclaredMethod("popElementFromStack"), ITEM));

			specialMethods.put(ITEM,            	ObjectUtil.assertNotNull(getDeclaredMethod("createItemObject"), ITEM));

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
		if (actualElement.size() != 0 && actualElement.lastElement().equals(ITEM)) {
			parsingElement.getLastItem().setTitle((String) element.body);
		}
	}

	/**
	 * Basic setter
	 */
	protected void setDescription(final RssElement element) {
		if (actualElement.size() != 0 && actualElement.lastElement().equals(ITEM)) {
			parsingElement.getLastItem().setDescription((String) element.body);
		}
	}

	/**
	 * Basic setter
	 */
	protected void setLink(final RssElement element) {
		if (actualElement.size() != 0 && actualElement.lastElement().equals(ITEM)) {
			parsingElement.getLastItem().setLink((String) element.body);
		}
	}

	/**
	 * Basic setter
	 */
	protected void setImage(final RssElement element) {
		if (actualElement.size() != 0 && actualElement.lastElement().equals(ITEM)) {
			parsingElement.getLastItem().setImageUrl((String) element.body);
		}
	}

	/**
	 * Basic setter
	 */
	protected void setEnclosure(final RssElement element) {
		if (actualElement.size() != 0 && actualElement.lastElement().equals(ITEM) && element.attributes.containsKey(ATTR_URL)) {
			final Attribute mimeAttr = element.attributes.get(ATTR_MIME_TYPE);
			if(mimeAttr == null || mimeAttr.getValue().startsWith("image/")) {
				parsingElement.getLastItem().setImageUrl(element.attributes.get(ATTR_URL).getValue());
			}
		}
	}

	/**
	 * Basic setter
	 */
	protected void setPubDate(final RssElement element) {
		if (actualElement.size() != 0 && actualElement.lastElement().equals(ITEM)) {
			parsingElement.getLastItem().setPubDate(RssUtil.get2822Date((String) element.body));
		}
	}

	/**
	 * Basic setter
	 */
	protected void setLastBuildDate(final RssElement element) {
		parsingElement.setLastBuildDate(RssUtil.get2822Date((String) element.body));
	}
}
