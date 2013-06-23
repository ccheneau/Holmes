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

public class PodcastsHandlerTest {
    @Inject
    private BackendManager backendManager;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testGetPodcasts() {
        PodcastsHandler podcastsHandler = new PodcastsHandler(backendManager);
        assertNotNull(podcastsHandler.getPodcasts());
    }

    @Test
    public void testGetPodcast() {
        PodcastsHandler podcastsHandler = new PodcastsHandler(backendManager);
        assertNotNull(podcastsHandler.getPodcast("castcodersTest"));
    }

    @Test
    public void testAddPodcast() {
        PodcastsHandler podcastsHandler = new PodcastsHandler(backendManager);
        ConfigurationFolder folder = new ConfigurationFolder(null, "newPodcast", "http://google.com");
        ConfigurationFolder newFolder = podcastsHandler.addPodcast(folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
    }

    @Test
    public void testEditPodcast() {
        PodcastsHandler podcastsHandler = new PodcastsHandler(backendManager);
        ConfigurationFolder folder = new ConfigurationFolder("castcodersTest", "editedCastcodersTest", "http://google.fr");
        ConfigurationFolder newFolder = podcastsHandler.editPodcast("castcodersTest", folder);
        assertNotNull(newFolder);
        assertEquals(newFolder, folder);
    }

    @Test
    public void testRemoveAudioFolder() {
        PodcastsHandler podcastsHandler = new PodcastsHandler(backendManager);
        ConfigurationFolder folder = podcastsHandler.removePodcast("castcodersTest");
        assertNotNull(folder);
        assertEquals(folder.getId(), "castcodersTest");
    }
}
