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

package net.holmes.core.backend.handler;

import net.holmes.core.media.MediaService;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.RootNode;
import net.holmes.core.transport.TransportService;
import net.holmes.core.transport.device.Device;
import net.holmes.core.transport.device.UnknownDeviceException;
import net.holmes.core.transport.session.StreamingSession;
import net.holmes.core.transport.session.UnknownSessionException;
import net.holmes.core.transport.upnp.UpnpDevice;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static net.holmes.core.media.MediaService.ChildNodeRequest;

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
     * Play video content.
     *
     * @return status
     */
    @GET
    @Path("/play/{index}")
    @Produces(MediaType.TEXT_PLAIN)
    public String play(@PathParam("index") int index) {
        Device device = getDevice();
        AbstractNode videoNode = getVideoContentNode(index);
        String url = mediaService.getNodeUrl(videoNode);
        try {
            transportService.play(device.getId(), url, videoNode);
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return "Play device:[" + device.getId() + "] Node type [" + videoNode.getType() + "] Node Url [" + url + "]";
    }

    /**
     * Pause video content playback.
     *
     * @return status
     */
    @GET
    @Path("/pause")
    @Produces(MediaType.TEXT_PLAIN)
    public String pause() {
        Device device = getDevice();
        try {
            transportService.pause(device.getId());
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return "Pause device:[" + device.getId() + "]";
    }

    /**
     * Stop video content play back.
     *
     * @return status
     */
    @GET
    @Path("/stop")
    @Produces(MediaType.TEXT_PLAIN)
    public String stop() {
        Device device = getDevice();
        try {
            transportService.stop(device.getId());
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return "Stop device:[" + device.getId() + "]";
    }

    /**
     * Resume video content playback.
     *
     * @return status
     */
    @GET
    @Path("/resume")
    @Produces(MediaType.TEXT_PLAIN)
    public String resume() {
        Device device = getDevice();
        try {
            transportService.resume(device.getId());
        } catch (UnknownDeviceException e) {
            return e.getMessage();
        }
        return "Resume device:[" + device.getId() + "]";
    }

    /**
     * Update video content playback status.
     *
     * @return status
     */
    @GET
    @Path("/status")
    @Produces(MediaType.TEXT_PLAIN)
    public String status() {
        Device device = getDevice();
        try {
            StreamingSession session = transportService.getSession(device.getId());
            return "Status device:[" + device.getId() + "]" + " session [" + session.toString() + "]";
        } catch (UnknownSessionException e) {
            return e.getMessage();
        }
    }

    /**
     * Get device.
     *
     * @return device
     */
    private Device getDevice() {
        Collection<Device> devices = transportService.getDevices();
        for (Device device : devices) {
            if (device instanceof UpnpDevice) return device;
        }
        return null;
    }

    /**
     * Get video content node.
     *
     * @return video content node
     */
    private AbstractNode getVideoContentNode(final int index) {
        AbstractNode node = mediaService.getNode(RootNode.VIDEO.getId());
        AbstractNode rootVideo = mediaService.getChildNodes(new ChildNodeRequest(node)).getChildNodes().iterator().next();
        Collection<AbstractNode> nodes = mediaService.getChildNodes(new ChildNodeRequest(rootVideo)).getChildNodes();
        int i = 0;
        for (AbstractNode childNode : nodes) {
            if (i == index) return childNode;
            i++;
        }
        return null;
    }
}
