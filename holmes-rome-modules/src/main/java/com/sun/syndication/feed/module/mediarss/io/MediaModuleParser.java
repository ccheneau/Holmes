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

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.module.mediarss.MediaModuleImpl;
import com.sun.syndication.feed.module.mediarss.types.Metadata;
import com.sun.syndication.feed.module.mediarss.types.Thumbnail;
import com.sun.syndication.feed.module.mediarss.types.Time;
import com.sun.syndication.io.ModuleParser;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nathanial X. Freitas
 */
public class MediaModuleParser implements ModuleParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaModuleParser.class);
    /**
     * Namespace instance for this URI.
     */
    private static final Namespace NS = Namespace.getNamespace(MediaModule.URI);

    /* (non-Javadoc)
     * @see com.sun.syndication.io.ModuleParser#getNamespaceUri()
     */
    @Override
    public String getNamespaceUri() {
        return MediaModule.URI;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.io.ModuleParser#parse(org.jdom.Element)
     */
    @Override
    public Module parse(final Element mmRoot) {
        MediaModuleImpl mod = new MediaModuleImpl();
        mod.setMetadata(parseMetadata(mmRoot));

        return mod;
    }

    /**
     * Parses the metadata.
     *
     * @param element the element
     * @return metadata
     */
    private Metadata parseMetadata(final Element element) {
        Metadata md = new Metadata();

        // thumbnails
        List<?> thumbnails = element.getChildren("thumbnail", NS);
        List<Thumbnail> tbnValues = new ArrayList<>();
        for (int i = 0; thumbnails != null && i < thumbnails.size(); i++) {
            try {
                Element thumb = (Element) thumbnails.get(i);
                if (thumb.getValue().startsWith("http"))
                    tbnValues.add(new Thumbnail(new URI(thumb.getValue()), null, null, null));
                else {
                    Time t = (thumb.getAttributeValue("time") == null) ? null : new Time(thumb.getAttributeValue("time"));
                    Integer width = (thumb.getAttributeValue("width") == null) ? null : Integer.valueOf(thumb.getAttributeValue("width"));
                    Integer height = (thumb.getAttributeValue("height") == null) ? null : Integer.valueOf(thumb.getAttributeValue("height"));
                    tbnValues.add(new Thumbnail(new URI(thumb.getAttributeValue("url")), width, height, t));
                }
            } catch (URISyntaxException | NumberFormatException ex) {
                LOGGER.warn("Exception parsing thumbnail tag.", ex);
            }
        }
        md.setThumbnail(tbnValues.toArray(new Thumbnail[tbnValues.size()]));

        return md;
    }
}
