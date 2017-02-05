package com.incomb.server.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incomb.server.config.exceptions.ReadConfigException;
import com.incomb.server.utils.ConfigUtil;

/**
 * Allows the user get a value of a key from a default or newly defined Configuration file
 * The configuration files must be in a JSON format
 *
 */
public class Config {

	/**
	 * <p>
	 * The {@link Logger} for this class.
	 * </p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

	/**
	 * default configuration path based on the current docBase
	 */
	private static final File DEFAULT_CONF_PATH = new File(ConfigUtil.getDocBase() + "/WEB-INF/conf/incomb_config.json");
	private static Config defaultInstance = null;

	/**
	 * Uses the {@link Config.DEFAULT_CONF_PATH}  to return or create a {@link Config} instance
	 * @return a new or already created {@link Config} instance
	 */
	public static Config getDefault() {
		if(defaultInstance == null) { // check here to prevent synchronization.
			synchronized(Config.class) {
				if(defaultInstance == null) {
					defaultInstance = new Config(DEFAULT_CONF_PATH);
				}
			}
		}

		return defaultInstance;
	}

	private final File importFile;
	private long lastModifyTime = 0;

	private Map<String, Object> config = null;

	/**
	 * Uses a the importfile param to create a new {@link Config} instance
	 * The configuration files must be in a JSON format
	 * @param importFile the file to read the configuration from
	 */
	public Config(final File importFile) {
		this.importFile = importFile;
		loadIfNeeded();
	}

	/**
	 * Wrapper method for Constructor with File parameter
	 * @param filePath the file to read the configuration from
	 */
	public Config(final String filePath) {
		this(new File(filePath));
	}

	/**
	 * Get a single or cascading configuration property
	 * To use cascaded values separate them with a "." character
	 * @param propertyKey The propertyKey to return
	 * @param type the desired type to cast to
	 * @return the casted value, or an exception if casting fails
	 */
	public <T> T getProperty(final String propertyKey, final Class<T> type) {
		loadIfNeeded();
		final String[] properties = propertyKey.split("\\.");
		return cast(getCascadingProperty(properties, config, 0), type);
	}

	/**
	 * Wrapper method for getProperty to always return a String
	 * @param propertyKey
	 * @return
	 */
	public String getStringProperty(final String propertyKey) {
		return getProperty(propertyKey, String.class);
	}

	/**
	 * Wrapper method for getProperty to always return an int
	 * @param propertyKey
	 * @return
	 */
	public int getIntProperty(final String propertyKey) {
		return getProperty(propertyKey, Integer.class);
	}

	/**
	 * Wrapper method for getProperty to always return a double
	 * @param propertyKey
	 * @return
	 */
	public double getDoubleProperty(final String propertyKey) {
		return getProperty(propertyKey, Double.class);
	}

	/**
	 * Wrapper method for getProperty to always return a boolean
	 * @param propertyKey
	 * @return
	 */
	public boolean getBooleanProperty(final String propertyKey) {
		return getProperty(propertyKey, Boolean.class);
	}

	/**
	 * Check if a property exists
	 * <b>Warning</b> does not work if the property is null
	 * @param propertyKey key to check
	 * @param type type to cast
	 * @return if the property exists
	 */
	public <T> boolean doesPropertyExist(final String propertyKey, final Class<T> type) {
		try {
			return type.cast(getProperty(propertyKey, type)) != null;
		} catch (final Exception e) {
			return false;
		}
	}

	private <T> T cast(final Object value, final Class<T> type){
		if(value instanceof Number) {
			final Number number = (Number) value;
			if(type.equals(byte.class)) {
				return type.cast(number.byteValue());
			}
			else if(type.equals(short.class)) {
				return type.cast(number.shortValue());
			}
			else if(type.equals(int.class)) {
				return type.cast(number.intValue());
			}
			else if(type.equals(long.class)) {
				return type.cast(number.longValue());
			}
			else if(type.equals(float.class)) {
				return type.cast(number.floatValue());
			}
			else if(type.equals(double.class)) {
				return type.cast(number.doubleValue());
			}
		}
		return type.cast(value);
	}

	@SuppressWarnings("unchecked")
	private Object getCascadingProperty(final String[] properties, final Map<String, Object> resultMap, int i) {
		Map<String, Object> toIterateThrough = new HashMap<String, Object>(resultMap);
		for (; i < properties.length; i++) {
			if (toIterateThrough.get(properties[i]) instanceof Map) {
				toIterateThrough = (Map<String, Object>) toIterateThrough.get(properties[i]);
				getCascadingProperty(properties, toIterateThrough, i);
			} else {
				return toIterateThrough.get(properties[i]);
			}
		}
		return toIterateThrough;
	}

	private void loadIfNeeded() {
		final long newModifyTime = importFile.lastModified();
		if(lastModifyTime == 0 || newModifyTime > lastModifyTime) {
			synchronized (this) {
				if(lastModifyTime == 0 || newModifyTime > lastModifyTime) {
					final JsonFactory factory = new JsonFactory();
					final ObjectMapper mapper = new ObjectMapper(factory);
					final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>(){};

					try {
						config = mapper.readValue(importFile, typeRef);
					} catch (final JsonParseException e) {
						LOGGER.error("Could not parse JSON file {}", importFile, e);
						throw new ReadConfigException(e, "Could not parse JSON file");

					} catch (final JsonMappingException e) {
						LOGGER.error("Could not map JSON file {}", importFile, e);
						throw new ReadConfigException(e, "Could not map JSON file");

					} catch (final IOException e) {
						LOGGER.error("General error while getting configuration from json file {}", importFile, e);
						throw new ReadConfigException(e, "General error while getting configuration from json file");
					}

					lastModifyTime = newModifyTime;
				}
			}
		}
	}

	/**
	 * Gets the importFile currently used by the {@link Config} instance
	 * @return the used config file
	 */
	public File getFile() {
		return importFile;
	}
}