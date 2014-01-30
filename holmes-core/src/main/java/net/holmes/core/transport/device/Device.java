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

package net.holmes.core.transport.device;

import java.net.InetAddress;
import java.util.List;

/**
 * Streaming device
 */
public abstract class Device {
    protected final String id;
    protected final String name;
    protected final InetAddress address;
    protected final List<String> supportedMimeTypes;

    /**
     * Instantiates a new device
     *
     * @param id                 device id
     * @param name               device name
     * @param address            device inet address
     * @param supportedMimeTypes supported mime types
     */
    protected Device(final String id, final String name, final InetAddress address, final List<String> supportedMimeTypes) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.supportedMimeTypes = supportedMimeTypes;
    }

    /**
     * Get device id.
     *
     * @return device id
     */
    public String getId() {
        return id;
    }

    /**
     * Get device name.
     *
     * @return device name
     */
    public String getName() {
        return name;
    }

    /**
     * Get device host address.
     *
     * @return device host address
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Get supported mime types.
     *
     * @return list of supported mime type
     */
    public List<String> getSupportedMimeTypes() {
        return supportedMimeTypes;
    }

    /**
     * Get device type.
     *
     * @return device type
     */
    public abstract String getType();

    /**
     * Check if video streaming is supported
     *
     * @return true if video streaming is supported
     */
    public abstract boolean isVideoSupported();

    /**
     * Check if audio streaming is supported
     *
     * @return true if audio streaming is supported
     */
    public abstract boolean isAudioSupported();

    /**
     * Check if image is supported is supported
     *
     * @return true if image is supported
     */
    public abstract boolean isImageSupported();

    /**
     * Check if image slide show is supported
     *
     * @return true if slide show is supported
     */
    public abstract boolean isSlideShowSupported();

    /**
     * Close device.
     */
    public abstract void close();
}
