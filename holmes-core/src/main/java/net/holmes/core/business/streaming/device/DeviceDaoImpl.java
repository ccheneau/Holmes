/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Device dao implementation.
 */
@Singleton
public final class DeviceDaoImpl implements DeviceDao {
    private final Map<String, Device> devices;

    /**
     * Instantiates a new device dao implementation.
     */
    public DeviceDaoImpl() {
        this.devices = new HashMap<>();
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
        if (device == null) {
            throw new UnknownDeviceException(deviceId);
        }
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
        Predicate<Device> p = device -> device.getAddress().getHostAddress().equals(hostAddress);
        return devices.values().stream()
                .filter(p)
                .collect(Collectors.<Device>toList());
    }
}
