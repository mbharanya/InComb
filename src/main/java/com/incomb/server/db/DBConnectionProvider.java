package com.incomb.server.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.jooq.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.config.Config;
import com.incomb.server.utils.CloseUtil;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * This class is the single point to retrieve a {@link Connection} to the database and
 * release it after using.
 *
 * <p>Use it like this:</p>
 * <code>
 *  final Connection con = DBConnectionProvider.getInstance().acquire(); <br />
 *	try { <br />
 *	&nbsp;&nbsp;&nbsp;&nbsp;// execute database statements <br />
 *	} <br />
 *	finally { <br />
 *	&nbsp;&nbsp;&nbsp;&nbsp;DBConnectionProvider.getInstance().release(con);<br />
 *	}
 * </code>
 *
 * <p>This class can be used as a {@link ConnectionProvider} for jooq.</p>
 */
public class DBConnectionProvider implements ConnectionProvider {

	/**
	 * This class uses the singleton pattern.
	 * This constant holds the single instance of this class and
	 * can be accessed by calling {@link #getInstance()}.
	 */
	private static final DBConnectionProvider INSTANCE = new DBConnectionProvider();

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DBConnectionProvider.class);

	/**
	 * Internal {@link MysqlDataSource} to retrieve {@link Connection}s.
	 */
	private final MysqlDataSource dataSource = new MysqlDataSource();

	/**
	 * Amount of returned {@link Connection}s by {@link #acquire()}.
	 */
	private int acquiredConnections = 0;

	/**
	 * It configures the {@link #dataSource} with properties from the default {@link Config}.
	 * <p>The constructor is only accessible for this class and subclasses.</p>
	 */
	protected DBConnectionProvider() {
		final Config conf = Config.getDefault();
		dataSource.setServerName(conf.getStringProperty("db.host"));
		dataSource.setPort(conf.getIntProperty("db.port"));
		dataSource.setUser(conf.getStringProperty("db.user"));
		dataSource.setPassword(conf.getStringProperty("db.password"));
		dataSource.setDatabaseName(conf.getStringProperty("db.name"));
	}

	/**
	 * This class uses the singleton pattern.
	 * This method returns the single instance of this class.
	 */
	public static DBConnectionProvider getInstance() {
		return INSTANCE;
	}

	/**
	 * Returns a {@link Connection} with auto commit = false.
	 * @throws RuntimeException if it couldn't retrieve a {@link Connection}.
	 */
	@Override
	public Connection acquire() {
		try {
			final Connection con = dataSource.getConnection();
			con.setAutoCommit(false);
			logConnectionStats(1);
			return con;
		} catch (final SQLException e) {
			LOGGER.error("Couldn't get a MySQL connection!", e);
			throw new RuntimeException("Couldn't get a MySQL connection!", e);
		}
	}

	/**
	 * Closes the given {@link Connection}.
	 * After that call it can't be used anymore.
	 * Please do a commit or rollback before calling this method.
	 */
	@Override
	public void release(final Connection connection) {
		CloseUtil.close(connection);
		logConnectionStats(-1);
	}

	/**
	 * Logs the the new amount of returned {@link Connection}s.
	 * @param delta the amount of returned or released connections.
	 */
	private void logConnectionStats(final int delta) {
		acquiredConnections += delta;
		//LOGGER.info("Connections info: Total acquired: {}, delta: {}.", acquiredConnections, delta);
	}
}
