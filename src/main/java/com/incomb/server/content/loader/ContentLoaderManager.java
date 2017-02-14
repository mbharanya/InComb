package com.incomb.server.content.loader;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.content.EContentSourceDaoType;
import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.Content;
import com.incomb.server.model.ContentSource;
import com.incomb.server.model.FetchHistory;
import com.incomb.server.model.News;
import com.incomb.server.model.dao.FetchHistoryDao;
import com.incomb.server.model.dao.IFinder;
import com.incomb.server.model.dao.NewsDao;
import com.incomb.server.utils.CloseUtil;

/**
 * This class is used for content loading. It runs as a single thread with a own thread pool.<br>
 * It loads content and save it into the Database.
 */
public class ContentLoaderManager extends Thread {

	/**
	 * This is the single instance of the {@link ContentLoaderManager}.
	 */
	private static ContentLoaderManager instance;

	/**
	 * The {@link ContentReaderFactory} used for creating an {@link IContentReader}.
	 */
	private final static ContentReaderFactory CONTENT_READER_FACTORY = ContentReaderFactory.getInstance();

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentLoaderManager.class);

	/**
	 * Minimal count of threads running in the thread pool.
	 */
	private static final int THREAD_COUNT_MIN = 2;

	/**
	 * Maximal count of threads running in the thread pool.
	 */
	private static final int THREAD_COUNT_MAX = 5;

	/**
	 * Keep alive time of an thread in seconds.
	 */
	private static final int THREAD_KEEP_ALIVE = 3;

	/**
	 * A {@link TreeSet} with every {@link ContentSource} which should be loaded, parsed and saved.
	 */
	protected static final SortedSet<ContentSourceExecutionInfo> CONTENT_SOURCES = new TreeSet<ContentSourceExecutionInfo>();

	/**
	 * Queue for the {@link ContentSavingThread}. Every Object which is ready to save into the database goes here.
	 */
	private static final List<Content> SAVE_QUEUE = new ArrayList<Content>();

	/**
	 * In this queue goes every object which is ready getting loaded by a {@link ContentLoader} thread.
	 */
	private static final BlockingQueue<Runnable> LOAD_QUEUE = new LinkedBlockingQueue<Runnable>();

	/**
	 * This {@link ThreadPoolExecutor} executes our ContentLoader. It will use the <code>LOAD_QUEUE</code> for starting new tasks.
	 */
	private static final ThreadPoolExecutor EXECUTOR   = new ThreadPoolExecutor(THREAD_COUNT_MIN, THREAD_COUNT_MAX, THREAD_KEEP_ALIVE, TimeUnit.SECONDS, LOAD_QUEUE);

    /**
     * Thread which is saving all content-objects into the database.
     */
	private final ContentSavingThread SAVING_THREAD = new ContentSavingThread(this);

	/**
	 * Used for shutting down this and all its threads.
	 */
	private boolean shuttingDown = false;

	/**
	 * Creates a new {@link ContentLoaderManager}.
	 */
	protected ContentLoaderManager() {
		setName("ContentLoaderManager");
	}

	/**
	 * Retunes the single instance of the {@link ContentLoaderManager}.
	 * @return {@link ContentLoaderManager}
	 */
	public static synchronized ContentLoaderManager getInstance() {
		if (instance == null) {
			instance = new ContentLoaderManager();
		}
		return instance;
	}
	/**
	 * Returns the default {@link ContentReaderFactory}.
	 * @return {@link ContentReaderFactory}
	 */
	public ContentReaderFactory getContentFactory() {
		return CONTENT_READER_FACTORY;
	}

	/**
	 * This method loads all {@link ContentSource}s from the database into the
	 * <code>CONTENT_SOURCES</code> {@link ArrayList}. The {@link ArrayList}
	 * will be sorted according the fetch time of each {@link ContentSource}
	 * object.
	 */
	public void loadContentSources() {
		final Connection connection = DBConnectionProvider.getInstance().acquire();

		try {
			final EContentSourceDaoType[] availableTypes = EContentSourceDaoType.values();
			for (final EContentSourceDaoType type : availableTypes) {
				final IFinder<? extends ContentSource> c = EContentSourceDaoType.getDaoAsInstance(connection, type);

				for (final ContentSource o : c.findAll()) {
					CONTENT_SOURCES.add(new ContentSourceExecutionInfo(o, o.getLastFetch(connection)));
				}
			}
		} finally {
			DBConnectionProvider.getInstance().release(connection);
		}
	}

	/**
	 * Adds a new ContentSource to the queue.
	 * @param source {@link ContentSource}
	 * @return <code>true</code> if this set did not already contain the specified element
	 */
	public boolean addContentSource(final ContentSource source) {
		return CONTENT_SOURCES.add(new ContentSourceExecutionInfo(source, source.getLastFetch(null)));
	}

	/**
	 * Removes the given contentSource from the queue.
	 * @param contentSource {@link ContentSource}
	 * @return <code>true</code> if this set contained the specified element
	 */
	public boolean removeContentSource(final ContentSource contentSource){
		for (final ContentSourceExecutionInfo info : CONTENT_SOURCES) {
			if (info.getContentSource() == contentSource) {
				return CONTENT_SOURCES.remove(info);
			}
		}
		return false;
	}

	/**
	 * This methods starts the content saving thread and also pre starts the core threads of the internal {@link ThreadPoolExecutor}.
	 * If this thread isn't interrupted or the {@link ContentLoaderManager} isnin't shutting down, the
	 * thread will wait till a {@link ContentSource} can be added to the <code>LOAD_QUEUE</code>. It the
	 * item gets added, it gets removed from the <code>CONTENT_SOURCES</code> list.
	 */
	@Override
	public void run() {
		super.run();

		shuttingDown = false;
		SAVING_THREAD.start();
		EXECUTOR.prestartCoreThread();

		while (!isInterrupted() && !shuttingDown) {

			if (!CONTENT_SOURCES.isEmpty()) {
				final ContentSourceExecutionInfo loadObject = CONTENT_SOURCES.first();

				final long waitTime = loadObject.getExecuteAt()-System.currentTimeMillis();

				if (waitTime > 0) {
					try {
						sleep(waitTime); /*If thread a other content-source cold be added with a smaller waiting time*/
					} catch (final InterruptedException e) { /* no one cares */ }
				}

				CONTENT_SOURCES.remove(loadObject);
				LOAD_QUEUE.add(new ContentLoader(this, loadObject.getContentSource(), loadObject.lastFetch));
			}
		}
	}

	/**
	 * This method shuts down the thread and the {@link ThreadPoolExecutor}.
	 */
	public void shutdown() {
		EXECUTOR.shutdown();
		SAVING_THREAD.interrupt();
		interrupt();
		shuttingDown = true;
	}

	/**
	 * This method shuts down the {@link ContentLoaderManager}, set it's instance to null and generate a new one.
	 * @return Newly created instance of the {@link ContentLoaderManager}
	 */
	public ContentLoaderManager reset() {
		shutdown();
		synchronized(this){
			instance = null;
		}
		return getInstance();
	}

	/**
	 * Returns <code>true</code> if the server is shutting down. <code>false</code> if not.
	 * @return boolean if the {@link ContentLoaderManager} is shutting down.
	 */
	public boolean isShuttingDown() { return shuttingDown; }

	/**
	 * Adds the <code>readContent</code> to the <code>SAVE_QUEUE</code> and creates and saves a new {@link FetchHistory}.
	 *
	 * @param contentLoader {@link ContentLoader} which read the content.
	 * @param readContent Content read.
	 */
	protected void joinThread(final ContentLoader contentLoader, final Content... readContent) {
		synchronized (SAVE_QUEUE) {
			SAVE_QUEUE.addAll(Arrays.asList(readContent));
		}

		synchronized (SAVING_THREAD) {
			if(SAVING_THREAD.getState().equals(State.WAITING)) {
				SAVING_THREAD.notify();
			}
		}

		LOGGER.debug("Read {} contents from content source {}.", readContent.length, contentLoader.getContentSource().getId());

		final Connection con = DBConnectionProvider.getInstance().acquire();

		FetchHistory history = null;
		try {
			history = new FetchHistory(contentLoader.getContentSource().getId(), new Timestamp(System.currentTimeMillis()), readContent.length > 0 ? true : false);
			new FetchHistoryDao(con).addFetchHistory(history);

			// fetch ContentSource again from the database because config may has chaged.
			//final ContentSourceDao dao = new ContentSourceDao(con);
			//addContentSource(dao.findContentSourceById(contentLoader.getContentSource().getId()));
			addContentSource(contentLoader.getContentSource());
			con.commit();

		} catch (final SQLException e) {
			LOGGER.error("SQLException occurred while saving FetchHistory and fetching ContentSource {}",
					history != null ? history.toString() : "history is null", e);

		} finally {
			DBConnectionProvider.getInstance().release(con);
		}
	}

	/**
	 * Thread for saving Content-Sources into the database.
	 */
	private static class ContentSavingThread extends Thread {
		private final List<Content> list = new ArrayList<Content>();
		ContentLoaderManager manager;

		/**
		 * Creates a new {@link ContentSavingThread} and sets its name to <b>ContentSavingThread</b>.
		 * @param manager
		 */
		private ContentSavingThread(final ContentLoaderManager manager) {
			setName("ContentSavingThread");
			this.manager = manager;
		}

		/**
		 * Saves all {@link Content} objects into the database while the server isn't shutting down.
		 * An object which will be saved gets removed from the <code>SAVE_QUEUE</code> list.
		 */
		@Override
		public void run() {
			super.run();

			while (!manager.isShuttingDown()) {
				try {
					boolean wait = false;
					synchronized (SAVE_QUEUE) {
						list.addAll(SAVE_QUEUE);
						SAVE_QUEUE.clear();

						wait = list.isEmpty();
					}

					if(list.size() > 50) {
						LOGGER.warn("Now starting to save {} contents.", list.size());
					}

					if(wait) {
						synchronized (this) {
							try {
								wait();
								continue; // take new news.
							} catch (final InterruptedException e) {
								break; // exit
							}
						}
					}

					final Connection con = DBConnectionProvider.getInstance().acquire();
					final NewsDao dao = new NewsDao(con);

					for (final Content e : list) {
						try {
							dao.saveNews((News) e);
							con.commit();
						} catch (final SQLException e1) {
							LOGGER.error("Failed commiting news obj: {}", e, e1);
						}
					}

					list.clear();
					CloseUtil.close(con);
				}
				catch(final Throwable t) {
					LOGGER.error("An error occurred during saving contents.", t);
				}
			}
		}
	}


	/**
	 * This tiny class is used for saving content-source and it's last fetch-history.
	 *
	 */
	protected static class ContentSourceExecutionInfo implements Comparable<ContentSourceExecutionInfo> {

		private final ContentSource contentSource;
		private final FetchHistory  lastFetch;
		private final long          executeAt;

		/**
		 * Creates a new {@link ContentSourceExecutionInfo}
		 * @param contentSource {@link ContentSource}
		 * @param lastFetch {@link FetchHistory} - Latest fetch from the ContentSource.
		 */
		public ContentSourceExecutionInfo(final ContentSource contentSource, final FetchHistory lastFetch) {
			this.contentSource = contentSource;
			this.lastFetch = lastFetch;

			if (lastFetch != null) {
				executeAt = lastFetch.getFetchTime().getTime() + contentSource.getInterval() * 1000;

			} else {
				executeAt = System.currentTimeMillis();
			}
		}

		public ContentSource getContentSource() { return contentSource; }

		/**
		 * Returns the remaining time till the {@link ContentSource} should be loaded.
		 * @return Long remaining time.
		 */
		public long getExecuteAt() { return executeAt; }

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final ContentSourceExecutionInfo other) {
			if (lastFetch == null && other.lastFetch != null) {
				return -1;
			}
			if (other.lastFetch == null && lastFetch != null) {
				return  1;
			}

			if (getExecuteAt() - other.getExecuteAt() < 0) {
				return -1;
			}
			if (getExecuteAt() - other.getExecuteAt() == 0) {
				if (getContentSource().getInterval() - other.getContentSource().getInterval() <= 0) {
					return 0;
				}
			}
			return 1;
		}
	}

}
