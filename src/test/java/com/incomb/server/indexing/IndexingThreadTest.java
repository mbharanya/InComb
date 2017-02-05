package com.incomb.server.indexing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.ATest;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.indexing.conf.IIndexTypeConf;
import com.incomb.server.indexing.conf.fields.IIndexFieldConf;

public class IndexingThreadTest extends ATest {

	private static final String INDEX_TYPE = "testType";

	private static final String FIELDNAME = "testField";

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexingThreadTest.class);

	@AfterClass
	public static void cleanUpDir() throws IOException {
		IndexManager.getInstance().getIndex().close();
		FileUtils.deleteDirectory(new File(Index.INDEX_LOCATION));
	}

	@After
	public void cleanUp() throws IOException {
		IndexManager.getInstance().getIndex().deleteAllDocuments();
		IndexManager.getInstance().getIndex().commit();
	}

	@Test
	public void testInsert() throws InterruptedException, IOException {
		doAndTestIndexChange("1", "testContent", EOperation.INSERT);
	}

	@Test
	public void testUpdate() throws InterruptedException, IOException {
		doAndTestIndexChange("1", "testContent", EOperation.INSERT); // write something in the index
		doAndTestIndexChange("1", "newContent", EOperation.UPDATE); // update index
		checkIndexContent("1", "testContent", 0); // check if old content isn't anymore available
	}

	@Test
	public void testDelete() throws InterruptedException, IOException {
		doAndTestIndexChange("1", "testContent", EOperation.INSERT); // write something in the index
		doAndTestIndexChange("1", "testContent", EOperation.DELETE); // remove document
	}

	private void doAndTestIndexChange(final String elementId, final String fieldContent,
			final EOperation operation) throws InterruptedException, IOException {
		final IIndexData data = new IIndexData() {

			@Override
			public List<IIndexElement> getElements() {
				final IIndexElement element = new IIndexElement() {

					@Override
					public EOperation getOperation() {
						return operation;
					}

					@Override
					public Locale getLocale() {
						return Locale.GERMAN;
					}

					@Override
					public String getId() {
						return elementId;
					}

					@Override
					public Object getContent(final String fieldName) {
						switch (fieldName) {
							case FIELDNAME:
								return fieldContent;

							default:
								return null;
						}
					}
				};

				return Arrays.asList(element);
			}

			@Override
			public IIndexTypeConf getConf() {
				return new IIndexTypeConf() {

					@Override
					public String getName() {
						return INDEX_TYPE;
					}

					@Override
					public List<IIndexFieldConf<?>> getFields() {
						final IIndexFieldConf<String> field = new IIndexFieldConf<String>() {

							@Override
							public String getName() {
								return FIELDNAME;
							}

							@Override
							public Field buildField(final String content) {
								return new StringField(getName(), content, Store.YES);
							}
						};

						final List<IIndexFieldConf<?>> returnValue = new ArrayList<>();
						returnValue.add(field);
						return returnValue;
					}

					@Override
					public List<? extends IIndexElement> getElements(final Connection con, final int start, final int amount) {
						return new ArrayList<>(); // not used for this test
					}

					@Override
					public void afterIndexing(final IIndexData indexData) {
						// TODO Auto-generated method stub

					}
				};
			}
		};

		IndexManager.getInstance().index(data);
		Thread.sleep(250); // wait until new data is indexed.
		LOGGER.info("Start searching. Index writing should be done now.");

		checkIndexContent(elementId, fieldContent, operation == EOperation.DELETE ? 0 : 1);
	}

	private void checkIndexContent(final String elementId,
			final String fieldContent, final int expectedAmount) throws IOException {
		final IndexReader reader = IndexManager.getInstance().getIndex().getIndexReader();
		final IndexSearcher searcher = new IndexSearcher(reader);
		final TopDocs topDocs = searcher.search(new TermQuery(new Term(FIELDNAME, fieldContent)), expectedAmount + 10);

		assertNotNull(topDocs);
		assertTrue(topDocs.totalHits == expectedAmount);

		if(expectedAmount > 0) {
			final ScoreDoc scoreDoc = topDocs.scoreDocs[0];
			assertNotNull(scoreDoc);

			final Document doc = reader.document(scoreDoc.doc);
			assertNotNull(doc);
			assertEquals(fieldContent, doc.get(FIELDNAME));
			assertEquals(elementId, doc.get(IIndexElement.FIELD_ID));
			assertEquals(INDEX_TYPE, doc.get(IIndexElement.FIELD_INDEX_TYPE));
		}
	}
}
