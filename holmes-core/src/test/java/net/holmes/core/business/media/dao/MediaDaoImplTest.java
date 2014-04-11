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

package net.holmes.core.business.media.dao;

import com.google.common.collect.Lists;
import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.configuration.ConfigurationNode;
import net.holmes.core.business.media.dao.icecast.IcecastDao;
import net.holmes.core.business.media.dao.icecast.IcecastEntry;
import net.holmes.core.business.media.dao.icecast.IcecastGenre;
import net.holmes.core.business.media.dao.index.MediaIndexDao;
import net.holmes.core.business.media.dao.index.MediaIndexElement;
import net.holmes.core.business.media.model.*;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.common.MimeType;
import net.holmes.core.common.UniqueIdGenerator;
import net.holmes.core.test.TestConfigurationDao;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static net.holmes.core.business.media.model.RootNode.*;
import static net.holmes.core.common.MediaType.*;
import static net.holmes.core.common.MimeType.MIME_TYPE_SUBTITLE;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class MediaDaoImplTest {

    @Test
    public void testGetNodeNotInIndex() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(null);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNull(result);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetPodcastNode() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        MediaIndexElement podcastElement = new MediaIndexElement(PODCAST.getId(), TYPE_PODCAST.getValue(), null, "path", "name", PODCAST.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(podcastElement);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), PodcastNode.class);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetIcecastGenreNode() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        MediaIndexElement icecastGenreElement = new MediaIndexElement(ICECAST.getId(), TYPE_ICECAST_GENRE.getValue(), null, "path", "name", ICECAST.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(icecastGenreElement);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), IcecastGenreNode.class);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetRawUrlNode() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        MediaIndexElement rawUrlElement = new MediaIndexElement(VIDEO.getId(), TYPE_RAW_URL.getValue(), "video/avi", "path", "name", ICECAST.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(rawUrlElement);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), RawUrlNode.class);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetBadFileNode() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", "path", "name", VIDEO.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNull(result);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetVideoFolderNode() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        ConfigurationNode videoNode = configurationDao.getFolders(VIDEO).get(0);

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoNode.getPath(), "name", VIDEO.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), FolderNode.class);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetVideoFolderNodeWithNoName() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        ConfigurationNode videoNode = configurationDao.getFolders(VIDEO).get(0);

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoNode.getPath(), null, VIDEO.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), FolderNode.class);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetVideoFileNode() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        ConfigurationNode videoNode = configurationDao.getFolders(VIDEO).get(0);
        Path videoFilePath = Paths.get(videoNode.getPath(), "video.avi");

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoFilePath.toFile().getPath(), videoFilePath.toFile().getName(), VIDEO.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);
        expect(mimeTypeManager.getMimeType(eq("video.avi"))).andReturn(MimeType.valueOf("video/avi"));

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), ContentNode.class);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetVideoFileNodeNoMimeType() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        ConfigurationNode videoNode = configurationDao.getFolders(VIDEO).get(0);
        Path videoFilePath = Paths.get(videoNode.getPath(), "video.avi");

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoFilePath.toFile().getPath(), videoFilePath.toFile().getName(), VIDEO.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);
        expect(mimeTypeManager.getMimeType(eq("video.avi"))).andReturn(null);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNull(result);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetChildNodesNotInIndex() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(null);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertTrue(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetChildNodesOfPodcast() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        ConfigurationNode podcastNode = configurationDao.getFolders(PODCAST).get(0);
        MediaIndexElement podcastElement = new MediaIndexElement(PODCAST.getId(), TYPE_PODCAST.getValue(), null, podcastNode.getPath(), podcastNode.getLabel(), PODCAST.isLocalPath(), true);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(podcastElement);
        mediaIndexDao.removeChildren(eq("nodeId"));
        expectLastCall();
        expect(mediaIndexDao.add(isA(MediaIndexElement.class))).andReturn(UniqueIdGenerator.newUniqueId()).atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertFalse(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetChildNodesOfBadPodcast() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        MediaIndexElement podcastElement = new MediaIndexElement(PODCAST.getId(), TYPE_PODCAST.getValue(), null, "badPath", "badLabel", PODCAST.isLocalPath(), true);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(podcastElement);
        mediaIndexDao.removeChildren(eq("nodeId"));
        expectLastCall();

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertTrue(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetChildNodesOfIcecastGenre() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        MediaIndexElement icecastElement = new MediaIndexElement(ICECAST.getId(), TYPE_ICECAST_GENRE.getValue(), null, "icecastGenre", "icecastGenre", ICECAST.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(icecastElement);
        expect(icecastDao.getEntriesByGenre(eq("icecastGenre"))).andReturn(Lists.newArrayList(new IcecastEntry("name", "url", "type", "genre")));
        expect(mediaIndexDao.add(isA(MediaIndexElement.class))).andReturn(UniqueIdGenerator.newUniqueId()).atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertFalse(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetChildNodesOfRawUrl() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        MediaIndexElement rawUrlElement = new MediaIndexElement(ICECAST.getId(), TYPE_RAW_URL.getValue(), null, "rawUrl", "rawUrl", ICECAST.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(rawUrlElement);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertTrue(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetChildNodesOfVideoFolder() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        ConfigurationNode videoNode = configurationDao.getFolders(VIDEO).get(0);
        Path videoFolderPath = Paths.get(videoNode.getPath(), "subFolder");

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoFolderPath.toFile().getPath(), videoFolderPath.toFile().getName(), VIDEO.isLocalPath(), true);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);
        expect(mediaIndexDao.add(isA(MediaIndexElement.class))).andReturn(UniqueIdGenerator.newUniqueId()).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.avi"))).andReturn(MimeType.valueOf("video/avi")).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.unknown"))).andReturn(null).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.srt"))).andReturn(MIME_TYPE_SUBTITLE).atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertFalse(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetChildNodesOfVideoFile() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        ConfigurationNode videoNode = configurationDao.getFolders(VIDEO).get(0);
        Path videoFolderPath = Paths.get(videoNode.getPath(), "video.avi");

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoFolderPath.toFile().getPath(), videoFolderPath.toFile().getName(), VIDEO.isLocalPath(), true);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertTrue(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetChildNodesOfVideoFolderBadMediaType() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        ConfigurationNode videoNode = configurationDao.getFolders(VIDEO).get(0);
        Path videoFolderPath = Paths.get(videoNode.getPath(), "subFolder");

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_AUDIO.getValue(), "video/avi", videoFolderPath.toFile().getPath(), videoFolderPath.toFile().getName(), VIDEO.isLocalPath(), true);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);
        expect(mediaIndexDao.add(isA(MediaIndexElement.class))).andReturn(UniqueIdGenerator.newUniqueId()).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.avi"))).andReturn(MimeType.valueOf("video/avi")).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.unknown"))).andReturn(null).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.srt"))).andReturn(MIME_TYPE_SUBTITLE).atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertFalse(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testCleanupCache() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        mediaIndexDao.clean();
        expectLastCall();

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        mediaDao.cleanUpCache();

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetSubRootChildNodesOfPodcast() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        mediaIndexDao.put(isA(String.class), isA(MediaIndexElement.class));
        expectLastCall().atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getSubRootChildNodes(PODCAST);
        assertNotNull(result);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

    @Test
    public void testGetSubRootChildNodesOfIcecast() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        expect(icecastDao.getGenres()).andReturn(Lists.newArrayList(new IcecastGenre("genreId", "genreName")));
        mediaIndexDao.put(isA(String.class), isA(MediaIndexElement.class));
        expectLastCall().atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getSubRootChildNodes(ICECAST);
        assertNotNull(result);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }


    @Test
    public void testGetSubRootChildNodesOfVideo() {
        ConfigurationDao configurationDao = new TestConfigurationDao();
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);
        IcecastDao icecastDao = createMock(IcecastDao.class);

        mediaIndexDao.put(isA(String.class), isA(MediaIndexElement.class));
        expectLastCall().atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao, icecastDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationDao, mimeTypeManager, mediaIndexDao, icecastDao);

        List<AbstractNode> result = mediaDao.getSubRootChildNodes(VIDEO);
        assertNotNull(result);

        verify(mimeTypeManager, mediaIndexDao, icecastDao);
    }

}
