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

package net.holmes.core.business.streaming.device;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * Device dao implementation.
 */
public final class DeviceDaoImpl implements DeviceDao {
    private final Map<String, Device> devices;

    /**
     * Instantiates a new device dao implementation.
     */
    public DeviceDaoImpl() {
        this.devices = Maps.newHashMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDevice(final Device device) {
        devices.put(device.getId(), device);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeDevice(final String deviceId) {
        Device device = devices.get(deviceId);
        if (device != null) {
            device.close();
            devices.remove(deviceId);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Device getDevice(final String deviceId) throws UnknownDeviceException {
        Device device = devices.get(deviceId);
        if (device == null) throw new UnknownDeviceException(deviceId);
        return device;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Device> getDevices() {
        return devices.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Device> findDevices(final String hostAddress) {
        return Collections2.filter(devices.values(), new Predicate<Device>() {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean apply(Device input) {
                return input.getAddress().getHostAddress().equals(hostAddress);
            }
        });
    }
}
