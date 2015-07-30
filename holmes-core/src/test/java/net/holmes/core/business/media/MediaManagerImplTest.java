/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

package net.holmes.core.business.media;

import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.configuration.ConfigurationManagerImpl;
import net.holmes.core.business.configuration.dao.ConfigurationDao;
import net.holmes.core.business.media.dao.MediaDao;
import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.media.model.FolderNode;
import net.holmes.core.business.media.model.MediaNode;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.common.event.MediaEvent;
import net.holmes.core.test.TestConfigurationDao;
import org.junit.Test;

import java.io.File;
import java.net.InetAddress;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static net.holmes.core.business.media.model.RootNode.*;
import static net.holmes.core.common.event.MediaEvent.MediaEventType.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class MediaManagerImplTest {

    @Test
    public void testGetNodeRoot() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        Optional<MediaNode> result = mediaManager.getNode(VIDEO.getId());
        assertNotNull(result);
        assertTrue(result.isPresent());

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    @Test
    public void testGetNode() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        expect(mediaDao.getNode(eq("nodeId"))).andReturn(Optional.of(new FolderNode("id", "parentId", "name")));

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        Optional<MediaNode> result = mediaManager.getNode("nodeId");
        assertNotNull(result);
        assertTrue(result.isPresent());

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    @Test
    public void testGetNodeNull() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        Optional<MediaNode> result = mediaManager.getNode(null);
        assertNotNull(result);
        assertFalse(result.isPresent());

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    @Test
    public void testGetNodeUrl() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        expect(localAddress.getHostAddress()).andReturn("localHost");

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        FolderNode node = new FolderNode("id", "parentId", "name");
        String result = mediaManager.getNodeUrl(node);

        assertNotNull(result);

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    @Test
    public void testCleanupCache() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        mediaDao.cleanUpCache();
        expectLastCall();

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        mediaManager.cleanUpCache();

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    @Test
    public void testHandleMediaEvent() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        expect(mediaDao.getRootNodeChildren(eq(VIDEO))).andReturn(getRootChildNodes(VIDEO, configurationDao));
        expect(mediaDao.getChildNodes(eq("videosTest"))).andReturn(new ArrayList<>());

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        MediaEvent mediaEvent = new MediaEvent(SCAN_NODE, VIDEO.getId());
        mediaManager.handleMediaEvent(mediaEvent);

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    @Test
    public void testHandleBadMediaEvent() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        MediaEvent mediaEvent = new MediaEvent(UNKNOWN, VIDEO.getId());
        mediaManager.handleMediaEvent(mediaEvent);

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    @Test
    public void testHandleMediaEventBadFolder() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        MimeType audioMimeType = MimeType.valueOf("audio/mp3");

        expect(mediaDao.getNode(eq("nodeId"))).andReturn(Optional.of(new ContentNode("id", "parentId", "name", new File(""), audioMimeType)));

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        MediaEvent mediaEvent = new MediaEvent(SCAN_NODE, "nodeId");
        mediaManager.handleMediaEvent(mediaEvent);

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    @Test
    public void testSearchChildNodesOfRoot() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        expect(mediaDao.getRootNodeChildren(eq(VIDEO))).andReturn(getRootChildNodes(VIDEO, configurationDao));
        expect(mediaDao.getRootNodeChildren(eq(PICTURE))).andReturn(getRootChildNodes(PICTURE, configurationDao));
        expect(mediaDao.getRootNodeChildren(eq(AUDIO))).andReturn(getRootChildNodes(AUDIO, configurationDao));

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        MediaSearchRequest request = new MediaSearchRequest(new FolderNode(ROOT.getId(), ROOT.getParentId(), ROOT.getBundleKey()), null);
        Collection<MediaNode> result = mediaManager.searchChildNodes(request);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    @Test
    public void testSearchChildNodesOfVideoRoot() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        expect(mediaDao.getRootNodeChildren(eq(VIDEO))).andReturn(getRootChildNodes(VIDEO, configurationDao));

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        MediaSearchRequest request = new MediaSearchRequest(new FolderNode(VIDEO.getId(), VIDEO.getParentId(), VIDEO.getBundleKey()), newArrayList("video/avi"));
        Collection<MediaNode> result = mediaManager.searchChildNodes(request);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSearchChildNodesOfFolder() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message");
        MediaDao mediaDao = createMock(MediaDao.class);
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        InetAddress localAddress = createMock(InetAddress.class);

        List<MediaNode> childNodes = new ArrayList<>();
        childNodes.add(new FolderNode("id", "parentId", "name"));
        MimeType videoMimeType = MimeType.valueOf("video/avi");
        MimeType audioMimeType = MimeType.valueOf("audio/mp3");
        childNodes.add(new ContentNode("id1", "parentId", "name", new File(""), videoMimeType));
        childNodes.add(new ContentNode("id2", "parentId", "name", new File(""), audioMimeType));

        expect(mediaDao.getChildNodes(eq("folderId"))).andReturn(childNodes);
        expect(mimeTypeManager.isMimeTypeCompliant(eq(videoMimeType), isA(List.class))).andReturn(true);
        expect(mimeTypeManager.isMimeTypeCompliant(eq(audioMimeType), isA(List.class))).andReturn(false);

        replay(mediaDao, mimeTypeManager, localAddress);

        MediaManagerImpl mediaManager = new MediaManagerImpl(configurationManager, resourceBundle, mediaDao, mimeTypeManager, localAddress);
        MediaSearchRequest request = new MediaSearchRequest(new FolderNode("folderId", "folderParentId", "folderName"), newArrayList("video/avi"));
        Collection<MediaNode> result = mediaManager.searchChildNodes(request);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());

        verify(mediaDao, mimeTypeManager, localAddress);
    }

    public List<MediaNode> getRootChildNodes(RootNode rootNode, ConfigurationDao configurationDao) {
        // Add folder nodes stored in configuration
        return configurationDao.getNodes(rootNode).stream()
                .map(configNode -> new FolderNode(configNode.getId(), rootNode.getId(), configNode.getLabel(), new File(configNode.getPath())))
                .collect(toList());
    }
}
