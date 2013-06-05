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

import java.io.Serializable;

/**
 * Simple data bean parent for scheme-value type entities.
 *
 * @author cooper
 */
public abstract class AbstractSchemeValue implements Serializable {
    private static final long serialVersionUID = -917128796432458636L;
    private String scheme;
    private String value;

    /**
     * Constructor.
     *
     * @param scheme the scheme
     * @param value  the value
     */
    AbstractSchemeValue(final String scheme, final String value) {
        if (scheme == null || value == null) throw new NullPointerException("Scheme or value cannot be null.");
        this.scheme = scheme;
        this.value = value;
    }

    public String getScheme() {
        return scheme;
    }

    public String getValue() {
        return value;
    }
}
