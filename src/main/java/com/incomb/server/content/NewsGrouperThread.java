package com.incomb.server.content;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.News;
import com.incomb.server.model.dao.NewsDao;

/**
 * Searches for similar {@link News} for another {@link News}
 * and sets to them the same news group id.
 *
 * <p>If an error occurred during grouping of a {@link News} we will retry it after {@value #RETRY_TIME}ms.</p>
 */
public class NewsGrouperThread extends Thread {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsGrouperThread.class);

	/**
	 * This class uses the singleton pattern.
	 * This constant holds the single instance of this class and
	 * can be accessed by calling {@link #getInstance()}.
	 */
	private static final NewsGrouperThread INSTANCE = new NewsGrouperThread();

	/**
	 * Time in milliseconds to wait until a failed {@link News} should be added again to the {@link #QUEUE}.
	 */
	private static final long RETRY_TIME = 60*1000;

	/**
	 * Contains all {@link Thread} which are sleeping to add a {@link News} to the {@link #QUEUE} again.
	 * @see #waitAddToQueue(News)
	 */
	private static final List<Thread> WAIT_ADD_THREADS = new ArrayList<>();

	/**
	 * All {@link News} which will be grouped.
	 * First in, first grouped.
	 */
	private static final Queue<News> QUEUE = new ConcurrentLinkedDeque<>();

	/**
	 * This class uses the singleton pattern.
	 * This method returns the single instance of this class.
	 */
	public static NewsGrouperThread getInstance() {
		return INSTANCE;
	}

	/**
	 * Sets the name of the Thread.
	 * The constructor is only accessible for this class and subclasses.
	 */
	protected NewsGrouperThread() {
		setName("NewsGrouperThread");
		setPriority(MIN_PRIORITY);
	}

	/**
	 * Adds a {@link News} to the {@link #QUEUE} so it will grouped with other {@link News}.
	 * @param news the {@link News} to group.
	 */
	public void addNews(final News news) {
		QUEUE.add(news);

		synchronized (this) {
			if(getState() == State.WAITING) {
				notify();
			}
		}
	}

	/**
	 * Waits until a {@link News} is in the {@link #QUEUE} and calls {@link #groupNews(News)}.
	 */
	@Override
	public void run() {
		super.run();

		LOGGER.info("NewsGrouperThread started.");

		while (!isInterrupted()) {
			try {
				if(QUEUE.isEmpty()) {
					try {
						synchronized (this) {
							wait();
						}
					} catch (final InterruptedException e) {
						// exit
						break;
					}
				}

				final News news = QUEUE.poll();
				if(news != null) {
					try {
						LOGGER.debug("Start grouping news {}.", news.getId());
						groupNews(news);
					}
					catch(final Exception e) {
						LOGGER.error("An exception was thrown during grouping news (id={}). "
								+ "Retrying later.", news.getId(), e);

						// add to queue again so we can try it later again.
						waitAddToQueue(news);
					}
				}
			}
			// catch any Throwable that the Thread never stops.
			catch(final Throwable t) {
				LOGGER.error("During execution of the NewsGrouperThread a throwable was thrown.", t);
			}
		}

		LOGGER.info("NewsGrouperThread stopped.");
	}

	/**
	 * Searches for similar {@link News} and sets the news group id of a similar news or
	 * takes a new one. At the end it saves all changed {@link News}.
	 *
	 * @param news the {@link News} to group.
	 * @throws Exception if an error occurred during grouping of the {@link News}.
	 */
	protected void groupNews(final News news) throws Exception {
		// find similar news.
		final SimilarNewsFinder finder = new SimilarNewsFinder(news);
		final List<News> similarNews = finder.getSimilar();

		// if no similar news was found -> return
		if(similarNews.isEmpty()) {
			news.setNewsGroupId(News.NEWSGROUPID_NO_GROUP);
			saveNews(Arrays.asList(news));
			return;
		}

		long newsGroupId = 0;

		// search for an existing news group id.
		for (final News otherNews : similarNews) {
			if(otherNews.getNewsGroupId() > 0) {
				LOGGER.debug("Similar news {} of given news {} has already a news group id {}. Using this.",
						otherNews.getId(), news.getId(), otherNews.getNewsGroupId());

				newsGroupId = otherNews.getNewsGroupId();
				break;
			}
		}

		// if no similar news is already in a group -> generate a new news group id
		if(newsGroupId == 0) {
			newsGroupId = NewsDao.getNewNewsGroupId();
			LOGGER.debug("No similar news of news {} has already a news group id. Genereated a new {}.",
					news.getId(), newsGroupId);
		}

		// all news on which the news group id was changed.
		final List<News> newsToUpdate = new ArrayList<>();

		// set the new news group id to the given news.
		news.setNewsGroupId(newsGroupId);
		newsToUpdate.add(news);

		// sets the new news group id to all news which are currently not in a group.
		for (final News otherNews : similarNews) {
			if(otherNews.getNewsGroupId() == News.NEWSGROUPID_NO_GROUP ||
					otherNews.getNewsGroupId() == News.NEWSGROUPID_NOT_GROUPED_YET) {
				otherNews.setNewsGroupId(newsGroupId);
				newsToUpdate.add(otherNews);

				LOGGER.debug("Similar news {} of news {} has not a news group id yet. Using {}.",
						otherNews.getId(), news.getId(), newsGroupId);
			}
		}

		// update the news
		saveNews(newsToUpdate);
	}

	/**
	 * Saves the given news.
	 * @param newsToSave all {@link News} which will be saved.
	 * @throws SQLException if the {@link News} couldn't be saved.
	 */
	private void saveNews(final List<News> newsToSave) throws SQLException {
		final Connection con = DBConnectionProvider.getInstance().acquire();
		try {
			final NewsDao dao = new NewsDao(con);

			for (final News news : newsToSave) {
				dao.setNewsGroup(news, news.getNewsGroupId());
			}

			con.commit();
		} catch (final SQLException e) {
			LOGGER.error("Can't commit connection with grouped news.", e);
			throw e;
		}
		finally {
			DBConnectionProvider.getInstance().release(con);
		}
	}

	/**
	 * Adds the given {@link News} again to the {@link #QUEUE} after {@value #RETRY_TIME}.
	 * @param news the {@link News} to add again.
	 */
	private void waitAddToQueue(final News news) {
		if(QUEUE.size() > 10) {
			LOGGER.error("More than 10 news couldn't been indexed!");
		}

		final Thread thread = new Thread() {
			@Override
			public void run() {
				synchronized (WAIT_ADD_THREADS) {
					WAIT_ADD_THREADS.add(this);
				}

				try {
					sleep(RETRY_TIME); // wait
					QUEUE.add(news);
				}
				catch (final InterruptedException e) {
					return; // exit
				}
				finally {
					synchronized (WAIT_ADD_THREADS) {
						WAIT_ADD_THREADS.remove(this);
					}
				}
			}
		};

		thread.setName("Wait Add To Queue for News " + news.getId());
		thread.start();
	}

	/**
	 * Stops the {@link Thread} and all "try again threads".
	 */
	public void shutdown() {
		interrupt();

		synchronized (WAIT_ADD_THREADS) {
			for (final Thread thread : WAIT_ADD_THREADS) {
				thread.interrupt();
			}
		}
	}
}
