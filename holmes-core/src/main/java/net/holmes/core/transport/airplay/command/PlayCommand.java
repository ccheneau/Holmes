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

import static net.holmes.core.transport.airplay.command.Command.CommandType.PLAY;
import static net.holmes.core.transport.airplay.command.Command.PostParameter.CONTENT_LOCATION;
import static net.holmes.core.transport.airplay.command.Command.PostParameter.START_POSITION;

/**
 * Airplay play command: Start video playback
 */
public abstract class PlayCommand extends Command {

    /**
     * Instantiates a new Airplay play command.
     *
     * @param contentUrl    content Url
     * @param startPosition start position between 0 (start) and 1 (end)
     */
    public PlayCommand(final String contentUrl, final Double startPosition) {
        super(PLAY);
        addPostParameter(CONTENT_LOCATION, contentUrl);
        addPostParameter(START_POSITION, String.format("%f", startPosition));
    }
}
