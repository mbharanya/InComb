package com.incomb.server.services.providers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.incomb.server.indexing.ISearchResult;
import com.incomb.server.indexing.SimpleSearchResult;
import com.incomb.server.model.Provider;
import com.incomb.server.model.dao.ProviderDao;
import com.incomb.server.services.AService;
import com.incomb.server.services.providers.model.ProviderModel;
import com.incomb.server.utils.LocaleUtil;
/**
 * Actions for provider specific resources
 */
@Path("/providers")
@Produces(MediaType.APPLICATION_JSON)
public class ProvidersService extends AService {
	/**
	 * Return a provider with the given search parameters
	 * @param search the name or part of the name of the provider
	 * @param localeStr Locale of the provider
	 * @param amount of results to return
	 * @param name of the provider
	 * @param con Connection to use - is injected automatically
	 * @return the found providers in a {@link SimpleSearchResult} with {@link ProviderModel}s
	 */
	@GET
	public Response getProviders(@QueryParam("search") final String search,
			@QueryParam("locale") final String localeStr,
			@QueryParam("amount") final int amount, @QueryParam("name") final String name,
			@Context final Connection con) {

		if(search != null) {
			return search(search, amount, localeStr, con);
		}

		if(name != null) {
			return ok(new SimpleSearchResult<>(new ProviderDao(con).getProvidersByName(name)));
		}

		return ok(new SimpleSearchResult<>(toModels(new ProviderDao(con).getProviders())));
	}

	/**
	 * Searches a provider with the given parameters
	 * @param search the name or part of the name of the provider
	 * @param amount of results to return
	 * @param localeStr Locale of the provider
	 * @param con Connection to use - is injected automatically
	 * @return the found providers in a {@link SimpleSearchResult} with {@link ProviderModel}s
	 */
	private Response search(final String search, final int amount, final String localeStr, final Connection con) {
		final Locale locale = LocaleUtil.toLocale(localeStr);
		if(locale == null) {
			throw new BadRequestException("Please specify a locale as GET parameter.");
		}

		final ISearchResult<Provider> providerResult = new ProviderDao(con).findProviders(search, locale, amount);
		final ISearchResult<ProviderModel> result = new SimpleSearchResult<>(
				toModels(providerResult.getResults()), providerResult.getTotalHits());

		return ok(result);
	}

	/**
	 * Converts a list of {@link Provider}s to a List of {@link ProviderModel}s
	 * @param providers to convert
	 * @return the converted List
	 */
	private List<ProviderModel> toModels(final List<Provider> providers) {
		final List<ProviderModel> models = new ArrayList<>();

		for (final Provider provider : providers) {
			models.add(new ProviderModel(provider));
		}

		return models;
	}
}
