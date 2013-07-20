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
import com.sun.syndication.feed.module.mediarss.MediaEntryModuleImpl;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.module.mediarss.MediaModuleImpl;
import com.sun.syndication.feed.module.mediarss.types.*;
import com.sun.syndication.io.ModuleParser;
import com.sun.syndication.io.impl.NumberParser;
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
        MediaModuleImpl mod;

        if (mmRoot.getName().equals("channel") || mmRoot.getName().equals("feed"))
            mod = new MediaModuleImpl();
        else
            mod = new MediaEntryModuleImpl();

        mod.setMetadata(parseMetadata(mmRoot));

        if (mod instanceof MediaEntryModuleImpl) {
            MediaEntryModuleImpl m = (MediaEntryModuleImpl) mod;
            m.setMediaContents(parseContent(mmRoot));
            m.setMediaGroups(parseGroup(mmRoot));
        }

        return mod;
    }

    /**
     * Parses the content.
     *
     * @param element the element
     * @return media content[]
     */
    private MediaContent[] parseContent(final Element element) {
        List<?> contents = element.getChildren("content", NS);
        List<MediaContent> values = new ArrayList<>();

        for (int i = 0; contents != null && i < contents.size(); i++) {
            Element content = (Element) contents.get(i);
            MediaContent mc = null;

            if (content.getAttributeValue("url") != null)
                try {
                    mc = new MediaContent(new UrlReference(new URI(content.getAttributeValue("url"))));
                } catch (Exception ex) {
                    LOGGER.warn("Exception parsing content tag.", ex);
                }

            if (mc != null) {
                values.add(mc);
                try {
                    mc.setAudioChannels((content.getAttributeValue("channels") == null) ? null : Integer.valueOf(content.getAttributeValue("channels")));
                    mc.setBitrate((content.getAttributeValue("bitrate") == null) ? null : Float.valueOf(content.getAttributeValue("bitrate")));
                    mc.setDuration((content.getAttributeValue("duration") == null) ? null : Long.valueOf(content.getAttributeValue("duration")));
                } catch (Exception ex) {
                    LOGGER.warn("Exception parsing content tag.", ex);
                }

                String expression = content.getAttributeValue("expression");

                if (expression != null) {
                    if (expression.equalsIgnoreCase("full"))
                        mc.setExpression(Expression.FULL);
                    else if (expression.equalsIgnoreCase("sample"))
                        mc.setExpression(Expression.SAMPLE);
                    else if (expression.equalsIgnoreCase("nonstop"))
                        mc.setExpression(Expression.NONSTOP);
                }

                mc.setFileSize((content.getAttributeValue("fileSize") == null) ? null : NumberParser.parseLong(content.getAttributeValue("fileSize")));
                mc.setFramerate((content.getAttributeValue("framerate") == null) ? null : NumberParser.parseFloat(content.getAttributeValue("framerate")));
                mc.setHeight((content.getAttributeValue("height") == null) ? null : NumberParser.parseInt(content.getAttributeValue("height")));
                mc.setLanguage(content.getAttributeValue("lang"));
                mc.setMetadata(parseMetadata(content));
                mc.setSamplingrate((content.getAttributeValue("samplingrate") == null) ? null : NumberParser.parseFloat(content.getAttributeValue("samplingrate")));

                mc.setType(content.getAttributeValue("type"));
                mc.setWidth((content.getAttributeValue("width") == null) ? null : NumberParser.parseInt(content.getAttributeValue("width")));

                mc.setDefaultContent((content.getAttributeValue("isDefault") == null) ? false : Boolean.valueOf(content.getAttributeValue("isDefault")));
            } else
                LOGGER.warn("Could not find MediaContent.");

        }
        return values.toArray(new MediaContent[values.size()]);
    }

    /**
     * Parses the group.
     *
     * @param element the element
     * @return media group[]
     */
    private MediaGroup[] parseGroup(final Element element) {
        List<?> groups = element.getChildren("group", NS);
        List<MediaGroup> values = new ArrayList<>();

        for (int i = 0; groups != null && i < groups.size(); i++) {
            Element group = (Element) groups.get(i);
            MediaGroup g = new MediaGroup(parseContent(group));

            for (int j = 0; j < g.getContents().length; j++)
                if (g.getContents()[j].isDefaultContent()) {
                    g.setDefaultContentIndex(j);
                    break;
                }

            g.setMetadata(parseMetadata(group));
            values.add(g);
        }

        return values.toArray(new MediaGroup[values.size()]);
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
