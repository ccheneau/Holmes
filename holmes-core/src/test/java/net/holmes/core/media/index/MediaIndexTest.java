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

package net.holmes.core.media.index;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.*;

public class MediaIndexTest {
    @Inject
    private MediaIndexManager mediaIndex;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    /**
     * Check that adding same data returns the same uuid
     */
    @Test
    public void testAddToMediaIndex() {
        String uuid1 = mediaIndex.add(new MediaIndexElement("parentId", "mediaType", "path", "name", true));
        MediaIndexElement indexElement = mediaIndex.get(uuid1);
        assertNotNull(indexElement);
        assertEquals(indexElement.getName(), "name");
        String uuid2 = mediaIndex.add(new MediaIndexElement("parentId", "mediaType", "path", "name", true));
        assertEquals(uuid1, uuid2);

        uuid2 = mediaIndex.add(new MediaIndexElement("parentId", "mediaType", "path", "name2", true));
        assertFalse(uuid1.equals(uuid2));
    }

    @Test
    public void testRemoveFromMediaIndex() {
        String uuid1 = mediaIndex.add(new MediaIndexElement("parentId", "mediaType", "path", "name", true));
        assertNotNull(mediaIndex.get(uuid1));
        mediaIndex.remove(uuid1);
        assertNull(mediaIndex.get(uuid1));
    }

    @Test
    public void testRemoveChildrenMediaIndex() {
        String uuid1 = mediaIndex.add(new MediaIndexElement("parentId", "mediaType", "path", "name", true));
        assertNotNull(mediaIndex.get(uuid1));
        String uuid2 = mediaIndex.add(new MediaIndexElement(uuid1, "mediaType", "path", "name", true));
        assertNotNull(mediaIndex.get(uuid2));
        mediaIndex.removeChildren(uuid1);
        assertNotNull(mediaIndex.get(uuid1));
        assertNull(mediaIndex.get(uuid2));
    }

    @Test
    public void testCleanMediaIndex() {
        String uuid1 = mediaIndex.add(new MediaIndexElement("parentId", "mediaType", "path", "name", true));
        assertNotNull(mediaIndex.get(uuid1));
        mediaIndex.clean();
        assertNull(mediaIndex.get(uuid1));
    }
}
