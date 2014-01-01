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

package net.holmes.core.transport.airplay.command;

import static net.holmes.core.transport.airplay.command.Command.CommandType.SEEK;
import static net.holmes.core.transport.airplay.command.Command.UrlParameter.POSITION;

/**
 * Airplay seek command: Seek at an arbitrary location in the video
 */
public abstract class SeekCommand extends Command {
    /**
     * Instantiates a new Airplay scrub command.
     *
     * @param position position in seconds
     */
    public SeekCommand(final Double position) {
        super(SEEK);
        addUrlParameter(POSITION, position.toString());
    }
}
