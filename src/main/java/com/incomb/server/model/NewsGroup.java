package com.incomb.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds {@link News} of the same news group.
 * It contains a {@link #mainNews} which is the most interesting one for the {@link User}.
 *
 * If it holds only a {@link #mainNews} than the {@link News} isn't grouped.
 */
public class NewsGroup {

	/**
	 * The most interesting {@link News} for the {@link User}.
	 */
	private News mainNews = null;

	/**
	 * All {@link News} except {@link #mainNews} in this news group.
	 */
	private List<? extends News> otherNews = new ArrayList<>();

	/**
	 * Creates a new {@link NewsGroup} with only a {@link #mainNews}.
	 * @param mainNews the most interesting {@link News} for the {@link User}.
	 */
	public NewsGroup(final News mainNews) {
		this.mainNews = mainNews;
	}

	/**
	 * Creates a new {@link NewsGroup}.
	 * @param mainNews the most interesting {@link News} for the {@link User}.
	 * @param otherNews all {@link News} except {@link #mainNews} in this news group.
	 */
	public NewsGroup(final News mainNews, final List<? extends News> otherNews) {
		this(mainNews);
		this.otherNews = otherNews;
	}

	/**
	 * @return the most interesting {@link News} for the {@link User}.
	 */
	public News getMainNews() {
		return mainNews;
	}

	/**
	 * Sets the most interesting {@link News} for the {@link User}.
	 * @param mainNews the most interesting {@link News} for the {@link User}.
	 */
	public void setMainNews(final News mainNews) {
		this.mainNews = mainNews;
	}

	/**
	 * @return all {@link News} except {@link #mainNews} in this news group.
	 */
	public List<? extends News> getOtherNews() {
		return otherNews;
	}

	/**
	 * Sets all {@link News} except {@link #mainNews} in this news group.
	 * @param otherNews all {@link News} except {@link #mainNews} in this news group.
	 */
	public void setOtherNews(final List<? extends News> otherNews) {
		this.otherNews = otherNews;
	}
}
