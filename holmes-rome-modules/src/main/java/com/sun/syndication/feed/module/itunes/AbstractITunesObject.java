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

package com.sun.syndication.feed.module.itunes;

/**
 * This is an abstract object that implements the attributes common across Feeds
 * or Items in an iTunes compatible RSS feed.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.4 $
 */
public abstract class AbstractITunesObject implements ITunes, Cloneable {
    /**
     * The URI that iTunes used for its custom tags.
     * <p>What is up with using a versioned DTD anyway?</p>\
     */
    public static final String URI = "http://www.itunes.com/dtds/podcast-1.0.dtd";

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
}
