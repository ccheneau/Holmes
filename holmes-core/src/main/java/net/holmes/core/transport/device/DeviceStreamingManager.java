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

import net.holmes.core.transport.device.model.Device;
import net.holmes.core.transport.device.model.DeviceResponse;
import net.holmes.core.transport.device.model.DeviceStatusResponse;

/**
 * Device streaming manager.
 */
public interface DeviceStreamingManager {

    /**
     * Play content to device
     *
     * @param device device
     * @param url    content url
     * @return device response
     */
    DeviceResponse play(Device device, String url);

    /**
     * Stop content playback.
     *
     * @param device device
     * @return device response
     */
    DeviceResponse stop(Device device);

    /**
     * Pause content playback.
     *
     * @param device device
     * @return device response
     */
    DeviceResponse pause(Device device);

    /**
     * Restore content playback.
     *
     * @param device device
     * @return device response
     */
    DeviceResponse restore(Device device);

    /**
     * Get content playback status
     *
     * @param device device
     * @return device status response
     */
    DeviceStatusResponse status(Device device);
}
