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

package net.holmes.core.business.streaming.airplay.command;

import net.holmes.core.business.streaming.device.CommandFailureHandler;

import static net.holmes.core.business.streaming.airplay.command.AirplayCommand.CommandType.RATE;
import static net.holmes.core.business.streaming.airplay.command.AirplayCommand.UrlParameter.VALUE;

/**
 * Airplay rate command: Change the playback rate
 */
public abstract class RateCommand extends AirplayCommand {

    /**
     * Instantiates a new Airplay rate command.
     *
     * @param rate           playback rate: 0 is paused, 1 is playing at the normal speed
     * @param failureHandler failure handler
     */
    public RateCommand(final Double rate, final CommandFailureHandler failureHandler) {
        super(RATE, failureHandler);
        addUrlParameter(VALUE, rate.toString());
    }
}
