package com.incomb.server.content.loader;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.ATest;
import com.incomb.server.model.ContentSource;
import com.incomb.server.model.FetchHistory;

public class ContentLoaderManagerTest extends ATest {

	private static class TestContentLoaderManager extends ContentLoaderManager {

		protected TestContentLoaderManager() { }

		private static TestContentLoaderManager instance;

		public static synchronized TestContentLoaderManager getInstance() {
			if (instance == null) {
				instance = new TestContentLoaderManager();
			}
			return instance;
		}

		protected SortedSet<ContentSourceExecutionInfo> getContentSources() { return CONTENT_SOURCES; }
	};

	private static class TestContentSource extends ContentSource {
		private static final long serialVersionUID = 1L;

		private final int interval;
		private final FetchHistory fetchHistory;

		public TestContentSource(final int interval, final FetchHistory fetchHistory) {
			this.interval = interval;
			this.fetchHistory = fetchHistory;
		}

		@Override
		public int getInterval() { return interval; }

		@Override
		public FetchHistory getLastFetch(final Connection connection) { return fetchHistory; }
	};


	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentLoaderManagerTest.class);

	@Test
	public void testAddAndExecuteInfoCompare() {
		final TestContentLoaderManager manager = TestContentLoaderManager.getInstance();
		manager.reset();

		final TestContentSource t1 = new TestContentSource(5*60, null);
		final TestContentSource t2 = new TestContentSource(1*60, new FetchHistory(0, new Timestamp(System.currentTimeMillis() - 2000), true));
		final TestContentSource t3 = new TestContentSource(1*60, new FetchHistory(0, new Timestamp(System.currentTimeMillis() - 3001), true));

		manager.addContentSource(t2); // 3.
		manager.addContentSource(t3); // 2.
		manager.addContentSource(t1); // 1.

		Assert.assertEquals(3, manager.getContentSources().size());

		Assert.assertEquals(t1, manager.getContentSources().first().getContentSource());
		Assert.assertTrue(manager.removeContentSource(t1));

		Assert.assertEquals(t3, manager.getContentSources().first().getContentSource());
		Assert.assertTrue(manager.removeContentSource(t3));

		Assert.assertEquals(t2, manager.getContentSources().first().getContentSource());
		Assert.assertTrue(manager.removeContentSource(t2));
	}

	@Test
	public void testLoad() throws InterruptedException {
		final TestContentLoaderManager manager = TestContentLoaderManager.getInstance();
		manager.reset();

		manager.loadContentSources();
		Assert.assertTrue(manager.getContentSources().size() > 0);

		manager.start();
		Assert.assertTrue(manager.isInterrupted() == false);

		Thread.sleep(2000);
		manager.shutdown();
		Assert.assertTrue(manager.isInterrupted() == true);
	}

}
