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

package net.holmes.core.airplay.command;

import com.google.common.collect.Maps;
import net.holmes.core.airplay.model.AirplayDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Airplay command manager implementation.
 */
public class AirplayCommandManagerImpl implements AirplayCommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirplayCommandManagerImpl.class);
    private Map<Integer, AirplayDevice> deviceMap = Maps.newHashMap();

    @Override
    public void addDevice(AirplayDevice device) {
        if (device != null && !deviceMap.containsKey(device.hashCode())) {
            LOGGER.info("Add Airplay device {}", device.toString());
            deviceMap.put(device.hashCode(), device);
        }
    }

    @Override
    public void removeDevice(AirplayDevice device) {
        if (device != null && deviceMap.containsKey(device.hashCode())) {
            LOGGER.info("Remove Airplay device {}", device.toString());
            deviceMap.remove(device.hashCode());
            //TODO close existing connection on remove
        }
    }

    @Override
    public Map<Integer, AirplayDevice> getDevices() {
        return deviceMap;
    }
}
