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

package com.sun.syndication.feed.module.itunes.io;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.itunes.AbstractITunesObject;
import com.sun.syndication.feed.module.itunes.EntryInformationImpl;
import com.sun.syndication.feed.module.itunes.FeedInformationImpl;
import com.sun.syndication.feed.module.itunes.types.Category;
import com.sun.syndication.feed.module.itunes.types.Duration;
import com.sun.syndication.feed.module.itunes.types.Subcategory;
import com.sun.syndication.io.ModuleParser;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.10 $
 */
public class ITunesParser implements ModuleParser {
    private static final Logger LOGGER = Logger.getLogger(ITunesParser.class.getName());
    private final Namespace ns;

    /**
     * Creates a new instance of ITunesParser
     */
    public ITunesParser() {
        ns = Namespace.getNamespace(AbstractITunesObject.URI);
    }

    /**
     * Creates a new instance of ITunesParser.
     *
     * @param ns the namespace
     */
    public ITunesParser(final Namespace ns) {
        this.ns = ns;
    }

    @Override
    public String getNamespaceUri() {
        return AbstractITunesObject.URI;
    }

    @Override
    public Module parse(final Element element) {
        AbstractITunesObject module = null;

        if (element.getName().equals("channel")) {
            FeedInformationImpl feedInfo = new FeedInformationImpl();
            module = feedInfo;

            //Now I am going to get the channel specific tags
            Element owner = element.getChild("owner", ns);

            if (owner != null) {
                Element name = owner.getChild("name", ns);

                if (name != null) {
                    feedInfo.setOwnerName(name.getValue().trim());
                }

                Element email = owner.getChild("email", ns);

                if (email != null) {
                    feedInfo.setOwnerEmailAddress(email.getValue().trim());
                }
            }

            Element image = element.getChild("image", ns);

            if (image != null && image.getAttributeValue("href") != null) {
                try {
                    URL imageURL = new URL(image.getAttributeValue("href").trim());
                    feedInfo.setImage(imageURL);
                } catch (MalformedURLException e) {
                    LOGGER.finer("Malformed URL Exception reading itunes:image tag: " + image.getAttributeValue("href"));
                }
            }

            List<?> categories = element.getChildren("category", ns);
            for (Object category1 : categories) {
                Element category = (Element) category1;
                if (category != null && category.getAttribute("text") != null) {
                    Category cat = new Category();
                    cat.setName(category.getAttribute("text").getValue().trim());

                    Element subcategory = category.getChild("category", ns);

                    if (subcategory != null && subcategory.getAttribute("text") != null) {
                        Subcategory subcategory1 = new Subcategory();
                        subcategory1.setName(subcategory.getAttribute("text").getValue().trim());
                        cat.setSubcategory(subcategory1);
                    }

                    feedInfo.getCategories().add(cat);
                }
            }

        } else if (element.getName().equals("item")) {
            EntryInformationImpl entryInfo = new EntryInformationImpl();
            module = entryInfo;

            //Now I am going to get the item specific tags

            Element duration = element.getChild("duration", ns);

            if (duration != null && duration.getValue() != null) {
                Duration dur = new Duration(duration.getValue().trim());
                entryInfo.setDuration(dur);
            }
        }
        if (module != null) {
            //All these are common to both Channel and Item
            Element author = element.getChild("author", ns);

            if (author != null && author.getText() != null) {
                module.setAuthor(author.getText());
            }

            Element block = element.getChild("block", ns);

            if (block != null) {
                module.setBlock(true);
            }

            Element explicit = element.getChild("explicit", ns);

            if (explicit != null && explicit.getValue() != null && explicit.getValue().trim().equalsIgnoreCase("yes")) {
                module.setExplicit(true);
            }

            Element keywords = element.getChild("keywords", ns);

            if (keywords != null) {
                StringTokenizer tok = new StringTokenizer(getXmlInnerText(keywords).trim(), ",");
                String[] keywordsArray = new String[tok.countTokens()];

                for (int i = 0; tok.hasMoreTokens(); i++) {
                    keywordsArray[i] = tok.nextToken();
                }

                module.setKeywords(keywordsArray);
            }

            Element subtitle = element.getChild("subtitle", ns);

            if (subtitle != null) {
                module.setSubtitle(subtitle.getTextTrim());
            }

            Element summary = element.getChild("summary", ns);

            if (summary != null) {
                module.setSummary(summary.getTextTrim());
            }
        }

        return module;
    }

    /**
     * Gets the xml inner text.
     *
     * @param element the element
     * @return the xml inner text
     */
    private String getXmlInnerText(final Element element) {
        StringBuilder sb = new StringBuilder();
        XMLOutputter xo = new XMLOutputter();
        List<?> children = element.getContent();
        sb.append(xo.outputString(children));

        return sb.toString();
    }
}
