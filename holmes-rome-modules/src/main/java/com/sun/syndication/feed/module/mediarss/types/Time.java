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

import com.sun.syndication.feed.impl.EqualsBean;

import java.io.Serializable;

/**
 * Represents a <a href="http://www.ietf.org/rfc/rfc2326.txt">RFC 2326 3.6 Normal Play Time</a> timestamp.
 *
 * @author cooper
 */
public class Time implements Serializable {
    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE * SECOND;
    private long milliseconds = 0;

    /**
     * Creates a new instance of Time
     *
     * @param value <a href="http://www.ietf.org/rfc/rfc2326.txt">RFC 2326 3.6 Normal Play Time</a> value
     */
    public Time(final String value) {
        String[] values = value.split(":");
        int count = values.length - 1;
        this.milliseconds = (long) (Double.parseDouble(values[count]) * SECOND);
        count--;

        if (count >= 0) {
            milliseconds += Long.parseLong(values[count]) * MINUTE;
            count--;
        }

        if (count >= 0) milliseconds += Long.parseLong(values[count]) * HOUR;
    }

    public long getValue() {
        return milliseconds;
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
