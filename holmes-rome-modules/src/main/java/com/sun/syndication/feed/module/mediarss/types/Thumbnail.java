/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

import java.io.Serializable;
import java.net.URI;

/**
 * <strong>&lt;media:thumbnail&gt;</strong></p>
 * <p/>
 * <p/>
 * <p>Allows particular images to be used as representative images for the media object. If multiple thumbnails are included, and time coding is not at play, it is assumed that the images are in order of importance. It has 1 required attribute and 3 optional attributes.</p>
 * <pre>        &lt;media:thumbnail url="http://www.foo.com/keyframe.jpg" width="75" height="50" time="12:05:01.123" /&gt;</pre>
 * <p><em>url</em> specifies the url of the thumbnail. It is a required attribute.</p>        <p> <em>height</em> specifies the height of the thumbnail. It is an optional attribute.</p>
 * <p> <em>width</em> specifies the width of the thumbnail. It is an optional attribute.</p>
 * <p><em>time</em>
 * specifies the time offset in relation to the media object.
 * Typically this is used when creating multiple key frames within a single video.
 * The format for this attribute should be in the DSM-CC's Normal Play Time (NTP) as used in RTSP [<a href="http://www.ietf.org/rfc/rfc2326.txt">RFC 2326 3.6 Normal Play Time</a>].
 * It is an optional attribute.</p>
 */
public class Thumbnail implements Cloneable, Serializable {
    private final URI thumbUrl;

    /**
     * Creates a new thumbnail
     *
     * @param url URL to thumbnail
     */
    public Thumbnail(final URI url) {
        this.thumbUrl = url;
    }

    /**
     * Returns the URL
     *
     * @return Returns the thumbUrl.
     */
    public URI getUrl() {
        return thumbUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return new Thumbnail(this.thumbUrl);
    }
}
