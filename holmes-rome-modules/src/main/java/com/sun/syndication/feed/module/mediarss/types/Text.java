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

/**
 * <strong>&lt;media:text&gt;</strong></p>
 * <p>Allows the inclusion of a text transcript, closed captioning, or lyrics of the media content.
 * Many of these elements are permitted to provide a time series of text.
 * In such cases, it is encouraged, but not required, that the elements be grouped by language and appear in time sequence order based on the <em>start</em> time.
 * Elements can have overlapping <em>start</em> and <em>end</em> times. It has 4 optional attributes.</p><pre>        &lt;media:text type="plain" lang="en" start="00:00:03.000"
 *        end="00:00:10.000"&gt; Oh, say, can you see&lt;/media:text&gt;
 * <p/>
 *        &lt;media:text type="plain" lang="en" start="00:00:10.000"
 *        end="00:00:17.000"&gt;By the dawn's early light&lt;/media:text&gt;
 * </pre>
 * <p><em>type</em> specifies the type of text embedded. Possible values are either 'plain' or 'html'. Default value is 'plain'. All html must be entity-encoded. It is an optional attribute.</p>
 * <p/>
 * <p><em>lang</em> is the primary language encapsulated in the media object. Language codes possible are detailed in RFC 3066. This attribute is used similar to the xml:lang attribute detailed in the XML 1.0 Specification (Third Edition). It is an optional attribute.</p>
 * <p/>
 * <p><em>start</em> specifies the start time offset that the text starts being relevant to the media object. An example of this would be for closed captioning.
 * It uses the NTP time code format (see: the time attribute used in &lt;media:thumbnail&gt;).&nbsp;It is an optional attribute.</p>
 * <p/>
 * <p><em>end</em> specifies the end time that the text is relevant.
 * If this attribute is not provided, and a <em>start</em> time is used, it is expected that the end time is either the end of the clip or the start of the next &lt;media:text&gt; element. </p>
 *
 * @author cooper
 */
public class Text implements Serializable {
    private static final long serialVersionUID = 9043514380583850045L;
    private final String type;
    private final String value;
    private final Time end;
    private final Time start;

    /**
     * Creates a text object.
     *
     * @param value value of the text
     */
    public Text(final String value) {
        this(null, value, null, null);
    }

    /**
     * Creates a new instance of Text.
     *
     * @param type  type of text
     * @param value value of text
     */
    public Text(final String type, final String value) {
        this(type, value, null, null);
    }

    /**
     * Creates a text object with start and end times.
     *
     * @param type  type of text
     * @param value value of text
     * @param start start time
     * @param end   end time
     */
    public Text(final String type, final String value, final Time start, final Time end) {
        this.type = type;
        this.value = value;
        this.start = start;
        this.end = end;
    }

    /**
     * End time of the text
     *
     * @return End time of the text
     */
    public Time getEnd() {
        return end;
    }

    /**
     * Start time of the text
     *
     * @return Start time of the text
     */
    public Time getStart() {
        return start;
    }

    /**
     * type of the text.
     *
     * @return type of the text
     */
    public String getType() {
        return this.type;
    }

    /**
     * Value of the text.
     *
     * @return type of the text
     */
    public String getValue() {
        return this.value;
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
