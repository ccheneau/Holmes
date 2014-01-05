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
    }

    public List<String> getSupportedMimeTypes() {
        return supportedMimeTypes;
    }

    public RemoteService getAvTransportService() {
        return avTransportService;
    }

    @Override
    public boolean isVideoSupported() {
        return true;
    }

    @Override
    public boolean isAudioSupported() {
        return true;
    }

    @Override
    public boolean isImageSupported() {
        return true;
    }

    @Override
    public boolean isSlideShowSupported() {
        return true;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("address", address)
                .add("supportedMimeTypes", supportedMimeTypes)
                .add("avTransportService", avTransportService)
                .toString();
    }

    @Override
    public void close() {
        // Nothing
    }
}
