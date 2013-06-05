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
import com.sun.syndication.feed.module.mediarss.MediaEntryModule;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.module.mediarss.types.*;
import com.sun.syndication.io.ModuleGenerator;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.HashSet;
import java.util.Set;

/**
 * The Class MediaModuleGenerator.
 */
public class MediaModuleGenerator implements ModuleGenerator {
    private static final Namespace NS = Namespace.getNamespace("media", MediaModule.URI);
    private static final Set<Namespace> NAMESPACES = new HashSet<>();

    static {
        NAMESPACES.add(NS);
    }

    @Override
    public String getNamespaceUri() {
        return MediaModule.URI;
    }

    @Override
    public Set<Namespace> getNamespaces() {
        return NAMESPACES;
    }

    @Override
    public void generate(final Module module, final Element element) {
        if (module instanceof MediaModule) {
            MediaModule m = (MediaModule) module;
            this.generateMetadata(m.getMetadata(), element);
            this.generatePlayer(m.getPlayer(), element);
        }

        if (module instanceof MediaEntryModule) {
            MediaEntryModule m = (MediaEntryModule) module;
            MediaGroup[] g = m.getMediaGroups();

            for (MediaGroup aG : g) {
                this.generateGroup(aG, element);
            }

            MediaContent[] c = m.getMediaContents();

            for (MediaContent aC : c) {
                this.generateContent(aC, element);
            }
        }
    }

    /**
     * Generate content.
     *
     * @param content the content
     * @param element the element
     */
    public void generateContent(final MediaContent content, final Element element) {
        Element mc = new Element("content", NS);
        this.addNotNullAttribute(mc, "channels", content.getAudioChannels());
        this.addNotNullAttribute(mc, "bitrate", content.getBitrate());
        this.addNotNullAttribute(mc, "duration", content.getDuration());
        this.addNotNullAttribute(mc, "expression", content.getExpression());
        this.addNotNullAttribute(mc, "fileSize", content.getFileSize());
        this.addNotNullAttribute(mc, "framerate", content.getFramerate());
        this.addNotNullAttribute(mc, "height", content.getHeight());
        this.addNotNullAttribute(mc, "lang", content.getLanguage());
        this.addNotNullAttribute(mc, "samplingrate", content.getSamplingrate());
        this.addNotNullAttribute(mc, "type", content.getType());
        this.addNotNullAttribute(mc, "width", content.getWidth());

        if (content.isDefaultContent()) {
            this.addNotNullAttribute(mc, "isDefault", "true");
        }

        if (content.getReference() instanceof UrlReference) {
            this.addNotNullAttribute(mc, "url", content.getReference());
            this.generatePlayer(content.getPlayer(), mc);
        } else {
            this.generatePlayer(content.getPlayer(), mc);
        }

        this.generateMetadata(content.getMetadata(), mc);
        element.addContent(mc);
    }

    /**
     * Generate group.
     *
     * @param group   the group
     * @param element the element
     */
    public void generateGroup(final MediaGroup group, final Element element) {
        Element t = new Element("group", NS);
        MediaContent[] c = group.getContents();

        for (MediaContent aC : c) {
            this.generateContent(aC, t);
        }

        this.generateMetadata(group.getMetadata(), t);
        element.addContent(t);
    }

    /**
     * Generate metadata.
     *
     * @param metadata the metadata
     * @param element  the element
     */
    public void generateMetadata(final Metadata metadata, final Element element) {
        if (metadata == null) {
            return;
        }

        Category[] cats = metadata.getCategories();

        for (Category cat : cats) {
            Element c = generateSimpleElement("category", cat.getValue());
            this.addNotNullAttribute(c, "scheme", cat.getScheme());
            this.addNotNullAttribute(c, "label", cat.getLabel());
            element.addContent(c);
        }

        Element copyright = addNotNullElement(element, "copyright", metadata.getCopyright());
        this.addNotNullAttribute(copyright, "url", metadata.getCopyrightUrl());

        Credit[] creds = metadata.getCredits();

        for (Credit cred : creds) {
            Element c = generateSimpleElement("credit", cred.getName());
            this.addNotNullAttribute(c, "role", cred.getRole());
            this.addNotNullAttribute(c, "scheme", cred.getScheme());
            element.addContent(c);
        }

        Element desc = addNotNullElement(element, "description", metadata.getDescription());
        this.addNotNullAttribute(desc, "type", metadata.getDescriptionType());

        if (metadata.getHash() != null) {
            Element hash = this.addNotNullElement(element, "hash", metadata.getHash().getValue());
            this.addNotNullAttribute(hash, "algo", metadata.getHash().getAlgorithm());
        }

        String[] keywords = metadata.getKeywords();

        if (keywords.length > 0) {
            StringBuilder keyword = new StringBuilder();
            keyword.append(keywords[0]);

            for (int i = 1; i < keywords.length; i++) {
                keyword.append(", ").append(keywords[i]);
            }
            this.addNotNullElement(element, "keywords", keyword.toString());
        }

        Rating[] rats = metadata.getRatings();

        for (Rating rat1 : rats) {
            Element rat = this.addNotNullElement(element, "rating", rat1.getValue());
            this.addNotNullAttribute(rat, "scheme", rat1.getScheme());

            if (rat1.equals(Rating.ADULT)) {
                this.addNotNullElement(element, "adult", "true");
            } else if (rat1.equals(Rating.NONADULT)) {
                this.addNotNullElement(element, "adult", "false");
            }
        }

        Text[] text = metadata.getText();

        for (Text aText : text) {
            Element t = this.addNotNullElement(element, "text", aText.getValue());
            this.addNotNullAttribute(t, "type", aText.getType());
            this.addNotNullAttribute(t, "start", aText.getStart());
            this.addNotNullAttribute(t, "end", aText.getEnd());
        }

        Thumbnail[] thumbs = metadata.getThumbnail();

        for (Thumbnail thumb : thumbs) {
            Element t = new Element("thumbnail", NS);
            this.addNotNullAttribute(t, "url", thumb.getUrl());
            this.addNotNullAttribute(t, "width", thumb.getWidth());
            this.addNotNullAttribute(t, "height", thumb.getHeight());
            this.addNotNullAttribute(t, "time", thumb.getTime());
            element.addContent(t);
        }

        Element title = this.addNotNullElement(element, "title", metadata.getTitle());
        this.addNotNullAttribute(title, "type", metadata.getTitleType());

        Restriction[] r = metadata.getRestrictions();

        for (Restriction aR : r) {
            Element res = this.addNotNullElement(element, "restriction", aR.getValue());
            this.addNotNullAttribute(res, "type", aR.getType());
            this.addNotNullAttribute(res, "relationship", aR.getRelationship());
        }
    }

    /**
     * Generate player.
     *
     * @param playerReference the player reference
     * @param element         the element
     */
    public void generatePlayer(final PlayerReference playerReference, final Element element) {
        if (playerReference == null) {
            return;
        }

        Element t = new Element("player", NS);
        this.addNotNullAttribute(t, "url", playerReference.getUrl());
        this.addNotNullAttribute(t, "width", playerReference.getWidth());
        this.addNotNullAttribute(t, "height", playerReference.getHeight());
        element.addContent(t);
    }

    /**
     * Adds the not null attribute.
     *
     * @param target the target
     * @param name   the name
     * @param value  the value
     */
    protected void addNotNullAttribute(final Element target, final String name, final Object value) {
        if (target != null && value != null) target.setAttribute(name, value.toString());
    }

    /**
     * Adds the not null element.
     *
     * @param target the target
     * @param name   the name
     * @param value  the value
     * @return element
     */
    protected Element addNotNullElement(final Element target, final String name, final Object value) {
        if (value == null) {
            return null;
        } else {
            Element e = generateSimpleElement(name, value.toString());
            target.addContent(e);

            return e;
        }
    }

    /**
     * Generate simple element.
     *
     * @param name  the name
     * @param value the value
     * @return element
     */
    protected Element generateSimpleElement(final String name, final String value) {
        Element element = new Element(name, NS);
        element.addContent(value);

        return element;
    }
}
