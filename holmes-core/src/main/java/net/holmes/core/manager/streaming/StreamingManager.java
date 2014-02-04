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

package net.holmes.core.manager.streaming;

import net.holmes.core.manager.media.model.AbstractNode;
import net.holmes.core.manager.streaming.device.Device;
import net.holmes.core.manager.streaming.device.UnknownDeviceException;
import net.holmes.core.manager.streaming.session.StreamingSession;
import net.holmes.core.manager.streaming.session.UnknownSessionException;

import java.util.Collection;

/**
 * Transport service.
 */
public interface StreamingManager {

    /**
     * Add device.
     *
     * @param device device to add
     */
    void addDevice(Device device);


    /**
     * Remove device.
     *
     * @param deviceId device to remove
     */
    void removeDevice(String deviceId);

    /**
     * Find devices.
     *
     * @param hostAddress host address
     * @return list of devices
     */
    Collection<Device> findDevices(String hostAddress);

    /**
     * Get all devices.
     *
     * @return list of all devices
     */
    Collection<Device> getDevices();


    /**
     * Get device.
     *
     * @param deviceId device id
     * @return list of all devices
     * @throws UnknownDeviceException
     */
    Device getDevice(String deviceId) throws UnknownDeviceException;

    /**
     * Get streaming session.
     *
     * @param deviceId device id
     * @return streaming session
     * @throws UnknownSessionException
     */
    StreamingSession getSession(String deviceId) throws UnknownSessionException;

    /**
     * Play content to device.
     *
     * @param deviceId   device id
     * @param contentUrl content url
     * @param node       node
     * @throws UnknownDeviceException
     */
    void play(String deviceId, String contentUrl, AbstractNode node) throws UnknownDeviceException;

    /**
     * Stop playback on device.
     *
     * @param deviceId device id
     * @throws UnknownDeviceException
     */
    void stop(String deviceId) throws UnknownDeviceException;

    /**
     * Pause content playback on device.
     *
     * @param deviceId device id
     * @throws UnknownDeviceException
     */
    void pause(String deviceId) throws UnknownDeviceException;

    /**
     * Resume content playback on device.
     *
     * @param deviceId device id
     * @throws UnknownDeviceException
     */
    void resume(String deviceId) throws UnknownDeviceException;
}
