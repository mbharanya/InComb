package com.incomb.server.services.locales;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.services.AService;
import com.incomb.server.utils.LocaleUtil;
/**
 * Actions for locales
 */
@Path("/locales")
@Produces(MediaType.APPLICATION_JSON)
public class LocalesService extends AService {

	/**
	 * @return a list of all available Locales
	 */
	@GET
	public Response getLocales() {
		final List<Locale> locales = LocaleUtil.getAllLocales();

		/*final List<String> strLocales = new ArrayList<>();
		for (final Locale locale : locales) {
			strLocales.add(locale.toString());
		}*/

		return ok(locales);
	}
}
