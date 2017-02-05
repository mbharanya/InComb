package com.incomb.server.indexing.conf.fields;

import java.util.Date;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;

/**
 * Wraps a {@link Date} into a {@link LongField}.
 * The value can be stored.
 * The value will be the timestamp in milliseconds.
 */
public class DateIndexFieldConf extends AStoreableIndexFieldConf<Date> {

	/**
	 * Creates a new instance.
	 * @param name the name of the field
	 * @param store true if the value should be stored
	 */
	public DateIndexFieldConf(final String name, final boolean store) {
		super(name, store);
	}

	/**
	 * {@inheritDoc}
	 * @see DateIndexFieldConf
	 */
	@Override
	public Field buildField(final Date content) {
		return new LongField(getName(), content.getTime(), isStored());
	}
}
