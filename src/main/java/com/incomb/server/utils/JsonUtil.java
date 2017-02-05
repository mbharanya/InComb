package com.incomb.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON specific static methods
 */
public class JsonUtil {

	/**
	 * <p>
	 * The {@link Logger} for this class.
	 * </p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

	/**
	 * Returns a JSON formatted String of a given bean using getters
	 * @param bean to create the JSON from
	 * @param formatOutput true if whitespace should be used to indent the output
	 * @return the JSON formatted String
	 */
	public static String getJson(final Object bean, final boolean formatOutput) {
		final ObjectMapper objectMapper = new ObjectMapper();
		if (formatOutput) {
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		}
		try {
			return objectMapper.writeValueAsString(bean);
		} catch (final JsonProcessingException e) {
			LOGGER.error("Error converting Bean to JSON {}", e);
		}
		return "";
	}

	/**
	 * Wrapper method for getJson that doesn't indent the output See :getJson() get
	 * @param bean
	 * @return the JSON formatted String
	 */
	public static String getJson(final Object bean) {
		return JsonUtil.getJson(bean, false);
	}

}
