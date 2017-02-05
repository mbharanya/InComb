package com.incomb.server.categories.indexing;

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
import com.incomb.server.model.Category;
import com.incomb.server.model.dao.CategoryDao;
import com.incomb.server.utils.LocaleUtil;

/**
 * This is the {@link IIndexTypeConf} for {@link Category}s.
 * It returns all fields which should be indexed and returns
 * all elements for a complete reindex.
 */
public class CategoryIndexType implements IIndexTypeConf {

	/**
	 * This class uses the singleton pattern.
	 * This constant holds the single instance of this class and
	 * can be accessed by calling {@link #getInstance()}.
	 */
	private static final CategoryIndexType INSTANCE = new CategoryIndexType();

	/**
	 * The name of this index type.
	 */
	public static final String INDEX_TYPE_NAME = "category";

	/**
	 * Contains the localized name of the {@link Category}.
	 */
	public static final String FIELD_NAME = "name";

	/**
	 * The constructor is only accessible for this class and subclasses.
	 */
	protected CategoryIndexType() {
	}

	/**
	 * This class uses the singleton pattern.
	 * This method returns the single instance of this class.
	 */
	public static CategoryIndexType getInstance() {
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
	 * Returns all {@link Category}s. For each {@link Category} and {@link Locale} a
	 * {@link CategoryIndexElement} is returned.
	 *
	 * {@link CategoryIndexElement#getOperation()} returns {@link EOperation#INSERT}.
	 */
	@Override
	public List<? extends IIndexElement> getElements(final Connection con, int start, int amount) {
		final List<Locale> locales = LocaleUtil.getAllLocales();

		// divide start because we want to return for each category and locale an element.
		start = start / locales.size();
		amount = amount / locales.size();

		final List<CategoryIndexElement> elements = new ArrayList<>();
		final List<Category> records = new CategoryDao(con).getCategories(start, amount);

		for (final Category record : records) {
			for(final Locale locale : locales) {
				elements.add(new CategoryIndexElement(record, locale, EOperation.INSERT));
			}
		}

		return elements;
	}

	@Override
	public void afterIndexing(final IIndexData indexData) {
		// do nothing at the moment
	}
}
