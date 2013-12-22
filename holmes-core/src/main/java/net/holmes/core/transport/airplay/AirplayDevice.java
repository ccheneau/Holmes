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

package net.holmes.core.transport.airplay;

import com.google.common.base.Objects;
import net.holmes.core.transport.device.Device;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Airplay streaming device.
 */
public class AirplayDevice extends Device {
    private final int port;
    private Socket socket = null;

    /**
     * Instantiates a new Airplay device
     *
     * @param id          device id
     * @param name        device name
     * @param address device host
     * @param port        device port
     */
    public AirplayDevice(final String id, final String name, final InetAddress address, final int port) {
        super(id, name, address);
        this.port = port;
    }

    @Override
    public void close() {
        if (socket != null) try {
            socket.close();
        } catch (IOException e) {
            // Nothing
        }
    }

    /**
     * Get device connection.
     *
     * @return device socket
     * @throws IOException
     */
    public Socket getConnection() throws IOException {
        if (socket == null || socket.isClosed())
            socket = new Socket(getInetAddress(), port);

        return socket;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(name)
                .addValue(inetAddress)
                .addValue(port)
                .toString();
    }
}
