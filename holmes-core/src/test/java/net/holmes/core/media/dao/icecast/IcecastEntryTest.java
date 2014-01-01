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

package net.holmes.core.media.dao.icecast;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class IcecastEntryTest {

    @Test
    public void testIcecastEntry() {
        IcecastEntry entry = new IcecastEntry("name", "url", "type", "genre");
        assertEquals(entry.getName(), "name");
        assertEquals(entry.getUrl(), "url");
        assertEquals(entry.getType(), "type");
        assertEquals(entry.getGenre(), "genre");
    }

    @Test
    public void testEquals() {
        IcecastEntry entry1 = new IcecastEntry("name", "url", "type", "genre");
        IcecastEntry entry2 = new IcecastEntry("name", "url", "type", "genre");
        IcecastEntry entry3 = new IcecastEntry("name1", "url", "type", "genre");
        IcecastEntry entry4 = new IcecastEntry("name", "url", "type1", "genre");
        IcecastEntry entry5 = new IcecastEntry("name", "url", "type", "genre1");

        assertEquals(entry1, entry1);
        assertEquals(entry1, entry2);
        assertNotEquals(entry1, null);
        assertNotEquals(entry1, "");
        assertNotEquals(entry1, entry3);
        assertNotEquals(entry1, entry4);
        assertNotEquals(entry1, entry5);
    }
}
