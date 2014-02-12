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

package com.sun.syndication.feed.module.itunes.types;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import static com.sun.syndication.io.impl.NumberParser.parseFloat;
import static com.sun.syndication.io.impl.NumberParser.parseLong;

/**
 * An encapsulation of the duration of a podcast. This will serialize (via .toString())
 * to HH:MM:SS format, and can parse [H]*H:[M]*M:[S]*S or [M]*M:[S]*S.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.7 $
 */
public final class Duration implements Serializable {

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
     * Creates a new instance of Duration specifying a length in milliseconds.
     *
     * @param milliseconds Creates a new instance of Duration specifying a length in milliseconds
     */
    public Duration(final long milliseconds) {
        this.milliseconds = milliseconds;
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
                this.milliseconds = (long) (parseFloat(tok.nextToken(), 0f) * SECOND);
                break;
            case 2:
                this.milliseconds = parseLong(tok.nextToken(), 0L) * MINUTE + (long) (parseFloat(tok.nextToken(), 0f) * SECOND);
                break;
            case 3:
                this.milliseconds = parseLong(tok.nextToken(), 0L) * HOUR + parseLong(tok.nextToken(), 0L) * MINUTE
                        + (long) (parseFloat(tok.nextToken(), 0f) * SECOND);
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

    /**
     * Returns a String representation in the formation HH:MM:SS.
     *
     * @return Returns a String representation in the formation HH:MM:SS
     */
    @Override
    public String toString() {
        Time time = new Time(this);
        return NUM_FORMAT.format(time.hours) + ":" + NUM_FORMAT.format(time.minutes) + ":" + NUM_FORMAT.format(Math.round(time.seconds));
    }

    /**
     * The Class Time.
     */
    private static class Time {

        /**
         * The hours.
         */
        private final int hours;
        /**
         * The minutes.
         */
        private final int minutes;
        /**
         * The seconds.
         */
        private final float seconds;

        /**
         * Constructor.
         *
         * @param duration the duration
         */
        public Time(final Duration duration) {
            long time = duration.getMilliseconds();
            hours = (int) (time / HOUR);
            time = time - hours * HOUR;
            minutes = (int) (time / MINUTE);
            time = time - minutes * MINUTE;
            seconds = (float) time / (float) SECOND;
        }
    }
}
