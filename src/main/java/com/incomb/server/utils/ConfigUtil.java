package com.incomb.server.utils;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Class to configure the system on startup.
 */
public class ConfigUtil {

	/**
	 * Place in the filesystem where the configuration file for Logback is.
	 */
	public static final String LOGBACK_CONFIG = "WEB-INF/conf/logback.xml";

	/**
	 * The absolute path to the document base (WEB-INF) where the configuration files exists.
	 */
	private static String docBase = System.getProperty("base.path");

	/**
	 * @return the absolute path to the document base (WEB-INF) where the configuration files exists.
	 */
	public static String getDocBase() {
		return docBase;
	}

	/**
	 * Sets the doc base.
	 * @param docBase the absolute path to the document base (WEB-INF) where the configuration files exists.
	 */
	public static void setDocBase(final String docBase) {
		ConfigUtil.docBase = docBase;
	}

	/**
	 * Initializes Logback with the configuration in the file at {@value #LOGBACK_CONFIG}.
	 */
	public static void initLogger() {
		// Initialize the logback logger
		LoggerContext loggercontext = null;
		try {
			loggercontext = (LoggerContext) LoggerFactory.getILoggerFactory();

			final JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(loggercontext);
			loggercontext.reset();

			configurator.doConfigure(getDocBase() + LOGBACK_CONFIG);
		} catch (final JoranException aException) {
			// StatusPrinter will handle this
		}

		StatusPrinter.printInCaseOfErrorsOrWarnings(loggercontext);
	}
}
