package com.incomb.server.services.news.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.incomb.server.model.Category;
import com.incomb.server.model.CombItem;
import com.incomb.server.model.ContentComment;
import com.incomb.server.model.News;
import com.incomb.server.model.Provider;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.CombItemDao;
import com.incomb.server.model.dao.ContentVoteDao;
import com.incomb.server.services.news.model.util.CssUtil;
import com.incomb.server.services.providers.model.ProviderModel;
import com.incomb.server.services.users.combItems.model.CombItemModel;
import com.incomb.server.services.utils.ContentCommentUtil;

/**
 * This extends the {@link News} with properties for the view.
 */
public class NewsModel extends News {

	private static final long serialVersionUID = 3861126516126247707L;

	/**
	 * The {@link Provider} of the {@link News}.
	 */
	private ProviderModel provider;

	/**
	 * The {@link Category} of the {@link News}.
	 */
	private Category category;

	/**
	 * The {@link CombItem} of the {@link News} and the {@link User}.
	 */
	private CombItemModel combItem = null;

	/**
	 * All {@link ContentComment}s sorted by date.
	 */
	private List<CommentModel> comments = new ArrayList<>();

	/**
	 * The amount of Ins and Combs.
	 */
	private VoteAmountModel voteAmounts = new VoteAmountModel(0, 0);

	/**
	 * Other news if existing.
	 */
	private List<NewsModel> otherNews = new ArrayList<>();

	/**
	 * Creates a new instance with all properties.
	 * @param news the {@link News} to copy.
	 * @param provider the {@link Provider} of the {@link News}.
	 * @param category the {@link Category} of the {@link News}.
	 * @param combItem the {@link CombItem} of the {@link News} and the {@link User}.
	 * @param comments all {@link ContentComment}s sorted by date.
	 * @param voteAmounts the amount of Ins and Combs.
	 * @param otherNews other news if existing.
	 */
	public NewsModel(final News news, final Provider provider, final Category category, final CombItem combItem,
			final List<CommentModel> comments, final VoteAmountModel voteAmounts, final List<NewsModel> otherNews) {

		super(news.getId(), news.getProviderId(), news.getCategoryId(), news.getTitle(), news.getLocale(),
				news.getText(), news.getPublishDate(), CssUtil.stripProtocolFromUrl(news.getLink()), news.getImageUrl(),
				news.getImageWidth(), news.getImageHeight(), news.getNewsGroupId());

		this.category = category;
		this.comments = comments;
		this.voteAmounts = voteAmounts;

		// set only if not null.
		if(provider != null) {
			this.provider = new ProviderModel(provider);
		}

		// set only if not null.
		if(combItem != null) {
			this.combItem = new CombItemModel(combItem);
		}

		// set only if not null.
		if(otherNews != null) {
			this.otherNews = otherNews;
		}
	}

	/**
	 * Creates a new instance and retrieves all needed properties from the DAO using the given {@link Connection}.
	 * @param news the {@link News} to copy.
	 * @param userId the {@link User} which will see this {@link News}.
	 * @param con the {@link Connection} to retrieve the needed properties.
	 */
	public NewsModel(final News news, final long userId, final Connection con) {
		this(news, null, userId, con);
	}

	/**
	 * Creates a new instance and retrieves all needed properties from the DAO using the given {@link Connection}.
	 * @param news the {@link News} to copy.
	 * @param otherNews other news if existing.
	 * @param userId the {@link User} which will see this {@link News}.
	 * @param con the {@link Connection} to retrieve the needed properties.
	 */
	public NewsModel(final News news, final List<NewsModel> otherNews, final long userId, final Connection con) {
		this(
			news, news.getProvider(con), news.getCategory(con),
			new CombItemDao(con).getCombItem(news.getContentId(), userId),
			ContentCommentUtil.getCommentsByContentId(news.getContentId(), con),
			new VoteAmountModel(new ContentVoteDao(con).getInsAmountByContentId(news.getContentId()), new ContentVoteDao(con).getCombsAmountByContentId(news.getContentId())),
			otherNews
		); // TODO: XMBomb[07.01.2015] why do can't I instantiate the votedao first?
	}

	/**
	 * @return the {@link Provider} of the {@link News}.
	 */
	public ProviderModel getProvider() {
		return provider;
	}

	/**
	 * @return the {@link Category} of the {@link News}.
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @return the {@link CombItem} of the {@link News} and the {@link User}.
	 */
	public CombItemModel getCombItem() {
		return combItem;
	}

	/**
	 * @return all {@link ContentComment}s sorted by date.
	 */
	public List<CommentModel> getComments() {
		return comments;
	}

	/**
	 * @return other {@link News} to this {@link News}.
	 */
	public List<NewsModel> getOtherNews() {
		return otherNews;
	}

	/**
	 * @return the amount of Ins and Combs.
	 */
	public VoteAmountModel getVoteAmounts() {
		return voteAmounts;
	}

	/**
	 * Sets the {@link Provider} of the {@link News}.
	 * @param provider the {@link Provider} of the {@link News}.
	 */
	public void setProvider(final ProviderModel provider) {
		this.provider = provider;
	}

	/**
	 * Sets the {@link Category} of the {@link News}.
	 * @param category the {@link Category} of the {@link News}.
	 */
	public void setCategory(final Category category) {
		this.category = category;
	}

	/**
	 * Sets the {@link CombItem} of the {@link News} and the {@link User}.
	 * @param combItem the {@link CombItem} of the {@link News} and the {@link User}.
	 */
	public void setCombItem(final CombItemModel combItem) {
		this.combItem = combItem;
	}

	/**
	 * Sets all {@link ContentComment}s sorted by date.
	 * @param comments all {@link ContentComment}s sorted by date.
	 */
	public void setComments(final List<CommentModel> comments) {
		this.comments = comments;
	}

	/**
	 * Sets other {@link News} to this {@link News}.
	 * @param otherNews other {@link News} to this {@link News}.
	 */
	public void setOtherNews(final List<NewsModel> otherNews) {
		this.otherNews = otherNews;
	}

	/**
	 * Sets the amount of Ins and Combs.
	 * @param voteAmounts the amount of Ins and Combs.
	 */
	public void setVoteAmounts(final VoteAmountModel voteAmounts) {
		this.voteAmounts = voteAmounts;
	}

	/**
	 * Removes the protocol and replaces it with CSS compatible protocol-less url
	 */
	@Override
	public String getImageUrl(){
		return CssUtil.stripProtocolFromUrl(super.getImageUrl());
	}
}
