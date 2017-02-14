package com.incomb.server;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlets.DefaultServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.config.Config;
import com.incomb.server.content.NewsGrouperThread;
import com.incomb.server.content.loader.ContentLoaderManager;
import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.indexing.IndexManager;
import com.incomb.server.model.News;
import com.incomb.server.model.dao.NewsDao;
import com.incomb.server.utils.ConfigUtil;

/**
 * This is the InComb Servlet.
 * It startups and shutdowns the system and handles request for angular.js routed paths.
 */
public class InCombServlet extends HttpServlet {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(InCombServlet.class);

	private static final long serialVersionUID = -3230837788917610559L;

	/**
	 * Sets the doc base at {@link ConfigUtil#setDocBase(String)},
	 * initializes the logger, starts the {@link ContentLoaderManager} and the
	 * {@link NewsGrouperThread}.
	 */
	@Override
	public void init() throws ServletException {
		super.init();

		ConfigUtil.setDocBase(getServletContext().getRealPath("/"));
		ConfigUtil.initLogger();
		LOGGER.info("Logger initialized successfully!");

		if(Config.getDefault().getBooleanProperty("contentLoader.run")) {
			final ContentLoaderManager loadingManager = ContentLoaderManager.getInstance();
			loadingManager.loadContentSources();
			loadingManager.start();
			LOGGER.info("ContentLoader started!");
		}
		else {
			LOGGER.warn("ContentLoader won't be started because it was disabled in incomb_config.json!");
		}

		indexNotIndexedNews();
		LOGGER.info("Not indexed news added to indexer.");

		final NewsGrouperThread newsGrouper = NewsGrouperThread.getInstance();
		newsGrouper.start();
		addUngroupedNewsToGrouper();
		LOGGER.info("NewsGrouperThread initialized.");
	}

	/**
	 * Indexes all not indexed {@link News}.
	 */
	private void indexNotIndexedNews() {
		final Connection con = DBConnectionProvider.getInstance().acquire();

		try {
			new NewsDao(con).indexNotIndexedNews();
		}
		catch(final Exception e) {
			LOGGER.error("Can't index unindexed news.", e);
		}
		finally {
			DBConnectionProvider.getInstance().release(con);
		}
	}

	/**
	 * Adds all not already grouped {@link News} to the {@link NewsGrouperThread}.
	 */
	private void addUngroupedNewsToGrouper() {
		final Connection con = DBConnectionProvider.getInstance().acquire();

		try {
			for (final News news : new NewsDao(con).getNewsToGroup()) {
				NewsGrouperThread.getInstance().addNews(news);
			}
		}
		catch(final Exception e) {
			LOGGER.error("Can't add ungrouped news to NewsGrouperThread queue.", e);
		}
		finally {
			DBConnectionProvider.getInstance().release(con);
		}
	}

	/**
	 * Shutdowns the system. Stops the {@link ContentLoaderManager}, {@link IndexManager}
	 * and the {@link NewsGrouperThread}.
	 */
	@Override
	public void destroy() {
		LOGGER.warn("Shutting servlet down!");
		super.destroy();

		ContentLoaderManager.getInstance().shutdown();
		IndexManager.getInstance().shutdown();
		NewsGrouperThread.getInstance().shutdown();
	}

	/**
	 * Rewrites all request without a file extension to <code>/index.html</code> and returns it
	 * to the {@link DefaultServlet}. This is necessary because for URLs which will be handled
	 * in Angular.js. It requires that Angular.js is loaded and that is by returning the content
	 * of the index.html.
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		final HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
			@Override
			public String getServletPath() {
				return "";
			}

			@Override
			public String getRequestURI() {
				return "";
			}

			@Override
			public String getPathInfo() {
				return super.getPathInfo().matches(".*\\/[^\\/]*\\.[^\\/]*$") ? super.getPathInfo() : "/index.html";
			}
		};

		final RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("default");
		dispatcher.forward(wrapper, response);
	}
}
