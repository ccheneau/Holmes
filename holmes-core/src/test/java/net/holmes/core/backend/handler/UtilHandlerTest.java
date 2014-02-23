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


import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UtilHandlerTest {

    @Test
    public void testGetVersion() {
        UtilHandler utilHandler = new UtilHandler("version");
        assertNotNull(utilHandler.getVersion());
        assertEquals(utilHandler.getVersion(), "version");
    }

    @Test
    public void testGetChildFolders() {
        UtilHandler utilHandler = new UtilHandler("version");
        Collection<UtilHandler.Folder> folders = utilHandler.getChildFolders("/");
        assertNotNull(folders);
    }

    @Test
    public void testGetChildFoldersNull() {
        UtilHandler utilHandler = new UtilHandler("version");
        Collection<UtilHandler.Folder> folders = utilHandler.getChildFolders(null);
        assertNotNull(folders);
    }

    @Test
    public void testFolder() {
        UtilHandler.Folder folder = new UtilHandler.Folder("data", "path");
        assertEquals(folder.getData(), "data");
        assertEquals(folder.getState(), "closed");
        assertNotNull(folder.getMetadata());
    }
}
