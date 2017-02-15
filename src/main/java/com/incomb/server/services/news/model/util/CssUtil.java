package com.incomb.server.services.news.model.util;

/**
 * Provides various methods to accommodate CSS quirks and features
 */
public class CssUtil {

    public static final String HTTP_PROTOCOL_REGEX = "^(http://|https://)";
    public static final String CSS_PROTOCOL_FREE_URL_PREFIX = "//";

    /**
     * @param url to strip the protocol from
     * @return the url stripped from all protocol information
     * i.e http:// and https://
     */
    public static String stripProtocolFromUrl(String url) {
        if (url != null){
            return url.replaceFirst(HTTP_PROTOCOL_REGEX, CSS_PROTOCOL_FREE_URL_PREFIX);
        }
        return null;
    }
}
