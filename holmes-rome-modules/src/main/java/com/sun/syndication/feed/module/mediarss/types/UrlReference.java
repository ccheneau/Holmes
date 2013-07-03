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

package com.sun.syndication.feed.module.mediarss.types;

import com.sun.syndication.feed.impl.EqualsBean;

import java.io.Serializable;
import java.net.URI;

/**
 * Used to indicate a URL primary reference for a MediaContent object.
 *
 * @author cooper
 */
public class UrlReference implements Reference, Serializable {
    private static final long serialVersionUID = -178149736468242989L;

    private final URI url;

    /**
     * Creates a new UrlReference.
     *
     * @param url URL to the media source
     */
    public UrlReference(final URI url) {
        if (url == null) throw new NullPointerException("url cannot be null.");
        this.url = url;
    }

    /**
     * Returns the URL value
     *
     * @return Returns the URL value
     */
    public URI getUrl() {
        return this.url;
    }

    @Override
    public boolean equals(final Object obj) {
        return new EqualsBean(this.getClass(), this).beanEquals(obj);
    }

    @Override
    public int hashCode() {
        return new EqualsBean(this.getClass(), this).beanHashCode();
    }
}
