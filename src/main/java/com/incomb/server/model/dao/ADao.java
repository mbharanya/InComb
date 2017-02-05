package com.incomb.server.model.dao;

import java.sql.Connection;

import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;

import com.incomb.server.db.DBConnectionProvider;

/**
 * Default implementation for all DAOs.
 * It prepares the {@link Configuration} with the {@link Connection}.
 */
public abstract class ADao {

	/**
	 * jooqs configuration which can be used to operate with the database.
	 */
	protected final Configuration jooqConfig;

	/**
	 * Creates a new instance with the given {@link Connection}.
	 * @param connection the {@link Connection} to use. If <code>null</code> a new {@link Connection}
	 * 			will automatically acquired.
	 */
	public ADao(final Connection connection) {
		if(connection == null) {
			jooqConfig = new DefaultConfiguration().set(DBConnectionProvider.getInstance());
		}
		else {
			jooqConfig = new DefaultConfiguration().set(connection);
		}

		jooqConfig.set(SQLDialect.MYSQL);
	}

}
