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

import com.google.common.collect.Lists;
import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.response.ConfigurationFolder;
import org.junit.Test;

import static net.holmes.core.business.media.model.RootNode.PICTURE;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PictureFoldersHandlerTest {

    @Test
    public void testGetPictureFolders() {
        BackendManager backendManager = createMock(BackendManager.class);

        expect(backendManager.getFolders(PICTURE)).andReturn(Lists.newArrayList(new ConfigurationFolder("imagesTest", "imagesTest", "path"))).atLeastOnce();

        replay(backendManager);
        PictureFoldersHandler pictureFoldersHandler = new PictureFoldersHandler(backendManager);
        assertNotNull(pictureFoldersHandler.getPictureFolders());
        verify(backendManager);
    }

    @Test
    public void testGetPictureFolder() {
        BackendManager backendManager = createMock(BackendManager.class);

        expect(backendManager.getFolder("imagesTest", PICTURE)).andReturn(new ConfigurationFolder("imagesTest", "imagesTest", "path")).atLeastOnce();

        replay(backendManager);
        PictureFoldersHandler pictureFoldersHandler = new PictureFoldersHandler(backendManager);
        assertNotNull(pictureFoldersHandler.getPictureFolder("imagesTest"));
        verify(backendManager);
    }

    @Test
    public void testAddPictureFolder() {
        BackendManager backendManager = createMock(BackendManager.class);
        ConfigurationFolder folder = new ConfigurationFolder(null, "newImagesTest", System.getProperty("java.io.tmpdir"));

        backendManager.addFolder(folder, PICTURE);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        PictureFoldersHandler pictureFoldersHandler = new PictureFoldersHandler(backendManager);
        ConfigurationFolder newFolder = pictureFoldersHandler.addPictureFolder(folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
        verify(backendManager);
    }

    @Test
    public void testEditPictureFolder() {
        BackendManager backendManager = createMock(BackendManager.class);
        ConfigurationFolder folder = new ConfigurationFolder("imagesTest", "editedImagesTest", System.getProperty("java.io.tmpdir"));

        backendManager.editFolder("imagesTest", folder, PICTURE);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        PictureFoldersHandler pictureFoldersHandler = new PictureFoldersHandler(backendManager);
        ConfigurationFolder newFolder = pictureFoldersHandler.editPictureFolder("imagesTest", folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
        verify(backendManager);
    }

    @Test
    public void testRemovePictureFolder() {
        BackendManager backendManager = createMock(BackendManager.class);

        backendManager.removeFolder("imagesTest", PICTURE);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        PictureFoldersHandler pictureFoldersHandler = new PictureFoldersHandler(backendManager);
        ConfigurationFolder folder = pictureFoldersHandler.removePictureFolder("imagesTest");
        assertNotNull(folder);
        assertEquals(folder.getId(), "imagesTest");
        verify(backendManager);
    }
}
