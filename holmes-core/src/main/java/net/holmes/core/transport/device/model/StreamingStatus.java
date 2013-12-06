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

package net.holmes.core.transport.device.model;

/**
 * Streaming status
 */
public final class StreamingStatus {
    private final Double duration;
    private final Double position;

    /**
     * Instantiates a new streaming status.
     *
     * @param duration duration in seconds
     * @param position playback position in seconds
     */
    public StreamingStatus(final Double duration, Double position) {
        this.duration = duration;
        this.position = position;
    }

    public Double getDuration() {
        return duration;
    }

    public Double getPosition() {
        return position;
    }
}
