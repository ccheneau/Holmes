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

package net.holmes.core.service.upnp.directory;

import com.google.common.collect.Lists;
import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.media.MediaManager;
import net.holmes.core.business.media.MediaSearchRequest;
import net.holmes.core.business.media.model.*;
import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.business.streaming.StreamingManager;
import net.holmes.core.business.streaming.airplay.device.AirplayDevice;
import net.holmes.core.business.streaming.upnp.device.UpnpDevice;
import org.fourthline.cling.model.message.Connection;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.SortCriterion;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static net.holmes.core.business.media.model.AbstractNode.NodeType.TYPE_PODCAST_ENTRY;
import static net.holmes.core.business.media.model.RootNode.VIDEO;
import static net.holmes.core.common.ConfigurationParameter.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;

public class ContentDirectoryServiceTest {

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testGetCapabilities() {
        ContentDirectoryService contentDirectoryService = new ContentDirectoryService();
        assertNotNull(contentDirectoryService.getSearchCapabilities());
        assertNotNull(contentDirectoryService.getSortCapabilities());
        assertNotNull(contentDirectoryService.getSystemUpdateID());
    }

    @Test
    public void testSearch() throws Exception {
        ContentDirectoryService contentDirectoryService = new ContentDirectoryService();
        BrowseResult result = contentDirectoryService.search("0", "", "", 0l, 1l, SortCriterion.valueOf("+name"), null);
        assertNotNull(result);
    }

    @Test
    public void testBrowseMetadata() throws ContentDirectoryException {
        ConfigurationManager configurationManager = createMock(ConfigurationManager.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        RemoteClientInfo remoteClientInfo = createMock(RemoteClientInfo.class);
        Connection connection = createMock(Connection.class);
        InetAddress inetAddress = createMock(InetAddress.class);
        UpnpDevice upnpDevice = createMock(UpnpDevice.class);
        AirplayDevice airplayDevice = createMock(AirplayDevice.class);

        ContentDirectoryService contentDirectoryService = new ContentDirectoryService();
        contentDirectoryService.setConfigurationManager(configurationManager);
        contentDirectoryService.setMediaManager(mediaManager);
        contentDirectoryService.setStreamingManager(streamingManager);

        expect(remoteClientInfo.getConnection()).andReturn(connection);
        expect(remoteClientInfo.getRemoteAddress()).andReturn(inetAddress);
        expect(inetAddress.getHostAddress()).andReturn("localhost");
        expect(streamingManager.findDevices(eq("localhost"))).andReturn(newArrayList(upnpDevice, airplayDevice));
        expect(upnpDevice.getSupportedMimeTypes()).andReturn(newArrayList("video/avi"));
        expect(mediaManager.getNode(eq("0"))).andReturn(new FolderNode("0", "-1", "root"));
        List<AbstractNode> rootChildren = Lists.<AbstractNode>newArrayList(new FolderNode(VIDEO.getId(), VIDEO.getParentId(), VIDEO.name()));
        expect(mediaManager.searchChildNodes(isA(MediaSearchRequest.class))).andReturn(rootChildren);
        expect(configurationManager.getParameter(UPNP_ADD_SUBTITLE)).andReturn(true);

        replay(mediaManager, streamingManager, remoteClientInfo, connection, inetAddress, upnpDevice, airplayDevice, configurationManager);

        BrowseResult result = contentDirectoryService.browse("0", BrowseFlag.METADATA, "", 0, 100, SortCriterion.valueOf("+name"), remoteClientInfo);
        assertNotNull(result);

        verify(mediaManager, streamingManager, remoteClientInfo, connection, inetAddress, upnpDevice, airplayDevice, configurationManager);
    }

    @Test
    public void testBrowseMetadataNoSubtitle() throws ContentDirectoryException {
        ConfigurationManager configurationManager = createMock(ConfigurationManager.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        RemoteClientInfo remoteClientInfo = createMock(RemoteClientInfo.class);
        Connection connection = createMock(Connection.class);
        InetAddress inetAddress = createMock(InetAddress.class);
        UpnpDevice upnpDevice = createMock(UpnpDevice.class);
        AirplayDevice airplayDevice = createMock(AirplayDevice.class);

        ContentDirectoryService contentDirectoryService = new ContentDirectoryService();
        contentDirectoryService.setConfigurationManager(configurationManager);
        contentDirectoryService.setMediaManager(mediaManager);
        contentDirectoryService.setStreamingManager(streamingManager);

        expect(remoteClientInfo.getConnection()).andReturn(connection);
        expect(remoteClientInfo.getRemoteAddress()).andReturn(inetAddress);
        expect(inetAddress.getHostAddress()).andReturn("localhost");
        expect(streamingManager.findDevices(eq("localhost"))).andReturn(newArrayList(upnpDevice, airplayDevice));
        expect(upnpDevice.getSupportedMimeTypes()).andReturn(newArrayList("video/avi"));
        expect(mediaManager.getNode(eq("0"))).andReturn(new FolderNode("0", "-1", "root"));
        List<AbstractNode> rootChildren = Lists.<AbstractNode>newArrayList(new FolderNode(VIDEO.getId(), VIDEO.getParentId(), VIDEO.name()));
        expect(mediaManager.searchChildNodes(isA(MediaSearchRequest.class))).andReturn(rootChildren);
        expect(configurationManager.getParameter(UPNP_ADD_SUBTITLE)).andReturn(false);

        replay(mediaManager, streamingManager, remoteClientInfo, connection, inetAddress, upnpDevice, airplayDevice, configurationManager);

        BrowseResult result = contentDirectoryService.browse("0", BrowseFlag.METADATA, "", 0, 100, SortCriterion.valueOf("+name"), remoteClientInfo);
        assertNotNull(result);

        verify(mediaManager, streamingManager, remoteClientInfo, connection, inetAddress, upnpDevice, airplayDevice, configurationManager);
    }

    @Test
    public void testBrowseMetadataBadClientInfo() throws ContentDirectoryException {
        ConfigurationManager configurationManager = createMock(ConfigurationManager.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        RemoteClientInfo remoteClientInfo = createMock(RemoteClientInfo.class);

        ContentDirectoryService contentDirectoryService = new ContentDirectoryService();
        contentDirectoryService.setConfigurationManager(configurationManager);
        contentDirectoryService.setMediaManager(mediaManager);
        contentDirectoryService.setStreamingManager(streamingManager);

        expect(remoteClientInfo.getConnection()).andReturn(null);
        expect(mediaManager.getNode(eq("0"))).andReturn(new FolderNode("0", "-1", "root"));
        List<AbstractNode> rootChildren = Lists.<AbstractNode>newArrayList(new FolderNode(VIDEO.getId(), VIDEO.getParentId(), VIDEO.name()));
        expect(mediaManager.searchChildNodes(isA(MediaSearchRequest.class))).andReturn(rootChildren);

        replay(mediaManager, streamingManager, remoteClientInfo, configurationManager);

        BrowseResult result = contentDirectoryService.browse("0", BrowseFlag.METADATA, "", 0, 100, SortCriterion.valueOf("+name"), remoteClientInfo);
        assertNotNull(result);

        verify(mediaManager, streamingManager, remoteClientInfo, configurationManager);
    }

    @Test(expected = ContentDirectoryException.class)
    public void testBrowseUnknownNode() throws ContentDirectoryException {
        ConfigurationManager configurationManager = createMock(ConfigurationManager.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        RemoteClientInfo remoteClientInfo = createMock(RemoteClientInfo.class);

        ContentDirectoryService contentDirectoryService = new ContentDirectoryService();
        contentDirectoryService.setConfigurationManager(configurationManager);
        contentDirectoryService.setMediaManager(mediaManager);
        contentDirectoryService.setStreamingManager(streamingManager);

        expect(mediaManager.getNode(eq("0"))).andReturn(null);

        replay(mediaManager, streamingManager, remoteClientInfo, configurationManager);

        try {
            BrowseResult result = contentDirectoryService.browse("0", BrowseFlag.METADATA, "", 0, 100, SortCriterion.valueOf("+name"), remoteClientInfo);
            assertNotNull(result);
        } finally {
            verify(mediaManager, streamingManager, remoteClientInfo, configurationManager);
        }
    }

    @Test
    public void testBrowseNoBrowseFlag() throws ContentDirectoryException {
        ConfigurationManager configurationManager = createMock(ConfigurationManager.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        RemoteClientInfo remoteClientInfo = createMock(RemoteClientInfo.class);

        ContentDirectoryService contentDirectoryService = new ContentDirectoryService();
        contentDirectoryService.setConfigurationManager(configurationManager);
        contentDirectoryService.setMediaManager(mediaManager);
        contentDirectoryService.setStreamingManager(streamingManager);

        expect(remoteClientInfo.getConnection()).andReturn(null);
        expect(mediaManager.getNode(eq("0"))).andReturn(new FolderNode("0", "-1", "root"));

        replay(mediaManager, streamingManager, remoteClientInfo, configurationManager);

        BrowseResult result = contentDirectoryService.browse("0", null, "", 0, 100, SortCriterion.valueOf("+name"), remoteClientInfo);
        assertNotNull(result);

        verify(mediaManager, streamingManager, remoteClientInfo, configurationManager);
    }

    @Test
    public void testBrowseDirectChildren() throws ContentDirectoryException, IOException {
        ConfigurationManager configurationManager = createMock(ConfigurationManager.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        RemoteClientInfo remoteClientInfo = createMock(RemoteClientInfo.class);

        ContentDirectoryService contentDirectoryService = new ContentDirectoryService();
        contentDirectoryService.setConfigurationManager(configurationManager);
        contentDirectoryService.setMediaManager(mediaManager);
        contentDirectoryService.setStreamingManager(streamingManager);

        expect(remoteClientInfo.getConnection()).andReturn(null);
        expect(mediaManager.getNode(eq("0"))).andReturn(new FolderNode("0", "-1", "root"));
        expect(mediaManager.getNodeUrl(isA(AbstractNode.class))).andReturn("url");
        expect(configurationManager.getParameter(PODCAST_PREPEND_ENTRY_NAME)).andReturn(true).atLeastOnce();

        List<AbstractNode> children = new ArrayList<>();
        children.add(new RawUrlNode(TYPE_PODCAST_ENTRY, "id1", "parentId", "name", MimeType.valueOf("video/avi"), "url", "duration"));
        children.add(new PodcastNode("id4", "parentId", "name", "url"));
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        children.add(new ContentNode("id5", "parentId", "name", file, MimeType.valueOf("video/avi")));
        AbstractNode dummyNode = createMock(AbstractNode.class);
        children.add(dummyNode);
        children.add(new RawUrlNode(TYPE_PODCAST_ENTRY, "id6", "parentId", "name", MimeType.valueOf("video/avi"), "url", "duration"));
        children.add(new RawUrlNode(TYPE_PODCAST_ENTRY, "id7", "parentId", "name", MimeType.valueOf("video/avi"), "url", "duration"));
        expect(mediaManager.searchChildNodes(isA(MediaSearchRequest.class))).andReturn(children).atLeastOnce();

        replay(mediaManager, streamingManager, remoteClientInfo, dummyNode, configurationManager);

        BrowseResult result = contentDirectoryService.browse("0", BrowseFlag.DIRECT_CHILDREN, "", 0, 6, SortCriterion.valueOf("+name"), remoteClientInfo);
        assertNotNull(result);

        verify(mediaManager, streamingManager, remoteClientInfo, dummyNode, configurationManager);
    }

    @Test
    public void testBrowseDirectChildrenWithManyPodcast() throws ContentDirectoryException, IOException {
        ConfigurationManager configurationManager = createMock(ConfigurationManager.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        RemoteClientInfo remoteClientInfo = createMock(RemoteClientInfo.class);

        ContentDirectoryService contentDirectoryService = new ContentDirectoryService();
        contentDirectoryService.setConfigurationManager(configurationManager);
        contentDirectoryService.setMediaManager(mediaManager);
        contentDirectoryService.setStreamingManager(streamingManager);

        expect(remoteClientInfo.getConnection()).andReturn(null);
        expect(mediaManager.getNode(eq("0"))).andReturn(new FolderNode("0", "-1", "root"));
        expect(configurationManager.getParameter(PODCAST_PREPEND_ENTRY_NAME)).andReturn(true).atLeastOnce();

        MimeType mimeType = MimeType.valueOf("video/avi");
        List<AbstractNode> children = new ArrayList<>();
        for (int i = 0; i <= 101; i++) {
            children.add(new RawUrlNode(TYPE_PODCAST_ENTRY, "id" + i, "parentId", "name", mimeType, "url", "duration"));
        }
        expect(mediaManager.searchChildNodes(isA(MediaSearchRequest.class))).andReturn(children).atLeastOnce();

        replay(mediaManager, streamingManager, remoteClientInfo, configurationManager);

        BrowseResult result = contentDirectoryService.browse("0", BrowseFlag.DIRECT_CHILDREN, "", 0, 6, SortCriterion.valueOf("+name"), remoteClientInfo);
        assertNotNull(result);

        verify(mediaManager, streamingManager, remoteClientInfo, configurationManager);
    }

    @Test
    public void testBrowseDirectChildrenNoPodcastPrependEntryName() throws ContentDirectoryException, IOException {
        ConfigurationManager configurationManager = createMock(ConfigurationManager.class);
        MediaManager mediaManager = createMock(MediaManager.class);
        StreamingManager streamingManager = createMock(StreamingManager.class);
        RemoteClientInfo remoteClientInfo = createMock(RemoteClientInfo.class);

        ContentDirectoryService contentDirectoryService = new ContentDirectoryService();
        contentDirectoryService.setConfigurationManager(configurationManager);
        contentDirectoryService.setMediaManager(mediaManager);
        contentDirectoryService.setStreamingManager(streamingManager);

        expect(remoteClientInfo.getConnection()).andReturn(null);
        expect(mediaManager.getNode(eq("0"))).andReturn(new FolderNode("0", "-1", "root"));
        expect(configurationManager.getParameter(PODCAST_PREPEND_ENTRY_NAME)).andReturn(false).atLeastOnce();

        List<AbstractNode> children = new ArrayList<>();
        children.add(new RawUrlNode(TYPE_PODCAST_ENTRY, "id1", "parentId", "name", MimeType.valueOf("video/avi"), "url", "duration"));
        expect(mediaManager.searchChildNodes(isA(MediaSearchRequest.class))).andReturn(children).atLeastOnce();

        replay(mediaManager, streamingManager, remoteClientInfo, configurationManager);

        BrowseResult result = contentDirectoryService.browse("0", BrowseFlag.DIRECT_CHILDREN, "", 0, 6, SortCriterion.valueOf("+name"), remoteClientInfo);
        assertNotNull(result);

        verify(mediaManager, streamingManager, remoteClientInfo, configurationManager);
    }

}
