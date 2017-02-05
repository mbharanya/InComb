package com.incomb.server.indexing.conf.fields;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;

/**
 * Wraps a {@link Integer} into a {@link IntField}.
 * The value can be stored.
 */
public class IntegerIndexFieldConf extends AStoreableIndexFieldConf<Integer> {

	/**
	 * Creates a new instance.
	 * @param name the name of the field
	 * @param store true if the value should be stored
	 */
	public IntegerIndexFieldConf(final String name, final boolean store) {
		super(name, store);
	}

	/**
	 * {@inheritDoc}
	 * @see IntegerIndexFieldConf
	 */
	@Override
	public Field buildField(final Integer content) {
		return new IntField(getName(), content, isStored());
	}
}
