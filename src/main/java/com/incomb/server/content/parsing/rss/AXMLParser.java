package com.incomb.server.content.parsing.rss;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.content.parsing.ContentParser;
import com.incomb.server.model.ContentSource;
import com.incomb.server.utils.ObjectUtil;

public abstract class AXMLParser<T extends RssDocument> implements ContentParser<T> {

	/**
	 * <p>
	 * The {@link Logger} for this class.
	 * </p>
	 */
	private static final Logger   LOGGER = LoggerFactory.getLogger(DefaultRssParser.class);

	protected final InputStream   inputStream;
	protected final ContentSource contentSource;

	/**
	 * This array contains setters which are called if start tag was found.
	 */
	protected String []           		availableSetters;

	//Special Methods for not easy to set properties
	/**
	 * Methods for special operations. For example creating a new item object.
	 */
	protected final Map<String, Method> specialMethods     = new HashMap<String, Method>();

	/**
	 * Methods for Start Tags
	 */
	protected final Map<String, Method> startElementMethods = new HashMap<String, Method>();

	/**
	 * Methods for End Tags
	 */
	protected final Map<String, Method> endElementMehods   = new HashMap<String, Method>();

	/**
	 * Stack with the actual Element
	 */
	protected final Stack<String>       actualElement      = new Stack<String>();

	protected T parsingElement;

	public AXMLParser(final ContentSource contentSource, final InputStream inputStream) {
		this.inputStream = inputStream;
		this.contentSource = contentSource;
	}

	// *************************************************************
	// -------- Basic Method for reflection Stuff goes here --------
	// *************************************************************

	/**
	 * Creates a new Parsing Element.
	 */
	@SuppressWarnings("unchecked")
	protected void createParsingElement(final String localPart, final XMLEvent event, final XMLEventReader eventReader) {
		parsingElement = (T) new RssDocument(contentSource);
	}

	/**
	 * Pushes a element into the local stack.
	 * <br>
	 * <b>Attention: Reflection-Method - do not change method header</b>
	 * @param localPart
	 * @param event - not used
	 * @param eventReader  - not used
	 */
	protected void pushElementIntoMap(final String localPart, final XMLEvent event, final XMLEventReader eventReader) {
		actualElement.push(localPart);

		if (specialMethods.containsKey(localPart)) {
			Method method;
			try {
				method = specialMethods.get(localPart);
				method.invoke(this);

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
				LOGGER.error("Error while invoking method for local part: {}", localPart, e);
			}
		}
	}

	/**
	 * Pops a element from the local stack.
	 * <br>
	 * <b> Attention: Reflection-Method - do not change method header</b>
	 */
	protected void popElementFromStack() {
		actualElement.pop();
	}

	// *************************************************************
	// -------- Special Method for dealing with fields goes here ---
	// *************************************************************

	protected void createItemObject() {
		parsingElement.addItem();
	}

	// *************************************************************
	// -------- Basic Methods to deal with fields go here ----------
	// *************************************************************

	/**
	 * This methods parses a standard element. If the <code>availableSetters</code> array
	 * contains the <code>localPart</code>, the method <code>set + localPart()</code> will be invoked.
	 */
	protected boolean parseStandardElement(final String localPart, final XMLEvent event, final XMLEventReader eventReader) {
		final String methodName = "set" + localPart.substring(0, 1).toUpperCase() + localPart.substring(1);
		if (!Arrays.asList(availableSetters).contains(methodName)) {
			return false;
		}

		Method method = null;
		try {
			final RssElement element = new RssElement();
			element.body = getCharacterData(event, eventReader);

			@SuppressWarnings("unchecked")
			final Iterator<Attribute> attributeIterator = event.asStartElement().getAttributes();
			while(attributeIterator.hasNext()) {
				final Attribute attribute = attributeIterator.next();
				element.attributes.put(attribute.getName().getLocalPart(), attribute);
			}

			method = ObjectUtil.assertNotNull(getDeclaredMethod(methodName, RssElement.class), methodName);
			method.invoke(this, element);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			LOGGER.error("Error while invoking method: {}", methodName, e);
			return false;

		} catch (final XMLStreamException e) {
			LOGGER.error("Exception occured while reading Rss from {}:", contentSource != null ? contentSource.getUrl() : "unknown", methodName, e);
			return true;
		}
		return true;
	}

	// *************************************************************
	// ---------  Parsing Methods ----------------------------------
	// *************************************************************

	/**
	 * Reads the data from the {@link XMLEvent} and returns it. CData
	 * will be handled special.
	 * @param event {@link XMLEvent}
	 * @param eventReader Reader for Reading {@link XMLEvent}
	 * @return Character data from element
	 * @throws XMLStreamException
	 */
	private String getCharacterData(XMLEvent event, final XMLEventReader eventReader) throws XMLStreamException {
		String result = "";
		event = eventReader.nextEvent();
		if (event instanceof Characters) {
			result = event.asCharacters().getData();
		}
		return result;
	}

	@Override
	public boolean parse() {
		LOGGER.debug("Started parsing rss feed of content source {}.", contentSource.getId());
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		try {
			final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			final XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);

			while (eventReader.hasNext()) {
				final XMLEvent event = eventReader.nextEvent();

				//Processing start elements
				if (event.isStartElement()) {
					final String localPart = event.asStartElement().getName().getLocalPart();

					final Method method = startElementMethods.get(localPart);
					if (method != null) {
						try {
							method.invoke(this, localPart, event, eventReader);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							LOGGER.error("Error while invoking method: {}", method.getName(), e);
							return false;
						}
					}

				//Processing end elements
				} else if (event.isEndElement()) {
					final String localPart = event.asEndElement().getName().getLocalPart();
					final Method method = endElementMehods.get(localPart);
					if (method != null) {
						try {
							method.invoke(this);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							LOGGER.error("Error while invoking method: {}", method.getName(), e);
							return false;
						}
					}
				}

			}
		} catch (final XMLStreamException e) {
			LOGGER.error("Error while reading rss-file: {}", contentSource != null ? contentSource.getUrl() : "unknown",  e);
			return false;
		}

		stopWatch.stop();
		LOGGER.debug("Successfully finished parsing rss feed of content source {} in {}ms.", contentSource.getId(), stopWatch.getTime());

		return true;
	}

	@Override
	public T getParsedObject() { return parsingElement; }

	protected static class RssElement {
		public Object body;
		public final Map<String, Attribute> attributes = new HashMap<>();
	}

	/**
	 * Returns the Method with the given Arguments. <code>Null</code> is return if the method is not found.
	 */
	protected Method getDeclaredMethod(final String name, final Class<?>... parameterTypes) {
		Class<?> clazz = getClass();

		do {
			for (final Method m : clazz.getDeclaredMethods()) {
				if (m.getName().contains(name)) {
					if (Arrays.equals(m.getParameterTypes(), parameterTypes)) {
						m.setAccessible(true);
						return m;
					}
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null );

		return null;
	}
}
