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

/**
 * Device streamer.
 */
public interface DeviceStreamer<T extends Device> {

    /**
     * Play content to device
     *
     * @param device device
     * @param url    content url
     */
    void play(T device, String url);

    /**
     * Stop content playback.
     *
     * @param device device
     */
    void stop(T device);

    /**
     * Pause content playback.
     *
     * @param device device
     */
    void pause(T device);

    /**
     * Resume content playback.
     *
     * @param device device
     */
    void resume(T device);

    /**
     * Update content playback status
     *
     * @param device device
     */
    void updateStatus(T device);
}
