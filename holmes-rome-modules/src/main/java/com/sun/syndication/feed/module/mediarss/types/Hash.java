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

/**
 * <strong>&lt;media:hash&gt;</strong></p>
 * <p/>
 * <p>This is the hash of the binary media file. It can appear multiple times as long as each instance is a different <em>algo</em>. It has 1 optional attribute.</p><p></p>
 * <p/>
 * <pre>        &lt;media:hash algo="md5"&gt;dfdec888b72151965a34b4b59031290a&lt;/media:hash&gt;</pre>
 * <p/>
 * <p><em>algo</em> indicates the algorithm used to create the hash. Possible values are 'md5' and 'sha-1'. Default value is 'md5'. It is an optional attribute.
 *
 * @author cooper
 */
public class Hash extends AbstractSchemeValue {
    private static final long serialVersionUID = 3566980635881544337L;

    /**
     * Creates a new instance of Hash
     *
     * @param algorithm algorithm used
     * @param value     value of the hash
     */
    public Hash(final String algorithm, final String value) {
        super(algorithm == null ? "MD5" : algorithm, value);
    }

    /**
     * Algorithm used for the hash
     *
     * @return Algorithm used for the hash
     */
    public String getAlgorithm() {
        return super.getScheme();
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
