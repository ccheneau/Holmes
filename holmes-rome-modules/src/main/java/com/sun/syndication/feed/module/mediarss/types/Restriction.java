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

import java.io.Serializable;

/**
 * <strong>&lt;media:restriction&gt; </strong></p>
 * <p/>
 * <p>Allows restrictions to be placed on the aggregator rendering the media in the feed.
 * Currently, restrictions are based on distributor (uri) and country codes.
 * This element is purely informational and no obligation can be assumed or implied.
 * Only one &lt;media:restriction&gt; element of the same <em>type</em> can be applied to a media object - all others will be ignored.&nbsp;Entities in this element should be space separated.
 * To allow the producer to explicitly declare his/her intentions, two literals are reserved: 'all', 'none'. These literals can only be used once. This element has 1 required attribute, and 1 optional attribute (with strict requirements for its exclusion).</p>
 * <p/>
 * <pre>        &lt;media:restriction relationship="allow" type="country"&gt;au us&lt;/media:restriction&gt;</pre>
 * <p/>
 * <p><em>relationship</em> indicates the type of relationship that the restriction represents (allow | deny). In the example above, the media object should only be syndicated in Australia and the United States. It is a required attribute.</p>
 * <p/>
 * <p><strong>Note:</strong> If the "allow" element is empty and the type is relationship is "allow", it is assumed that the empty list means "allow nobody" and the media should not be syndicated.</p>
 * <p>A more explicit method would be:</p>
 * <p/>
 * <pre>        &lt;media:restriction relationship="allow" type="country"&gt;au us&lt;/media:restriction&gt;</pre>
 * <p/>
 * <p><em>type</em> specifies the type of restriction (country | uri) that the media can be syndicated. It is an optional attribute; however can only be excluded when using one of the literal values "all" or "none". </p>
 * <p/>
 * <p>"country" allows restrictions to be placed based on country code. [<a href="http://www.iso.org/iso/en/prods-services/iso3166ma/index.html">ISO 3166</a>]</p>
 * <p>"uri" allows restrictions based on URI. Examples: urn:apple, http://images.google.com, urn:yahoo, etc.
 *
 * @author cooper
 */
public class Restriction implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 7944281267467298628L;

    /**
     * The relationship.
     */
    private Relationship relationship;

    /**
     * The value.
     */
    private String value;

    /**
     * The type.
     */
    private Type type;

    /**
     * Creates a new instance of Restriction.
     *
     * @param relationship a Restriction.Relationship object
     * @param type         A Restriction.Type object
     * @param value        a value for the restriction.
     */
    public Restriction(final Relationship relationship, final Type type, final String value) {
        if (value == null || relationship == null) {
            throw new NullPointerException("Value and Relationship cannot be null.");
        }

        if (type == null && !(value.equals("all") || value.equals("none"))) {
            throw new NullPointerException("Type is required if the value is other than 'all' or 'none'.");
        }

        this.relationship = relationship;
        this.type = type;
        this.value = value;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
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

    /**
     * Indicates the action of the relationship.
     */
    public static final class Relationship implements Serializable {

        /**
         * The Constant serialVersionUID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * An Allow relationship.
         */
        public static final Relationship ALLOW = new Relationship("allow");

        /**
         * A deny relationship.
         */
        public static final Relationship DENY = new Relationship("deny");

        /**
         * The value.
         */
        private final String value;

        /**
         * Constructor.
         *
         * @param value the value
         */
        private Relationship(final String value) {
            this.value = value;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * Indicated the type of the relationship.
     */
    public static final class Type implements Serializable {

        /**
         * The Constant serialVersionUID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Indicates a Country type.
         */
        public static final Type COUNTRY = new Type("country");

        /**
         * Indicates a URI for a special restriction type.
         */
        public static final Type URI = new Type("uri");

        /**
         * The value.
         */
        private final String value;

        /**
         * Constructor.
         *
         * @param value the value
         */
        private Type(final String value) {
            this.value = value;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return this.value;
        }
    }
}
