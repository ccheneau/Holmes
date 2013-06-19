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

package com.sun.syndication.feed.module.itunes.types;

import com.sun.syndication.io.impl.NumberParser;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.StringTokenizer;

/**
 * An encapsulation of the duration of a podcast. This will serialize (via .toString())
 * to HH:MM:SS format, and can parse [H]*H:[M]*M:[S]*S or [M]*M:[S]*S.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.7 $
 */
public final class Duration implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -1960363504597514365L;
    /**
     * The Constant SECOND.
     */
    private static final long SECOND = 1000L;
    /**
     * The Constant MINUTE.
     */
    private static final long MINUTE = SECOND * 60L;
    /**
     * The Constant HOUR.
     */
    private static final long HOUR = MINUTE * 60L;
    /**
     * The Constant NUM_FORMAT.
     */
    private static final NumberFormat NUM_FORMAT = NumberFormat.getNumberInstance();

    static {
        NUM_FORMAT.setMinimumFractionDigits(0);
        NUM_FORMAT.setMaximumFractionDigits(0);
        NUM_FORMAT.setMinimumIntegerDigits(2);
        NUM_FORMAT.setGroupingUsed(false);
    }

    /**
     * The milliseconds.
     */
    private long milliseconds;

    /**
     * Creates a new Duration object with 0 length.
     */
    public Duration() {
        super();
    }

    /**
     * Creates a new instance of Duration specifying a length in milliseconds.
     *
     * @param milliseconds Creates a new instance of Duration specifying a length in milliseconds
     */
    public Duration(final long milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * Creates a new duration object with the given hours, minutes and seconds.
     *
     * @param hours   number of hours
     * @param minutes number of minutes
     * @param seconds number of seconds
     */
    public Duration(final int hours, final int minutes, final float seconds) {
        this.milliseconds = hours * HOUR + minutes * MINUTE + (long) (seconds * SECOND);
    }

    /**
     * Creates a new Duration parsing the String value.
     *
     * @param duration A String to parse
     */
    public Duration(final String duration) {
        StringTokenizer tok = new StringTokenizer(duration, ":");
        switch (tok.countTokens()) {
            case 1:
                this.milliseconds = (long) (NumberParser.parseFloat(tok.nextToken(), 0f) * SECOND);
                break;
            case 2:
                this.milliseconds = NumberParser.parseLong(tok.nextToken(), 0L) * MINUTE + (long) (NumberParser.parseFloat(tok.nextToken(), 0f) * SECOND);
                break;
            case 3:
                this.milliseconds = NumberParser.parseLong(tok.nextToken(), 0L) * HOUR + NumberParser.parseLong(tok.nextToken(), 0L) * MINUTE
                        + (long) (NumberParser.parseFloat(tok.nextToken(), 0f) * SECOND);
                break;
            default:
                throw new RuntimeException("Illegal time value: " + duration);
        }
    }

    /**
     * Returns the millisecond length
     *
     * @return the millisecond length
     */
    public long getMilliseconds() {
        return milliseconds;
    }
}
