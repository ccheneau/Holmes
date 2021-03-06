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

package net.holmes.core.common.event;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Media event.
 */
public class MediaEvent {

    private final MediaEventType type;
    private final String parameter;

    /**
     * Instantiates a new media event.
     *
     * @param type      media type
     * @param parameter parameter
     */
    public MediaEvent(final MediaEventType type, final String parameter) {
        this.type = type;
        this.parameter = parameter;
    }

    /**
     * Get media event type.
     *
     * @return media event type
     */
    public MediaEventType getType() {
        return type;
    }

    /**
     * Get media event parameter.
     *
     * @return media event parameter
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this)
                .add("type", type)
                .add("parameter", parameter)
                .toString();
    }

    /**
     * Media event type.
     */
    public enum MediaEventType {
        SCAN_NODE, UNKNOWN
    }
}
