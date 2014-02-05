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

import static net.holmes.core.business.media.model.RootNode.VIDEO;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VideoFoldersHandlerTest {

    @Test
    public void testGetVideoFolders() {
        BackendManager backendManager = createMock(BackendManager.class);

        expect(backendManager.getFolders(VIDEO)).andReturn(Lists.newArrayList(new ConfigurationFolder("videosTest", "videosTest", System.getProperty("java.io.tmpdir")))).atLeastOnce();

        replay(backendManager);
        VideoFoldersHandler videoFoldersHandler = new VideoFoldersHandler(backendManager);
        assertNotNull(videoFoldersHandler.getVideoFolders());
        verify(backendManager);
    }

    @Test
    public void testGetVideoFolder() {
        BackendManager backendManager = createMock(BackendManager.class);

        expect(backendManager.getFolder("videosTest", VIDEO)).andReturn(new ConfigurationFolder("videosTest", "videosTest", System.getProperty("java.io.tmpdir"))).atLeastOnce();

        replay(backendManager);
        VideoFoldersHandler videoFoldersHandler = new VideoFoldersHandler(backendManager);
        assertNotNull(videoFoldersHandler.getVideoFolder("videosTest"));
        verify(backendManager);
    }

    @Test
    public void testAddVideoFolder() {
        BackendManager backendManager = createMock(BackendManager.class);
        ConfigurationFolder folder = new ConfigurationFolder(null, "newVideoFolder", System.getProperty("java.io.tmpdir"));

        backendManager.addFolder(folder, VIDEO);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        VideoFoldersHandler videoFoldersHandler = new VideoFoldersHandler(backendManager);
        ConfigurationFolder newFolder = videoFoldersHandler.addVideoFolder(folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
        verify(backendManager);
    }

    @Test
    public void testEditVideoFolder() {
        BackendManager backendManager = createMock(BackendManager.class);
        ConfigurationFolder folder = new ConfigurationFolder("videosTest", "editedVideosTest", System.getProperty("java.io.tmpdir"));

        backendManager.editFolder("videosTest", folder, VIDEO);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        VideoFoldersHandler videoFoldersHandler = new VideoFoldersHandler(backendManager);
        ConfigurationFolder newFolder = videoFoldersHandler.editVideoFolder("videosTest", folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
        verify(backendManager);
    }

    @Test
    public void testRemoveVideoFolder() {
        BackendManager backendManager = createMock(BackendManager.class);

        backendManager.removeFolder("videosTest", VIDEO);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        VideoFoldersHandler videoFoldersHandler = new VideoFoldersHandler(backendManager);
        ConfigurationFolder folder = videoFoldersHandler.removeVideoFolder("videosTest");
        assertNotNull(folder);
        assertEquals(folder.getId(), "videosTest");
        verify(backendManager);
    }
}
