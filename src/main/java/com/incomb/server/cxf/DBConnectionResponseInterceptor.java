package com.incomb.server.cxf;

import java.sql.Connection;
import java.sql.SQLException;

import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link AbstractPhaseInterceptor} commits or rollbacks the {@link Connection} when
 * the response will be sent to the client.
 *
 * <ul>
 * 	<li>If no {@link Connection} was used during the request nothing will be done.</li>
 * 	<li>If the response will be sent with a server error status code (500-599) than the
 * {@link Connection} will be rollbacked and closed.</li>
 * 	<li>Otherwise the {@link Connection} will be commited and closed.</li>
 * </ul>
 */
@Provider
public class DBConnectionResponseInterceptor extends AbstractPhaseInterceptor<Message> {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DBConnectionResponseInterceptor.class);

	/**
	 * Creates a new instance and sets the execution phase to {@link Phase#SEND}.
	 */
	public DBConnectionResponseInterceptor() {
		super(Phase.SEND);
	}

	/**
	 * Handles the {@link Connection} in the given {@link Message}.
	 * @see DBConnectionResponseInterceptor
	 */
	@Override
	public void handleMessage(final Message message) throws Fault {
		final Connection con = (Connection) message.getExchange().getInMessage().getContextualProperty(
				DBConnectionContextProvider.PROPERTY_CONNECTION);

		if(con != null) {
			try {
				final int responseCode = (int) message.get(Message.RESPONSE_CODE);

				if(Family.familyOf(responseCode).equals(Family.SERVER_ERROR)) {
					con.rollback();
				}
				else {
					con.commit();
				}

				con.close();
			}
			catch(final SQLException e) {
				LOGGER.error("Can't commit/rollback/close db connection because of an SQLException.", e);
			}
		}
	}
}
