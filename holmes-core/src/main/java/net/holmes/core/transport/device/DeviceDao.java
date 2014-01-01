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

import java.util.Collection;

/**
 * Device dao
 */
public interface DeviceDao {

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
     * Get device.
     *
     * @param deviceId device id
     * @return device
     * @throws UnknownDeviceException
     */
    Device getDevice(final String deviceId) throws UnknownDeviceException;

    /**
     * Get all devices.
     *
     * @return list of all devices
     */
    Collection<Device> getDevices();

    /**
     * Find devices.
     *
     * @param hostAddress host address
     * @return list of devices
     */
    Collection<Device> findDevices(final String hostAddress);
}
