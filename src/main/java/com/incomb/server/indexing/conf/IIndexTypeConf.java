package com.incomb.server.indexing.conf;

import java.sql.Connection;
import java.util.List;

import com.incomb.server.indexing.IIndexData;
import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.conf.fields.IIndexFieldConf;

/**
 * This is a configuration of a type of elements which should
 * be indexed. This configuration describes mainly their fields.
 */
public interface IIndexTypeConf {

	/**
	 * @return an identifying string for this type.
	 */
	String getName();

	/**
	 * @return configurations for all fields (columns) which will be indexed.
	 */
	List<IIndexFieldConf<?>> getFields();

	/**
	 * This method is used for reindexing the whole data. It should
	 * return a subset of all elements.
	 *
	 * The {@link IIndexElement#getOperation()} must return {@link EOperation#INSERT}.
	 *
	 * @param con A transactional connection to the database to get the elements if needed.
	 * @param start first record index to return
	 * @param amount amount of records to return or less if there aren't enough records.
	 *
	 * @return the requested elements or an empty list if no exists
	 * 		in the requested range.
	 */
	List<? extends IIndexElement> getElements(final Connection con, final int start, final int amount);

	/**
	 * This method will be called after an {@link IIndexData} of this {@link IIndexTypeConf}
	 * was successfully indexed.
	 * @param indexData the indexed elements.
	 */
	void afterIndexing(IIndexData indexData);
}
