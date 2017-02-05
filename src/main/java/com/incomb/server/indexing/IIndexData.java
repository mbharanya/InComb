package com.incomb.server.indexing;

import java.util.List;

import com.incomb.server.indexing.conf.IIndexTypeConf;

/**
 * Contains {@link IIndexElement} which should be inserted, updated or deleted
 * in the Lucene index. All {@link IIndexElement} must be from the same {@link IIndexTypeConf}.
 */
public interface IIndexData {

	/**
	 * The {@link IIndexTypeConf} of the {@link IIndexElement} returned by {@link #getElements()}.
	 */
	IIndexTypeConf getConf();

	/**
	 * Returns {@link IIndexElement}s which should be modified in the index.
	 * {@link IIndexElement#getOperation()} returns the wanted operation.
	 */
	List<? extends IIndexElement> getElements();
}
