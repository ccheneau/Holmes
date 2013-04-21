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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.mediarss.MediaEntryModuleImpl;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.module.mediarss.MediaModuleImpl;
import com.sun.syndication.feed.module.mediarss.types.Category;
import com.sun.syndication.feed.module.mediarss.types.Credit;
import com.sun.syndication.feed.module.mediarss.types.Expression;
import com.sun.syndication.feed.module.mediarss.types.Hash;
import com.sun.syndication.feed.module.mediarss.types.MediaContent;
import com.sun.syndication.feed.module.mediarss.types.MediaGroup;
import com.sun.syndication.feed.module.mediarss.types.Metadata;
import com.sun.syndication.feed.module.mediarss.types.PlayerReference;
import com.sun.syndication.feed.module.mediarss.types.Rating;
import com.sun.syndication.feed.module.mediarss.types.Restriction;
import com.sun.syndication.feed.module.mediarss.types.Text;
import com.sun.syndication.feed.module.mediarss.types.Thumbnail;
import com.sun.syndication.feed.module.mediarss.types.Time;
import com.sun.syndication.feed.module.mediarss.types.UrlReference;
import com.sun.syndication.io.ModuleParser;
import com.sun.syndication.io.impl.NumberParser;

/**
 * @author Nathanial X. Freitas
 *
 */
public class MediaModuleParser implements ModuleParser {
    private static final Logger LOGGER = Logger.getLogger(MediaModuleParser.class.getName());

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
        MediaModuleImpl mod = null;

        if (mmRoot.getName().equals("channel") || mmRoot.getName().equals("feed")) {
            mod = new MediaModuleImpl();
        } else {
            mod = new MediaEntryModuleImpl();
        }

        mod.setMetadata(parseMetadata(mmRoot));
        mod.setPlayer(parsePlayer(mmRoot));

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
        ArrayList<MediaContent> values = new ArrayList<MediaContent>();

        for (int i = 0; contents != null && i < contents.size(); i++) {
            Element content = (Element) contents.get(i);
            MediaContent mc = null;

            if (content.getAttributeValue("url") != null) {
                try {
                    mc = new MediaContent(new UrlReference(new URI(content.getAttributeValue("url"))));
                    mc.setPlayer(parsePlayer(content));
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Exception parsing content tag.", ex);
                }
            } else {
                mc = new MediaContent(parsePlayer(content));
            }
            if (mc != null) {
                values.add(mc);
                try {
                    mc.setAudioChannels((content.getAttributeValue("channels") == null) ? null : new Integer(content.getAttributeValue("channels")));
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Exception parsing content tag.", ex);
                }
                try {
                    mc.setBitrate((content.getAttributeValue("bitrate") == null) ? null : new Float(content.getAttributeValue("bitrate")));
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Exception parsing content tag.", ex);
                }
                try {
                    mc.setDuration((content.getAttributeValue("duration") == null) ? null : new Long(content.getAttributeValue("duration")));
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Exception parsing content tag.", ex);
                }

                String expression = content.getAttributeValue("expression");

                if (expression != null) {
                    if (expression.equalsIgnoreCase("full")) {
                        mc.setExpression(Expression.FULL);
                    } else if (expression.equalsIgnoreCase("sample")) {
                        mc.setExpression(Expression.SAMPLE);
                    } else if (expression.equalsIgnoreCase("nonstop")) {
                        mc.setExpression(Expression.NONSTOP);
                    }
                }

                try {
                    mc.setFileSize((content.getAttributeValue("fileSize") == null) ? null : NumberParser.parseLong(content.getAttributeValue("fileSize")));
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Exception parsing content tag.", ex);
                }
                try {
                    mc.setFramerate((content.getAttributeValue("framerate") == null) ? null : NumberParser.parseFloat(content.getAttributeValue("framerate")));
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Exception parsing content tag.", ex);
                }
                try {
                    mc.setHeight((content.getAttributeValue("height") == null) ? null : NumberParser.parseInt(content.getAttributeValue("height")));
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Exception parsing content tag.", ex);
                }

                mc.setLanguage(content.getAttributeValue("lang"));
                mc.setMetadata(parseMetadata(content));
                try {
                    mc.setSamplingrate((content.getAttributeValue("samplingrate") == null) ? null : NumberParser.parseFloat(content
                            .getAttributeValue("samplingrate")));
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Exception parsing content tag.", ex);
                }

                mc.setType(content.getAttributeValue("type"));
                try {
                    mc.setWidth((content.getAttributeValue("width") == null) ? null : NumberParser.parseInt(content.getAttributeValue("width")));
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Exception parsing content tag.", ex);
                }

                mc.setDefaultContent((content.getAttributeValue("isDefault") == null) ? false : Boolean.getBoolean(content.getAttributeValue("isDefault")));
            } else {
                LOGGER.log(Level.WARNING, "Could not find MediaContent.");
            }

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
        ArrayList<MediaGroup> values = new ArrayList<MediaGroup>();

        for (int i = 0; groups != null && i < groups.size(); i++) {
            Element group = (Element) groups.get(i);
            MediaGroup g = new MediaGroup(parseContent(group));

            for (int j = 0; j < g.getContents().length; j++) {
                if (g.getContents()[j].isDefaultContent()) {
                    g.setDefaultContentIndex(Integer.valueOf(j));

                    break;
                }
            }

            g.setMetadata(parseMetadata(group));
            values.add(g);
        }

        return values.toArray(new MediaGroup[values.size()]);
    }

    /**
     * Parses the metadata.
     *
     * @param e the e
     * @return metadata
     */
    private Metadata parseMetadata(final Element e) {
        Metadata md = new Metadata();
        // categories
        List<?> categories = e.getChildren("category", NS);
        List<Category> catValues = new ArrayList<Category>();

        for (int i = 0; categories != null && i < categories.size(); i++) {
            try {
                Element cat = (Element) categories.get(i);
                catValues.add(new Category(cat.getAttributeValue("scheme"), cat.getAttributeValue("label"), cat.getText()));
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Exception parsing category tag.", ex);
            }
        }
        md.setCategories(catValues.toArray(new Category[catValues.size()]));

        // copyright
        try {
            Element copy = e.getChild("copyright", NS);
            if (copy != null) {
                md.setCopyright(copy.getText());
                md.setCopyrightUrl((copy.getAttributeValue("url") != null) ? new URI(copy.getAttributeValue("url")) : null);
            }
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.WARNING, "Exception parsing copyright tag.", ex);
        }

        // credits
        List<?> credits = e.getChildren("credit", NS);
        ArrayList<Credit> crValues = new ArrayList<Credit>();
        for (int i = 0; credits != null && i < credits.size(); i++) {
            Element cred = (Element) credits.get(i);
            crValues.add(new Credit(cred.getAttributeValue("scheme"), cred.getAttributeValue("role"), cred.getText()));
            md.setCredits(crValues.toArray(new Credit[crValues.size()]));
        }

        // description
        Element description = e.getChild("description", NS);
        if (description != null) {
            md.setDescription(description.getText());
            md.setDescriptionType(description.getAttributeValue("type"));
        }

        // hash
        Element hash = e.getChild("hash", NS);
        if (hash != null) md.setHash(new Hash(hash.getAttributeValue("algo"), hash.getText()));

        // keywords
        Element keywords = e.getChild("keywords", NS);
        if (keywords != null) {
            StringTokenizer tok = new StringTokenizer(keywords.getText(), ",");
            String[] value = new String[tok.countTokens()];
            for (int i = 0; tok.hasMoreTokens(); i++) {
                value[i] = tok.nextToken().trim();
            }
            md.setKeywords(value);
        }

        // ratings
        List<?> ratings = e.getChildren("rating", NS);
        List<Rating> ratValues = new ArrayList<Rating>();
        for (int i = 0; ratings != null && i < ratings.size(); i++) {
            Element rat = (Element) ratings.get(i);
            if (rat.getText() != null && rat.getAttributeValue("scheme") != null) ratValues.add(new Rating(rat.getAttributeValue("scheme"), rat.getText()));
        }
        md.setRatings(ratValues.toArray(new Rating[ratValues.size()]));

        // text
        List<?> texts = e.getChildren("text", NS);
        List<Text> txtValues = new ArrayList<Text>();

        for (int i = 0; texts != null && i < texts.size(); i++) {
            Element text = (Element) texts.get(i);
            Time start = (text.getAttributeValue("start") == null) ? null : new Time(text.getAttributeValue("start"));
            Time end = (text.getAttributeValue("end") == null) ? null : new Time(text.getAttributeValue("end"));
            txtValues.add(new Text(text.getAttributeValue("type"), text.getTextTrim(), start, end));
        }
        md.setText(txtValues.toArray(new Text[txtValues.size()]));

        // thumbnails
        List<?> thumbnails = e.getChildren("thumbnail", NS);
        List<Thumbnail> tbnValues = new ArrayList<Thumbnail>();
        for (int i = 0; thumbnails != null && i < thumbnails.size(); i++) {
            try {
                Element thumb = (Element) thumbnails.get(i);
                if (thumb.getValue().startsWith("http")) {
                    tbnValues.add(new Thumbnail(new URI(thumb.getValue()), null, null, null));
                } else {
                    Time t = (thumb.getAttributeValue("time") == null) ? null : new Time(thumb.getAttributeValue("time"));
                    Integer width = (thumb.getAttributeValue("width") == null) ? null : new Integer(thumb.getAttributeValue("width"));
                    Integer height = (thumb.getAttributeValue("height") == null) ? null : new Integer(thumb.getAttributeValue("height"));
                    tbnValues.add(new Thumbnail(new URI(thumb.getAttributeValue("url")), width, height, t));
                }
            } catch (URISyntaxException ex) {
                LOGGER.log(Level.WARNING, "Exception parsing thumbnail tag.", ex);
            }
        }

        md.setThumbnail(tbnValues.toArray(new Thumbnail[tbnValues.size()]));

        // title
        Element title = e.getChild("title", NS);
        if (title != null) {
            md.setTitle(title.getText());
            md.setTitleType(title.getAttributeValue("type"));
        }

        // restrictions
        List<?> restrictions = e.getChildren("restriction", NS);
        List<Restriction> rstValues = new ArrayList<Restriction>();
        for (int i = 0; i < restrictions.size(); i++) {
            Element r = (Element) restrictions.get(i);
            Restriction.Type type = null;

            if (r.getAttributeValue("type").equalsIgnoreCase("uri")) {
                type = Restriction.Type.URI;
            } else if (r.getAttributeValue("type").equalsIgnoreCase("country")) {
                type = Restriction.Type.COUNTRY;
            }

            Restriction.Relationship relationship = null;

            if (r.getAttributeValue("relationship").equalsIgnoreCase("allow")) {
                relationship = Restriction.Relationship.ALLOW;
            } else if (r.getAttributeValue("relationship").equalsIgnoreCase("deny")) {
                relationship = Restriction.Relationship.DENY;
            }

            Restriction value = new Restriction(relationship, type, r.getTextTrim());
            rstValues.add(value);
        }

        md.setRestrictions(rstValues.toArray(new Restriction[rstValues.size()]));

        // handle adult
        Element adult = e.getChild("adult", NS);
        if (adult != null && md.getRatings().length == 0) {
            Rating[] r = new Rating[1];
            if (adult.getTextTrim().equals("true")) {
                r[0] = new Rating("urn:simple", "adult");
            } else {
                r[0] = new Rating("urn:simple", "nonadult");
            }
            md.setRatings(r);
        }
        return md;
    }

    /**
     * Parses the player.
     *
     * @param element the element
     * @return player reference
     */
    private PlayerReference parsePlayer(final Element element) {
        Element player = element.getChild("player", NS);
        PlayerReference p = null;

        if (player != null) {
            Integer width = (player.getAttributeValue("width") == null) ? null : new Integer(player.getAttributeValue("width"));
            Integer height = (player.getAttributeValue("height") == null) ? null : new Integer(player.getAttributeValue("height"));

            try {
                p = new PlayerReference(new URI(player.getAttributeValue("url")), width, height);
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Exception parsing player tag.", ex);
            }
        }

        return p;
    }
}
