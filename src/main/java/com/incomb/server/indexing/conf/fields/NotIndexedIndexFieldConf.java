package com.incomb.server.indexing.conf.fields;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;

/**
 * Wraps a value into a {@link StoredField}.
 * The value will be stored but not analyzed. This conf can be used if you
 * want to have a value only stored to recreate the {@link Document} but you
 * cannot search for this value.
 */
public class NotIndexedIndexFieldConf extends ADefaultIndexFieldConf<Object> {

	/**
	 * Creates a new instance.
	 * @param name the name of the field.
	 */
	public NotIndexedIndexFieldConf(final String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 * @see NotIndexedIndexFieldConf
	 */
	@Override
	public Field buildField(final Object content) {
		return new StoredField(getName(), content.toString());
	}
}
