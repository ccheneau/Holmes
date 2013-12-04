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

package net.holmes.core.transport.device.model;

import com.google.common.base.Objects;

import java.util.List;

/**
 * Device
 */
public class Device {
    private final String id;
    private final DeviceType deviceType;
    private final String name;
    private final String hostAddress;
    private final int port;
    private final List<String> supportedMimeTypes;

    /**
     * Instantiates a new device
     *
     * @param id                 device id
     * @param deviceType         device type
     * @param name               device name
     * @param hostAddress        device host
     * @param port               device port
     * @param supportedMimeTypes supported mime types
     */
    public Device(final String id, final DeviceType deviceType, final String name, final String hostAddress, final int port, final List<String> supportedMimeTypes) {
        this.id = id;
        this.deviceType = deviceType;
        this.name = name;
        this.hostAddress = hostAddress;
        this.port = port;
        this.supportedMimeTypes = supportedMimeTypes;
    }

    public String getId() {
        return id;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public String getName() {
        return name;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public int getPort() {
        return port;
    }

    public List<String> getSupportedMimeTypes() {
        return supportedMimeTypes;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(deviceType)
                .addValue(name)
                .addValue(hostAddress)
                .addValue(port)
                .addValue(supportedMimeTypes)
                .toString();
    }
}
