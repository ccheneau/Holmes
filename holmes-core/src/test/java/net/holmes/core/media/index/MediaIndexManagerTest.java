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

package net.holmes.core.media.index;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.common.configuration.ConfigurationNode;
import net.holmes.core.common.event.ConfigurationEvent;
import net.holmes.core.media.model.RootNode;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static net.holmes.core.common.event.ConfigurationEvent.EventType.*;
import static org.junit.Assert.*;

public class MediaIndexManagerTest {
    @Inject
    private MediaIndexManager mediaIndexManager;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    /**
     * Check that adding same data returns the same uuid
     */
    @Test
    public void testAddToMediaIndex() {
        String uuid1 = mediaIndexManager.add(new MediaIndexElement("parentId", "mediaType", "path", "name", true));
        MediaIndexElement indexElement = mediaIndexManager.get(uuid1);
        assertNotNull(indexElement);
        assertEquals(indexElement.getName(), "name");
        String uuid2 = mediaIndexManager.add(new MediaIndexElement("parentId", "mediaType", "path", "name", true));
        assertEquals(uuid1, uuid2);

        uuid2 = mediaIndexManager.add(new MediaIndexElement("parentId", "mediaType", "path", "name2", true));
        assertFalse(uuid1.equals(uuid2));
    }

    @Test
    public void testRemoveFromMediaIndex() {
        String uuid1 = mediaIndexManager.add(new MediaIndexElement("parentId", "mediaType", "path", "name", true));
        assertNotNull(mediaIndexManager.get(uuid1));
        mediaIndexManager.remove(uuid1);
        assertNull(mediaIndexManager.get(uuid1));
    }

    @Test
    public void testRemoveChildrenMediaIndex() {
        String uuid1 = mediaIndexManager.add(new MediaIndexElement("parentId", "mediaType", "path", "name", true));
        assertNotNull(mediaIndexManager.get(uuid1));
        String uuid2 = mediaIndexManager.add(new MediaIndexElement(uuid1, "mediaType", "path", "name", true));
        assertNotNull(mediaIndexManager.get(uuid2));
        mediaIndexManager.removeChildren(uuid1);
        assertNotNull(mediaIndexManager.get(uuid1));
        assertNull(mediaIndexManager.get(uuid2));
    }

    @Test
    public void testCleanMediaIndexNonExistingParent() {
        String uuid1 = mediaIndexManager.add(new MediaIndexElement("parentId", "mediaType", "path", "name", true));
        assertNotNull(mediaIndexManager.get(uuid1));
        mediaIndexManager.clean();
        assertNull(mediaIndexManager.get(uuid1));
    }

    @Test
    public void testCleanMediaIndexNonExistingFile() {
        String uuid1 = mediaIndexManager.add(new MediaIndexElement("1_VIDEOS", "mediaType", "path", "name", true));
        assertNotNull(mediaIndexManager.get(uuid1));
        mediaIndexManager.clean();
        assertNull(mediaIndexManager.get(uuid1));
    }

    @Test
    public void testCleanMediaIndexNonLocalPath() {
        String uuid1 = mediaIndexManager.add(new MediaIndexElement("4_PODCASTS", "mediaType", "path", "name", false));
        assertNotNull(mediaIndexManager.get(uuid1));
        mediaIndexManager.clean();
        assertNotNull(mediaIndexManager.get(uuid1));
    }

    @Test
    public void testHandleConfigEventAdd() {
        if (mediaIndexManager instanceof MediaIndexManagerImpl) {
            MediaIndexManagerImpl mediaIndexManagerImpl = (MediaIndexManagerImpl) mediaIndexManager;
            ConfigurationEvent configurationEvent = new ConfigurationEvent(ADD_FOLDER, new ConfigurationNode("id", "label", "path"), RootNode.VIDEO);
            mediaIndexManagerImpl.handleConfigEvent(configurationEvent);
        }
    }

    @Test
    public void testHandleConfigEventUpdate() {
        if (mediaIndexManager instanceof MediaIndexManagerImpl) {
            MediaIndexManagerImpl mediaIndexManagerImpl = (MediaIndexManagerImpl) mediaIndexManager;
            ConfigurationEvent configurationEvent = new ConfigurationEvent(UPDATE_FOLDER, new ConfigurationNode("id", "label", "path"), RootNode.VIDEO);
            mediaIndexManagerImpl.handleConfigEvent(configurationEvent);
        }
    }

    @Test
    public void testHandleConfigEventDelete() {
        if (mediaIndexManager instanceof MediaIndexManagerImpl) {
            MediaIndexManagerImpl mediaIndexManagerImpl = (MediaIndexManagerImpl) mediaIndexManager;
            ConfigurationEvent configurationEvent = new ConfigurationEvent(DELETE_FOLDER, new ConfigurationNode("id", "label", "path"), RootNode.VIDEO);
            mediaIndexManagerImpl.handleConfigEvent(configurationEvent);
        }
    }

    @Test
    public void testHandleConfigEventUnknown() {
        if (mediaIndexManager instanceof MediaIndexManagerImpl) {
            MediaIndexManagerImpl mediaIndexManagerImpl = (MediaIndexManagerImpl) mediaIndexManager;
            ConfigurationEvent configurationEvent = new ConfigurationEvent(ConfigurationEvent.EventType.UNKNOWN, new ConfigurationNode("id", "label", "path"), RootNode.VIDEO);
            mediaIndexManagerImpl.handleConfigEvent(configurationEvent);
        }
    }
}
