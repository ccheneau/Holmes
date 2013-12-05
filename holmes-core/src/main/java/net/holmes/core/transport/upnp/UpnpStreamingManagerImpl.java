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

package net.holmes.core.transport.upnp;

import net.holmes.core.transport.device.DeviceStreamingManager;
import net.holmes.core.transport.device.model.Device;
import net.holmes.core.transport.device.model.DeviceResponse;
import net.holmes.core.transport.device.model.DeviceStatusResponse;

/**
 * UPnP streaming manager implementation.
 */
public class UpnpStreamingManagerImpl implements DeviceStreamingManager {
    @Override
    public DeviceResponse play(Device device, String url) {
        return null;
    }

    @Override
    public DeviceResponse stop(Device device) {
        return null;
    }

    @Override
    public DeviceResponse pause(Device device) {
        return null;
    }

    @Override
    public DeviceResponse restore(Device device) {
        return null;
    }

    @Override
    public DeviceStatusResponse status(Device device) {
        return null;
    }
}
