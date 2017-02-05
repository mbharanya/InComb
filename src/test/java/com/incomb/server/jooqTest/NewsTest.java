package com.incomb.server.jooqTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;

import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;

import com.incomb.server.ATest;
import com.incomb.server.model.Category;
import com.incomb.server.model.Content;
import com.incomb.server.model.Module;
import com.incomb.server.model.News;
import com.incomb.server.model.Provider;
import com.incomb.server.model.dao.NewsDao;
import com.incomb.server.model.dao.internal.InternalCategoryDao;
import com.incomb.server.model.dao.internal.InternalModuleDao;
import com.incomb.server.model.dao.internal.InternalProviderDao;
import com.incomb.server.model.tables.ContentTable;
import com.incomb.server.model.tables.NewsTable;

public class NewsTest extends ATest {

	@Test
	public void testSave() throws SQLException {
		final Configuration jooqConfig = new DefaultConfiguration().set(con).set(SQLDialect.MYSQL);

		final News news = insertNews(con, jooqConfig);

		// Test now

		final NewsTable newsTable = new NewsTable();
		final News fetchedNews = DSL.using(jooqConfig).selectFrom(newsTable).where(newsTable.CONTENT_ID.eq(news.getId())).fetchOneInto(News.class);

		final ContentTable contentTable = new ContentTable();
		final Content fetchedContent = DSL.using(jooqConfig).selectFrom(contentTable).where(contentTable.ID.eq(news.getId())).fetchOneInto(Content.class);

		assertNotNull(fetchedNews);
		assertNotNull(fetchedContent);
	}

	private News insertNews(final Connection con, final Configuration jooqConfig) {
		final Module module = new Module(9999, "key", "", "");
		final Provider provider = new Provider(9999, "Test-Provider", "", "", null);
		final Category category = new Category(9999, "testKey", "", module.getId());

		new InternalModuleDao(jooqConfig).insert(module);
		new InternalProviderDao(jooqConfig).insert(provider);
		new InternalCategoryDao(jooqConfig).insert(category);

		final News news = new News(99999, provider.getId(), category.getId(), "Test-News", Locale.GERMAN,
				"Lorem ipsum dolor ...", new Timestamp(System.currentTimeMillis()), "http://incomb.com",
				"/img/logo.svg", 232, 32, 287389);

		new NewsDao(con).saveNews(news);
		return news;
	}

	@Test
	public void testGet() {
		final Configuration jooqConfig = new DefaultConfiguration().set(con).set(SQLDialect.MYSQL);

		final News news = insertNews(con, jooqConfig);

		final News fetchedNews = new NewsDao(con).getNews(news.getId());
		assertNotNull(fetchedNews);

		assertEquals(news.getTitle(), fetchedNews.getTitle());
		assertEquals(news.getText(), fetchedNews.getText());
		assertEquals(news.getProviderId(), fetchedNews.getProviderId());
		assertEquals(news.getCategoryId(), fetchedNews.getCategoryId());
		assertEquals(news.getLocale(), fetchedNews.getLocale());

		assertEquals(news.getLink(), fetchedNews.getLink());
		assertEquals(news.getImageUrl(), fetchedNews.getImageUrl());
		assertEquals(news.getImageWidth(), fetchedNews.getImageWidth());
		assertEquals(news.getImageHeight(), fetchedNews.getImageHeight());
		assertEquals(news.getNewsGroupId(), fetchedNews.getNewsGroupId());
	}
}
