package com.incomb.server.services.news;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.indexing.ISearchResult;
import com.incomb.server.indexing.SimpleSearchResult;
import com.incomb.server.model.News;
import com.incomb.server.model.NewsGroup;
import com.incomb.server.model.dao.NewsDao;
import com.incomb.server.model.dao.NewsDao.SearchParams;
import com.incomb.server.model.dao.NewsDao.SearchParams.ENewsSource;
import com.incomb.server.model.dao.NewsDao.SearchParams.ESortField;
import com.incomb.server.services.AService;
import com.incomb.server.services.news.model.NewsModel;
import com.incomb.server.utils.LocaleUtil;

/**
 * Service to return {@link News} and {@link NewsGroup}s.
 */
@Path("/news")
@Produces(MediaType.APPLICATION_JSON)
public class NewsService extends AService {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsService.class);

	/**
	 * Searches {@link News} or {@link NewsGroup}s (depends on notGrouped parameter) for the given params.
	 * Parameters are the same as in {@link SearchParams}.
	 * @return {@link ISearchResult} containing {@link News} or {@link NewsGroup}s.
	 */
	@GET
	public Response getNews(@QueryParam("providerId") final int providerId, @QueryParam("categoryId") final int categoryId,
			@QueryParam("userId") final long userId, @QueryParam("startPublishDate") final long startPublishDate,
			@QueryParam("endPublishDate") final long endPublishDate, @QueryParam("searchText") final String searchText,
			@QueryParam("locale") final String localeStr, @QueryParam("offset") final int offset,
			@QueryParam("amount") final int amount, @QueryParam("sortorder") final int sortorder,
			@QueryParam("sourceBees") final boolean sourceBees, @QueryParam("sourceUser") final boolean sourceUser,
			@QueryParam("groupNews") final boolean notGrouped, @Context final Connection con) {

		if(userId > 0) {
			getAccessChecker().checkLoggedInUser(userId);
		}

		final SearchParams params = new SearchParams();
		params.providerId = providerId;
		params.categoryId = categoryId;
		params.userId = userId;
		params.startPublishDate = startPublishDate > 0 ? new Date(startPublishDate) : null;
		params.endPublishDate = endPublishDate > 0 ? new Date(endPublishDate) : null;
		params.searchText = searchText;
		params.locale = localeStr == null ? null : LocaleUtil.toLocale(localeStr);
		params.offset = offset;
		params.amount = amount;

		if(sortorder > 0) {
			final ESortField sortField = ESortField.getById(sortorder);
			params.sortFields.put(sortField, sortField.getDefaultOrder());
		}

		if(!sourceBees) {
			params.newsSources.remove(ENewsSource.BEES);
		}

		if(!sourceUser) {
			params.newsSources.remove(ENewsSource.USER);
		}

		if(notGrouped) {
			final ISearchResult<News> newsResult = new NewsDao(con).getNews(params);
			final List<NewsModel> models = new ArrayList<>();

			// convert news to newsmodels.
			for (final News news : newsResult.getResults()) {
				models.add(new NewsModel(news, params.userId, con));
			}

			return ok(new SimpleSearchResult<>(models, newsResult.getTotalHits()));
		}
		else {
			final StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			// get news groups from dao.
			final ISearchResult<NewsGroup> newsResult = new NewsDao(con).getNewsGroups(params);

			stopWatch.stop();
			LOGGER.info("Getting NewsGroups from NewsDao took {}ms.", stopWatch.getTime());

			final List<NewsModel> models = new ArrayList<>();

			// convert from News to NewsModel
			for (final NewsGroup newsGroup : newsResult.getResults()) {
				final List<NewsModel> otherNews = new ArrayList<>();
				for (final News news : newsGroup.getOtherNews()) {
					otherNews.add(new NewsModel(news, params.userId, con));
				}

				models.add(new NewsModel(newsGroup.getMainNews(), otherNews, params.userId, con));
			}

			return ok(new SimpleSearchResult<>(models, newsResult.getTotalHits()));
		}
	}
}
