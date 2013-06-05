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
 * Expression determines if the object is a sample or the full version of the object, or even if it is a continuous stream (sample | full | nonstop). Default value is 'full'. It is an optional attribute.
 */
public final class Expression implements Serializable {
    private static final long serialVersionUID = 2356898289547679768L;

    /**
     * Represents a complete media object.
     */
    public static final Expression FULL = new Expression("full");

    /**
     * Represents a sample media object.
     */
    public static final Expression SAMPLE = new Expression("sample");

    /**
     * represents a streaming media object.
     */
    public static final Expression NONSTOP = new Expression("nonstop");
    private final String value;

    /**
     * Constructor.
     *
     * @param value the value
     */
    private Expression(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
