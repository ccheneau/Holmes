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

import net.holmes.core.backend.response.DeviceBrowseResult;
import net.holmes.core.backend.response.PlaybackDevice;
import net.holmes.core.backend.response.PlaybackStatus;
import net.holmes.core.business.media.MediaManager;
import net.holmes.core.business.media.MediaSearchRequest;
import net.holmes.core.business.media.model.AbstractNode;
import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.media.model.FolderNode;
import net.holmes.core.business.media.model.PodcastNode;
import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.business.streaming.StreamingManager;
import net.holmes.core.business.streaming.device.Device;
import net.holmes.core.business.streaming.device.UnknownDeviceException;
import net.holmes.core.business.streaming.session.StreamingSession;
import net.holmes.core.business.streaming.session.UnknownSessionException;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static net.holmes.core.backend.response.DeviceBrowseResult.*;
import static net.holmes.core.business.media.model.RootNode.VIDEO;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class StreamingHandlerTest {

    @Test
    public void testGetDevices() {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        Device device = createMock(Device.class);

        expect(streamingManager.getDevices()).andReturn(newArrayList(device)).atLeastOnce();
        expect(device.getId()).andReturn("deviceId").atLeastOnce();
        expect(device.getName()).andReturn("deviceName").atLeastOnce();
        expect(device.getType()).andReturn("deviceType").atLeastOnce();
        expect(device.isVideoSupported()).andReturn(true).atLeastOnce();
        expect(device.isAudioSupported()).andReturn(true).atLeastOnce();
        expect(device.isImageSupported()).andReturn(true).atLeastOnce();
        expect(device.isSlideShowSupported()).andReturn(true).atLeastOnce();

        replay(mediaManager, streamingManager, device);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        List<PlaybackDevice> devices = streamingHandler.getDevices();
        assertNotNull(devices);
        assertEquals(1, devices.size());
        PlaybackDevice playbackDevice = devices.get(0);
        assertEquals("deviceId", playbackDevice.getDeviceId());
        assertEquals("deviceName", playbackDevice.getDeviceName());
        assertEquals("deviceType", playbackDevice.getDeviceType());
        assertTrue(playbackDevice.isVideoSupported());
        assertTrue(playbackDevice.isAudioSupported());
        assertTrue(playbackDevice.isImageSupported());
        assertTrue(playbackDevice.isSlideShowSupported());
        verify(mediaManager, streamingManager, device);
    }

    @Test
    public void testPlayOK() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);

        expect(mediaManager.getNode(eq("contentId"))).andReturn(new ContentNode("id", "parentId", "name", new File("file"), MimeType.valueOf("video/x-msvideo"))).atLeastOnce();
        expect(mediaManager.getNodeUrl(isA(AbstractNode.class))).andReturn("contentUrl").atLeastOnce();
        streamingManager.play(eq("deviceId"), eq("contentUrl"), isA(AbstractNode.class));
        expectLastCall().atLeastOnce();

        replay(mediaManager, streamingManager);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        String result = streamingHandler.play("deviceId", "contentId");
        assertNull(result);
        verify(mediaManager, streamingManager);
    }

    @Test
    public void testPlayKO() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);

        expect(mediaManager.getNode(eq("contentId"))).andReturn(new ContentNode("id", "parentId", "name", new File("file"), MimeType.valueOf("video/x-msvideo"))).atLeastOnce();
        expect(mediaManager.getNodeUrl(isA(AbstractNode.class))).andReturn("contentUrl").atLeastOnce();
        streamingManager.play(eq("deviceId"), eq("contentUrl"), isA(AbstractNode.class));
        expectLastCall().andThrow(new UnknownDeviceException("deviceId")).atLeastOnce();

        replay(mediaManager, streamingManager);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        String result = streamingHandler.play("deviceId", "contentId");
        assertNotNull(result);
        assertTrue(result.contains("deviceId"));
        verify(mediaManager, streamingManager);
    }

    @Test
    public void testPauseOK() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);

        streamingManager.pause(eq("deviceId"));
        expectLastCall().atLeastOnce();

        replay(mediaManager, streamingManager);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        String result = streamingHandler.pause("deviceId");
        assertNull(result);
        verify(mediaManager, streamingManager);
    }

    @Test
    public void testPauseKO() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);

        streamingManager.pause(eq("deviceId"));
        expectLastCall().andThrow(new UnknownDeviceException("deviceId")).atLeastOnce();

        replay(mediaManager, streamingManager);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        String result = streamingHandler.pause("deviceId");
        assertNotNull(result);
        assertTrue(result.contains("deviceId"));
        verify(mediaManager, streamingManager);
    }

    @Test
    public void testStopOK() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);

        streamingManager.stop(eq("deviceId"));
        expectLastCall().atLeastOnce();

        replay(mediaManager, streamingManager);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        String result = streamingHandler.stop("deviceId");
        assertNull(result);
        verify(mediaManager, streamingManager);
    }

    @Test
    public void testStopKO() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);

        streamingManager.stop(eq("deviceId"));
        expectLastCall().andThrow(new UnknownDeviceException("deviceId")).atLeastOnce();

        replay(mediaManager, streamingManager);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        String result = streamingHandler.stop("deviceId");
        assertNotNull(result);
        assertTrue(result.contains("deviceId"));
        verify(mediaManager, streamingManager);
    }

    @Test
    public void testResumeOK() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);

        streamingManager.resume(eq("deviceId"));
        expectLastCall().atLeastOnce();

        replay(mediaManager, streamingManager);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        String result = streamingHandler.resume("deviceId");
        assertNull(result);
        verify(mediaManager, streamingManager);
    }

    @Test
    public void testResumeKO() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);

        streamingManager.resume(eq("deviceId"));
        expectLastCall().andThrow(new UnknownDeviceException("deviceId")).atLeastOnce();

        replay(mediaManager, streamingManager);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        String result = streamingHandler.resume("deviceId");
        assertNotNull(result);
        assertTrue(result.contains("deviceId"));
        verify(mediaManager, streamingManager);
    }

    @Test
    public void testStatusOK() throws UnknownSessionException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);

        StreamingSession session = new StreamingSession("contentName", "contentUrl");
        expect(streamingManager.getSession(eq("deviceId"))).andReturn(session).atLeastOnce();

        replay(mediaManager, streamingManager);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        PlaybackStatus result = streamingHandler.status("deviceId");
        assertNotNull(result);
        assertEquals(result.getContentName(), session.getContentName());
        assertEquals(result.getDuration(), session.getDuration().longValue());
        assertEquals(result.getPosition(), session.getPosition().longValue());
        verify(mediaManager, streamingManager);
    }

    @Test
    public void testStatusKO() throws UnknownSessionException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);

        expect(streamingManager.getSession(eq("deviceId"))).andThrow(new UnknownSessionException("deviceId"));

        replay(mediaManager, streamingManager);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        PlaybackStatus result = streamingHandler.status("deviceId");
        assertNotNull(result);
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("deviceId"));
        verify(mediaManager, streamingManager);
    }

    @Test
    public void testBrowseRootVideoNotSupported() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        Device device = createMock(Device.class);

        expect(streamingManager.getDevice(eq("deviceId"))).andReturn(device).atLeastOnce();
        expect(mediaManager.getNode(eq("0"))).andReturn(null).atLeastOnce();
        expect(device.isVideoSupported()).andReturn(false).atLeastOnce();

        replay(mediaManager, streamingManager, device);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        DeviceBrowseResult result = streamingHandler.browse("deviceId", "0");
        assertNotNull(result);
        assertEquals(result.getParentNodeId(), "0");

        verify(mediaManager, streamingManager, device);
    }

    @Test
    public void testBrowseRootVideoSupported() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        Device device = createMock(Device.class);

        FolderNode videoRootNode = new FolderNode(VIDEO.getId(), VIDEO.getParentId(), VIDEO.getId());
        ContentNode contentNode = new ContentNode("id", "parentId", "name", new File("file"), MimeType.valueOf("video/x-msvideo"));
        FolderNode folderNode = new FolderNode("id", "parentId", "name");
        Collection<AbstractNode> searchResult = newArrayList(contentNode, folderNode);

        expect(streamingManager.getDevice(eq("deviceId"))).andReturn(device).atLeastOnce();
        expect(mediaManager.getNode(eq("0"))).andReturn(null).atLeastOnce();
        expect(mediaManager.getNode(eq(VIDEO.getId()))).andReturn(videoRootNode).atLeastOnce();
        expect(mediaManager.searchChildNodes(isA(MediaSearchRequest.class))).andReturn(searchResult).atLeastOnce();
        expect(mediaManager.getNodeUrl(isA(AbstractNode.class))).andReturn("nodeUrl").atLeastOnce();
        expect(device.isVideoSupported()).andReturn(true).atLeastOnce();
        expect(device.getSupportedMimeTypes()).andReturn(null).atLeastOnce();

        replay(mediaManager, streamingManager, device);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        DeviceBrowseResult result = streamingHandler.browse("deviceId", "0");
        assertNotNull(result);
        assertEquals(result.getParentNodeId(), "0");
        assertEquals(1, result.getContents().size());
        assertEquals(1, result.getFolders().size());

        verify(mediaManager, streamingManager, device);
    }

    @Test
    public void testBrowseValidNode() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        Device device = createMock(Device.class);

        FolderNode node = new FolderNode("nodeId", "nodeParentId", "nodeName");
        ContentNode contentNode = new ContentNode("idContent", "parentId", "nameContent", new File("file"), MimeType.valueOf("video/x-msvideo"));
        FolderNode folderNode = new FolderNode("idFolder", "parentId", "nameFolder");
        PodcastNode podcastNode = new PodcastNode("idPodcast", "parentId", "namePodcast", "podcastUrl");
        Collection<AbstractNode> searchResult = newArrayList(contentNode, folderNode, podcastNode);

        expect(streamingManager.getDevice(eq("deviceId"))).andReturn(device).atLeastOnce();
        expect(mediaManager.getNode(eq("nodeId"))).andReturn(node).atLeastOnce();
        expect(mediaManager.searchChildNodes(isA(MediaSearchRequest.class))).andReturn(searchResult).atLeastOnce();
        expect(mediaManager.getNodeUrl(isA(AbstractNode.class))).andReturn("nodeUrl").atLeastOnce();
        expect(device.getSupportedMimeTypes()).andReturn(null).atLeastOnce();

        replay(mediaManager, streamingManager, device);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        DeviceBrowseResult result = streamingHandler.browse("deviceId", "nodeId");
        assertNotNull(result);
        assertEquals(result.getParentNodeId(), "nodeId");
        assertEquals(1, result.getContents().size());
        BrowseContent browseContent = result.getContents().get(0);
        assertEquals("idContent", browseContent.getNodeId());
        assertEquals("nameContent", browseContent.getContentName());
        assertEquals("nodeUrl", browseContent.getContentUrl());
        assertEquals(1, result.getFolders().size());
        BrowseFolder browseFolder = result.getFolders().get(0);
        assertEquals("idFolder", browseFolder.getNodeId());
        assertEquals("nameFolder", browseFolder.getFolderName());
        verify(mediaManager, streamingManager, device);
    }

    @Test
    public void testBrowseUnknownDevice() throws UnknownDeviceException {
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        Device device = createMock(Device.class);

        expect(streamingManager.getDevice(eq("deviceId"))).andThrow(new UnknownDeviceException("deviceId"));

        replay(mediaManager, streamingManager, device);
        StreamingHandler streamingHandler = new StreamingHandler(mediaManager, streamingManager);
        DeviceBrowseResult result = streamingHandler.browse("deviceId", "0");
        assertNotNull(result);
        assertEquals(result.getParentNodeId(), "0");
        assertTrue(result.getErrorMessage().contains("deviceId"));

        verify(mediaManager, streamingManager, device);
    }
}
