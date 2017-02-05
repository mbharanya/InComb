package com.incomb.server.services.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.incomb.server.model.ContentComment;
import com.incomb.server.model.dao.ContentCommentDao;
import com.incomb.server.model.dao.UserDao;
import com.incomb.server.services.news.model.CommentModel;
import com.incomb.server.services.news.model.NewsModel;
import com.incomb.server.services.users.UserUtil;
/**
 * Contains static helper methods to use with {@link ContentComment}s
 */
public class ContentCommentUtil {

	/**
	 * @return a list of {@link CommentModel} for the given content and userId.
	 * 			The {@link CommentModel} has no {@link NewsModel} attached.
	 */
	public static List<CommentModel> getCommentsByContentId(final long contentId, final Connection con) {
		final List<CommentModel> comments = new ArrayList<>();
		final List<ContentComment> contentComments = new ContentCommentDao(con).getCommentsByContentId(contentId);

		final UserDao userDao = new UserDao(con);
		for(final ContentComment contentComment: contentComments){
			comments.add(new CommentModel(contentComment,
					UserUtil.getModel(false, userDao.findById(contentComment.getUserId()), con), null));
		}

		return comments;
	}
}
