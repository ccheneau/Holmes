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
import net.holmes.core.manager.media.MediaManager;
import net.holmes.core.manager.media.model.AbstractNode;
import net.holmes.core.manager.media.model.ContentNode;
import net.holmes.core.manager.media.model.FolderNode;
import net.holmes.core.manager.streaming.StreamingManager;
import net.holmes.core.manager.streaming.device.Device;
import net.holmes.core.manager.streaming.device.UnknownDeviceException;
import net.holmes.core.manager.streaming.session.StreamingSession;
import net.holmes.core.manager.streaming.session.UnknownSessionException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static net.holmes.core.backend.response.DeviceBrowseResult.BrowseContent;
import static net.holmes.core.backend.response.DeviceBrowseResult.BrowseFolder;
import static net.holmes.core.manager.media.MediaManager.ChildNodeRequest;
import static net.holmes.core.manager.media.MediaManager.ChildNodeResult;
import static net.holmes.core.manager.media.model.RootNode.VIDEO;

/**
 * Handler for streaming REST requests.
 */
@Path("/backend/streaming")
public class StreamingHandler {
    private final MediaManager mediaManager;
    private final StreamingManager streamingManager;

    /**
     * Instantiates a new StreamingHandler.
     *
     * @param mediaManager     media manager
     * @param streamingManager streaming manager
     */
    @Inject
    public StreamingHandler(final MediaManager mediaManager, final StreamingManager streamingManager) {
        this.mediaManager = mediaManager;
        this.streamingManager = streamingManager;
    }

    /**
     * Get playback devices.
     *
     * @return playback device list
     */
    @GET
    @Path("/devices")
    @Produces(APPLICATION_JSON)
    public List<PlaybackDevice> getDevices() {
        List<PlaybackDevice> playbackDevices = Lists.newArrayList();
        for (Device device : streamingManager.getDevices())
            playbackDevices.add(buildPlaybackDevice(device));

        return playbackDevices;
    }

    /**
     * Play content.
     *
     * @return error message or null
     */
    @GET
    @Path("/play/{deviceId}/{contentId}")
    @Produces(TEXT_PLAIN)
    public String play(@PathParam("deviceId") String deviceId, @PathParam("contentId") String contentId) {
        AbstractNode contentNode = mediaManager.getNode(contentId);
        String url = mediaManager.getNodeUrl(contentNode);
        try {
            streamingManager.play(deviceId, url, contentNode);
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return null;
    }

    /**
     * Pause content playback.
     *
     * @return error message or null
     */
    @GET
    @Path("/pause/{deviceId}")
    @Produces(TEXT_PLAIN)
    public String pause(@PathParam("deviceId") String deviceId) {
        try {
            streamingManager.pause(deviceId);
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return null;
    }

    /**
     * Stop content play back.
     *
     * @return error message or null
     */
    @GET
    @Path("/stop/{deviceId}")
    @Produces(TEXT_PLAIN)
    public String stop(@PathParam("deviceId") String deviceId) {
        try {
            streamingManager.stop(deviceId);
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return null;
    }

    /**
     * Resume content playback.
     *
     * @return error message or null
     */
    @GET
    @Path("/resume/{deviceId}")
    @Produces(TEXT_PLAIN)
    public String resume(@PathParam("deviceId") String deviceId) {
        try {
            streamingManager.resume(deviceId);
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return null;
    }

    /**
     * Update content playback status.
     *
     * @return playback status
     */
    @GET
    @Path("/status/{deviceId}")
    @Produces(APPLICATION_JSON)
    public PlaybackStatus status(@PathParam("deviceId") String deviceId) {
        PlaybackStatus status = new PlaybackStatus();
        try {
            StreamingSession session = streamingManager.getSession(deviceId);
            status.setContentName(session.getContentName());
            status.setDuration(session.getDuration());
            status.setPosition(session.getPosition());
        } catch (UnknownSessionException e) {
            status.setErrorMessage(e.getMessage());
        }
        return status;
    }

    /**
     * Browse for contents and folders on device.
     *
     * @param deviceId device id
     * @param nodeId   node id
     * @return folders and contents on device
     */
    @GET
    @Path("/browse/{deviceId}/{nodeId}")
    @Produces(APPLICATION_JSON)
    public DeviceBrowseResult browse(@PathParam("deviceId") String deviceId, @PathParam("nodeId") String nodeId) {
        DeviceBrowseResult result = new DeviceBrowseResult();
        result.setParentNodeId(nodeId);
        try {
            // Get device
            Device device = streamingManager.getDevice(deviceId);

            // Get browse node
            AbstractNode node = mediaManager.getNode(nodeId);
            if (node == null && device.isVideoSupported()) node = mediaManager.getNode(VIDEO.getId());

            if (node != null) {
                // Get child nodes
                ChildNodeResult childNodesResult = mediaManager.getChildNodes(new ChildNodeRequest(node, device.getSupportedMimeTypes()));
                // Build browse result
                for (AbstractNode abstractNode : childNodesResult.getChildNodes())
                    if (abstractNode instanceof FolderNode)
                        result.getFolders().add(buildBrowseFolder(abstractNode));
                    else if (abstractNode instanceof ContentNode)
                        result.getContents().add(buildBrowseContent(abstractNode));

            }
        } catch (UnknownDeviceException e) {
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    /**
     * Build playback device.
     *
     * @param device device
     * @return playback device
     */
    private PlaybackDevice buildPlaybackDevice(final Device device) {
        PlaybackDevice playbackDevice = new PlaybackDevice();
        playbackDevice.setDeviceId(device.getId());
        playbackDevice.setDeviceName(device.getName());
        playbackDevice.setDeviceType(device.getType());
        playbackDevice.setVideoSupported(device.isVideoSupported());
        playbackDevice.setAudioSupported(device.isAudioSupported());
        playbackDevice.setImageSupported(device.isImageSupported());
        playbackDevice.setSlideShowSupported(device.isSlideShowSupported());
        return playbackDevice;
    }

    /**
     * Build browse folder.
     *
     * @param node node
     * @return browse folder
     */
    private BrowseFolder buildBrowseFolder(final AbstractNode node) {
        BrowseFolder browseFolder = new BrowseFolder();
        browseFolder.setNodeId(node.getId());
        browseFolder.setFolderName(node.getName());
        return browseFolder;
    }

    /**
     * Build browse content.
     *
     * @param node node
     * @return browse content
     */
    private BrowseContent buildBrowseContent(final AbstractNode node) {
        BrowseContent browseContent = new BrowseContent();
        browseContent.setNodeId(node.getId());
        browseContent.setContentName(node.getName());
        browseContent.setContentUrl(mediaManager.getNodeUrl(node));
        return browseContent;
    }
}
