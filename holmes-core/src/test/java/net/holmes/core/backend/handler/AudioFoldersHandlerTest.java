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

import static net.holmes.core.manager.media.model.RootNode.AUDIO;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AudioFoldersHandlerTest {

    @Test
    public void testGetAudioFolders() {
        BackendManager backendManager = createMock(BackendManager.class);

        expect(backendManager.getFolders(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationFolder("audiosTest", "audiosTest", "path"))).atLeastOnce();

        replay(backendManager);
        AudioFoldersHandler audioFoldersHandler = new AudioFoldersHandler(backendManager);
        assertNotNull(audioFoldersHandler.getAudioFolders());
        verify(backendManager);
    }

    @Test
    public void testGetAudioFolder() {
        BackendManager backendManager = createMock(BackendManager.class);

        expect(backendManager.getFolder("audiosTest", AUDIO)).andReturn(new ConfigurationFolder("audiosTest", "audiosTest", "path")).atLeastOnce();

        replay(backendManager);
        AudioFoldersHandler audioFoldersHandler = new AudioFoldersHandler(backendManager);
        assertNotNull(audioFoldersHandler.getAudioFolder("audiosTest"));
        verify(backendManager);
    }

    @Test
    public void testAddAudioFolder() {
        BackendManager backendManager = createMock(BackendManager.class);
        ConfigurationFolder folder = new ConfigurationFolder(null, "newAudioFolder", System.getProperty("java.io.tmpdir"));

        backendManager.addFolder(folder, AUDIO);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        AudioFoldersHandler audioFoldersHandler = new AudioFoldersHandler(backendManager);
        ConfigurationFolder newFolder = audioFoldersHandler.addAudioFolder(folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
        verify(backendManager);
    }

    @Test
    public void testEditAudioFolder() {
        BackendManager backendManager = createMock(BackendManager.class);
        ConfigurationFolder folder = new ConfigurationFolder("audiosTest", "editedAudiosTest", System.getProperty("java.io.tmpdir"));

        backendManager.editFolder("audiosTest", folder, AUDIO);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        AudioFoldersHandler audioFoldersHandler = new AudioFoldersHandler(backendManager);
        ConfigurationFolder newFolder = audioFoldersHandler.editAudioFolder("audiosTest", folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
        verify(backendManager);
    }

    @Test
    public void testRemoveAudioFolder() {
        BackendManager backendManager = createMock(BackendManager.class);

        backendManager.removeFolder("audiosTest", AUDIO);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        AudioFoldersHandler audioFoldersHandler = new AudioFoldersHandler(backendManager);
        ConfigurationFolder folder = audioFoldersHandler.removeAudioFolder("audiosTest");
        assertNotNull(folder);
        assertEquals(folder.getId(), "audiosTest");
        verify(backendManager);
    }
}
