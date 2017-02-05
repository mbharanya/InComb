package com.incomb.server.categories.indexing;

import java.util.Locale;

import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.model.Category;

/**
 * Represents a {@link Category} which will be indexed.
 */
public class CategoryIndexElement implements IIndexElement {

	/**
	 * The {@link Category} to index.
	 */
	private final Category category;

	/**
	 * The {@link Locale} to get locale specific texts.
	 */
	private final Locale locale;

	/**
	 * The operation of the element.
	 */
	private final EOperation operation;

	/**
	 * Creates a new instance with all needed properties.
	 * @param category the {@link Category} to index.
	 * @param locale the {@link Locale} to get locale specific texts.
	 * @param operation the operation of the element
	 */
	public CategoryIndexElement(final Category category, final Locale locale, final EOperation operation) {
		this.category = category;
		this.locale = locale;
		this.operation = operation;
	}

	/**
	 * @return the operation of the element.
	 */
	@Override
	public EOperation getOperation() {
		return operation;
	}

	/**
	 * @return the {@link Locale} of the locale specific texts.
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @return {@link Category#getId()}
	 */
	@Override
	public String getId() {
		return String.valueOf(category.getId());
	}

	/**
	 * Returns the content of the field.
	 *
	 * Available fields:
	 * <ul>
	 * 	<li>{@link CategoryIndexType#FIELD_NAME}</li>
	 * </ul>
	 */
	@Override
	public Object getContent(final String fieldName) {
		switch (fieldName) {
			case CategoryIndexType.FIELD_NAME:
				return category.getName(getLocale());
			default:
				throw new IllegalArgumentException("Field name " + fieldName + " is unknown.");
		}
	}
}
