/*
 * Copyright 2006 Nathanial X. Freitas, openvision.tv
 *
 * This code is currently released under the Mozilla Public License.
 * http://www.mozilla.org/MPL/
 *
 * Alternately you may apply the terms of the Apache Software License
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.feed.module.mediarss.io;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.io.impl.RSS20Parser;

/**
 * The Class RSS20YahooParser.
 *
 * @author Nathanial X. Freitas
 */
public class RSS20YahooParser extends RSS20Parser {
    /** the Yahoo Namespace URI they sometimes use in the returns for video.search.yahoo.com RSS feed. */
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
        boolean ok = false;

        Element rssRoot = document.getRootElement();
        Namespace defaultNS = rssRoot.getNamespace();

        ok = defaultNS != null && defaultNS.equals(getRSSNamespace());

        return ok;
    }

    /**
     * Returns the namespace used by RSS elements in document of the RSS 1.0
     * <P>
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
