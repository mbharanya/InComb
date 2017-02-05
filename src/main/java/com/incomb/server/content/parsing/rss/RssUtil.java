package com.incomb.server.content.parsing.rss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains helper methods for parsing RSS feeds.
 */
public class RssUtil {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RssUtil.class);

	public static final String RFC_2822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

	private RssUtil() {

	}

	/**
	 * Returns the rfc2822Date for the given String.
	 * @param rfc2822Date - Date to parse
	 * @return if the rfc2822Date is not valid, <code>null</code> will be returned.
	 */
	public static Date get2822Date(final String rfc2822Date) {
		final SimpleDateFormat dateFormatter = new SimpleDateFormat(RFC_2822_DATE_FORMAT, Locale.ENGLISH);

		try {
			return dateFormatter.parse(rfc2822Date);
		} catch (final ParseException e) {
			LOGGER.warn("Can't parse date '{}' of a rss news.", rfc2822Date);
		}

		return null;
	}

	public static Date get3339Date(String rfc3339Date) {
		Date date = null;

		try {
			// if there is no time zone, we don't need to do any special parsing.
			if (rfc3339Date.endsWith("Z")) {
				try {
					// spec for RFC3339
					final SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
					date = s.parse(rfc3339Date);
				} catch (final java.text.ParseException pe) { // try again with optional decimals
					// spec for RFC3339 (with fractional seconds)
					final SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
					s.setLenient(true);
					date = s.parse(rfc3339Date);
				}

				return date;
			}

			// step one, split off the timezone.
			final String firstpart = rfc3339Date.substring(0, rfc3339Date.lastIndexOf('-'));
			String secondpart = rfc3339Date.substring(rfc3339Date.lastIndexOf('-'));

			// step two, remove the colon from the timezone offset
			secondpart = secondpart.substring(0, secondpart.indexOf(':'))
					+ secondpart.substring(secondpart.indexOf(':') + 1);
			rfc3339Date = firstpart + secondpart;

			// spec for RFC3339
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			try {
				date = s.parse(rfc3339Date);
			} catch (final java.text.ParseException pe) {// try again with optional decimals
				// spec for RFC3339 (with fractional seconds)
				s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
				s.setLenient(true);
				date = s.parse(rfc3339Date);
			}
		}
		catch(final ParseException e) {
			// return nothing
		}

		return date;
	}
}
