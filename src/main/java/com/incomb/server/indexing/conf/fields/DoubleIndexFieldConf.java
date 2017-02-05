package com.incomb.server.indexing.conf.fields;

import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;

/**
 * Wraps a {@link Double} into a {@link DoubleField}.
 * The value can be stored.
 */
public class DoubleIndexFieldConf extends AStoreableIndexFieldConf<Double> {

	/**
	 * Creates a new instance.
	 * @param name the name of the field
	 * @param store true if the value should be stored
	 */
	public DoubleIndexFieldConf(final String name, final boolean store) {
		super(name, store);
	}

	/**
	 * {@inheritDoc}
	 * @see DoubleIndexFieldConf
	 */
	@Override
	public Field buildField(final Double content) {
		return new DoubleField(getName(), content, isStored());
	}
}
