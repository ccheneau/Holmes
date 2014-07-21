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
import net.holmes.core.business.configuration.UnknownNodeException;
import net.holmes.core.common.event.ConfigurationEvent;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static net.holmes.core.business.media.model.RootNode.AUDIO;
import static net.holmes.core.business.media.model.RootNode.PODCAST;
import static net.holmes.core.common.ConfigurationParameter.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

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
    public void testGetFolder() throws UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNode(eq(AUDIO), eq("id"))).andReturn(new ConfigurationNode("id", "name", "path"));

        replay(configurationDao, eventBus);

        BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
        assertNotNull(backendManager.getFolder("id", AUDIO));

        verify(configurationDao, eventBus);
    }

    @Test
    public void testGetNullFolder() throws UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNode(eq(AUDIO), eq("id"))).andReturn(null);

        replay(configurationDao, eventBus);

        BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
        assertNull(backendManager.getFolder("id", AUDIO));

        verify(configurationDao, eventBus);
    }

    @Test(expected = BackendException.class)
    public void testGetBadFolder() throws UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNode(eq(AUDIO), eq("bad_id"))).andThrow(new UnknownNodeException("bad_id"));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.getFolder("bad_id", AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testGetBadPodcast() throws UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.getNode(eq(PODCAST), eq("bad_id"))).andThrow(new UnknownNodeException("bad_id"));

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

        expect(configurationDao.findNode(AUDIO, null, "newAudioFolder", System.getProperty("java.io.tmpdir"))).andReturn(null);
        expect(configurationDao.addNode(eq(AUDIO), isA(ConfigurationNode.class))).andReturn(true);
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

    @Test
    public void testAddExistingFolder() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.findNode(AUDIO, null, "newAudioFolder", System.getProperty("java.io.tmpdir"))).andReturn(null);
        expect(configurationDao.addNode(eq(AUDIO), isA(ConfigurationNode.class))).andReturn(false);

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

        expect(configurationDao.findNode(AUDIO, null, "newAudioFolder", System.getProperty("java.io.tmpdir"))).andReturn(null);
        configurationDao.addNode(eq(AUDIO), isA(ConfigurationNode.class));
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

        expect(configurationDao.findNode(AUDIO, null, "name", System.getProperty("java.io.tmpdir"))).andReturn(new ConfigurationNode("id", "name", "path"));

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

        expect(configurationDao.findNode(AUDIO, null, "newAudioFolder", System.getProperty("java.io.tmpdir"))).andReturn(new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir")));

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

        expect(configurationDao.findNode(PODCAST, null, "newPodcast", "http://google.com")).andReturn(null);
        expect(configurationDao.addNode(eq(PODCAST), isA(ConfigurationNode.class))).andReturn(true);
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

        expect(configurationDao.findNode(PODCAST, null, "name", "http://google.com")).andReturn(new ConfigurationNode("id", "name", "http://google.com"));

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

        expect(configurationDao.findNode(PODCAST, null, "newPodcast", "http://google.com")).andReturn(new ConfigurationNode("id", "name", "http://google.com"));

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

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.addFolder(new ConfigurationFolder(null, "newPodcast", "bad_url"), PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }


    @Test(expected = BackendException.class)
    public void testEditFolderIOException() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.findNode(AUDIO, "id", "editedAudiosTest", System.getProperty("java.io.tmpdir"))).andReturn(null);
        expect(configurationDao.editNode(AUDIO, "id", "editedAudiosTest", System.getProperty("java.io.tmpdir"))).andThrow(new IOException());

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("id", new ConfigurationFolder("id", "editedAudiosTest", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testEditFolder() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.findNode(AUDIO, "id", "name", System.getProperty("java.io.tmpdir"))).andReturn(null);
        expect(configurationDao.editNode(AUDIO, "id", "name", System.getProperty("java.io.tmpdir"))).andReturn(new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir")));
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
    public void testEditPodcast() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.findNode(PODCAST, "id", "editedPodcast", "http://google.com")).andReturn(null);
        expect(configurationDao.editNode(PODCAST, "id", "editedPodcast", "http://google.com")).andReturn(new ConfigurationNode("id", "editedPodcast", "http://google.com"));
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
    public void testEditBadFolder() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.findNode(AUDIO, "bad_id", "editedAudiosTest", System.getProperty("java.io.tmpdir"))).andReturn(null);
        expect(configurationDao.editNode(AUDIO, "bad_id", "editedAudiosTest", System.getProperty("java.io.tmpdir"))).andThrow(new UnknownNodeException("bad_id"));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("bad_id", new ConfigurationFolder("id", "editedAudiosTest", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testEditBadPodcast() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.findNode(PODCAST, "bad_id", "editedPodcast", "http://google.com")).andReturn(null);
        expect(configurationDao.editNode(PODCAST, "bad_id", "editedPodcast", "http://google.com")).andThrow(new UnknownNodeException("bad_id"));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("bad_id", new ConfigurationFolder("id", "editedPodcast", "http://google.com"), PODCAST);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testEditFolderNotSaved() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.findNode(AUDIO, "id", "name", System.getProperty("java.io.tmpdir"))).andReturn(null);
        expect(configurationDao.editNode(AUDIO, "id", "name", System.getProperty("java.io.tmpdir"))).andReturn(null);

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.editFolder("id", new ConfigurationFolder("id", "name", System.getProperty("java.io.tmpdir")), AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testRemoveFolder() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        ConfigurationNode node = new ConfigurationNode("id", "name", System.getProperty("java.io.tmpdir"));
        expect(configurationDao.removeNode("id", AUDIO)).andReturn(node);
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
    public void testRemoveFolderIOException() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.removeNode(eq("id"), eq(AUDIO))).andThrow(new IOException());

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.removeFolder("id", AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test
    public void testRemovePodcast() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.removeNode("id", PODCAST)).andReturn(new ConfigurationNode("id", "label", "path"));
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
    public void testRemoveBadFolder() throws UnknownNodeException, IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.removeNode("bad_folder", AUDIO)).andThrow(new UnknownNodeException("bad_folder"));

        replay(configurationDao, eventBus);

        try {
            BackendManagerImpl backendManager = new BackendManagerImpl(configurationDao, eventBus);
            backendManager.removeFolder("bad_folder", AUDIO);
        } finally {
            verify(configurationDao, eventBus);
        }
    }

    @Test(expected = BackendException.class)
    public void testRemoveBadPodcast() throws UnknownNodeException, IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);
        EventBus eventBus = createMock(EventBus.class);

        expect(configurationDao.removeNode("bad_id", PODCAST)).andThrow(new UnknownNodeException("bad_id"));

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
        configurationDao.save();
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
        configurationDao.save();
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
