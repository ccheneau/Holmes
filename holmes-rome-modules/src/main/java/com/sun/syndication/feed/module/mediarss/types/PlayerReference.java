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
 * <strong>&lt;media:player&gt;</strong></p>
 * <p>Allows the media object to be accessed through a web browser media player console.
 * This element is required only if a direct media <em>url</em> attribute is not specified in the &lt;media:content&gt; element. It has 1 required attribute, and 2 optional attributes.</p>
 * <pre>        &lt;media:player url="http://www.foo.com/player?id=1111" height="200" width="400" /&gt;</pre>
 * <p><em>url</em> is the url of the player console that plays the media. It is a required attribute.</p>
 * <p/>
 * <p><em>height</em> is the height of the browser window that the <em>url</em> should be opened in. It is an optional attribute.</p>
 * <p><em>width</em> is the width of the browser window that the <em>url</em> should be opened in. It is an optional attribute.</p>
 *
 * @author cooper
 */
public class PlayerReference implements Reference, Serializable {
    private static final long serialVersionUID = -2618211012392630834L;

    private Integer height;
    private Integer width;
    private URI url;

    /**
     * Creates a new instance of PlayerReference
     *
     * @param url    url of the player
     * @param width  width of the player
     * @param height height of the player
     */
    public PlayerReference(final URI url, final Integer width, final Integer height) {
        if (url == null) throw new NullPointerException("url cannot be null.");

        this.url = url;
        this.height = height;
        this.width = width;
    }

    /**
     * Height of the player
     *
     * @return Height of the player
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * URL of the player
     *
     * @return URL of the player
     */
    public URI getUrl() {
        return url;
    }

    /**
     * Width of the player
     *
     * @return Width of the player
     */
    public Integer getWidth() {
        return width;
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
