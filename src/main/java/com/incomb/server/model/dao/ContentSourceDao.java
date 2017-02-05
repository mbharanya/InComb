package com.incomb.server.model.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.ContentSource;
import com.incomb.server.model.Provider;
import com.incomb.server.model.dao.internal.InternalContentSourceDao;
import com.incomb.server.model.tables.ContentSourceTable;
import com.incomb.server.utils.LocaleUtil;

/**
 * With this DAO you can query the {@link ContentSource}s.
 */
public class ContentSourceDao extends ADao implements IFinder<ContentSource> {

	/**
	 * The table to build SQL queries.
	 */
	private static final ContentSourceTable TABLE = new ContentSourceTable();

	/**
	 * Jooq's DAO with helpful methods.
	 */
	private final InternalContentSourceDao dao;

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * Every query will be sent over this {@link Connection}.
	 * If <code>null</code> was given then for each query a new
	 * {@link Connection} will be acquired from the {@link DBConnectionProvider}.
	 * @param connection the {@link Connection} to use or <code>null</code>.
	 */
	public ContentSourceDao(final Connection connection) {
		super(connection);
		dao = new InternalContentSourceDao(jooqConfig);
	}

	/**
	 * @return ContentSource.class
	 */
	@Override
	public Class<ContentSource> findForClass() { return ContentSource.class; }

	/**
	 * @see #getAllContentSources()
	 */
	@Override
	public List<ContentSource> findAll() {
		return getAllContentSources();
	}

	/**
	 * Returns all existing {@link ContentSource}s.
	 * The result isn't sorted.
	 * @return {@link List} containing the found {@link ContentSource}.
	 */
	public List<ContentSource> getAllContentSources() {
		return dao.findAll();
	}

	/**
	 * Finds the {@link ContentSource} with the given content source id.
	 * @param id the id to find the {@link ContentSource}.
	 * @return {@link ContentSource} or <code>null</code> if not found.
	 */
	public ContentSource findContentSourceById(final int id) {
		return dao.findById(id);
	}

	/**
	 * Fetches all {@link Locale}s for which {@link ContentSource}s
	 * of the given {@link Provider} are available.
	 *
	 * @param providerId
	 * @return a List (never null) with the {@link Locale}s.
	 */
	public List<Locale> getLocalesOfProvider(final int providerId) {
		final Result<Record1<String>> result = DSL.using(jooqConfig).
				select(TABLE.LOCALE).
				from(TABLE).
				where(TABLE.PROVIDER_ID.eq(providerId)).
				groupBy(TABLE.LOCALE).
				fetch();

		final List<Locale> locales = new ArrayList<>();
		for (final String localeStr : result.getValues(TABLE.LOCALE)) {
			locales.add(LocaleUtil.toLocale(localeStr));
		}

		return locales;
	}

}
