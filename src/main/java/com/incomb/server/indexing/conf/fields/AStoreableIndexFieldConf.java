package com.incomb.server.indexing.conf.fields;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;

/**
 * A default implementation of a lucene field which is can be stored or not.
 * @param <T> the type of the content which it wraps.
 */
public abstract class AStoreableIndexFieldConf<T> extends ADefaultIndexFieldConf<T> {

	/**
	 * true if the field should be stored so it will returned when the {@link Document}
	 * will be rebuild after indexing.
	 */
	private final boolean store;

	/**
	 * Creates a new instance with the name and if it's storeable.
	 * @param name the name of the field.
	 * @param store true if the field should be stored.
	 */
	public AStoreableIndexFieldConf(final String name, final boolean store) {
		super(name);
		this.store = store;
	}

	/**
	 * Returns true if the field is storeable.
	 */
	protected Store isStored() {
		return store ? Store.YES : Store.NO;
	}
}
