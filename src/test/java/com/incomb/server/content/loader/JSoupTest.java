package com.incomb.server.content.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.utils.HtmlUtil;

public class JSoupTest {
	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JSoupTest.class);
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2292.0 Safari/537.36";
	
	@Test
	public void parseNzz() throws IOException{
		final String url = "http://www.nzz.ch/international/naher-osten-und-nordafrika/al-jazeera-reporter-peter-greste-kommt-frei-1.18473475";
		final Document doc = Jsoup.connect(url)
				.userAgent(USER_AGENT)
				.timeout(15000)
				.get();
		final Elements newsArticle = doc.select("#articleBodyText div");
		LOGGER.debug(newsArticle.html());
	}
	
	@Test
	public void parse20min() throws IOException{
		final String url = "http://www.20min.ch/schweiz/bern/story/Erdbeben-bringt-Bieler-um-den-Schlaf-30643190";
		final Document doc = Jsoup.connect(url)
				.userAgent(USER_AGENT)
				.timeout(15000)
				.get();
		final Elements newsArticle = doc.select(".story_text");
		LOGGER.debug(newsArticle.text());
	}
	
	@Test
	public void parseAnything() throws IOException{

		final String url = "http://www.pcgameshardware.de/Internet-Thema-34041/News/VPN-schuetzt-nicht-1149669";
		
		final Document doc = Jsoup.connect(url)
				.userAgent(USER_AGENT)
				.timeout(15000)
				.get();
		final Elements pTags = doc.getElementsByTag("p");
		int maxLength = 0;
		Element biggestElement = null;
		for (final Element pTag : pTags){
			if (pTag.text().length() > maxLength && !isIllegalStringInTag(pTag)){
				maxLength = pTag.text().length();
				biggestElement = pTag;
			}
		}
				
		final Elements parentElements = biggestElement.parent().getAllElements();
		
			
		final List<Element> validElements = new ArrayList<>();
		
		for(final Element parentElement : parentElements){
			if (!isIllegalStringInTag(parentElement)){
				validElements.add(parentElement);
			}
		}
		
		String mainText = "";
		
		for(final Element validElement : validElements){
			mainText += HtmlUtil.removeTags(validElement.text(), false)+"\n";
		}

		LOGGER.debug(mainText);
	}
	
	private boolean isIllegalStringInTag(final Element tag){
		final String[] illegalWords = {"advert", "werbung", "anzeige", "adsense"};
		
		if (tag == null){
			return false;
		}
		
		for (final String word : illegalWords) {
			final Attributes tagAttrs = tag.attributes();
			if (tagAttrs != null){
				for(final Attribute attr : tagAttrs){
					if(attr.toString().toLowerCase().contains(word.toLowerCase())){
						return true;
					}
				}
			}else{
				return false;
			}
		}
		return false;
	}
}
