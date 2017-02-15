package com.incomb.server.services.news.model.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by XMBomb on 15.02.2017.
 */
public class CssUtilTest {
    @Test
    public void stripProtocolFromUrl() throws Exception {
        String httpsUrl =  CssUtil.stripProtocolFromUrl("https://example.com");
        String httpUrl =  CssUtil.stripProtocolFromUrl("http://example.com");

        String expected = "//example.com";
        Assert.assertEquals(expected, httpsUrl);
        Assert.assertEquals(expected, httpUrl);
        Assert.assertEquals(null, CssUtil.stripProtocolFromUrl(null));
    }

}