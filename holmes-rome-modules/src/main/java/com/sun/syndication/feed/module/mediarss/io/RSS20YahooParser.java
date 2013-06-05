/*
 * Copyright (C) 2012-2013  Cedric Cheneau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sun.syndication.feed.module.mediarss.io;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.io.impl.RSS20Parser;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * The Class RSS20YahooParser.
 *
 * @author Nathanial X. Freitas
 */
public class RSS20YahooParser extends RSS20Parser {
    /**
     * the Yahoo Namespace URI they sometimes use in the returns for video.search.yahoo.com RSS feed.
     */
    private static final String RSS_URI = "urn:yahoo:yn";

    /**
     * Constructor.
     */
    public RSS20YahooParser() {
        this("rss_2.0yahoo");
    }

    /**
     * Constructor.
     *
     * @param type the type
     */
    protected RSS20YahooParser(final String type) {
        super(type);
    }

    /**
     * Indicates if a JDom document is an RSS instance that can be parsed with the parser.
     * <p/>
     * It checks for RDF ("http://www.w3.org/1999/02/22-rdf-syntax-ns#") and
     * RSS ("http://purl.org/rss/1.0/") namespaces being defined in the root element.
     *
     * @param document document to check if it can be parsed with this parser implementation.
     * @return <b>true</b> if the document is RSS1., <b>false</b> otherwise.
     */
    @Override
    public boolean isMyType(final Document document) {
        Element rssRoot = document.getRootElement();
        Namespace defaultNS = rssRoot.getNamespace();

        return defaultNS != null && defaultNS.equals(getRSSNamespace());
    }

    /**
     * Returns the namespace used by RSS elements in document of the RSS 1.0
     * <p/>
     *
     * @return returns "http://purl.org/rss/1.0/".
     */
    @Override
    protected Namespace getRSSNamespace() {
        return Namespace.getNamespace(RSS_URI);
    }

    /**
     * After we parse the feed we put "rss_2.0" in it (so converters and generators work)
     * this parser is a phantom.
     *
     * @param rssRoot the rss root
     * @return wire feed
     */
    @Override
    protected WireFeed parseChannel(final Element rssRoot) {
        WireFeed wFeed = super.parseChannel(rssRoot);
        wFeed.setFeedType("rss_2.0");

        return wFeed;
    }
}
