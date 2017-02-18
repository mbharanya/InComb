package com.incomb.server.services.indexes;

import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.incomb.server.config.Config;
import com.incomb.server.indexing.IndexManager;
import com.incomb.server.indexing.conf.IIndexTypeConf;
import com.incomb.server.services.AService;
/**
 * Actions for indexing
 */
@Path("/indexes/{index}")
public class IndexService extends AService {

	private static final String HEADER_AUTH = "Authorization";

	/**
	 * Reindex an Index for a given indexType
	 * @param index type to re-index
	 * @return HTTP No Content if Successful
	 */
	@PUT
	public Response reIndex(@PathParam("index") final String index) {
		if(!Config.getDefault().getStringProperty("auth.index").equals(
				getRequest().getHeader(HEADER_AUTH))) {
			throw new ForbiddenException("Access restricted for reindex.");
		}
		
		try {
			final Class<? extends IIndexTypeConf> indexClass =
					(Class<? extends IIndexTypeConf>) Class.forName(index);

			IIndexTypeConf typeConf = null;
			try {
				typeConf = indexClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// no simple constructor exists -> maybe it's a singleton.

				typeConf = (IIndexTypeConf) MethodUtils.invokeExactStaticMethod(indexClass, "getInstance");
			}

			IndexManager.getInstance().reIndex(typeConf);

			return Response.noContent().build();
		} catch (ClassNotFoundException | ClassCastException | NoSuchMethodException |
				InvocationTargetException | IllegalAccessException e) {
			throw new NotFoundException("Can't find index: " + index);
		}
	}
}
