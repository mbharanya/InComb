package com.incomb.server.indexing;

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;

import com.incomb.server.indexing.conf.IIndexTypeConf;

/**
 * This class contains helper methods for test purposes.
 */
public class IndexTestUtil {

	public static void deleteAllDocuments(final IIndexTypeConf typeConf) throws IOException {
		final TermQuery deleteQuery = new TermQuery(new Term(
				IIndexElement.FIELD_INDEX_TYPE, typeConf.getName()));
		IndexManager.getInstance().getIndex().deleteDocuments(deleteQuery);
	}
}
