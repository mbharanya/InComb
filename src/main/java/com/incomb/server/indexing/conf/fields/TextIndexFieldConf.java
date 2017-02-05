package com.incomb.server.indexing.conf.fields;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

/**
 * Wraps a {@link String} into a {@link TextField}.
 * The value can be stored and will be tokenized. That means that only if the
 * user searches for a single part of value the document will be found.
 * @see StringIndexFieldConf
 */
public class TextIndexFieldConf extends AStoreableIndexFieldConf<String> {

	/**
	 * Creates a new instance.
	 * @param name the name of the field
	 * @param store true if the value should be stored
	 */
	public TextIndexFieldConf(final String name, final boolean store) {
		super(name, store);
	}

	/**
	 * {@inheritDoc}
	 * @see TextIndexFieldConf
	 */
	@Override
	public Field buildField(final String content) {
		return new TextField(getName(), content, isStored());
	}
}
