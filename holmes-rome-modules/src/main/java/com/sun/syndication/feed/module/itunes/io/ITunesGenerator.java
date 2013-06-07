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
import com.sun.syndication.io.ModuleGenerator;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.3 $
 */
public class ITunesGenerator implements ModuleGenerator {
    private static final Set<Namespace> SET = new HashSet<>();
    private static final Namespace NS = Namespace.getNamespace(AbstractITunesObject.PREFIX, AbstractITunesObject.URI);

    static {
        SET.add(NS);
    }

    /**
     * Creates a new instance of ITunesGenerator
     */
    public ITunesGenerator() {
    }

    @Override
    public void generate(final Module module, final Element element) {
        Element root = element;

        while (root.getParent() != null && root.getParent() instanceof Element) {
            root = (Element) root.getParent();
        }

        root.addNamespaceDeclaration(NS);

        if (!(module instanceof AbstractITunesObject)) {
            return;
        }

        AbstractITunesObject itunes = (AbstractITunesObject) module;

        if (itunes instanceof FeedInformationImpl) {
            //Do Channel Specific Stuff.
            FeedInformationImpl info = (FeedInformationImpl) itunes;
            Element owner = this.generateSimpleElement("owner", "");
            Element email = this.generateSimpleElement("email", info.getOwnerEmailAddress());
            owner.addContent(email);

            Element name = this.generateSimpleElement("name", info.getOwnerName());
            owner.addContent(name);
            element.addContent(owner);

            if (info.getImage() != null) {
                Element image = this.generateSimpleElement("image", "");
                image.setAttribute("href", info.getImage().toExternalForm());
                element.addContent(image);
            }

            for (Category cat : info.getCategories()) {
                Element category = this.generateSimpleElement("category", "");
                category.setAttribute("text", cat.getName());

                if (cat.getSubcategory() != null) {
                    Element subcategory = this.generateSimpleElement("category", "");
                    subcategory.setAttribute("text", cat.getSubcategory().getName());
                    category.addContent(subcategory);
                }

                element.addContent(category);
            }
        } else if (itunes instanceof EntryInformationImpl) {
            EntryInformationImpl info = (EntryInformationImpl) itunes;

            if (info.getDuration() != null) {
                element.addContent(this.generateSimpleElement("duration", info.getDuration().toString()));
            }
        }

        if (itunes.getAuthor() != null) {
            element.addContent(this.generateSimpleElement("author", itunes.getAuthor()));
        }

        if (itunes.getBlock()) {
            element.addContent(this.generateSimpleElement("block", ""));
        }

        if (itunes.getExplicit()) {
            element.addContent(this.generateSimpleElement("explicit", "yes"));
        } else {
            element.addContent(this.generateSimpleElement("explicit", "no"));
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < itunes.getKeywords().length; i++) {
            if (i != 0) sb.append(", ");
            sb.append(itunes.getKeywords()[i]);
        }

        element.addContent(this.generateSimpleElement("keywords", sb.toString()));

        if (itunes.getSubtitle() != null) {
            element.addContent(this.generateSimpleElement("subtitle", itunes.getSubtitle()));
        }

        if (itunes.getSummary() != null) {
            element.addContent(this.generateSimpleElement("summary", itunes.getSummary()));
        }
    }

    /**
     * Returns the list of namespaces this module uses.
     *
     * @return set of Namespace objects.
     */
    @Override
    public Set<Namespace> getNamespaces() {
        return SET;
    }

    /**
     * Returns the namespace URI this module handles.
     *
     * @return Returns the namespace URI this module handles.
     */
    @Override
    public String getNamespaceUri() {
        return AbstractITunesObject.URI;
    }

    /**
     * Generate simple element.
     *
     * @param name  the name
     * @param value the value
     * @return element
     */
    private Element generateSimpleElement(final String name, final String value) {
        Element element = new Element(name, NS);
        element.addContent(value);
        return element;
    }
}
