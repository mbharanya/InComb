package com.incomb.server.content;

import java.sql.Connection;

import com.incomb.server.model.ContentSource;
import com.incomb.server.model.dao.IFinder;
import com.incomb.server.model.dao.RssFeedContentSourceDao;
import com.incomb.server.utils.ObjectUtil;

/**
 * The {@link EContentSourceDaoType} enum represents all available sub classes
 * of the {@link ContentSource} class. 
 */
public enum EContentSourceDaoType {
	
	RSS_FEED_CONTENT_SOURCE();
	
	/**
	 * Returns the dao for the given <code>type</code>.
	 */
	public static IFinder<? extends ContentSource> getDaoAsInstance(Connection con, EContentSourceDaoType type) {
		ObjectUtil.assertNotNull(con, "connection");
		
		switch(type) {
			case RSS_FEED_CONTENT_SOURCE: return new RssFeedContentSourceDao(con);
			default: throw new IllegalArgumentException("No Dao configured for: " + type.name() );
		}
	}
}
