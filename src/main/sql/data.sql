-- --------------------------------------- --
--             InComb Data SQL             --
--                                         --
--   Please execute the schema.sql first.  --
-- --------------------------------------- --

-- Modules --
INSERT INTO incomb.module (id, name_key, image_path, template_path) VALUES(1, "modules.news", "", "");

-- Categories --
INSERT INTO incomb.category (id, module_id, name_key, image_path) VALUES(1, 1, "categories.news.switzerland", "");
INSERT INTO incomb.category (id, module_id, name_key, image_path) VALUES(2, 1, "categories.news.worldNews", "");
INSERT INTO incomb.category (id, module_id, name_key, image_path) VALUES(3, 1, "categories.news.sport", "");
INSERT INTO incomb.category (id, module_id, name_key, image_path) VALUES(4, 1, "categories.news.economy", "");
INSERT INTO incomb.category (id, module_id, name_key, image_path) VALUES(5, 1, "categories.news.digital", "");
INSERT INTO incomb.category (id, module_id, name_key, image_path) VALUES(6, 1, "categories.news.health", "");

-- Providers --
INSERT INTO incomb.provider (id, name, image_path, website) VALUES ( 1, "20 Minuten", "/img/providers/20min.png", "https://www.20min.ch");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES ( 2, "Blick", "/img/providers/blick.png", "https://www.blick.ch");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES ( 3, "Die Welt", "/img/providers/diewelt.png", "https://www.welt.de");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES ( 4, "Engadget", "/img/providers/engadget.png", "https://www.engadget.com/");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES ( 5, "Frankfurter Allgemeine Zeitung", "/img/providers/faz.png", "https://www.faz.net/");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES ( 6, "Golem", "/img/providers/golem.png", "https://www.golem.de/");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES ( 7, "Microsoft", "/img/providers/microsoft.png", "https://www.microsoft.com");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES ( 8, "New York Times", "/img/providers/newyorktimes.png", "https://www.nytimes.com/");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES ( 9, "NZZ", "/img/providers/nzz.png", "https://www.nzz.ch/");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES (10, "Stadt Bremerhaven", "/img/providers/stadtbremerhaven.png", "https://stadt-bremerhaven.de/");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES (11, "Süddeutsche Zeitung", "/img/providers/sueddeutsche.png", "https://www.sueddeutsche.de/");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES (12, "Tages-Anzeiger", "/img/providers/tagesanzeiger.png", "https://www.tagesanzeiger.ch/");
INSERT INTO incomb.provider (id, name, image_path, website, parser_class) VALUES (13, "The Guardian", "/img/providers/theguardian.png", "https://www.theguardian.com/", "com.incomb.server.content.parsing.rss.providerSpecific.GuardianRssContentParser");
INSERT INTO incomb.provider (id, name, image_path, website) VALUES (14, "The Verge", "/img/providers/theverge.png", "https://www.theverge.com/");
INSERT INTO incomb.provider (id, name, image_path, website, parser_class) VALUES (15, "Watson", "/img/providers/watson.png", "https://www.watson.ch/", "com.incomb.server.content.parsing.rss.AtomParser");

-- Content Sources --

-- 20 Minuten --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES ( 1, 1, 1, 300, 'https://www.20min.ch/rss/rss.tmpl?type=rubrik&get=2', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES ( 2, 1, 2, 300, 'https://www.20min.ch/rss/rss.tmpl?type=rubrik&get=3', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES ( 3, 1, 3, 300, 'https://www.20min.ch/rss/rss.tmpl?type=channel&get=9', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES ( 4, 1, 5, 300, 'https://www.20min.ch/rss/rss.tmpl?type=channel&get=10', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES ( 5, 1, 4, 300, 'https://www.20min.ch/rss/rss.tmpl?type=channel&get=8', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES ( 6, 1, 6, 300, 'https://www.20min.ch/rss/rss.tmpl?type=channel&get=25', "de");

-- Blick --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES ( 7, 2, 1, 600, 'https://www.blick.ch/news/schweiz/rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES ( 8, 2, 2, 600, 'https://www.blick.ch/news/ausland/rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES ( 9, 2, 3, 600, 'https://www.blick.ch/sport/rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (10, 2, 5, 600, 'https://www.blick.ch/life/digital/rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (11, 2, 4, 600, 'https://www.blick.ch/news/wirtschaft/rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (12, 2, 6, 600, 'https://www.blick.ch/life/rss', "de");

-- Die Welt --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (13, 3, 2, 600, 'https://www.welt.de/politik/ausland/?service=Rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (14, 3, 3, 600, 'https://www.welt.de/sport/?service=Rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (15, 3, 4, 600, 'https://www.welt.de/wirtschaft/?service=Rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (16, 3, 6, 600, 'https://www.welt.de/gesundheit/?service=Rss', "de");

-- Engadget --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (17, 4, 5, 600, 'https://www.engadget.com/rss.xml', "en");

-- Frankfurter Allgemeine Zeitung --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (18, 5, 2, 600, 'https://www.faz.net/rss/aktuell/politik/ausland/', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (19, 5, 3, 600, 'https://www.faz.net/rss/aktuell/sport/', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (20, 5, 4, 600, 'https://www.faz.net/rss/aktuell/wirtschaft/', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (21, 5, 6, 600, 'https://www.faz.net/rss/aktuell/gesellschaft/gesundheit/', "de");

-- Golem --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (22, 6, 5, 600, 'https://rss.golem.de/rss.php?feed=RSS2.0', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (23, 6, 4, 600, 'https://rss.golem.de/rss.php?tp=wirtschaft&feed=RSS2.0', "de");

-- Microsoft --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (24, 7, 5, 600, 'https://www.microsoft.com/germany/msdn/rss/aktuell.xml', "de");

-- New York Times --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (25, 8, 2, 600, 'https://rss.nytimes.com/services/xml/rss/nyt/World.xml', "en");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (26, 8, 3, 600, 'https://rss.nytimes.com/services/xml/rss/nyt/Sports.xml', "en");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (27, 8, 5, 600, 'https://rss.nytimes.com/services/xml/rss/nyt/Technology.xml', "en");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (28, 8, 4, 600, 'https://rss.nytimes.com/services/xml/rss/nyt/Business.xml', "en");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (29, 8, 6, 600, 'https://rss.nytimes.com/services/xml/rss/nyt/Health.xml', "en");

-- NZZ --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (30, 9, 1, 300, 'https://www.nzz.ch/schweiz.rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (31, 9, 2, 300, 'https://www.nzz.ch/international.rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (32, 9, 3, 300, 'https://www.nzz.ch/sport.rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (33, 9, 5, 300, 'https://www.nzz.ch/mehr/digital.rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (34, 9, 4, 300, 'https://www.nzz.ch/wirtschaft.rss', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (35, 9, 6, 300, 'https://www.nzz.ch/lebensart.rss', "de");

-- Stadt Bremerhaven --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (36,10, 5, 300, 'https://feeds.feedburner.com/stadt-bremerhaven/dqXM?format=xml', "de");

-- Süddeutsche Zeitung --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (37,11, 3, 300, 'https://rss.sueddeutsche.de/rss/Sport', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (38,11, 5, 300, 'https://rss.sueddeutsche.de/rss/Digital', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (39,11, 4, 300, 'https://rss.sueddeutsche.de/rss/Wirtschaft', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (40,11, 6, 300, 'https://rss.sueddeutsche.de/rss/gesundheit', "de");

-- Tages-Anzeiger --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (41,12, 1, 300, 'https://www.tagesanzeiger.ch/schweiz/rss.html', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (42,12, 2, 300, 'https://www.tagesanzeiger.ch/ausland/rss.html', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (43,12, 3, 300, 'https://www.tagesanzeiger.ch/sport/rss.html', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (44,12, 5, 300, 'https://www.tagesanzeiger.ch/digital/rss.html', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (45,12, 4, 300, 'https://www.tagesanzeiger.ch/wirtschaft/rss.html', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (46,12, 6, 300, 'https://www.tagesanzeiger.ch/leben/rss.html', "de");

-- The Guardian --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (47,13, 2, 300, 'https://www.theguardian.com/world/rss', "en");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (48,13, 3, 300, 'https://feeds.theguardian.com/theguardian/uk/sport/rss', "en");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (49,13, 5, 300, 'https://feeds.theguardian.com/theguardian/technology/rss', "en");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (50,13, 4, 300, 'https://www.theguardian.com/uk/business/rss', "en");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (51,13, 6, 300, 'https://feeds.theguardian.com/theguardian/lifeandstyle/rss', "en");

-- The Verge --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (52,14, 5, 300, 'https://www.theverge.com/rss/index.xml', "en");

-- Watson --
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (53,15, 1, 300, 'https://www.watson.ch/api/1.0/rss/index.xml?tag=Schweiz', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (54,15, 2, 300, 'https://www.watson.ch/api/1.0/rss/index.xml?tag=International', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (55,15, 3, 300, 'https://www.watson.ch/api/1.0/rss/index.xml?tag=Sport', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (56,15, 4, 300, 'https://www.watson.ch/api/1.0/rss/index.xml?tag=Wirtschaft', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (57,15, 5, 300, 'https://www.watson.ch/api/1.0/rss/index.xml?tag=Digital%20%26%20Games', "de");
INSERT INTO incomb.content_source (id, provider_id, category_id, `interval`, url, locale) VALUES (58,15, 6, 300, 'https://www.watson.ch/api/1.0/rss/index.xml?tag=Gesundheit', "de");

-- RSS Feed Content Source --
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (1);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (2);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (3);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (4);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (5);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (6);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (7);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (8);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (9);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (10);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (11);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (12);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (13);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (14);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (15);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (16);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (17);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (18);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (19);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (20);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (21);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (22);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (23);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (24);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (25);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (26);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (27);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (28);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (29);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (30);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (31);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (32);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (33);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (34);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (35);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (36);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (37);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (38);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (39);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (40);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (41);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (42);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (43);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (44);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (45);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (46);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (47);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (48);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (49);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (50);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (51);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (52);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (53);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (54);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (55);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (56);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (57);
INSERT INTO `rss_feed_content_source` (`content_source_id`) VALUES (58);
