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

package net.holmes.core.backend.handler;


import net.holmes.core.business.version.ReleaseInfo;
import net.holmes.core.business.version.VersionManager;
import org.junit.Test;

import java.util.Collection;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UtilHandlerTest {

    @Test
    public void testGetVersion() {
        VersionManager versionManager = createMock(VersionManager.class);

        expect(versionManager.getCurrentVersion()).andReturn("version");
        replay(versionManager);

        UtilHandler utilHandler = new UtilHandler(versionManager);
        String version = utilHandler.getVersion();
        assertNotNull(version);
        assertEquals(version, "version");

        verify(versionManager);
    }

    @Test
    public void testGetNullVersion() {
        VersionManager versionManager = createMock(VersionManager.class);

        expect(versionManager.getCurrentVersion()).andReturn(null);
        replay(versionManager);

        UtilHandler utilHandler = new UtilHandler(versionManager);
        String version = utilHandler.getVersion();
        assertNotNull(version);
        assertEquals(version, "alpha");

        verify(versionManager);
    }

    @Test
    public void testGetReleaseInfo() {
        VersionManager versionManager = createMock(VersionManager.class);

        expect(versionManager.getReleaseInfo()).andReturn(new ReleaseInfo("name", true, "url"));
        replay(versionManager);

        UtilHandler utilHandler = new UtilHandler(versionManager);
        ReleaseInfo releaseInfo = utilHandler.getReleaseInfo();
        assertNotNull(releaseInfo);

        verify(versionManager);
    }

    @Test
    public void testGetChildFolders() {
        VersionManager versionManager = createMock(VersionManager.class);

        replay(versionManager);

        UtilHandler utilHandler = new UtilHandler(versionManager);
        Collection<UtilHandler.Folder> folders = utilHandler.getChildFolders("/");
        assertNotNull(folders);

        verify(versionManager);
    }

    @Test
    public void testGetChildFoldersNull() {
        VersionManager versionManager = createMock(VersionManager.class);

        replay(versionManager);

        UtilHandler utilHandler = new UtilHandler(versionManager);
        Collection<UtilHandler.Folder> folders = utilHandler.getChildFolders(null);
        assertNotNull(folders);

        verify(versionManager);
    }

    @Test
    public void testFolder() {
        UtilHandler.Folder folder = new UtilHandler.Folder("data", "path");
        assertEquals(folder.getData(), "data");
        assertEquals(folder.getState(), "closed");
        assertNotNull(folder.getMetadata());
    }
}
