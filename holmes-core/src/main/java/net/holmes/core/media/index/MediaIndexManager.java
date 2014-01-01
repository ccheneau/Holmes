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

package net.holmes.core.media.index;

/**
 * Media index manager.
 */
public interface MediaIndexManager {

    /**
     * Gets the media index element.
     *
     * @param uuid element uuid
     * @return media index element
     */
    MediaIndexElement get(String uuid);

    /**
     * Adds media index element.
     *
     * @param element index element
     * @return new element's uuid
     */
    String add(MediaIndexElement element);

    /**
     * Put media index element.
     *
     * @param uuid    element uuid
     * @param element element to put
     */
    void put(String uuid, MediaIndexElement element);

    /**
     * Removes media index element.
     *
     * @param uuid element uuid
     */
    void remove(String uuid);

    /**
     * Removes child media index elements.
     *
     * @param uuid parent element uuid
     */
    void removeChildren(String uuid);

    /**
     * Clean media index.
     */
    void clean();
}
