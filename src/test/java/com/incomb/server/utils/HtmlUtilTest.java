package com.incomb.server.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by XMBomb on 24.02.2017.
 */
public class HtmlUtilTest {

    @Test
    public void removeTags() throws Exception {
            String tooManyLineBreaksAtBeginningAndEnd = "\n\n\n\ncontent\n\n\n\n";
            String tooManyLineBreaksInContent = "content\n\n\n\nmoreContent";

            String tooManyHtmlBreakTagsAtBeginningAndEnd = "<br><br><br><br><br>content<br><br><br><br><br>";
            String tooManyHtmlBreakTagsInContent = "content<br><br><br>moreContent<br><br>";

            String tooManyHtml4BreakTagsAtBeginningAndEnd = "<br /><br /><br /><br /><br />content<br /><br /><br /><br /><br />";
            String tooManyHtml4BreakTagsInContent = "content<br /><br /><br />moreContent<br /><br />";

            String singleContent = "content";
            String multipleContents = "content<br />moreContent";

            Assert.assertEquals(singleContent, HtmlUtil.removeTags(tooManyLineBreaksAtBeginningAndEnd, true));
            Assert.assertEquals(singleContent, HtmlUtil.removeTags(tooManyHtmlBreakTagsAtBeginningAndEnd, true));
            Assert.assertEquals(singleContent, HtmlUtil.removeTags(tooManyHtml4BreakTagsAtBeginningAndEnd, true));

            Assert.assertEquals(multipleContents, HtmlUtil.removeTags(tooManyLineBreaksInContent, true));
            Assert.assertEquals(multipleContents, HtmlUtil.removeTags(tooManyHtmlBreakTagsInContent, true));
            Assert.assertEquals(multipleContents, HtmlUtil.removeTags(tooManyHtml4BreakTagsInContent, true));
    }
}