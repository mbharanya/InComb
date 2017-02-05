package com.incomb.server.indexing.conf;

import com.incomb.server.indexing.IIndexElement;

/**
 * Operation of {@link IIndexElement}.
 */
public enum EOperation {

	/**
	 * The {@link IIndexElement} will be added to the index and
	 * it doesn't exist a {@link IIndexElement} with the same {@link IIndexTypeConf} and id in it.
	 */
	INSERT,

	/**
	 * A {@link IIndexElement} with the same {@link IIndexTypeConf} and id exists already in the index.
	 * The old one will be deleted and the new one will be inserted.
	 * It has the same effect if you use {@link #DELETE} and #{@link #INSERT} afterwards.
	 */
	UPDATE,

	/**
	 * A {@link IIndexElement} with the same {@link IIndexTypeConf} and id exists already in the index.
	 * It will removed.
	 */
	DELETE;
}
