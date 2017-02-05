package com.incomb.server.indexing.conf.fields;

import org.apache.lucene.document.Field;

import com.incomb.server.indexing.IIndexElement;

/**
 * An implementation of this wraps a value from {@link IIndexElement#getContent(String)}
 * into a lucene {@link Field}. Each implementation wraps a specific type of data in a
 * suitable {@link Field} implementation with a implementation configuration.
 * @param <T> the type of the content which it wraps.
 */
public interface IIndexFieldConf<T> {

	/**
	 * @return the identifying name of the field.
	 */
	String getName();

	/**
	 * Wraps the given content into a lucene {@link Field} with a suitable configuration.
	 * @param content the value to wrap.
	 * @return the lucene {@link Field}.
	 */
	Field buildField(T content);
}
