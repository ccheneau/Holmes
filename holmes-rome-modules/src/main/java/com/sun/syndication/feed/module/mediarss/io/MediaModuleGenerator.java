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

import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.mediarss.MediaEntryModule;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.module.mediarss.types.Category;
import com.sun.syndication.feed.module.mediarss.types.Credit;
import com.sun.syndication.feed.module.mediarss.types.MediaContent;
import com.sun.syndication.feed.module.mediarss.types.MediaGroup;
import com.sun.syndication.feed.module.mediarss.types.Metadata;
import com.sun.syndication.feed.module.mediarss.types.PlayerReference;
import com.sun.syndication.feed.module.mediarss.types.Rating;
import com.sun.syndication.feed.module.mediarss.types.Restriction;
import com.sun.syndication.feed.module.mediarss.types.Text;
import com.sun.syndication.feed.module.mediarss.types.Thumbnail;
import com.sun.syndication.feed.module.mediarss.types.UrlReference;
import com.sun.syndication.io.ModuleGenerator;

/**
 * The Class MediaModuleGenerator.
 */
public class MediaModuleGenerator implements ModuleGenerator {
    private static final Namespace NS = Namespace.getNamespace("media", MediaModule.URI);
    private static final Set<Namespace> NAMESPACES = new HashSet<Namespace>();

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

            for (int i = 0; i < g.length; i++) {
                this.generateGroup(g[i], element);
            }

            MediaContent[] c = m.getMediaContents();

            for (int i = 0; i < c.length; i++) {
                this.generateContent(c[i], element);
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
     * @param group the group
     * @param element the element
     */
    public void generateGroup(final MediaGroup group, final Element element) {
        Element t = new Element("group", NS);
        MediaContent[] c = group.getContents();

        for (int i = 0; i < c.length; i++) {
            this.generateContent(c[i], t);
        }

        this.generateMetadata(group.getMetadata(), t);
        element.addContent(t);
    }

    /**
     * Generate metadata.
     *
     * @param metadate the metadate
     * @param element the element
     */
    public void generateMetadata(final Metadata metadate, final Element element) {
        if (metadate == null) {
            return;
        }

        Category[] cats = metadate.getCategories();

        for (int i = 0; i < cats.length; i++) {
            Element c = generateSimpleElement("category", cats[i].getValue());
            this.addNotNullAttribute(c, "scheme", cats[i].getScheme());
            this.addNotNullAttribute(c, "label", cats[i].getLabel());
            element.addContent(c);
        }

        Element copyright = addNotNullElement(element, "copyright", metadate.getCopyright());
        this.addNotNullAttribute(copyright, "url", metadate.getCopyrightUrl());

        Credit[] creds = metadate.getCredits();

        for (int i = 0; i < creds.length; i++) {
            Element c = generateSimpleElement("credit", creds[i].getName());
            this.addNotNullAttribute(c, "role", creds[i].getRole());
            this.addNotNullAttribute(c, "scheme", creds[i].getScheme());
            element.addContent(c);
        }

        Element desc = addNotNullElement(element, "description", metadate.getDescription());
        this.addNotNullAttribute(desc, "type", metadate.getDescriptionType());

        if (metadate.getHash() != null) {
            Element hash = this.addNotNullElement(element, "hash", metadate.getHash().getValue());
            this.addNotNullAttribute(hash, "algo", metadate.getHash().getAlgorithm());
        }

        String[] keywords = metadate.getKeywords();

        if (keywords.length > 0) {
            StringBuilder keyword = new StringBuilder();
            keyword.append(keywords[0]);

            for (int i = 1; i < keywords.length; i++) {
                keyword.append(", ").append(keywords[i]);
            }
            this.addNotNullElement(element, "keywords", keyword.toString());
        }

        Rating[] rats = metadate.getRatings();

        for (int i = 0; i < rats.length; i++) {
            Element rat = this.addNotNullElement(element, "rating", rats[i].getValue());
            this.addNotNullAttribute(rat, "scheme", rats[i].getScheme());

            if (rats[i].equals(Rating.ADULT)) {
                this.addNotNullElement(element, "adult", "true");
            } else if (rats[i].equals(Rating.NONADULT)) {
                this.addNotNullElement(element, "adult", "false");
            }
        }

        Text[] text = metadate.getText();

        for (int i = 0; i < text.length; i++) {
            Element t = this.addNotNullElement(element, "text", text[i].getValue());
            this.addNotNullAttribute(t, "type", text[i].getType());
            this.addNotNullAttribute(t, "start", text[i].getStart());
            this.addNotNullAttribute(t, "end", text[i].getEnd());
        }

        Thumbnail[] thumbs = metadate.getThumbnail();

        for (int i = 0; i < thumbs.length; i++) {
            Element t = new Element("thumbnail", NS);
            this.addNotNullAttribute(t, "url", thumbs[i].getUrl());
            this.addNotNullAttribute(t, "width", thumbs[i].getWidth());
            this.addNotNullAttribute(t, "height", thumbs[i].getHeight());
            this.addNotNullAttribute(t, "time", thumbs[i].getTime());
            element.addContent(t);
        }

        Element title = this.addNotNullElement(element, "title", metadate.getTitle());
        this.addNotNullAttribute(title, "type", metadate.getTitleType());

        Restriction[] r = metadate.getRestrictions();

        for (int i = 0; i < r.length; i++) {
            Element res = this.addNotNullElement(element, "restriction", r[i].getValue());
            this.addNotNullAttribute(res, "type", r[i].getType());
            this.addNotNullAttribute(res, "relationship", r[i].getRelationship());
        }
    }

    /**
     * Generate player.
     *
     * @param playerReference the player reference
     * @param element the element
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
     * @param name the name
     * @param value the value
     */
    protected void addNotNullAttribute(final Element target, final String name, final Object value) {
        if (target == null || value == null) {
            return;
        } else {
            target.setAttribute(name, value.toString());
        }
    }

    /**
     * Adds the not null element.
     *
     * @param target the target
     * @param name the name
     * @param value the value
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
     * @param name the name
     * @param value the value
     * @return element
     */
    protected Element generateSimpleElement(final String name, final String value) {
        Element element = new Element(name, NS);
        element.addContent(value);

        return element;
    }
}
