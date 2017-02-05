package com.incomb.server.content.parsing.rss;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.content.parsing.ContentParser;
import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.ContentSource;
import com.incomb.server.model.Provider;
import com.incomb.server.model.dao.ProviderDao;

/**
 * {@link RssContentParserFactory} is a Singleton for creating the provider specific {@link DefaultRssParser}
 * for {@link ContentSource}s.
 *
 */
public class RssContentParserFactory {

	/**
	* <p>The {@link Logger} for this class.</p>
	*/
	private static final Logger LOGGER = LoggerFactory.getLogger(RssContentParserFactory.class);

	/**
	 * Singleton
	 */
	private static RssContentParserFactory instance;

	/**
	 * No instances for you - woop woop!
	 */
	private RssContentParserFactory() { }

	/**
	 * Returns the specified parser saved on the {@link Provider} object from the {@link ContentSource}.
	 * If no parser is specified the {@link DefaultRssParser} is returned.
	 */
	@SuppressWarnings("unchecked")
	public ContentParser<RssDocument> get(final ContentSource contentSource, final InputStream stream) {
		final Connection con = DBConnectionProvider.getInstance().acquire();
		Provider provider = null;

		try {
			final ProviderDao providerDao = new ProviderDao(con);
			provider = providerDao.getProvider(contentSource.getProviderId());

		} finally {
			DBConnectionProvider.getInstance().release(con);
		}

		if (provider.getParserClass() ==  null) {
			return new DefaultRssParser<RssDocument>(contentSource, stream);
		}

		ContentParser<RssDocument> obj = null;
		try {
			final Class<ContentParser<RssDocument>> clazz = (Class<ContentParser<RssDocument>>) Class.forName(provider.getParserClass());
			final Constructor<ContentParser<RssDocument>> constructor = clazz.getConstructor(ContentSource.class, InputStream.class);
			obj = constructor.newInstance(contentSource, stream);

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			LOGGER.error("Cannot instantiate parser for provider: {} with id {}", provider.getName(), provider.getId(), e);
		}
		return obj;
	}

	/**
	 * Creates a new {@link RssContentParserFactory} instance if
	 * no object has already been created.
	 *
	 * @return the single {@link RssContentParserFactory}
	 */
	public static synchronized RssContentParserFactory getInstance() {
		if (instance == null) {
			instance = new RssContentParserFactory();
		}
		return instance;
	}
}
