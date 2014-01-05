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

package net.holmes.core.transport.upnp;

import com.google.common.base.Objects;
import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.transport.device.Device;
import org.fourthline.cling.model.meta.RemoteService;

import java.net.InetAddress;
import java.util.List;

/**
 * Upnp streaming device.
 */
public class UpnpDevice extends Device {

    private final List<String> supportedMimeTypes;
    private final RemoteService avTransportService;
    private boolean videoSupported = false;
    private boolean audioSupported = false;
    private boolean imageSupported = false;

    /**
     * Instantiates a new Upnp device
     *
     * @param id                 device id
     * @param name               device name
     * @param hostAddress        device host
     * @param supportedMimeTypes list of supported mime types
     * @param avTransportService AV transport service
     */
    public UpnpDevice(final String id, final String name, final InetAddress hostAddress, final List<String> supportedMimeTypes, final RemoteService avTransportService) {
        super(id, name, hostAddress);
        this.supportedMimeTypes = supportedMimeTypes;
        this.avTransportService = avTransportService;
        if (supportedMimeTypes != null) {
            for (String supportedMimeType : supportedMimeTypes) {
                MimeType mimeType = new MimeType(supportedMimeType);
                switch (mimeType.getType()) {
                    case TYPE_VIDEO:
                        videoSupported = true;
                        break;
                    case TYPE_AUDIO:
                        audioSupported = true;
                        break;
                    case TYPE_IMAGE:
                        imageSupported = true;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public List<String> getSupportedMimeTypes() {
        return supportedMimeTypes;
    }

    public RemoteService getAvTransportService() {
        return avTransportService;
    }

    @Override
    public boolean isVideoSupported() {
        return videoSupported;
    }

    @Override
    public boolean isAudioSupported() {
        return audioSupported;
    }

    @Override
    public boolean isImageSupported() {
        return imageSupported;
    }

    @Override
    public boolean isSlideShowSupported() {
        return imageSupported;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("address", address)
                .add("videoSupported", videoSupported)
                .add("audioSupported", audioSupported)
                .add("imageSupported", imageSupported)
                .add("avTransportService", avTransportService)
                .add("supportedMimeTypes", supportedMimeTypes)
                .toString();
    }

    @Override
    public void close() {
        // Nothing
    }
}
