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

import net.holmes.core.airplay.command.model.AbstractCommand;
import net.holmes.core.airplay.command.model.CommandResponse;

import java.util.Map;

/**
 * Airplay command manager
 */
public interface AirplayCommandManager {

    /**
     * Add Airplay device.
     *
     * @param device device
     */
    void addDevice(AirplayDevice device);

    /**
     * Remove Airplay device.
     *
     * @param device device
     */
    void removeDevice(AirplayDevice device);

    /**
     * Get Airplay devices.
     *
     * @return Airplay devices
     */
    Map<Integer, AirplayDevice> getDevices();

    /**
     * Send command to Airplay device.
     *
     * @param deviceId device ID
     * @param command  command to run
     * @return command response
     */
    CommandResponse sendCommand(Integer deviceId, AbstractCommand command);
}
