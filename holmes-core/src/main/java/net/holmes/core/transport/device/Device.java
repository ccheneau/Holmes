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

package net.holmes.core.transport.device;

/**
 * Streaming device
 */
public abstract class Device {
    protected final String id;
    protected final String name;
    protected final String hostAddress;

    /**
     * Instantiates a new device
     *
     * @param id          device id
     * @param name        device name
     * @param hostAddress device host
     */
    public Device(final String id, final String name, final String hostAddress) {
        this.id = id;
        this.name = name;
        this.hostAddress = hostAddress;
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
    public String getHostAddress() {
        return hostAddress;
    }
}
