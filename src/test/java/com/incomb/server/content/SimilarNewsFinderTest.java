package com.incomb.server.content;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.ATest;
import com.incomb.server.model.News;
import com.incomb.server.model.dao.NewsDao;

public class SimilarNewsFinderTest extends ATest {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SimilarNewsFinderTest.class);

	@Test
	public void testGetNews() {
		final List<News> muchNews = new NewsDao().getNews(4500, 100);
		for (final News news : muchNews) {
			final SimilarNewsFinder finder = new SimilarNewsFinder(news);
			final List<News> similarNews = finder.getSimilar();

			LOGGER.info("{} similar news for {} - {}: ", similarNews.size(), news.getId(), news.getTitle());
			for (final News similar : similarNews) {
				LOGGER.info("{} - {}", similar.getId(), similar.getTitle());
			}
			LOGGER.info("");
			LOGGER.info("");
		}
	}
}
