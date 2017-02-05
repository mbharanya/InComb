package com.incomb.server.services.news.model;

import java.sql.Connection;

import com.incomb.server.model.ContentComment;
import com.incomb.server.model.News;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.NewsDao;
import com.incomb.server.model.dao.UserDao;
import com.incomb.server.services.users.model.UserModel;

/**
 * Simple Bean for Comments to communicate with the client
 */
public class CommentModel extends ContentComment {
	private static final long serialVersionUID = 1714215379193039571L;

	/**
	 * The {@link User} which has given the comment.
	 */
	private UserModel user;

	/**
	 * The {@link News} to which the comment was written.
	 */
	private NewsModel news;

	/**
	 * Creates a new instance with all properties.
	 * @param comment the {@link ContentComment} to copy.
	 * @param user the {@link User} which has given the comment.
	 * @param news the {@link News} to which the comment was written.
	 */
	public CommentModel(final ContentComment comment, final UserModel user, final NewsModel news) {
		super(comment.getId(), comment.getUserId(), comment.getContentId(), comment.getCommentDate(), comment.getComment());
		user.setEmail(null);
		this.user = user;
		this.news = news;
	}

	/**
	 * Creates a new instance with the given {@link ContentComment} and loads all other properties from the DAOs.
	 * @param comment the {@link ContentComment} to copy.
	 * @param con the {@link Connection} to load other properties.
	 */
	public CommentModel(final ContentComment comment, final Connection con) {
		this(
			comment,
			new UserModel(new UserDao().findById(comment.getUserId())),
			new NewsModel(new NewsDao(con).getNews(comment.getContentId()), comment.getUserId(), con)
		);
	}

	/**
	 * @return the {@link User} which has given the comment.
	 */
	public UserModel getUser() {
		return user;
	}

	/**
	 * Sets the {@link User} which has given the comment.
	 * @param user the {@link User} which has given the comment.
	 */
	public void setUser(final UserModel user) {
		this.user = user;
	}

	/**
	 * @return the {@link News} to which the comment was written.
	 */
	public NewsModel getNews() {
		return news;
	}

	/**
	 * Sets the {@link News} to which the comment was written.
	 * @param news the {@link News} to which the comment was written.
	 */
	public void setNews(final NewsModel news) {
		this.news = news;
	}
}
