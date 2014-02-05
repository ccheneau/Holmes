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

package net.holmes.core.business.streaming.airplay.device;

import com.google.common.base.Objects;
import net.holmes.core.business.streaming.device.Device;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Airplay streaming device.
 */
public class AirplayDevice extends Device {
    private final static String AIRPLAY_DEVICE_TYPE = "Airplay";
    private final int port;
    private final AirplayFeatures features;
    private Socket socket = null;

    /**
     * Instantiates a new Airplay device
     *
     * @param id       device id
     * @param name     device name
     * @param address  device host
     * @param port     device port
     * @param features device features
     */
    public AirplayDevice(final String id, final String name, final InetAddress address, final int port, final AirplayFeatures features) {
        super(id, name, address, null);
        this.port = port;
        this.features = features;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return AIRPLAY_DEVICE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVideoSupported() {
        return features.isVideoSupported();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAudioSupported() {
        return features.isAudioSupported();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isImageSupported() {
        return features.isImageSupported();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSlideShowSupported() {
        return features.isSlideShowSupported();
    }

    /**
     * {@inheritDoc}
     */
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
     * @param socketFactory socket factory
     * @return device socket
     * @throws IOException
     */
    public Socket getConnection(final SocketFactory socketFactory) throws IOException {
        if (socket == null || socket.isClosed())
            socket = socketFactory.createSocket(getAddress(), port);

        return socket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("address", address)
                .add("port", port)
                .add("features", features)
                .toString();
    }
}
