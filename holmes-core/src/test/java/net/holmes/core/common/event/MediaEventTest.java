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

package net.holmes.core.common.event;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * MediaEvent Tester.
 */
public class MediaEventTest {

    /**
     * Method: getType()
     */
    @Test
    public void testGetType() throws Exception {
        MediaEvent event = buildMediaEvent("");
        assertEquals(event.getType(), MediaEvent.MediaEventType.SCAN_NODE);
    }

    /**
     * Method: getParameter()
     */
    @Test
    public void testGetParameter() throws Exception {
        MediaEvent event = buildMediaEvent("");
        assertEquals(event.getParameter(), "parameter");
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception {
        MediaEvent event1 = buildMediaEvent("");
        MediaEvent event2 = buildMediaEvent("");
        assertNotNull(event1.toString());
        assertEquals(event1.toString(), event2.toString());
    }

    private MediaEvent buildMediaEvent(String suffix) {
        return new MediaEvent(MediaEvent.MediaEventType.SCAN_NODE, "parameter" + suffix);
    }
}
