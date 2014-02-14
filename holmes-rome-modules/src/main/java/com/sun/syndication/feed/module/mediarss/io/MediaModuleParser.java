/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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
import com.sun.syndication.feed.module.mediarss.MediaModuleImpl;
import com.sun.syndication.feed.module.mediarss.types.Metadata;
import com.sun.syndication.feed.module.mediarss.types.Thumbnail;
import com.sun.syndication.io.ModuleParser;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.sun.syndication.feed.module.mediarss.MediaModule.URI;
import static org.jdom.Namespace.getNamespace;

/**
 * @author Nathanial X. Freitas
 */
public class MediaModuleParser implements ModuleParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaModuleParser.class);
    /**
     * Namespace instance for this URI.
     */
    private static final Namespace NS = getNamespace(URI);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNamespaceUri() {
        return URI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Module parse(final Element element) {
        MediaModuleImpl mediaModule = new MediaModuleImpl();
        mediaModule.setMetadata(parseMetadata(element));
        return mediaModule;
    }

    /**
     * Parses the metadata.
     *
     * @param element the element
     * @return metadata
     */
    private Metadata parseMetadata(final Element element) {
        Metadata metadata = new Metadata();

        // thumbnails
        List<?> thumbnails = element.getChildren("thumbnail", NS);
        for (Object thumbnail : thumbnails)
            try {
                Element thumb = (Element) thumbnail;
                if (thumb.getValue().toLowerCase().startsWith("http"))
                    metadata.addThumbnail(new Thumbnail(new URI(thumb.getValue())));
            } catch (URISyntaxException ex) {
                LOGGER.warn("Exception parsing thumbnail tag.", ex);
            }

        return metadata;
    }
}
