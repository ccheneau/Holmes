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
import net.holmes.core.backend.response.DeviceBrowseResult;
import net.holmes.core.backend.response.PlaybackDevice;
import net.holmes.core.backend.response.PlaybackStatus;
import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.ContentNode;
import net.holmes.core.media.model.FolderNode;
import net.holmes.core.media.model.PodcastNode;
import net.holmes.core.transport.TransportService;
import net.holmes.core.transport.device.Device;
import net.holmes.core.transport.device.UnknownDeviceException;
import net.holmes.core.transport.session.StreamingSession;
import net.holmes.core.transport.session.UnknownSessionException;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static net.holmes.core.backend.response.DeviceBrowseResult.BrowseContent;
import static net.holmes.core.backend.response.DeviceBrowseResult.BrowseFolder;
import static net.holmes.core.media.MediaService.ChildNodeResult;
import static net.holmes.core.media.model.RootNode.VIDEO;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class StreamingHandlerTest {

    @Test
    public void testGetDevices() {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);
        Device device = createMock(Device.class);

        expect(transportService.getDevices()).andReturn(Lists.<Device>newArrayList(device)).atLeastOnce();
        expect(device.getId()).andReturn("deviceId").atLeastOnce();
        expect(device.getName()).andReturn("deviceName").atLeastOnce();
        expect(device.getType()).andReturn("deviceType").atLeastOnce();
        expect(device.isVideoSupported()).andReturn(true).atLeastOnce();
        expect(device.isAudioSupported()).andReturn(true).atLeastOnce();
        expect(device.isImageSupported()).andReturn(true).atLeastOnce();
        expect(device.isSlideShowSupported()).andReturn(true).atLeastOnce();

        replay(mediaService, transportService, device);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
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
        verify(mediaService, transportService, device);
    }

    @Test
    public void testPlayOK() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);

        expect(mediaService.getNode(eq("contentId"))).andReturn(new ContentNode("id", "parentId", "name", new File("file"), MimeType.valueOf("video/x-msvideo"))).atLeastOnce();
        expect(mediaService.getNodeUrl(isA(AbstractNode.class))).andReturn("contentUrl").atLeastOnce();
        transportService.play(eq("deviceId"), eq("contentUrl"), isA(AbstractNode.class));
        expectLastCall().atLeastOnce();

        replay(mediaService, transportService);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        String result = streamingHandler.play("deviceId", "contentId");
        assertNull(result);
        verify(mediaService, transportService);
    }

    @Test
    public void testPlayKO() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);

        expect(mediaService.getNode(eq("contentId"))).andReturn(new ContentNode("id", "parentId", "name", new File("file"), MimeType.valueOf("video/x-msvideo"))).atLeastOnce();
        expect(mediaService.getNodeUrl(isA(AbstractNode.class))).andReturn("contentUrl").atLeastOnce();
        transportService.play(eq("deviceId"), eq("contentUrl"), isA(AbstractNode.class));
        expectLastCall().andThrow(new UnknownDeviceException("deviceId")).atLeastOnce();

        replay(mediaService, transportService);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        String result = streamingHandler.play("deviceId", "contentId");
        assertNotNull(result);
        assertTrue(result.contains("deviceId"));
        verify(mediaService, transportService);
    }

    @Test
    public void testPauseOK() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);

        transportService.pause(eq("deviceId"));
        expectLastCall().atLeastOnce();

        replay(mediaService, transportService);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        String result = streamingHandler.pause("deviceId");
        assertNull(result);
        verify(mediaService, transportService);
    }

    @Test
    public void testPauseKO() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);

        transportService.pause(eq("deviceId"));
        expectLastCall().andThrow(new UnknownDeviceException("deviceId")).atLeastOnce();

        replay(mediaService, transportService);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        String result = streamingHandler.pause("deviceId");
        assertNotNull(result);
        assertTrue(result.contains("deviceId"));
        verify(mediaService, transportService);
    }

    @Test
    public void testStopOK() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);

        transportService.stop(eq("deviceId"));
        expectLastCall().atLeastOnce();

        replay(mediaService, transportService);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        String result = streamingHandler.stop("deviceId");
        assertNull(result);
        verify(mediaService, transportService);
    }

    @Test
    public void testStopKO() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);

        transportService.stop(eq("deviceId"));
        expectLastCall().andThrow(new UnknownDeviceException("deviceId")).atLeastOnce();

        replay(mediaService, transportService);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        String result = streamingHandler.stop("deviceId");
        assertNotNull(result);
        assertTrue(result.contains("deviceId"));
        verify(mediaService, transportService);
    }

    @Test
    public void testResumeOK() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);

        transportService.resume(eq("deviceId"));
        expectLastCall().atLeastOnce();

        replay(mediaService, transportService);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        String result = streamingHandler.resume("deviceId");
        assertNull(result);
        verify(mediaService, transportService);
    }

    @Test
    public void testResumeKO() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);

        transportService.resume(eq("deviceId"));
        expectLastCall().andThrow(new UnknownDeviceException("deviceId")).atLeastOnce();

        replay(mediaService, transportService);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        String result = streamingHandler.resume("deviceId");
        assertNotNull(result);
        assertTrue(result.contains("deviceId"));
        verify(mediaService, transportService);
    }

    @Test
    public void testStatusOK() throws UnknownSessionException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);

        StreamingSession session = new StreamingSession();
        session.setContentName("contentName");
        session.setDuration(1l);
        session.setPosition(0l);
        expect(transportService.getSession(eq("deviceId"))).andReturn(session).atLeastOnce();

        replay(mediaService, transportService);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        PlaybackStatus result = streamingHandler.status("deviceId");
        assertNotNull(result);
        assertEquals(result.getContentName(), session.getContentName());
        assertEquals(result.getDuration(), session.getDuration().longValue());
        assertEquals(result.getPosition(), session.getPosition().longValue());
        verify(mediaService, transportService);
    }

    @Test
    public void testStatusKO() throws UnknownSessionException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);

        expect(transportService.getSession(eq("deviceId"))).andThrow(new UnknownSessionException("deviceId"));

        replay(mediaService, transportService);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        PlaybackStatus result = streamingHandler.status("deviceId");
        assertNotNull(result);
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("deviceId"));
        verify(mediaService, transportService);
    }

    @Test
    public void testBrowseRootVideoNotSupported() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);
        Device device = createMock(Device.class);

        expect(transportService.getDevice(eq("deviceId"))).andReturn(device).atLeastOnce();
        expect(mediaService.getNode(eq("0"))).andReturn(null).atLeastOnce();
        expect(device.isVideoSupported()).andReturn(false).atLeastOnce();

        replay(mediaService, transportService, device);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        DeviceBrowseResult result = streamingHandler.browse("deviceId", "0");
        assertNotNull(result);
        assertEquals(result.getParentNodeId(), "0");

        verify(mediaService, transportService, device);
    }

    @Test
    public void testBrowseRootVideoSupported() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);
        Device device = createMock(Device.class);

        FolderNode videoRootNode = new FolderNode(VIDEO.getId(), VIDEO.getParentId(), VIDEO.getId());
        ContentNode contentNode = new ContentNode("id", "parentId", "name", new File("file"), MimeType.valueOf("video/x-msvideo"));
        FolderNode folderNode = new FolderNode("id", "parentId", "name");
        ChildNodeResult childNodeResult = new ChildNodeResult(Lists.newArrayList(contentNode, folderNode), 2);

        expect(transportService.getDevice(eq("deviceId"))).andReturn(device).atLeastOnce();
        expect(mediaService.getNode(eq("0"))).andReturn(null).atLeastOnce();
        expect(mediaService.getNode(eq(VIDEO.getId()))).andReturn(videoRootNode).atLeastOnce();
        expect(mediaService.getChildNodes(isA(MediaService.ChildNodeRequest.class))).andReturn(childNodeResult).atLeastOnce();
        expect(mediaService.getNodeUrl(isA(AbstractNode.class))).andReturn("nodeUrl").atLeastOnce();
        expect(device.isVideoSupported()).andReturn(true).atLeastOnce();
        expect(device.getSupportedMimeTypes()).andReturn(null).atLeastOnce();

        replay(mediaService, transportService, device);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        DeviceBrowseResult result = streamingHandler.browse("deviceId", "0");
        assertNotNull(result);
        assertEquals(result.getParentNodeId(), "0");
        assertEquals(1, result.getContents().size());
        assertEquals(1, result.getFolders().size());

        verify(mediaService, transportService, device);
    }

    @Test
    public void testBrowseValidNode() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);
        Device device = createMock(Device.class);

        FolderNode node = new FolderNode("nodeId", "nodeParentId", "nodeName");
        ContentNode contentNode = new ContentNode("idContent", "parentId", "nameContent", new File("file"), MimeType.valueOf("video/x-msvideo"));
        FolderNode folderNode = new FolderNode("idFolder", "parentId", "nameFolder");
        PodcastNode podcastNode = new PodcastNode("idPodcast", "parentId", "namePodcast", "podcastUrl");
        ChildNodeResult childNodeResult = new ChildNodeResult(Lists.newArrayList(contentNode, folderNode, podcastNode), 3);

        expect(transportService.getDevice(eq("deviceId"))).andReturn(device).atLeastOnce();
        expect(mediaService.getNode(eq("nodeId"))).andReturn(node).atLeastOnce();
        expect(mediaService.getChildNodes(isA(MediaService.ChildNodeRequest.class))).andReturn(childNodeResult).atLeastOnce();
        expect(mediaService.getNodeUrl(isA(AbstractNode.class))).andReturn("nodeUrl").atLeastOnce();
        expect(device.getSupportedMimeTypes()).andReturn(null).atLeastOnce();

        replay(mediaService, transportService, device);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
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
        verify(mediaService, transportService, device);
    }

    @Test
    public void testBrowseUnknownDevice() throws UnknownDeviceException {
        MediaService mediaService = createMock(MediaService.class);
        TransportService transportService = createMock(TransportService.class);
        Device device = createMock(Device.class);

        expect(transportService.getDevice(eq("deviceId"))).andThrow(new UnknownDeviceException("deviceId"));

        replay(mediaService, transportService, device);
        StreamingHandler streamingHandler = new StreamingHandler(mediaService, transportService);
        DeviceBrowseResult result = streamingHandler.browse("deviceId", "0");
        assertNotNull(result);
        assertEquals(result.getParentNodeId(), "0");
        assertTrue(result.getErrorMessage().contains("deviceId"));

        verify(mediaService, transportService, device);
    }
}
