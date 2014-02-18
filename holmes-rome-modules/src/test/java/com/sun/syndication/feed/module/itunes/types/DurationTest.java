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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DurationTest {

    @Test
    public void testDurationSecond() {
        Duration duration = new Duration("60");
        assertEquals(60000, duration.getMilliseconds());
    }

    @Test
    public void testDurationMinute() {
        Duration duration = new Duration("01:00");
        assertEquals(60000, duration.getMilliseconds());
    }

    @Test
    public void testDurationHour() {
        Duration duration = new Duration("01:00:00");
        assertEquals(3600000, duration.getMilliseconds());
    }

    @Test(expected = RuntimeException.class)
    public void testDurationDay() {
        new Duration("01:00:00:00");
    }

}