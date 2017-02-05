package com.incomb.server.services.translations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

import com.incomb.server.i18n.Translator;
import com.incomb.server.i18n.TranslatorManager;
import com.incomb.server.services.AService;
import com.incomb.server.utils.LocaleUtil;

/**
 * Actions for specific Translations of Locales
 */
@Path("/translations/{locale}")
@Produces(MediaType.APPLICATION_JSON)
public class TranslationService extends AService {

	/**
	 * Returns Translations of a given {@link Locale}
	 * @param locale to get the Translation of
	 * @param request The HTTP-Request - is injected automatically
	 * @return the translation
	 */
	@GET
	public Response getTranslations(@PathParam("locale") final String locale,
			@Context final Request request) {

		final Translator translator = TranslatorManager.getTranslator(LocaleUtil.toLocale(locale));
		if(translator == null) {
			throw new NotFoundException();
		}

		final File file = translator.getFile();
		final Date lastModified = new Date(file.lastModified());

		ResponseBuilder respBuilder = request.evaluatePreconditions(lastModified);
		if(respBuilder == null) {
			respBuilder = Response.ok();
		}

		return respBuilder.lastModified(lastModified).entity(new StreamingOutput() {
			@Override
			public void write(final OutputStream output) throws IOException, WebApplicationException {
				IOUtils.copy(new FileInputStream(file), output);
			}
		}).build();
	}
}
