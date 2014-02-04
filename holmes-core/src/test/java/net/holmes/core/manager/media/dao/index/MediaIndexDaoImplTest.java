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

package net.holmes.core.manager.media.dao.index;

import net.holmes.core.common.event.ConfigurationEvent;
import net.holmes.core.manager.configuration.ConfigurationNode;
import net.holmes.core.manager.media.model.RootNode;
import org.junit.Test;

import static net.holmes.core.common.event.ConfigurationEvent.EventType.*;
import static org.junit.Assert.*;

public class MediaIndexDaoImplTest {

    /**
     * Check that adding same data returns the same uuid
     */
    @Test
    public void testAddToMediaIndex() {
        MediaIndexDaoImpl mediaIndexDao = new MediaIndexDaoImpl();

        String uuid1 = mediaIndexDao.add(new MediaIndexElement("parentId", "mediaType", "mimeType", "path", "name", true, false));
        MediaIndexElement indexElement = mediaIndexDao.get(uuid1);
        assertNotNull(indexElement);
        assertEquals(indexElement.getName(), "name");
        String uuid2 = mediaIndexDao.add(new MediaIndexElement("parentId", "mediaType", "mimeType", "path", "name", true, false));
        assertEquals(uuid1, uuid2);

        uuid2 = mediaIndexDao.add(new MediaIndexElement("parentId", "mediaType", "path", "mimeType", "name2", true, false));
        assertFalse(uuid1.equals(uuid2));
    }

    @Test
    public void testRemoveChildrenMediaIndex() {
        MediaIndexDaoImpl mediaIndexDao = new MediaIndexDaoImpl();

        String uuid1 = mediaIndexDao.add(new MediaIndexElement("parentId", "mediaType", "mimeType", "path", "name", true, false));
        assertNotNull(mediaIndexDao.get(uuid1));
        String uuid2 = mediaIndexDao.add(new MediaIndexElement(uuid1, "mediaType", "mimeType", "path", "name", true, false));
        assertNotNull(mediaIndexDao.get(uuid2));
        mediaIndexDao.removeChildren(uuid1);
        assertNotNull(mediaIndexDao.get(uuid1));
        assertNull(mediaIndexDao.get(uuid2));
    }

    @Test
    public void testCleanMediaIndexNonExistingParent() {
        MediaIndexDaoImpl mediaIndexDao = new MediaIndexDaoImpl();

        String uuid1 = mediaIndexDao.add(new MediaIndexElement("parentId", "mediaType", "mimeType", "path", "name", true, false));
        assertNotNull(mediaIndexDao.get(uuid1));
        mediaIndexDao.clean();
        assertNull(mediaIndexDao.get(uuid1));
    }

    @Test
    public void testCleanMediaIndexNonExistingFile() {
        MediaIndexDaoImpl mediaIndexDao = new MediaIndexDaoImpl();

        String uuid1 = mediaIndexDao.add(new MediaIndexElement("1_VIDEOS", "mediaType", "mimeType", "path", "name", true, false));
        assertNotNull(mediaIndexDao.get(uuid1));
        mediaIndexDao.clean();
        assertNull(mediaIndexDao.get(uuid1));
    }

    @Test
    public void testCleanMediaIndexNonLocalPath() {
        MediaIndexDaoImpl mediaIndexDao = new MediaIndexDaoImpl();

        String uuid1 = mediaIndexDao.add(new MediaIndexElement("4_PODCASTS", "mediaType", "mimeType", "path", "name", false, false));
        assertNotNull(mediaIndexDao.get(uuid1));
        mediaIndexDao.clean();
        assertNull(mediaIndexDao.get(uuid1));
    }

    @Test
    public void testCleanMediaIndexLocked() {
        MediaIndexDaoImpl mediaIndexDao = new MediaIndexDaoImpl();

        String uuid1 = mediaIndexDao.add(new MediaIndexElement("4_PODCASTS", "mediaType", "mimeType", "path", "name", false, true));
        assertNotNull(mediaIndexDao.get(uuid1));
        mediaIndexDao.clean();
        assertNotNull(mediaIndexDao.get(uuid1));
    }

    @Test
    public void testHandleConfigEventAdd() {
        MediaIndexDaoImpl mediaIndexDao = new MediaIndexDaoImpl();

        ConfigurationEvent configurationEvent = new ConfigurationEvent(ADD_FOLDER, new ConfigurationNode("id", "label", "path"), RootNode.VIDEO);
        mediaIndexDao.handleConfigEvent(configurationEvent);
    }

    @Test
    public void testHandleConfigEventUpdate() {
        MediaIndexDaoImpl mediaIndexDao = new MediaIndexDaoImpl();

        ConfigurationEvent configurationEvent = new ConfigurationEvent(UPDATE_FOLDER, new ConfigurationNode("id", "label", "path"), RootNode.VIDEO);
        mediaIndexDao.handleConfigEvent(configurationEvent);
    }

    @Test
    public void testHandleConfigEventDelete() {
        MediaIndexDaoImpl mediaIndexDao = new MediaIndexDaoImpl();

        ConfigurationEvent configurationEvent = new ConfigurationEvent(DELETE_FOLDER, new ConfigurationNode("id", "label", "path"), RootNode.VIDEO);
        mediaIndexDao.handleConfigEvent(configurationEvent);
    }

    @Test
    public void testHandleConfigEventUnknown() {
        MediaIndexDaoImpl mediaIndexDao = new MediaIndexDaoImpl();

        ConfigurationEvent configurationEvent = new ConfigurationEvent(ConfigurationEvent.EventType.UNKNOWN, new ConfigurationNode("id", "label", "path"), RootNode.VIDEO);
        mediaIndexDao.handleConfigEvent(configurationEvent);
    }
}
