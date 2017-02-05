package com.incomb.server.cxf;

import java.sql.Connection;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

import com.incomb.server.db.DBConnectionProvider;

/**
 * Provides a database {@link Connection} for each {@link Request}.
 * The {@link Connection} can be used by adding it as a method parameter and
 * annotating it with {@link Context}.
 *
 * Example:
 * <code>public Response get(@Context Connection con)</code>
 */
public class DBConnectionContextProvider implements ContextProvider<Connection> {

	/**
	 * Name of the contextual property in the {@link Message} to access the {@link Connection}.
	 */
	public static final String PROPERTY_CONNECTION = "connection";

	/**
	 * Returns the {@link Connection} for the given {@link Message} and saves it as a
	 * contextual property.
	 * If a {@link Connection} was already opened for the given {@link Message} than
	 * the already opened one will returned.
	 */
	@Override
	public Connection createContext(final Message message) {
		Connection con = (Connection) message.getContextualProperty(PROPERTY_CONNECTION);

		if(con == null) {
			con = DBConnectionProvider.getInstance().acquire();
			message.setContextualProperty(PROPERTY_CONNECTION, con);
		}

		return con;
	}
}
