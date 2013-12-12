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

package net.holmes.core.transport;

import net.holmes.core.transport.device.exception.UnknownDeviceException;
import net.holmes.core.transport.device.model.Device;

import java.util.Collection;

/**
 * Transport service.
 */
public interface TransportService {

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
    Collection<Device> findDevices(final String hostAddress);

    /**
     * Get all devices.
     *
     * @return list of all devices
     */
    Collection<Device> getDevices();

    /**
     * Play content to device.
     *
     * @param deviceId    device id
     * @param contentUrl  content url
     * @param contentName content name
     * @throws UnknownDeviceException
     */
    void play(String deviceId, String contentUrl, String contentName) throws UnknownDeviceException;

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

    /**
     * Update status on device.
     *
     * @param deviceId device id
     * @throws UnknownDeviceException
     */
    void updateStatus(String deviceId) throws UnknownDeviceException;
}
