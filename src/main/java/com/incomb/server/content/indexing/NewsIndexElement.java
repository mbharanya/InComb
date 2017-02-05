package com.incomb.server.content.indexing;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.incomb.server.indexing.IIndexElement;
import com.incomb.server.indexing.conf.EOperation;
import com.incomb.server.model.ContentComment;
import com.incomb.server.model.ContentVote;
import com.incomb.server.model.News;
import com.incomb.server.model.dao.ContentCommentDao;
import com.incomb.server.model.dao.ContentVoteDao;

/**
 * Represents a {@link News} which will be indexed.
 */
public class NewsIndexElement implements IIndexElement {

	/**
	 * The {@link News} to index.
	 */
	private final News news;

	/**
	 * The operation of the element.
	 */
	private final EOperation operation;

	/**
	 * Creates a new instance with all needed properties.
	 * @param news the {@link News} to index.
	 * @param operation the operation of the element
	 */
	public NewsIndexElement(final News news, final EOperation operation) {
		this.news = news;
		this.operation = operation;
	}

	/**
	 * @return the operation of the element.
	 */
	@Override
	public EOperation getOperation() {
		return operation;
	}

	/**
	 * @return the {@link Locale} of the locale specific texts.
	 */
	@Override
	public Locale getLocale() {
		return news.getLocale();
	}

	/**
	 * @return {@link News#getId()}
	 */
	@Override
	public String getId() {
		return String.valueOf(news.getId());
	}

	/**
	 * Returns the content of the field.
	 *
	 * Available fields:
	 * <ul>
	 * 	<li>{@link NewsIndexType#FIELD_TITLE}</li>
	 *  <li>{@link NewsIndexType#FIELD_TITLE_SORT}</li>
	 *  <li>{@link NewsIndexType#FIELD_DESCRIPTION}</li>
	 *  <li>{@link NewsIndexType#FIELD_LINK}</li>
	 *  <li>{@link NewsIndexType#FIELD_PROVIDERID}</li>
	 *  <li>{@link NewsIndexType#FIELD_CATEGORYID}</li>
	 *  <li>{@link NewsIndexType#FIELD_PROVIDER}</li>
	 *  <li>{@link NewsIndexType#FIELD_CATEGORY}</li>
	 *  <li>{@link NewsIndexType#FIELD_IMAGE_URL}</li>
	 *  <li>{@link NewsIndexType#FIELD_IMAGE_WIDTH}</li>
	 *  <li>{@link NewsIndexType#FIELD_IMAGE_HEIGHT}</li>
	 *  <li>{@link NewsIndexType#FIELD_NEWSGROUPID}</li>
	 *  <li>{@link NewsIndexType#FIELD_PUBLISH_DATE}</li>
	 *  <li>{@link NewsIndexType#FIELD_IN}</li>
	 *  <li>{@link NewsIndexType#FIELD_INS_AMOUNT}</li>
	 *  <li>{@link NewsIndexType#FIELD_COMB}</li>
	 *  <li>{@link NewsIndexType#FIELD_COMMENT_OF}</li>
	 *  <li>{@link NewsIndexType#FIELD_COMMENTS_AMOUNT}</li>
	 * </ul>
	 */
	@Override
	public Object getContent(final String fieldName) {
		switch (fieldName) {
			case NewsIndexType.FIELD_TITLE:
			case NewsIndexType.FIELD_TITLE_SORT:
				return news.getTitle();
			case NewsIndexType.FIELD_DESCRIPTION:
				return news.getText();
			case NewsIndexType.FIELD_LINK:
				return news.getLink();
			case NewsIndexType.FIELD_PROVIDERID:
				return news.getProviderId();
			case NewsIndexType.FIELD_CATEGORYID:
				return news.getCategoryId();
			case NewsIndexType.FIELD_PROVIDER:
				return news.getProvider(null).getName();
			case NewsIndexType.FIELD_CATEGORY:
				return news.getCategory(null).getName(getLocale());
			case NewsIndexType.FIELD_IMAGE_URL:
				return news.getImageUrl();
			case NewsIndexType.FIELD_IMAGE_WIDTH:
				return news.getImageWidth();
			case NewsIndexType.FIELD_IMAGE_HEIGHT:
				return news.getImageHeight();
			case NewsIndexType.FIELD_NEWSGROUPID:
				return news.getNewsGroupId();
			case NewsIndexType.FIELD_PUBLISH_DATE:
				return news.getPublishDate();
			case NewsIndexType.FIELD_IN:
				return getUserIdsOfVotes(new ContentVoteDao().getInVotes(news.getId()));
			case NewsIndexType.FIELD_INS_AMOUNT:
				return new ContentVoteDao().getInsAmountByContentId(news.getId());
			case NewsIndexType.FIELD_COMB:
				return getUserIdsOfVotes(new ContentVoteDao().getCombVotes(news.getId()));
			case NewsIndexType.FIELD_COMMENT_OF:
				return getUserIdsOfComments(new ContentCommentDao().getCommentsByContentId(news.getId()));
			case NewsIndexType.FIELD_COMMENTS_AMOUNT:
				return new ContentCommentDao().getCommentsAmountForContentId(news.getId());
			default:
				throw new IllegalArgumentException("Field name " + fieldName + " is unknown.");
		}
	}

	/**
	 * Helper method which extracts all user ids of the given {@link ContentVote}s.
	 */
	private Set<Long> getUserIdsOfVotes(final List<ContentVote> votes) {
		final Set<Long> userIds = new HashSet<>();

		for (final ContentVote vote : votes) {
			userIds.add(vote.getUserId());
		}

		return userIds;
	}

	/**
	 * Helper method which extracts all user ids of the given {@link ContentComment}s.
	 */
	private Set<Long> getUserIdsOfComments(final List<ContentComment> comments) {
		final Set<Long> userIds = new HashSet<>();

		for (final ContentComment comment : comments) {
			userIds.add(comment.getUserId());
		}

		return userIds;
	}

	/**
	 * @return the indexed {@link News}
	 */
	public News getNews() {
		return news;
	}
}