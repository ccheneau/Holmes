/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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
package com.sun.syndication.feed.module.itunes;

import com.sun.syndication.feed.module.itunes.types.Duration;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntryInformationImplTest {

    @Test
    public void testEntryInformationImpl() {
        EntryInformationImpl entryInformation = new EntryInformationImpl();
        assertNotNull(entryInformation.getInterface());
        assertNull(entryInformation.getDurationString());
    }

    @Test
    public void testGetDuration() {
        EntryInformationImpl entryInformation = new EntryInformationImpl();
        entryInformation.setDuration(new Duration(3600000));
        assertEquals("01:00:00", entryInformation.getDurationString());
        EntryInformationImpl entryInformation2 = new EntryInformationImpl();
        entryInformation2.copyFrom(entryInformation);
        assertEquals("01:00:00", entryInformation2.getDurationString());
    }

}
