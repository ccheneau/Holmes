/*
 * Copyright 2006 Marc Wick, geonames.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.feed.module.georss;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

/**
 * static utility methods for georss.
 *
 * @author Marc Wick
 * @version $Id: GeoRSSUtils.java,v 1.4 2007/04/18 09:59:29 marcwick Exp $
 *
 */
public final class GeoRSSUtils {

    /**
     * Constructor.
     */
    private GeoRSSUtils() {

    }

    /**
     * Trim whitespace.
     *
     * @param in the in
     * @return string
     */
    public static String trimWhitespace(final String in) {
        StringBuffer strbuf = new StringBuffer();
        int i = 0;
        while (i < in.length() && Character.isWhitespace(in.charAt(i)))
            i++;

        boolean wasWhite = false;
        for (; i < in.length(); ++i) {
            char ch = in.charAt(i);
            if (Character.isWhitespace(ch)) wasWhite = true;
            else {
                if (wasWhite) strbuf.append(' ');
                strbuf.append(ch);
                wasWhite = false;
            }
        }
        return strbuf.toString();
    }

    /**
     * This convenience method checks whether there is any geoRss Element and
     * will return it (georss simple or W3GGeo).
     *
     * @param entry
     *            the element in the feed which might have a georss element
     * @return a georssmodule or null if none is present
     */
    public static GeoRSSModule getGeoRSS(final SyndEntry entry) {
        GeoRSSModule simple = (GeoRSSModule) entry.getModule(GeoRSSModule.GEORSS_GEORSS_URI);
        GeoRSSModule w3cGeo = (GeoRSSModule) entry.getModule(GeoRSSModule.GEORSS_W3CGEO_URI);
        GeoRSSModule gml = (GeoRSSModule) entry.getModule(GeoRSSModule.GEORSS_GML_URI);
        if (gml != null) return gml;
        if (simple != null) return simple;
        if (w3cGeo != null) return w3cGeo;

        return null;
    }

    /**
     * This convenience method checks whether there is any geoRss Element and
     * will return it (georss simple or W3GGeo).
     *
     * @param feed
     *            the element in the feed which might have a georss element
     * @return a georssmodule or null if none is present
     */
    public static GeoRSSModule getGeoRSS(final SyndFeed feed) {
        GeoRSSModule simple = (GeoRSSModule) feed.getModule(GeoRSSModule.GEORSS_GEORSS_URI);
        GeoRSSModule w3cGeo = (GeoRSSModule) feed.getModule(GeoRSSModule.GEORSS_W3CGEO_URI);
        GeoRSSModule gml = (GeoRSSModule) feed.getModule(GeoRSSModule.GEORSS_GML_URI);

        if (gml != null) return gml;
        if (simple != null) return simple;
        if (w3cGeo != null) return w3cGeo;

        return null;
    }
}
