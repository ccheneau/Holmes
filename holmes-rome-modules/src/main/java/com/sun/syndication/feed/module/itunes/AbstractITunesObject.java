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

package com.sun.syndication.feed.module.itunes;

/**
 * This is an abstract object that implements the attributes common across Feeds
 * or Items in an iTunes compatible RSS feed.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.4 $
 */
public abstract class AbstractITunesObject implements ITunes, Cloneable {
    private static final long serialVersionUID = -1507584666860485534L;

    /**
     * The URI that iTunes used for its custom tags.
     * <p>What is up with using a versioned DTD anyway?</p>\
     */
    public static final String URI = "http://www.itunes.com/dtds/podcast-1.0.dtd";

    /**
     * The RDF namespace URI.
     */
    public static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * A default prefix to use for iTunes tags.
     */
    public static final String PREFIX = "itunes";
    private String author;
    private boolean block;
    private boolean explicit;
    private String[] keywords;
    private String subtitle;
    private String summary;

    /**
     * Constructor.
     */
    AbstractITunesObject() {
        keywords = null;
    }

    /**
     * Defined by the ROME module API
     *
     * @param obj Object to copy from
     */
    @Override
    public abstract void copyFrom(Object obj);

    /**
     * Defined by the ROME API
     *
     * @return Class of the Interface for this module.
     */
    @Override
    public Class<?> getInterface() {
        return getClass();
    }

    /**
     * The URI this module implements
     *
     * @return "http://www.itunes.com/dtds/podcast-1.0.dtd"
     */
    @Override
    public String getUri() {
        return AbstractITunesObject.URI;
    }

    /**
     * Required by the ROME API
     *
     * @return A clone of this module object
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Returns the author string for this feed or entry
     *
     * @return Returns the author string for this feed or entry
     */
    @Override
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author string for this feed or entry
     *
     * @param author Sets the author string for this feed or entry
     */
    @Override
    public void setAuthor(final String author) {
        this.author = author;
    }

    /**
     * Boolean as to whether to block this feed or entry
     *
     * @return Boolean as to whether to block this feed or entry
     */
    @Override
    public boolean getBlock() {
        return block;
    }

    /**
     * Boolean as to whether to block this feed or entry
     *
     * @param block Boolean as to whether to block this feed or entry
     */
    @Override
    public void setBlock(final boolean block) {
        this.block = block;
    }

    /**
     * Boolean as to whether this feed or entry contains adult content
     *
     * @return Boolean as to whether this feed or entry contains adult content
     */
    @Override
    public boolean getExplicit() {
        return explicit;
    }

    /**
     * Boolean as to whether this feed or entry contains adult content
     *
     * @param explicit Boolean as to whether this feed or entry contains adult content
     */
    @Override
    public void setExplicit(final boolean explicit) {
        this.explicit = explicit;
    }

    /**
     * A list of keywords for this feed or entry
     * <p/>
     * Must not contain spaces
     *
     * @return A list of keywords for this feed or entry
     */
    @Override
    public String[] getKeywords() {
        return keywords == null ? new String[0] : keywords;
    }

    /**
     * A list of keywords for this feed or entry
     * <p/>
     * Must not contain spaces
     *
     * @param keywords A list of keywords for this feed or entry
     */
    @Override
    public void setKeywords(final String[] keywords) {
        this.keywords = keywords;
    }

    /**
     * A subtitle for this feed or entry
     *
     * @return A subtitle for this feed or entry
     */
    @Override
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * A subtitle for this feed or entry
     *
     * @param subtitle A subtitle for this feed or entry
     */
    @Override
    public void setSubtitle(final String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * A subtitle for this feed or entry
     *
     * @return A subtitle for this feed or entry
     */
    @Override
    public String getSummary() {
        return summary;
    }

    /**
     * A subtitle for this feed or entry
     *
     * @param summary A subtitle for this feed or entry
     */
    @Override
    public void setSummary(final String summary) {
        this.summary = summary;
    }
}
