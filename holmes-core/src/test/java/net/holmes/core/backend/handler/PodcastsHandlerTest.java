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

import static net.holmes.core.manager.media.model.RootNode.PODCAST;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PodcastsHandlerTest {

    @Test
    public void testGetPodcasts() {
        BackendManager backendManager = createMock(BackendManager.class);

        expect(backendManager.getFolders(PODCAST)).andReturn(Lists.newArrayList(new ConfigurationFolder("fauxRaccordsTest", "editedCastcodersTest", "http://google.fr"))).atLeastOnce();

        replay(backendManager);
        PodcastsHandler podcastsHandler = new PodcastsHandler(backendManager);
        assertNotNull(podcastsHandler.getPodcasts());
        verify(backendManager);
    }

    @Test
    public void testGetPodcast() {
        BackendManager backendManager = createMock(BackendManager.class);

        expect(backendManager.getFolder("fauxRaccordsTest", PODCAST)).andReturn(new ConfigurationFolder("fauxRaccordsTest", "editedCastcodersTest", "http://google.fr")).atLeastOnce();

        replay(backendManager);
        PodcastsHandler podcastsHandler = new PodcastsHandler(backendManager);
        assertNotNull(podcastsHandler.getPodcast("fauxRaccordsTest"));
        verify(backendManager);
    }

    @Test
    public void testAddPodcast() {
        BackendManager backendManager = createMock(BackendManager.class);
        ConfigurationFolder folder = new ConfigurationFolder(null, "newPodcast", "http://google.com");

        backendManager.addFolder(folder, PODCAST);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        PodcastsHandler podcastsHandler = new PodcastsHandler(backendManager);
        ConfigurationFolder newFolder = podcastsHandler.addPodcast(folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
        verify(backendManager);
    }

    @Test
    public void testEditPodcast() {
        BackendManager backendManager = createMock(BackendManager.class);
        ConfigurationFolder folder = new ConfigurationFolder("fauxRaccordsTest", "editedCastcodersTest", "http://google.fr");

        backendManager.editFolder("fauxRaccordsTest", folder, PODCAST);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        PodcastsHandler podcastsHandler = new PodcastsHandler(backendManager);
        ConfigurationFolder newFolder = podcastsHandler.editPodcast("fauxRaccordsTest", folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
        verify(backendManager);
    }

    @Test
    public void testRemovePodcast() {
        BackendManager backendManager = createMock(BackendManager.class);

        backendManager.removeFolder("fauxRaccordsTest", PODCAST);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        PodcastsHandler podcastsHandler = new PodcastsHandler(backendManager);
        ConfigurationFolder folder = podcastsHandler.removePodcast("fauxRaccordsTest");
        assertNotNull(folder);
        assertEquals(folder.getId(), "fauxRaccordsTest");
        verify(backendManager);
    }
}
