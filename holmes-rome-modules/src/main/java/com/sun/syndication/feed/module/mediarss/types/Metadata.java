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

package com.sun.syndication.feed.module.mediarss.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Optional Elements.
 * <p> The following elements are optional and may appear as sub-elements of &lt;channel&gt;, &lt;item&gt;, &lt;media:content&gt; and/or &lt;media:group&gt;.
 * <p/>
 * <p/>
 * </p><p>
 * <p/>
 * When an element appears at a shallow level, such as &lt;channel&gt; or &lt;item&gt;, it means that the element should be applied to every media object within its scope.</p>
 * <p/>
 * <p/>
 * <p>
 * Duplicated elements appearing at deeper levels of the document tree have higher priority over other levels.
 * <p/>
 * For example, &lt;media:content&gt; level elements are favored over &lt;item&gt; level elements.
 * The priority level is listed from strongest to weakest: &lt;media:content&gt;, &lt;media:group&gt;, &lt;item&gt;, &lt;channel&gt;.</p>
 *
 * @author cooper
 */
public class Metadata implements Cloneable, Serializable {

    private final List<Thumbnail> thumbnails;

    /**
     * Creates a new instance of Metadata
     */
    public Metadata() {
        thumbnails = new ArrayList<>();
    }


    /**
     * <strong>&lt;media:thumbnail&gt;</strong></p>
     * <p/>
     * <p/>
     * <p>Allows particular images to be used as representative images for the media object. If multiple thumbnails are included, and time coding is not at play, it is assumed that the images are in order of importance. It has 1 required attribute and 3 optional attributes.</p>
     * <pre>        &lt;media:thumbnail url="http://www.foo.com/keyframe.jpg" width="75" height="50" time="12:05:01.123" /&gt;</pre>
     * <p><em>url</em> specifies the url of the thumbnail. It is a required attribute.</p>        <p> <em>height</em> specifies the height of the thumbnail. It is an optional attribute.</p>
     * <p> <em>width</em> specifies the width of the thumbnail. It is an optional attribute.</p>
     * <p/>
     * <p/>
     * <p><em>time</em>
     * specifies the time offset in relation to the media object.
     * Typically this is used when creating multiple key frames within a single video.
     * The format for this attribute should be in the DSM-CC's Normal Play Time (NTP) as used in RTSP [<a href="http://www.ietf.org/rfc/rfc2326.txt">RFC 2326 3.6 Normal Play Time</a>]. It is an optional attribute.</p>
     *
     * @param thumbnail thumbnails for the image
     */
    public void addThumbnail(final Thumbnail thumbnail) {
        this.thumbnails.add(thumbnail);
    }

    /**
     * <strong>&lt;media:thumbnail&gt;</strong></p>
     * <p/>
     * <p/>
     * <p>Allows particular images to be used as representative images for the media object. If multiple thumbnails are included, and time coding is not at play, it is assumed that the images are in order of importance. It has 1 required attribute and 3 optional attributes.</p>
     * <pre>        &lt;media:thumbnail url="http://www.foo.com/keyframe.jpg" width="75" height="50" time="12:05:01.123" /&gt;</pre>
     * <p><em>url</em> specifies the url of the thumbnail. It is a required attribute.</p>        <p> <em>height</em> specifies the height of the thumbnail. It is an optional attribute.</p>
     * <p> <em>width</em> specifies the width of the thumbnail. It is an optional attribute.</p>
     * <p/>
     * <p/>
     * <p><em>time</em>
     * specifies the time offset in relation to the media object.
     * Typically this is used when creating multiple key frames within a single video.
     * The format for this attribute should be in the DSM-CC's Normal Play Time (NTP) as used in RTSP [<a href="http://www.ietf.org/rfc/rfc2326.txt">RFC 2326 3.6 Normal Play Time</a>]. It is an optional attribute.</p>
     *
     * @return Thumbnails for the image
     */
    public List<Thumbnail> getThumbnails() {
        return thumbnails;
    }

    /**
     * <strong>&lt;media:copyright&gt;</strong></p>
     * <p>Copyright information for media object.  It has 1 optional attribute.</p>
     * <p/>
     * <pre>        &lt;media:copyright url="http://blah.com/additional-info.html"&gt;2005 FooBar Media&lt;/media:copyright&gt;</pre>
     * <p><em>url</em> is the url for a terms of use page or additional copyright information. If the media is operating under a Creative Commons license, the Creative Commons module should be used instead. It is an optional attribute.</p>
     *
     * @return Link to more copyright information.
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        Metadata md = new Metadata();
        for (Thumbnail thumbnail : thumbnails) {
            md.addThumbnail(thumbnail);
        }
        return md;
    }
}
