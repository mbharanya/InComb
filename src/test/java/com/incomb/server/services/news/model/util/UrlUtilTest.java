package com.incomb.server.services.news.model.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by XMBomb on 15.02.2017.
 */
public class UrlUtilTest {
    @Test
    public void stripProtocolFromUrl() throws Exception {
        String httpsUrl =  UrlUtil.stripProtocolFromUrl("https://example.com");
        String httpUrl =  UrlUtil.stripProtocolFromUrl("http://example.com");

        String expected = "//example.com";
        Assert.assertEquals(expected, httpsUrl);
        Assert.assertEquals(expected, httpUrl);
        Assert.assertEquals(null, UrlUtil.stripProtocolFromUrl(null));
    }

}