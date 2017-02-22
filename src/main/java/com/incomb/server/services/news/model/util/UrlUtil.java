package com.incomb.server.services.news.model.util;

/**
 * Provides various methods to modify URLs
 */
public class UrlUtil {

    public static final String HTTP_PROTOCOL_REGEX = "^(http://|https://)";
    public static final String PROTOCOL_FREE_URL_PREFIX = "//";

    /**
     * @param url to strip the protocol from
     * @return the url stripped from all protocol information
     * i.e http:// and https://
     */
    public static String stripProtocolFromUrl(String url) {
        if (url != null){
            return url.replaceFirst(HTTP_PROTOCOL_REGEX, PROTOCOL_FREE_URL_PREFIX);
        }
        return null;
    }
}
