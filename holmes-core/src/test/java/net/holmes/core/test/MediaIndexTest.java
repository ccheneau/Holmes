/**
* Copyright (C) 2012  Cedric Cheneau
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
package net.holmes.core.test;

import javax.inject.Inject;

import junit.framework.TestCase;
import net.holmes.core.TestModule;
import net.holmes.core.media.index.IMediaIndex;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MediaIndexTest extends TestCase {
    @Inject
    private IMediaIndex mediaIndex;

    @Override
    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    /**
     * Check that adding same data returns the same uuid
     */
    @Test
    public void testAddMediaIndex() {
        String uuid1 = mediaIndex.add("parentId", "mediaType", "path", "name");
        String uuid2 = mediaIndex.add("parentId", "mediaType", "path", "name");
        assertEquals(uuid1, uuid2);

        uuid2 = mediaIndex.add("parentId", "mediaType", "path", "name2");
        assertFalse(uuid1.equals(uuid2));
    }
}
