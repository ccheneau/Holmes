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

package net.holmes.core.common;

import java.util.UUID;

/**
 * Utility class to generate an unique ID
 */
public final class UniqueIdGenerator {

    /**
     * Private constructor
     */
    private UniqueIdGenerator() {
        // Nothing
    }

    /**
     * Generate a new unique ID
     *
     * @return unique ID
     */
    public static String newUniqueId() {
        return String.valueOf(UUID.randomUUID().getMostSignificantBits());
    }
}
