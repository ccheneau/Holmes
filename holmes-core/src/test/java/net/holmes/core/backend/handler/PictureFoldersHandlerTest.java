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

package net.holmes.core.backend.handler;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.TestModule;
import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.response.ConfigurationFolder;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PictureFoldersHandlerTest {
    @Inject
    private BackendManager backendManager;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testGetPictureFolders() {
        PictureFoldersHandler pictureFoldersHandler = new PictureFoldersHandler(backendManager);
        assertNotNull(pictureFoldersHandler.getPictureFolders());
    }

    @Test
    public void testGetPictureFolder() {
        PictureFoldersHandler pictureFoldersHandler = new PictureFoldersHandler(backendManager);
        assertNotNull(pictureFoldersHandler.getPictureFolder("imagesTest"));
    }

    @Test
    public void testAddPictureFolder() {
        PictureFoldersHandler pictureFoldersHandler = new PictureFoldersHandler(backendManager);
        ConfigurationFolder folder = new ConfigurationFolder(null, "newImagesTest", System.getProperty("java.io.tmpdir"));
        ConfigurationFolder newFolder = pictureFoldersHandler.addPictureFolder(folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
    }

    @Test
    public void testEditPictureFolder() {
        PictureFoldersHandler pictureFoldersHandler = new PictureFoldersHandler(backendManager);
        ConfigurationFolder folder = new ConfigurationFolder("imagesTest", "editedImagesTest", System.getProperty("java.io.tmpdir"));
        ConfigurationFolder newFolder = pictureFoldersHandler.editPictureFolder("imagesTest", folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
    }

    @Test
    public void testRemovePictureFolder() {
        PictureFoldersHandler pictureFoldersHandler = new PictureFoldersHandler(backendManager);
        ConfigurationFolder folder = pictureFoldersHandler.removePictureFolder("imagesTest");
        assertNotNull(folder);
        assertEquals(folder.getId(), "imagesTest");
    }
}
