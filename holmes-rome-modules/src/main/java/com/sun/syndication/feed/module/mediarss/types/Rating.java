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
import com.sun.syndication.feed.impl.ToStringBean;

/**
 * <strong>&lt;media:rating&gt;</strong></p>
 * <p/>
 * <p/>
 * <p>This allows the permissible audience to be declared. If this element is not included, it assumes that no restrictions are necessary. It has one optional attribute.</p>
 * <p/>
 * <pre>               &lt;media:rating scheme="urn:simple"&gt;adult&lt;/media:rating&gt;
 *               &lt;media:rating scheme="urn:icra"&gt;r (cz 1 lz 1 nz 1 oz 1 vz 1)&lt;/media:rating&gt;
 *               &lt;media:rating scheme="urn:mpaa"&gt;pg&lt;/media:rating&gt;
 * <p/>
 *               &lt;media:rating scheme="urn:v-chip"&gt;tv-y7-fv&lt;/media:rating&gt;</pre>
 * <p/>
 * <p/>
 * <p><em>scheme</em> is the URI that identifies the rating scheme. It is an optional attribute. If this attribute is not included, the default scheme is urn:simple (adult | non adult).</p>
 * <p/>
 * For compatibility, a media:adult tag will appear in the ratings as a urn:simple equiv.
 *
 * @author cooper
 */
public class Rating extends AbstractSchemeValue {
    private static final long serialVersionUID = 429385772347911315L;

    /**
     * urn:simple adult. This will be populated on the deprecated media:adult tag as well,
     */
    public static final Rating ADULT = new Rating("urn:simple", "adult");

    /**
     * urn:simple non adult. This will be populated on the deprecated media:adult tag as well,
     */
    public static final Rating NONADULT = new Rating("urn:simple", "nonadult");

    /**
     * Constructs a new Rating object.
     *
     * @param scheme scheme used for the rating
     * @param value  value of the rating.
     */
    public Rating(final String scheme, final String value) {
        super(scheme, value);
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
        ToStringBean tsBean = new ToStringBean(this.getClass(), this);
        return tsBean.toString();
    }
}
