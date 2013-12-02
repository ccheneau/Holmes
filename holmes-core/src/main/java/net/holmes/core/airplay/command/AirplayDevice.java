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

package net.holmes.core.airplay.command;

import com.google.common.base.Objects;

/**
 * Air play device
 */
public class AirplayDevice {

    private final String name;
    private final String hostAddress;
    private final int port;

    /**
     * Instantiates a new Airplay device.
     *
     * @param name        device name
     * @param hostAddress device host address
     * @param port        device port
     */
    public AirplayDevice(final String name, final String hostAddress, final int port) {
        this.name = name;
        this.hostAddress = hostAddress;
        this.port = port;
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

    @Override
    public int hashCode() {
        return Objects.hashCode(name, hostAddress, port);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;

        final AirplayDevice other = (AirplayDevice) obj;
        return Objects.equal(this.name, other.name) && Objects.equal(this.hostAddress, other.hostAddress) && Objects.equal(this.port, other.port);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(name)
                .addValue(hostAddress)
                .addValue(port)
                .toString();
    }
}
