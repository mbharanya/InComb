package com.incomb.server.content.parsing.rss;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incomb.server.model.ContentSource;
import com.incomb.server.model.News;
import com.incomb.server.utils.HtmlUtil;

/**
 * This {@link RssItem} represents the item tag in a rss document.
 * All getters and setter are just a basic set and get if nothing other is mentioned.
 */
public class RssItem {

	/**
	 * <p>The {@link Logger} for this class.</p>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RssItem.class);

	private static final int IMAGE_CONNECT_TIMEOUT = 2000;

	private static final int IMAGE_READ_TIMEOUT = 30000;

	/**
	 * Parent element
	 */
	private RssDocument document;

	private String title;
	private String description;

	private String link;
	private String imageUrl;
	private Date   pubDate;
	private int    imageWidth;
	private int    imageHeight;

	private Date   updated;

	/**
	 * Creates a new {@link RssItem} with the {@link RssDocument} as parent object.
	 */
	public RssItem(final RssDocument doc) { document = doc; }

	/**
	 * Creates a new {@link News} object with its information.
	 * @return a new {@link News} object
	 */
	public News createContentObj() {
		final ContentSource src = document.getContentSource();

		if (StringUtils.isBlank(getImageUrl())) {
			setImageUrl(parseImage());
		}

		setImageSizes();
		filterDescription();

		final Timestamp publishDate = getPubDate() != null ? new Timestamp(getPubDate().getTime()) : null;
		return new News(0, src.getProviderId(), src.getCategoryId(), getTitle(), src.getLocale(), getDescription(),
				publishDate, getLink(), imageUrl, getImageWidth(), getImageHeight(), News.NEWSGROUPID_NOT_GROUPED_YET);
	}

	public String getTitle() { return title; }
	public void setTitle(final String title) { this.title = title; }

	public String getDescription() { return description; }
	public void setDescription(final String description) { this.description = description; }

	public RssDocument getDocument() { return document; }
	public void setDocument(final RssDocument document) { this.document = document; }

	public String getLink() { return link; }
	public void setLink(final String link) { this.link = link; }

	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(final String imageUrl) { this.imageUrl = imageUrl; }

	public Date getPubDate() { return pubDate; }
	public void setPubDate(final Date pubDate) { this.pubDate = pubDate; }

	public int getImageWidth() { return imageWidth; }
	public void setImageWidth(final String imageWidth) { this.imageWidth = NumberUtils.toInt(imageWidth); 	}

	public int getImageHeight() { return imageHeight; }
	public void setImageHeight(final String imageHeight) { this.imageHeight = NumberUtils.toInt(imageHeight); }

	public Date getUpdated() { return updated; }
	public void setUpdated(final Date updated) { this.updated = updated; }

	/**
	 * Tries to parse the image url out of the description. If it fails <code>null</code> will be returned.
	 * @return Image url in the description. <code>Null</code> if no description was found.
	 */
	private String parseImage() {
		final Document doc = Jsoup.parse(getDescription());
		final Elements imgs = doc.getElementsByTag("img");
		for (final Element img : imgs) {
			String src = img.attr("src");
			if(StringUtils.isNotBlank(src)) {

				if(src.startsWith("/")) {
					try {
						final URL feedUrl = new URL(document.getContentSource().getUrl());
						src = "//" + feedUrl.getHost() + src;
					} catch (final MalformedURLException e) {
						// next
						continue;
					}
				}

				return src;
			}
		}

		return null;
	}

	/**
	 * Calculates the image size.
	 */
	public void setImageSizes() {
		String url = getImageUrl();
		if(StringUtils.isNotBlank(imageUrl)) {
			if(url.startsWith("//")) {
				url = "http:" + url;
			}

			try {
				final URLConnection con = new URL(url).openConnection();
				con.setConnectTimeout(IMAGE_CONNECT_TIMEOUT);
				con.setReadTimeout(IMAGE_READ_TIMEOUT);

				final BufferedImage image = ImageIO.read(con.getInputStream());
				if (image != null) {
					imageWidth = image.getWidth();
					imageHeight = image.getHeight();
				}
			} catch (final IOException e) {
				LOGGER.info("Can't calculate image sizes for image {} of news.", getImageUrl(), e);
			}
		}
	}

	/**
	 * Trims the {@link #description} and removes all tags.
	 */
	public void filterDescription() {
		setDescription(HtmlUtil.removeTags(getDescription().trim(), true));
	}
}
