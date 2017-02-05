package com.incomb.server.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility for HTML modifications.
 */
public class HtmlUtil {
	
	/**
	 * Matches all break tags (<code><pre><br>, <br />, <br/>, ...</pre></code>)
	 */
	public static final Pattern BREAK_TAGS_PATTERN = Pattern.compile("<br[^>]*>", Pattern.MULTILINE);

	/**
	 * Matches all tags (starting with '<', ending with '>').
	 */
	public static final Pattern REMOVE_TAGS_PATTERN = Pattern.compile("<[^>]+>", Pattern.MULTILINE);

	/**
	 * Private constructor because the class contains only static helper methods.
	 * Does nothing.
	 */
	private HtmlUtil() {

	}

	/**
	 * Removes all tags from the given {@link String}.
	 * This method is null safe.
	 *
	 * @param content the {@link String} where the tags should be removed.
	 * @param preserveBreaks if true all break (<br />) tag will not removed.
	 * @return the modified {@link String}.
	 */
	public static String removeTags(final String content, final boolean preserveBreaks) {
		String returnValue = content;

		if(StringUtils.isNotEmpty(returnValue)) {
			if(preserveBreaks) {
				returnValue = BREAK_TAGS_PATTERN.matcher(returnValue).replaceAll("\n");
			}

			returnValue = REMOVE_TAGS_PATTERN.matcher(returnValue).replaceAll("");

			if(preserveBreaks) {
				returnValue = returnValue.replace("\n", "<br />");
			}
		}

		return returnValue;
	}
}
