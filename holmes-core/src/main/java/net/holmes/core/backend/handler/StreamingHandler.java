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
import net.holmes.core.backend.response.BrowseResult;
import net.holmes.core.backend.response.PlaybackDevice;
import net.holmes.core.backend.response.PlaybackStatus;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.ContentNode;
import net.holmes.core.media.model.FolderNode;
import net.holmes.core.media.model.RootNode;
import net.holmes.core.transport.TransportService;
import net.holmes.core.transport.device.Device;
import net.holmes.core.transport.device.UnknownDeviceException;
import net.holmes.core.transport.session.StreamingSession;
import net.holmes.core.transport.session.UnknownSessionException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static net.holmes.core.backend.response.BrowseResult.BrowseContent;
import static net.holmes.core.backend.response.BrowseResult.BrowseFolder;
import static net.holmes.core.media.MediaService.ChildNodeRequest;
import static net.holmes.core.media.MediaService.ChildNodeResult;

/**
 * Handler for streaming REST requests.
 */
@Path("/backend/streaming")
public class StreamingHandler {
    final MediaService mediaService;
    final TransportService transportService;

    /**
     * Instantiates a new StreamingHandler.
     *
     * @param mediaService     media service
     * @param transportService transport service
     */
    @Inject
    public StreamingHandler(final MediaService mediaService, final TransportService transportService) {
        this.mediaService = mediaService;
        this.transportService = transportService;
    }

    /**
     * Get playback devices.
     *
     * @return playback device list
     */
    @GET
    @Path("/devices")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PlaybackDevice> getDevices() {
        List<PlaybackDevice> playbackDevices = Lists.newArrayList();
        for (Device device : transportService.getDevices()) {
            playbackDevices.add(new PlaybackDevice(device.getId(), device.getName(), device.isVideoSupported(), device.isAudioSupported(), device.isImageSupported(), device.isSlideShowSupported()));
        }
        return playbackDevices;
    }

    /**
     * Play content.
     *
     * @return status
     */
    @GET
    @Path("/play/{deviceId}/{contentId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String play(@PathParam("deviceId") String deviceId, @PathParam("contentId") String contentId) {
        AbstractNode contentNode = mediaService.getNode(contentId);
        String url = mediaService.getNodeUrl(contentNode);
        try {
            transportService.play(deviceId, url, contentNode);
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return null;
    }

    /**
     * Pause content playback.
     *
     * @return status
     */
    @GET
    @Path("/pause/{deviceId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String pause(@PathParam("deviceId") String deviceId) {
        try {
            transportService.pause(deviceId);
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return null;
    }

    /**
     * Stop content play back.
     *
     * @return status
     */
    @GET
    @Path("/stop/{deviceId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String stop(@PathParam("deviceId") String deviceId) {
        try {
            transportService.stop(deviceId);
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return null;
    }

    /**
     * Resume content playback.
     *
     * @return status
     */
    @GET
    @Path("/resume/{deviceId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String resume(@PathParam("deviceId") String deviceId) {
        try {
            transportService.resume(deviceId);
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
    @Produces(MediaType.APPLICATION_JSON)
    public PlaybackStatus status(@PathParam("deviceId") String deviceId) {
        try {
            StreamingSession session = transportService.getSession(deviceId);
            return new PlaybackStatus(session.getContentName(), session.getDuration(), session.getPosition());
        } catch (UnknownSessionException e) {
            return new PlaybackStatus(e.getMessage());
        }
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
    @Produces(MediaType.APPLICATION_JSON)
    public BrowseResult browse(@PathParam("deviceId") String deviceId, @PathParam("nodeId") String nodeId) {
        BrowseResult result = new BrowseResult();
        result.setParentNodeId(nodeId);
        try {
            // Get device
            Device device = transportService.getDevice(deviceId);

            // Get browse node
            AbstractNode node = mediaService.getNode(nodeId);
            if (node == null && device.isVideoSupported()) node = mediaService.getNode(RootNode.VIDEO.getId());

            if (node != null) {
                // Get child nodes
                ChildNodeResult childNodesResult = mediaService.getChildNodes(new ChildNodeRequest(node, device.getSupportedMimeTypes()));
                // Build browse result
                for (AbstractNode abstractNode : childNodesResult.getChildNodes())
                    if (abstractNode instanceof FolderNode)
                        result.getFolders().add(new BrowseFolder(abstractNode.getId(), abstractNode.getName()));
                    else if (abstractNode instanceof ContentNode)
                        result.getContents().add(new BrowseContent(abstractNode.getId(), abstractNode.getName(), mediaService.getNodeUrl(abstractNode)));
            }
        } catch (UnknownDeviceException e) {
            result.setErrorMessage("Unknown device");
        }
        return result;
    }
}
