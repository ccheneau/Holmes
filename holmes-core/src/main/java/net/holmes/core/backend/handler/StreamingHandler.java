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

package net.holmes.core.backend.handler;

import net.holmes.core.backend.response.DeviceBrowseResult;
import net.holmes.core.backend.response.PlaybackDevice;
import net.holmes.core.backend.response.PlaybackStatus;
import net.holmes.core.business.media.MediaManager;
import net.holmes.core.business.media.MediaSearchRequest;
import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.media.model.FolderNode;
import net.holmes.core.business.media.model.MediaNode;
import net.holmes.core.business.streaming.StreamingManager;
import net.holmes.core.business.streaming.device.Device;
import net.holmes.core.business.streaming.device.UnknownDeviceException;
import net.holmes.core.business.streaming.session.StreamingSession;
import net.holmes.core.business.streaming.session.UnknownSessionException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.*;
import static net.holmes.core.backend.response.DeviceBrowseResult.*;
import static net.holmes.core.business.media.model.RootNode.VIDEO;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Handler for streaming REST requests.
 */
@Path("/backend/streaming")
public class StreamingHandler {
    private static final Logger LOGGER = getLogger(StreamingHandler.class);

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
        Collection<Device> devices = streamingManager.getDevices();
        List<PlaybackDevice> playbackDevices = new ArrayList<>(devices.size());
        playbackDevices.addAll(devices.stream().map(this::buildPlaybackDevice).collect(toList()));

        return playbackDevices;
    }

    /**
     * Play content.
     *
     * @param deviceId  device id
     * @param contentId content id
     * @return error message or null
     */
    @GET
    @Path("/play/{deviceId}/{contentId}")
    @Produces(TEXT_PLAIN)
    public String play(@PathParam("deviceId") final String deviceId, @PathParam("contentId") final String contentId) {
        Optional<MediaNode> contentNode = mediaManager.getNode(contentId);
        String url = mediaManager.getNodeUrl(contentNode.get());
        try {
            streamingManager.play(deviceId, url, contentNode.get());
        } catch (UnknownDeviceException e) {
            LOGGER.error(e.getMessage(), e);
            return e.getMessage();
        }
        return null;
    }

    /**
     * Pause content playback.
     *
     * @param deviceId device id
     * @return error message or null
     */
    @GET
    @Path("/pause/{deviceId}")
    @Produces(TEXT_PLAIN)
    public String pause(@PathParam("deviceId") final String deviceId) {
        try {
            streamingManager.pause(deviceId);
        } catch (UnknownDeviceException e) {
            LOGGER.error(e.getMessage(), e);
            return e.getMessage();
        }
        return null;
    }

    /**
     * Stop content play back.
     *
     * @param deviceId device id
     * @return error message or null
     */
    @GET
    @Path("/stop/{deviceId}")
    @Produces(TEXT_PLAIN)
    public String stop(@PathParam("deviceId") final String deviceId) {
        try {
            streamingManager.stop(deviceId);
        } catch (UnknownDeviceException e) {
            LOGGER.error(e.getMessage(), e);
            return e.getMessage();
        }
        return null;
    }

    /**
     * Resume content playback.
     *
     * @param deviceId device id
     * @return error message or null
     */
    @GET
    @Path("/resume/{deviceId}")
    @Produces(TEXT_PLAIN)
    public String resume(@PathParam("deviceId") final String deviceId) {
        try {
            streamingManager.resume(deviceId);
        } catch (UnknownDeviceException e) {
            LOGGER.error(e.getMessage(), e);
            return e.getMessage();
        }
        return null;
    }

    /**
     * Update content playback status.
     *
     * @param deviceId device id
     * @return playback status
     */
    @GET
    @Path("/status/{deviceId}")
    @Produces(APPLICATION_JSON)
    public PlaybackStatus status(@PathParam("deviceId") final String deviceId) {
        PlaybackStatus status = new PlaybackStatus();
        try {
            StreamingSession session = streamingManager.getSession(deviceId);
            status.setContentName(session.getContentName());
            status.setDuration(session.getDuration());
            status.setPosition(session.getPosition());
        } catch (UnknownSessionException e) {
            LOGGER.error(e.getMessage(), e);
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
    public DeviceBrowseResult browse(@PathParam("deviceId") final String deviceId, @PathParam("nodeId") final String nodeId) {
        DeviceBrowseResult result = new DeviceBrowseResult();
        result.setParentNodeId(nodeId);
        try {
            // Get device
            Device device = streamingManager.getDevice(deviceId);

            // Get browse node
            Optional<MediaNode> node = mediaManager.getNode(nodeId);
            if (!node.isPresent() && device.isVideoSupported()) {
                node = mediaManager.getNode(VIDEO.getId());
            }

            if (node.isPresent()) {
                addBrowseResult(result, device, node.get());

            }
        } catch (UnknownDeviceException e) {
            LOGGER.error(e.getMessage(), e);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    /**
     * Add folders and contents to browse result.
     *
     * @param result browse result
     * @param device device
     * @param node   node
     */
    private void addBrowseResult(final DeviceBrowseResult result, final Device device, final MediaNode node) {
        // Get child nodes
        Collection<MediaNode> searchResult = mediaManager.searchChildNodes(new MediaSearchRequest(node, device.getSupportedMimeTypes()));
        // Build browse result
        for (MediaNode mediaNode : searchResult) {
            if (mediaNode instanceof FolderNode) {
                result.getFolders().add(buildBrowseFolder(mediaNode));
            } else if (mediaNode instanceof ContentNode) {
                result.getContents().add(buildBrowseContent(mediaNode));
            }
        }
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
    private BrowseFolder buildBrowseFolder(final MediaNode node) {
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
    private BrowseContent buildBrowseContent(final MediaNode node) {
        BrowseContent browseContent = new BrowseContent();
        browseContent.setNodeId(node.getId());
        browseContent.setContentName(node.getName());
        browseContent.setContentUrl(mediaManager.getNodeUrl(node));
        return browseContent;
    }
}
