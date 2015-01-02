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

package net.holmes.core.business.media.dao.index;

import org.junit.Test;

import static org.junit.Assert.*;

public class MediaIndexElementTest {

    @Test
    public void testEquals() {
        MediaIndexElement element1 = new MediaIndexElement("parentId", "mediaType", "mimeType", "path", "name", true, true);
        MediaIndexElement element2 = new MediaIndexElement("parentId", "mediaType", "mimeType", "path", "name", true, true);
        MediaIndexElement element3 = new MediaIndexElement("parentId1", "mediaType", "mimeType", "path", "name", true, true);
        MediaIndexElement element4 = new MediaIndexElement("parentId", "mediaType1", "mimeType", "path", "name", true, true);
        MediaIndexElement element5 = new MediaIndexElement("parentId", "mediaType", "mimeType", "path1", "name", true, true);
        MediaIndexElement element6 = new MediaIndexElement("parentId", "mediaType", "mimeType", "path", "name1", false, true);
        MediaIndexElement element7 = new MediaIndexElement("parentId", "mediaType", "mimeType1", "path", "name1", false, true);
        MediaIndexElement element8 = new MediaIndexElement("parentId", "mediaType", "mimeType1", "path", "name", false, false);
        assertTrue(element1.isLocalPath());
        assertTrue(element1.isLocked());
        assertNotNull(element1.getMimeType());
        assertEquals(element1, element1);
        assertEquals(element1, element2);
        assertNotEquals(element1, null);
        assertNotEquals(element1, "element1");
        assertNotEquals(element1, element3);
        assertNotEquals(element1, element4);
        assertNotEquals(element1, element5);
        assertNotEquals(element1, element6);
        assertNotEquals(element1, element7);
        assertNotEquals(element1, element8);
    }
}
