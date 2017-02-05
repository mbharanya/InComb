package com.incomb.server.providers.indexing;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.incomb.server.indexing.IIndexData;
import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.indexing.conf.IIndexTypeConf;
import com.incomb.server.indexing.conf.fields.IIndexFieldConf;
import com.incomb.server.indexing.conf.fields.TextIndexFieldConf;
import com.incomb.server.model.Provider;
import com.incomb.server.model.dao.ContentSourceDao;
import com.incomb.server.model.dao.ProviderDao;

/**
 * This is the {@link IIndexTypeConf} for {@link Provider}s.
 * It returns all fields which should be indexed and returns
 * all elements for a complete reindex.
 */
public class ProviderIndexType implements IIndexTypeConf {

	/**
	 * This class uses the singleton pattern.
	 * This constant holds the single instance of this class and
	 * can be accessed by calling {@link #getInstance()}.
	 */
	private static final ProviderIndexType INSTANCE = new ProviderIndexType();

	/**
	 * The name of this index type.
	 */
	public static final String INDEX_TYPE_NAME = "provider";

	/**
	 * Contains the name of the {@link Provider}.
	 */
	public static final String FIELD_NAME = "name";

	/**
	 * The constructor is only accessible for this class and subclasses.
	 */
	protected ProviderIndexType() {

	}

	/**
	 * This class uses the singleton pattern.
	 * This method returns the single instance of this class.
	 */
	public static ProviderIndexType getInstance() {
		return INSTANCE;
	}

	/**
	 * Returns the name.
	 */
	@Override
	public String getName() {
		return INDEX_TYPE_NAME;
	}

	/**
	 * Returns all configured fields of this type.
	 *
	 * Available fields:
	 * <ul>
	 * 	<li>{@link #FIELD_NAME}</li>
	 * </ul>
	 */
	@Override
	public List<IIndexFieldConf<?>> getFields() {
		final List<IIndexFieldConf<?>> fields = new ArrayList<>();
		fields.add(new TextIndexFieldConf(FIELD_NAME, true));
		return fields;
	}

	/**
	 * Returns all {@link Provider}s. For each language the {@link Provider} provides news
	 * a {@link ProviderIndexElement} is returned.
	 *
	 * {@link ProviderIndexElement#getOperation()} returns {@link EOperation#INSERT}.
	 */
	@Override
	public List<? extends IIndexElement> getElements(final Connection con, final int start, final int amount) {
		// TODO use start and amount

		final List<ProviderIndexElement> elements = new ArrayList<>();
		final List<Provider> records = new ProviderDao(con).getProviders(start, amount);

		for (final Provider record : records) {
			for(final Locale locale : new ContentSourceDao(con).getLocalesOfProvider(record.getId())) {
				elements.add(new ProviderIndexElement(record, locale, EOperation.INSERT));
			}
		}

		return elements;
	}

	@Override
	public void afterIndexing(final IIndexData indexData) {
		// do nothing at the moment
	}
}
