package com.incomb.server.indexing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.jooq.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.indexing.conf.IIndexTypeConf;
import com.incomb.server.indexing.conf.fields.IIndexFieldConf;

/**
 * This {@link Thread} changes the Lucene index.
 * Only one instance of this class has to be running at the same time.
 */
class IndexingThread extends Thread {

	/**
	 * Statistics for one executed {@link IIndexData}.
	 */
	class IndexingStats {

		/**
		 * Measures the time for the execution.
		 */
		private final StopWatch stopWatch = new StopWatch();

		/**
		 * Amount of added {@link Document}s.
		 */
		private int addedDocs = 0;

		/**
		 * Amount of updated {@link Document}s.
		 */
		private int updatedDocs = 0;

		/**
		 * Amount of deleted {@link Document}s.
		 */
		private int deletedDocs = 0;

		/**
		 * Creates a new instance and starts the {@link StopWatch}.
		 */
		private IndexingStats() {
			stopWatch.start();
		}

		/**
		 * Returns the statistics of the changes.
		 */
		@Override
		public String toString() {
			return String.format("Added %d, updated %d and deleted %d docs in %d ms.",
					addedDocs, updatedDocs, deletedDocs, stopWatch.getTime());
		}
	}

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexingThread.class);

	/**
	 * The {@link Queue} of {@link IIndexData} which should be still changed in the index.
	 */
	private final Queue<IIndexData> queue = new ConcurrentLinkedQueue<>();

	/**
	 * The {@link Index} where the changes should be made.
	 */
	private final Index index;

	/**
	 * The {@link IIndexData} which is currently executing.
	 */
	private IIndexData indexData;

	/**
	 * The statistics for the current {@link IIndexData}.
	 */
	private IndexingStats stats = new IndexingStats();

	/**
	 * Creates a new instance and sets the name to "IndexingThread".
	 * @param index the {@link Index} where the changes should be made.
	 */
	public IndexingThread(final Index index) {
		setName("IndexingThread");
		this.index = index;
	}

	/**
	 * Runs until the {@link Thread} will be interrupted.
	 * Executes every {@link IIndexData} in the {@link #queue} until it is empty.
	 * If the {@link #queue} is empty than the {@link Thread} waits until {@link #add(IIndexData)} will be called.
	 */
	@Override
	public void run() {
		while(!isInterrupted()) {
			if(queue.isEmpty()) {
				try {
					synchronized (this) {
						wait(); // wait for new IIndexData
					}
				} catch (final InterruptedException e) {
					// exit
					break;
				}
			}

			final IIndexData data = queue.poll();
			if(data != null) {
				indexData = data;
				stats = new IndexingStats();
				index();
			}
		}
	}

	/**
	 * Adds the {@link IIndexData} to the {@link #queue}.
	 * @param data the {@link IIndexData} to execute.
	 */
	void add(final IIndexData data) {
		queue.add(data);

		if(queue.size() > 10) {
			LOGGER.warn("Indexing queue contains currently {} index datas.", queue.size());
		}

		synchronized (this) {
			// notify Thread if it's waiting
			if(getState() == State.WAITING) {
				notify();
			}
		}
	}

	/**
	 * Executes the current {@link IIndexData} which is set to {@link #indexData}.
	 * Afterwards it commits or rollbacks the {@link Index}.
	 */
	private void index() {
		final IIndexTypeConf conf = indexData.getConf();

		LOGGER.debug("Start indexing {} elements of {}.", indexData.getElements().size(), conf.getName());

		try {
			int i = 0;
			for (final IIndexElement element : indexData.getElements()) {
				switch (element.getOperation()) {
					case INSERT:
						add(element);
						stats.addedDocs++;
						break;
					case UPDATE:
						update(element);
						stats.updatedDocs++;
						break;
					case DELETE:
						delete(element);
						stats.deletedDocs++;
						break;
				}

				if(i++ % 50 == 0 && indexData.getElements().size() > 50) {
					LOGGER.info("{} of {} elements indexed.", i + 1, indexData.getElements().size());
				}
			}

			try {
				index.commit();
				conf.afterIndexing(indexData);
				// LOGGER.debug("Finished indexing of {}: {}", conf.getName(), stats.toString());
			} catch (final IOException e) {
				index.rollback();
				LOGGER.error("Can't commit index of {}.", conf.getName(), e);
			}
		} catch (final Throwable e) {
			try {
				index.rollback();
			} catch (final IOException e1) {
				LOGGER.error("Can't rollback index because of an exception.", e);
			}

			LOGGER.error("Can't change index because of an exception.", e);
		}
	}

	/**
	 * Adds the given {@link IIndexElement} to the index.
	 * It builds the {@link Document} with its {@link Field}s and writes it to {@link Index}.
	 * @param element the {@link IIndexElement} to add.
	 * @throws IOException if an error occurred in the index.
	 */
	@SuppressWarnings("unchecked")
	private void add(final IIndexElement element) throws IOException {
		final IIndexTypeConf conf = indexData.getConf();

		final Document doc = new Document();

		// add id field
		final StringField idField = new StringField(IIndexElement.FIELD_ID, element.getId(), Store.YES);
		doc.add(idField);

		// add index type field
		final StringField indexTypeField = new StringField(IIndexElement.FIELD_INDEX_TYPE, conf.getName(), Store.YES);
		doc.add(indexTypeField);

		final Locale locale = element.getLocale();
		if(element.getLocale() != null) {
			doc.add(new StringField(IIndexElement.FIELD_LOCALE, locale.getLanguage(), Store.YES));
		}

		for (@SuppressWarnings("rawtypes") final IIndexFieldConf fieldConf : conf.getFields()) {
			Object fieldContent = element.getContent(fieldConf.getName());

			if(fieldContent != null) {
				if(!(fieldContent instanceof Collection<?>)) {
					fieldContent = Arrays.asList(fieldContent);
				}

				for (final Object content : (Collection<?>) fieldContent) {
					doc.add(fieldConf.buildField(content));
				}
			}
		}

		index.addDocument(doc, locale);
	}

	/**
	 * Updates the given {@link IIndexElement} in the index by deleting the old {@link Document} first
	 * and adding the new one.
	 * @param element the {@link IIndexElement} to update.
	 * @throws IOException if an error occurred in the index.
	 */
	private void update(final IIndexElement element) throws IOException {
		delete(element);
		add(element);
	}

	/**
	 * Deletes the given {@link IIndexElement} from the index.
	 * @param element the {@link IIndexElement} to remove.
	 * @throws IOException if an error occurred in the index.
	 */
	private void delete(final IIndexElement element) throws IOException {
		final IIndexTypeConf conf = indexData.getConf();

		// build query for deletion
		final BooleanQuery query = new BooleanQuery();
		query.add(new TermQuery(new Term(IIndexElement.FIELD_ID, element.getId())), Occur.MUST);
		query.add(new TermQuery(new Term(IIndexElement.FIELD_INDEX_TYPE, conf.getName())), Occur.MUST);

		index.deleteDocuments(query);
	}
}
