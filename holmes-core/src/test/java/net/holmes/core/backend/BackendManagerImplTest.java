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

package net.holmes.core.backend;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import net.holmes.core.backend.exception.BackendException;
import net.holmes.core.backend.response.ConfigurationFolder;
import net.holmes.core.backend.response.Settings;
import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.configuration.ConfigurationNode;
import net.holmes.core.common.event.ConfigurationEvent;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static net.holmes.core.business.media.model.RootNode.AUDIO;
import static net.holmes.core.business.media.model.RootNode.PODCAST;
import static net.holmes.core.common.parameter.ConfigurationParameter.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BackendManagerImplTest {

    @Test
    public void testGetFolders() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
        Collection<ConfigurationFolder> folders = backendManager.getFolders(AUDIO);
        assertNotNull(folders);
        assertEquals(1, folders.size());

        verify(configurationDao, eventBus);
    }

    @Test
    public void testGetFolder() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
        assertNotNull(backendManager.getFolder("id", AUDIO));

        verify(configurationDao, eventBus);
    }

    @Test(expected = BackendException.class)
    public void testGetBadFolder() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            assertNotNull(backendManager.getFolder("bad_id", AUDIO));
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testGetBadPodcast() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            assertNotNull(backendManager.getFolder("bad_id", PODCAST));
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testAddFolder() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));
        configurationDao.saveConfig();
        expectLastCall();
        eventBus.post(isA(ConfigurationEvent.class));
        expectLastCall();

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "newAudioFolder", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddFolderIOException() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));
        configurationDao.saveConfig();
        expectLastCall().andThrow(new IOException());

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "newAudioFolder", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddFolderWithSameName() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "name", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddFolderWithSamePath() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir"))));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "newAudioFolder", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testAddPodcast() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));
        configurationDao.saveConfig();
        expectLastCall();
        eventBus.post(isA(ConfigurationEvent.class));
        expectLastCall();

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "newPodcast", "http://google.com"), PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddPodcastWithSameName() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "name", "http://google.com"), PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddPodcastWithSameUrl() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "http://google.com")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "newPodcast", "http://google.com"), PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddFolderWithoutName() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, null, System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddFolderWithoutPath() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "newAudioFolder", null), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddFolderWithBadPath() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "newAudioFolder", "bad_path"), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddPodcastWithoutName() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, null, "http://google.com"), PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddPodcastWithoutUrl() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "newPodcast", null), PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testAddPodcastWithBadUrl() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "newPodcast", "bad_url"), PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testEditFolderName() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir"))));
        configurationDao.saveConfig();
        expectLastCall();
        eventBus.post(isA(ConfigurationEvent.class));
        expectLastCall();

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("id", new ConfigurationFolder("id", "editedAudiosTest", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testEditFolderIOException() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir"))));
        configurationDao.saveConfig();
        expectLastCall().andThrow(new IOException());

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("id", new ConfigurationFolder("id", "editedAudiosTest", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testEditFolderPath() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));
        configurationDao.saveConfig();
        expectLastCall();
        eventBus.post(isA(ConfigurationEvent.class));
        expectLastCall();

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("id", new ConfigurationFolder("id", "name", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testEditFolderNameAndPath() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "path")));
        configurationDao.saveConfig();
        expectLastCall();
        eventBus.post(isA(ConfigurationEvent.class));
        expectLastCall();

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("id", new ConfigurationFolder("id", "editedAudiosTest", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testEditFolderNoChanges() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir"))));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("id", new ConfigurationFolder("id", "name", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testEditPodcast() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "http://google.com")));
        configurationDao.saveConfig();
        expectLastCall();
        eventBus.post(isA(ConfigurationEvent.class));
        expectLastCall();

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("id", new ConfigurationFolder("id", "editedPodcast", "http://google.com"), PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testEditBadFolder() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir"))));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("bad_id", new ConfigurationFolder("id", "editedAudiosTest", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testEditBadPodcast() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "http://google.com")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("bad_id", new ConfigurationFolder("id", "editedPodcast", "http://google.com"), PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testRemoveFolder() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir"))));
        configurationDao.saveConfig();
        expectLastCall();
        eventBus.post(isA(ConfigurationEvent.class));
        expectLastCall();

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.removeFolder("id", AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testRemoveFolderIOException() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir"))));
        configurationDao.saveConfig();
        expectLastCall().andThrow(new IOException());

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.removeFolder("id", AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testRemovePodcast() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "http://google.com")));
        configurationDao.saveConfig();
        expectLastCall();
        eventBus.post(isA(ConfigurationEvent.class));
        expectLastCall();

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.removeFolder("id", PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testRemoveBadFolder() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(AUDIO)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir"))));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.removeFolder("bad_folder", AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testRemoveBadPodcast() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNodes(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationNode("id", "name", "http://google.com")));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.removeFolder("bad_id", PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testGetSettings() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getParameter(UPNP_SERVER_NAME)).andReturn("serverName");
        expect(configurationDao.getParameter(PODCAST_PREPEND_ENTRY_NAME)).andReturn(true);
        expect(configurationDao.getParameter(ICECAST_ENABLE)).andReturn(false);

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            Settings settings = backendManager.getSettings();
            assertNotNull(settings);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testSaveSettings() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        configurationDao.setParameter(UPNP_SERVER_NAME, "holmes");
        expectLastCall();
        configurationDao.setParameter(PODCAST_PREPEND_ENTRY_NAME, true);
        expectLastCall();
        configurationDao.setParameter(ICECAST_ENABLE, true);
        expectLastCall();
        configurationDao.saveConfig();
        expectLastCall();
        eventBus.post(isA(ConfigurationEvent.class));
        expectLastCall();

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.saveSettings(new Settings("holmes", true, true));
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testSaveSettingsWithoutServerName() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.saveSettings(new Settings(null, true, true));
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testSaveSettingsIOException() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        configurationDao.setParameter(UPNP_SERVER_NAME, "holmes");
        expectLastCall();
        configurationDao.setParameter(PODCAST_PREPEND_ENTRY_NAME, true);
        expectLastCall();
        configurationDao.setParameter(ICECAST_ENABLE, true);
        expectLastCall();
        configurationDao.saveConfig();
        expectLastCall().andThrow(new IOException());

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.saveSettings(new Settings("holmes", true, true));
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testSaveSettingsWithEmptyServerName() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.saveSettings(new Settings("", true, true));
        } finally {
            verify(configurationDao, eventBus);
        }
    }
}
