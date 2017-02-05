package com.incomb.server.providers.indexing;

import java.util.Locale;

import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.model.Provider;

/**
 * Represents a {@link Provider} which will be indexed.
 */
public class ProviderIndexElement implements IIndexElement {

	/**
	 * The {@link Provider} to index.
	 */
	private final Provider provider;

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
	 * @param provider the {@link Provider} to index.
	 * @param locale the {@link Locale} to get locale specific texts.
	 * @param operation the operation of the element
	 */
	public ProviderIndexElement(final Provider provider, final Locale locale, final EOperation operation) {
		this.provider = provider;
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
	 * @return {@link Provider#getId()}
	 */
	@Override
	public String getId() {
		return String.valueOf(provider.getId());
	}

	/**
	 * Returns the content of the field.
	 *
	 * Available fields:
	 * <ul>
	 * 	<li>{@link ProviderIndexType#FIELD_NAME}</li>
	 * </ul>
	 */
	@Override
	public Object getContent(final String fieldName) {
		switch (fieldName) {
			case ProviderIndexType.FIELD_NAME:
				return provider.getName();
			default:
				throw new IllegalArgumentException("Field name " + fieldName + " is unknown.");
		}
	}

}
