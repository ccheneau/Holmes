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

package net.holmes.core.upnp.metadata;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Upnp device metadata implementation.
 */
public class UpnpDeviceMetadataImpl implements UpnpDeviceMetadata {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpnpDeviceMetadataImpl.class);
    private Map<String, List<String>> deviceMimeTypes = Maps.newConcurrentMap();

    @Override
    public void addDevice(String deviceHost, List<String> availableMimeTypes) {
        LOGGER.info("Add Upnp device [{}] with supported mime types: {}", deviceHost, availableMimeTypes);
        deviceMimeTypes.put(deviceHost, availableMimeTypes);
    }

    @Override
    public List<String> getAvailableMimeTypes(String deviceHost) {
        return deviceMimeTypes.get(deviceHost);
    }

    @Override
    public void removeDevice(String deviceHost) {
        LOGGER.info("Remove Upnp device [{}]", deviceHost);
        deviceMimeTypes.remove(deviceHost);
    }
}
