package com.incomb.server.indexing.conf.fields;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;

/**
 * Wraps a {@link Long} into a {@link LongField}.
 * The value can be stored.
 */
public class LongIndexFieldConf extends AStoreableIndexFieldConf<Long> {

	/**
	 * Creates a new instance.
	 * @param name the name of the field
	 * @param store true if the value should be stored
	 */
	public LongIndexFieldConf(final String name, final boolean store) {
		super(name, store);
	}

	/**
	 * {@inheritDoc}
	 * @see LongIndexFieldConf
	 */
	@Override
	public Field buildField(final Long content) {
		return new LongField(getName(), content, isStored());
	}
}
