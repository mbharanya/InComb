package com.incomb.server.model.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.ATest;
import com.incomb.server.content.indexing.NewsIndexType;
import com.incomb.server.indexing.ISearchResult;
import com.incomb.server.indexing.IndexManager;
import com.incomb.server.indexing.IndexTestUtil;
import com.incomb.server.model.Category;
import com.incomb.server.model.CategoryPreference;
import com.incomb.server.model.News;
import com.incomb.server.model.Provider;
import com.incomb.server.model.TagPreference;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.NewsDao.SearchParams;
import com.incomb.server.model.dao.internal.InternalCategoryDao;
import com.incomb.server.model.dao.internal.InternalCategoryPreferenceDao;
import com.incomb.server.model.dao.internal.InternalProviderDao;
import com.incomb.server.model.dao.internal.InternalTagPreferenceDao;
import com.incomb.server.model.tables.CategoryPreferenceTable;
import com.incomb.server.model.tables.CategoryTable;
import com.incomb.server.model.tables.ContentSourceTable;
import com.incomb.server.model.tables.ContentTable;
import com.incomb.server.model.tables.FetchHistoryTable;
import com.incomb.server.model.tables.NewsTable;
import com.incomb.server.model.tables.RssFeedContentSourceTable;

public class NewsDaoTest extends ATest {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsDaoTest.class);

	@Before
	public void clear() throws IOException {
		IndexTestUtil.deleteAllDocuments(NewsIndexType.getInstance());
		DSL.using(con).delete(new CategoryPreferenceTable()).execute();
		//DSL.using(con).truncate(new UserTable()).execute();
		DSL.using(con).delete(new NewsTable()).execute();
		DSL.using(con).delete(new ContentTable()).execute();
		DSL.using(con).delete(new RssFeedContentSourceTable()).execute();
		DSL.using(con).delete(new FetchHistoryTable()).execute();
		DSL.using(con).delete(new ContentSourceTable()).execute();
		DSL.using(con).delete(new CategoryTable()).execute();
	}

	@After
	public void rebuild() {
		IndexManager.getInstance().reIndex(NewsIndexType.getInstance());
	}

	@Test
	public void testGetNews() throws InterruptedException {
		final Locale locale = Locale.GERMAN;

		final User user = new User(99, "", "", "", "", "", new Timestamp(System.currentTimeMillis()), false);
		new UserDao(con).insert(user);

		final Category catSwitzerland = new Category(100, "", "", 1);
		final Category catSports = new Category(101, "", "", 1);
		final Category catDigital = new Category(102, "", "", 1);
		new InternalCategoryDao(jooqConfig).insert(catSwitzerland, catSports, catDigital);

		final CategoryPreference catPrefSwitzerland = new CategoryPreference(user.getId(), catSwitzerland.getId(), 0.5f);
		final CategoryPreference catPrefSports = new CategoryPreference(user.getId(), catSports.getId(), 0.0f);
		final CategoryPreference catPrefDigital = new CategoryPreference(user.getId(), catDigital.getId(), 1.0f);
		new InternalCategoryPreferenceDao(jooqConfig).insert(catPrefSwitzerland, catPrefSports, catPrefDigital);

		final TagPreference tagChristmas = new TagPreference(user.getId(), "Weihnachten");
		final TagPreference tagJava = new TagPreference(user.getId(), "Java");
		new InternalTagPreferenceDao(jooqConfig).insert(tagChristmas, tagJava);

		final Provider provider = new Provider(232, "", "", "", null);
		new InternalProviderDao(jooqConfig).insert(provider);

		final News news1  = createNews(1000001, catDigital, "Blub blab blib", ts(2,  8), provider, locale);
		final News news2  = createNews(1000002, catDigital, "Blub blab blib", ts(2, 17), provider, locale);
		final News news3  = createNews(1000003, catDigital, "Bl Weihnachten", ts(2,  8), provider, locale);
		final News news4  = createNews(1000004, catDigital, "Blub Java blib", ts(2, 17), provider, locale);

		final News news5  = createNews(1000005, catSports, "Blub blab blib", ts(2,  8), provider, locale);
		final News news6  = createNews(1000006, catSports, "Blub blab blib", ts(2, 17), provider, locale);
		final News news7  = createNews(1000007, catSports, "Bl Weihnachten", ts(2,  8), provider, locale);
		final News news8  = createNews(1000008, catSports, "Blub Java blib", ts(2, 17), provider, locale);

		final News news9  = createNews(1000009, catSwitzerland, "Blub blab blib", ts(2,  8), provider, locale);
		final News news10 = createNews(1000010, catSwitzerland, "Blub blab blib", ts(2, 17), provider, locale);
		final News news11 = createNews(1000011, catSwitzerland, "Bl Weihnachten", ts(2,  8), provider, locale);
		final News news12 = createNews(1000012, catSwitzerland, "Blub Java blib", ts(2, 17), provider, locale);

		final News news13 = createNews(1000013, catDigital, "Bl Weihnachten", ts(1,  17), provider, locale);
		final News news14 = createNews(1000014, catSports, "Bl Weihnachten", ts(1, 17), provider, locale);
		final News news15 = createNews(1000015, catSwitzerland, "Bl Weihnachten", ts(1, 17), provider, locale);

		final NewsDao newsDao = new NewsDao(con);
		newsDao.saveNews(news1);
		newsDao.saveNews(news2);
		newsDao.saveNews(news3);
		newsDao.saveNews(news4);
		newsDao.saveNews(news5);
		newsDao.saveNews(news6);
		newsDao.saveNews(news7);
		newsDao.saveNews(news8);
		newsDao.saveNews(news9);
		newsDao.saveNews(news10);
		newsDao.saveNews(news11);
		newsDao.saveNews(news12);
		newsDao.saveNews(news13);
		newsDao.saveNews(news14);
		newsDao.saveNews(news15);

		Thread.sleep(10000); // wait till all news are indexed

		final SearchParams conf = new SearchParams();
		conf.userId = user.getId();
		conf.locale = Locale.GERMAN;

		final ISearchResult<News> results = newsDao.getNews(conf);
		assertNotNull(results);
		assertEquals(15, results.getResults().size());

		LOGGER.info("+---------+----------+---------+-----------------------+--------------+");
		LOGGER.info("| ID      | Category | Date    | Title                 | Score        |");
		LOGGER.info("+---------+----------+---------+-----------------------+--------------+");

		for (final News news : results.getResults()) {
			LOGGER.info("| " + StringUtils.leftPad(Long.toString(news.getId()), 7) + " | " +
						StringUtils.leftPad(Integer.toString(news.getCategoryId()), 8) + " | " +
						StringUtils.leftPad(Integer.toString(news.getPublishDate().getDate()), 2) + "d " +
						StringUtils.leftPad(Integer.toString(news.getPublishDate().getHours()), 2) + "h | " +
						StringUtils.rightPad(news.getTitle(), 21) + " |              |");
		}

		LOGGER.info("+---------+----------+---------+-----------------------+--------------+");

		//  1. 1000004 - Java
		//  2. 1000012 - Java
		//  3. 1000008 - Java
		//  4. 1000003 - Weihnachten
		//  5. 1000011 - Weihnachten
		//  6. 1000007 - Weihnachten
		//  7. 1000002 - 17h
		//  8. 1000010 - 17h
		//  9. 1000006 - 17h
		// 10. 1000001 - Digital
		// 11. 1000009 - Switzerland
		// 12. 1000005 - Sports
		// 13. 1000013 - yesterday, Digital
		// 14. 1000015 - yesterday, Switzerland
		// 15. 1000014 - yesterday, Sports

		assertEquals(1000004, results.getResults().get(0).getId());
		assertEquals(1000012, results.getResults().get(1).getId());
		assertEquals(1000008, results.getResults().get(2).getId());
		assertEquals(1000003, results.getResults().get(3).getId());
		assertEquals(1000011, results.getResults().get(4).getId());
		assertEquals(1000007, results.getResults().get(5).getId());
		assertEquals(1000002, results.getResults().get(6).getId());
		assertEquals(1000010, results.getResults().get(7).getId());
		assertEquals(1000006, results.getResults().get(8).getId());
		assertEquals(1000001, results.getResults().get(9).getId());
		assertEquals(1000009, results.getResults().get(10).getId());
		assertEquals(1000005, results.getResults().get(11).getId());
		assertEquals(1000013, results.getResults().get(12).getId());
		assertEquals(1000015, results.getResults().get(13).getId());
		assertEquals(1000014, results.getResults().get(14).getId());
	}

	private News createNews(final long id, final Category category, final String title, final Timestamp publishDate, final Provider provider, final Locale locale) {
		final Connection theCon = this.con;
		return new News(id, provider.getId(), category.getId(), title, locale, "Boring text", publishDate, id + "", "", 0, 0, 0) {
			private static final long serialVersionUID = -5245446644055934630L;

			@Override
			public Provider getProvider(final Connection con) {
				return super.getProvider(theCon);
			}

			@Override
			public Category getCategory(final Connection con) {
				return super.getCategory(theCon);
			}
		};
	}

	private Timestamp ts(final int day, final int hour) {
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return new Timestamp(cal.getTimeInMillis());
	}
}
