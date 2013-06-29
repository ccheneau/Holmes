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

package net.holmes.core.backend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.backend.response.ConfigurationFolder;
import net.holmes.core.backend.response.Settings;
import net.holmes.core.media.model.RootNode;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.*;

public class BackendManagerTest {

    @Inject
    private BackendManager backendManager;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testGetFolders() {
        assertNotNull(backendManager.getFolders(RootNode.AUDIO));
        assertNotNull(backendManager.getFolders(RootNode.PICTURE));
        assertNotNull(backendManager.getFolders(RootNode.VIDEO));
        assertNotNull(backendManager.getFolders(RootNode.PODCAST));
    }

    @Test
    public void testGetFolder() {
        // Test get folders
        assertNotNull(backendManager.getFolder("audiosTest", RootNode.AUDIO));
        assertNotNull(backendManager.getFolder("imagesTest", RootNode.PICTURE));
        assertNotNull(backendManager.getFolder("videosTest", RootNode.VIDEO));
        assertNotNull(backendManager.getFolder("fauxRaccordsTest", RootNode.PODCAST));

        // Check get bab folder throws exception
        try {
            backendManager.getFolder("badFolder", RootNode.VIDEO);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // Check get bab podcast folder throws exception
        try {
            backendManager.getFolder("badFolder", RootNode.PODCAST);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testAddFolder() {
        // test nominal add folder
        int originalSize = backendManager.getFolders(RootNode.AUDIO).size();
        backendManager.addFolder(new ConfigurationFolder(null, "newAudioFolder", System.getProperty("java.io.tmpdir")), RootNode.AUDIO);
        assertEquals(backendManager.getFolders(RootNode.AUDIO).size(), originalSize + 1);

        // test add folder with same name throws exception
        try {
            backendManager.addFolder(new ConfigurationFolder(null, "newAudioFolder", System.getProperty("java.io.tmpdir")), RootNode.AUDIO);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // test add folder with same path throws exception
        try {
            backendManager.addFolder(new ConfigurationFolder(null, "newAudioFolder2", System.getProperty("java.io.tmpdir")), RootNode.AUDIO);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testAddPodcast() {
        // test nominal add folder
        int originalSize = backendManager.getFolders(RootNode.PODCAST).size();
        backendManager.addFolder(new ConfigurationFolder(null, "newPodcast", "http://google.com"), RootNode.PODCAST);
        assertEquals(backendManager.getFolders(RootNode.PODCAST).size(), originalSize + 1);

        // test add podcast with same name throws exception
        try {
            backendManager.addFolder(new ConfigurationFolder(null, "newPodcast", "http://google.com"), RootNode.PODCAST);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // test add podcast with same url throws exception
        try {
            backendManager.addFolder(new ConfigurationFolder(null, "newPodcast2", "http://google.com"), RootNode.PODCAST);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testAddBadFolder() {
        // test add folder without name throws exception
        try {
            backendManager.addFolder(new ConfigurationFolder(null, null, System.getProperty("java.io.tmpdir")), RootNode.AUDIO);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // test add folder without path throws exception
        try {
            backendManager.addFolder(new ConfigurationFolder(null, "newAudioFolder", null), RootNode.AUDIO);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // test add folder with bad path throws exception
        try {
            backendManager.addFolder(new ConfigurationFolder(null, "newAudioFolder", "bad_path"), RootNode.AUDIO);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testAddBadPodcast() {
        // test add podcast without name throws exception
        try {
            backendManager.addFolder(new ConfigurationFolder(null, null, "http://google.com"), RootNode.PODCAST);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // test add podcast without url throws exception
        try {
            backendManager.addFolder(new ConfigurationFolder(null, "newPodcast", null), RootNode.PODCAST);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // test add podcast with bad url throws exception
        try {
            backendManager.addFolder(new ConfigurationFolder(null, "newPodcast", "bad_url"), RootNode.PODCAST);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testEditFolder() {
        // test nominal edit folder
        backendManager.editFolder("audiosTest", new ConfigurationFolder("audiosTest", "editedAudiosTest", System.getProperty("java.io.tmpdir")), RootNode.AUDIO);
        ConfigurationFolder folder = backendManager.getFolder("audiosTest", RootNode.AUDIO);
        assertEquals(folder.getName(), "editedAudiosTest");
        assertEquals(folder.getPath(), System.getProperty("java.io.tmpdir"));
    }

    @Test
    public void testEditPodcast() {
        // test nominal edit podcast
        backendManager.editFolder("fauxRaccordsTest", new ConfigurationFolder("fauxRaccordsTest", "editedPodcast", "http://google.com"), RootNode.PODCAST);
        ConfigurationFolder folder = backendManager.getFolder("fauxRaccordsTest", RootNode.PODCAST);
        assertEquals(folder.getName(), "editedPodcast");
        assertEquals(folder.getPath(), "http://google.com");
    }

    @Test
    public void testEditBadFolder() {
        // test nominal edit bad folder
        try {
            backendManager.editFolder("bad_folder", new ConfigurationFolder("bad_folder", "editedAudiosTest", System.getProperty("java.io.tmpdir")), RootNode.AUDIO);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);

        }
    }

    @Test
    public void testEditBadPodcast() {
        // test nominal edit bad podcast
        try {
            backendManager.editFolder("bad_podcast", new ConfigurationFolder("fauxRaccordsTest", "editedPodcast", "http://google.com"), RootNode.PODCAST);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);

        }
    }

    @Test
    public void testRemoveFolder() {
        // test nominal remove folder
        int originalSize = backendManager.getFolders(RootNode.AUDIO).size();
        backendManager.removeFolder("audiosTest", RootNode.AUDIO);
        assertEquals(backendManager.getFolders(RootNode.AUDIO).size(), originalSize - 1);
    }

    @Test
    public void testRemovePodcast() {
        // test nominal remove podcast
        int originalSize = backendManager.getFolders(RootNode.PODCAST).size();
        backendManager.removeFolder("fauxRaccordsTest", RootNode.PODCAST);
        assertEquals(backendManager.getFolders(RootNode.PODCAST).size(), originalSize - 1);
    }

    @Test
    public void testRemoveBadFolder() {
        // test nominal remove bad folder
        try {
            backendManager.removeFolder("bad_folder", RootNode.AUDIO);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testRemoveBadPodcast() {
        // test nominal remove bad podcast
        try {
            backendManager.removeFolder("bad_podcast", RootNode.PODCAST);
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testGetSettings() {
        // test nominal get settings
        assertNotNull(backendManager.getSettings());
    }

    @Test
    public void testSaveSettings() {
        // test nominal save settings
        backendManager.saveSettings(new Settings("holmes", 8085, true, true, true));
    }

    @Test
    public void testSaveBadSettings() {
        // test nominal save bad settings
        try {
            backendManager.saveSettings(new Settings(null, 8085, true, true, true));
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
        try {
            backendManager.saveSettings(new Settings("", 8085, true, true, true));
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
        try {
            backendManager.saveSettings(new Settings("holmes", null, true, true, true));
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
        try {
            backendManager.saveSettings(new Settings("holmes", 80, true, true, true));
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }
}
