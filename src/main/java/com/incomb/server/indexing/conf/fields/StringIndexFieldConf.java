package com.incomb.server.indexing.conf.fields;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

/**
 * Wraps a {@link String} into a {@link StringField}.
 * The value can be stored but won't be tokenized. That means that only if the
 * user searches for the whole value the document will be found. This can be
 * used for example for article numbers.
 * @see TextIndexFieldConf
 */
public class StringIndexFieldConf extends AStoreableIndexFieldConf<String> {

	/**
	 * Creates a new instance.
	 * @param name the name of the field
	 * @param store true if the value should be stored
	 */
	public StringIndexFieldConf(final String name, final boolean store) {
		super(name, store);
	}

	/**
	 * {@inheritDoc}
	 * @see StringIndexFieldConf
	 */
	@Override
	public Field buildField(final String content) {
		return new StringField(getName(), content, isStored());
	}
}
