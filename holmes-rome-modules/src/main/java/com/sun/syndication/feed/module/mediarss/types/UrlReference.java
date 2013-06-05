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
import java.net.URISyntaxException;

/**
 * Used to indicate a URL primary reference for a MediaContent object.
 *
 * @author cooper
 */
public class UrlReference implements Reference, Serializable {
    private static final long serialVersionUID = -178149736468242989L;

    private URI url;

    /**
     * Creates a new UrlReference.
     *
     * @param url URL to the media source
     */
    public UrlReference(final URI url) {
        super();
        if (url == null) {
            throw new NullPointerException("url cannot be null.");
        }

        this.url = url;
    }

    /**
     * Creates a new instance of UrlReference.
     *
     * @param url String value of a URL
     * @throws URISyntaxException the uRI syntax exception
     */
    public UrlReference(final String url) throws URISyntaxException {
        super();
        if (url == null) {
            throw new NullPointerException("url cannot be null.");
        }

        this.url = new URI(url);
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
        EqualsBean eBean = new EqualsBean(this.getClass(), this);
        return eBean.beanEquals(obj);
    }

    @Override
    public int hashCode() {
        EqualsBean equals = new EqualsBean(this.getClass(), this);
        return equals.beanHashCode();
    }

    @Override
    public String toString() {
        return url.toString();
    }
}
