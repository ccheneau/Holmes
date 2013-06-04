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

/*
 * Category.java
 *
 * Created on April 18, 2006, 8:04 PM
 *
 * This code is currently released under the Mozilla Public License.
 * http://www.mozilla.org/MPL/
 *
 * Alternately you may apply the terms of the Apache Software License
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.syndication.feed.module.mediarss.types;

import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ToStringBean;

import java.io.Serializable;

/**
 * <strong>&lt;media:category&gt;</strong></p>
 * <p> Allows a taxonomy to be set that gives an indication of the type of media content, and its particular contents.
 * It has 2 optional attributes.  </p>
 * <pre>        &lt;media:category scheme="http://search.yahoo.com/mrss/category_
 *        schema"&gt;music/artist/album/song&lt;/media:category&gt;
 * <p/>
 *        &lt;media:category scheme="http://dmoz.org" label="Ace Ventura - Pet
 *        Detective"&gt;Arts/Movies/Titles/A/Ace_Ventura_Series/Ace_Ventura_
 *        -_Pet_Detective&lt;/media:category&gt;
 * <p/>
 *        &lt;media:category scheme="urn:flickr:tags"&gt;ycantpark
 *        mobile&lt;/media:category&gt;</pre>
 * <p/>
 * <p><em>scheme</em> is the URI that identifies the categorization scheme. It is an optional attribute. If this attribute is not included, the default scheme is 'http://search.yahoo.com/mrss/category_schema'.</p>
 * <p/>
 * <p><em>label</em> is the human readable label that can be displayed in end user applications. It is an optional attribute.</p>
 *
 * @author cooper
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 5182373808661745402L;
    /**
     * Schema for FLICKR tags
     */
    public static final String SCHEME_FLICKR_TAGS = "urn:flickr:tags";
    private String label;
    private String scheme;
    private String value;

    /**
     * Creates a new instance of Category
     *
     * @param scheme scheme used
     * @param label  label for the category
     * @param value  value of the category item
     */
    public Category(final String scheme, final String label, final String value) {
        this.scheme = scheme;
        this.value = value;
        this.label = label;
    }

    /**
     * Creates a new Category.
     *
     * @param value value of the category.
     */
    public Category(final String value) {
        this.value = value;
    }

    /**
     * label is the human readable label that can be displayed in end user applications. It is an optional attribute.
     *
     * @return label is the human readable label that can be displayed in end user applications. It is an optional attribute.
     */
    public String getLabel() {
        return label;
    }

    /**
     * scheme is the URI that identifies the categorization scheme. It is an optional attribute. If this attribute is not included, the default scheme is 'http://search.yahoo.com/mrss/category_schema'.
     *
     * @return scheme is the URI that identifies the categorization scheme. It is an optional attribute. If this attribute is not included, the default scheme is 'http://search.yahoo.com/mrss/category_schema'.
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * value of the category
     *
     * @return value of the category
     */
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
}
