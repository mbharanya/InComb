package com.incomb.server.indexing;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.indexing.conf.IIndexTypeConf;
import com.incomb.server.utils.CloseUtil;

/**
 * This class manages all {@link Index} instances and the single {@link IndexingThread}.
 * It's the point to trigger a change in the index.
 */
public class IndexManager {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexManager.class);

	/**
	 * This class uses the singleton pattern.
	 * This constant holds the single instance of this class and
	 * can be accessed by calling {@link #getInstance()}.
	 */
	private static final IndexManager INSTANCE = new IndexManager();

	/**
	 * The amount of {@link IIndexElement} per chunk which is used when a whole
	 * {@link IIndexTypeConf} will be reindexed.
	 * @see #reIndex(IIndexTypeConf)
	 */
	private static final int CHUNK_SIZE = 200;

	/**
	 * The currently used Lucene version.
	 */
	static final Version LUCENE_VERSION = Version.LATEST;

	/**
	 * The name of the single {@link Index}.
	 */
	static final String INDEX_NAME = "main";

	/**
	 * The single {@link Index}.
	 */
	private final Index index;

	/**
	 * The single {@link IndexingThread}.
	 */
	final IndexingThread indexingThread;

	/**
	 * Creates the single {@link #index} and {@link #indexingThread} and starts it.
	 * The constructor is only accessible for this class and subclasses.
	 */
	protected IndexManager() {
		index = new Index(INDEX_NAME);

		indexingThread = new IndexingThread(index);
		indexingThread.start();
	}

	/**
	 * This class uses the singleton pattern.
	 * This method returns the single instance of this class.
	 */
	public static IndexManager getInstance() {
		return INSTANCE;
	}

	/**
	 * @return the single {@link Index}.
	 */
	Index getIndex() {
		return index;
	}

	/**
	 * Triggers a change in the index.
	 * The {@link IIndexData} will be added to the queue.
	 * @param data the {@link IIndexData} with the change.
	 */
	public void index(final IIndexData data) {
		indexingThread.add(data);
	}

	/**
	 * Removes the old and builds the whole index for the given {@link IIndexTypeConf} again.
	 * It takes the {@link IIndexElement} from {@link IIndexTypeConf#getElements(Connection, int, int)}
	 * with chunks in the size of {@value #CHUNK_SIZE}.
	 * This runs asynchronously.
	 * @param typeConf the {@link IIndexTypeConf} to reindex.
	 */
	public void reIndex(final IIndexTypeConf typeConf) {
		new Thread() {
			@Override
			public void run() {
				final StopWatch stopWatch = new StopWatch();
				stopWatch.start();

				final Connection con = DBConnectionProvider.getInstance().acquire();
				try {
					LOGGER.info("Started indexing of index type {}.", typeConf.getName());

					try {
						final TermQuery deleteQuery = new TermQuery(new Term(
								IIndexElement.FIELD_INDEX_TYPE, typeConf.getName()));
						index.deleteDocuments(deleteQuery);
					} catch (final IOException e1) {
						LOGGER.error("Can't delete old index of type {}.", typeConf.getName(), e1);
						return;
					}

					int start = 0;
					while(!isInterrupted()) {
						final List<? extends IIndexElement> elements = typeConf.getElements(con, start, CHUNK_SIZE);
						start += CHUNK_SIZE;

						if(elements.isEmpty()) {
							stopWatch.stop();
							LOGGER.info("Finished reindexing of index type {} in {}ms.",
									typeConf.getName(), stopWatch.getTime());
							break; // no more elements.
						}

						final SimpleIndexData indexData = new SimpleIndexData(typeConf, elements);
						index(indexData);

						try {
							Thread.sleep(5000); // wait some time, because we won't blew up the memory.
						} catch (final InterruptedException e) {
							break; // exit.
						}
					}
				}
				finally {
					DBConnectionProvider.getInstance().release(con);
				}
			}
		}.start();
	}

	/**
	 * Stops the {@link #indexingThread} and closes the {@link #index}.
	 * Must be called when the application is shutting down.
	 */
	public void shutdown() {
		indexingThread.interrupt();
		CloseUtil.close(index);
	}
}
