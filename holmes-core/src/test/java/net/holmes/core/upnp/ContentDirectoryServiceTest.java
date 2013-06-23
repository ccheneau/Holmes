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

package net.holmes.core.upnp;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.TestModule;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ContentDirectoryServiceTest {

    private ContentDirectoryService contentDirectoryService;

    @Before
    public void setUp() {
        contentDirectoryService = new ContentDirectoryService();
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(contentDirectoryService);
    }

    @Test
    public void testBrowseRootDC() throws ContentDirectoryException {
        BrowseResult result = contentDirectoryService.browse("0", BrowseFlag.DIRECT_CHILDREN, null, 0, 10, null);
        assertNotNull(result);

    }

    @Test
    public void testBrowseRootMetadata() throws ContentDirectoryException {
        BrowseResult result = contentDirectoryService.browse("0", BrowseFlag.METADATA, null, 0, 10, null);
        assertNotNull(result);

    }

    @Test
    public void testBrowseNull() {
        try {
            BrowseResult result = contentDirectoryService.browse(null, BrowseFlag.METADATA, null, 0, 10, null);
            fail();
        } catch (ContentDirectoryException e) {
            assertNotNull(e);
        }

    }
}
