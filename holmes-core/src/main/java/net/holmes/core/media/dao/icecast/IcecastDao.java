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

package net.holmes.core.media.dao.icecast;

import java.util.Collection;
import java.util.List;

/**
 * Dao for Icecast data.
 */
public interface IcecastDao {

    /**
     * Check Icecast Yellow page. Download and parse Yellow page if needed.
     */
    void checkYellowPage();

    /**
     * Parse yellow page.
     *
     * @return true on parsing success
     */
    boolean parseYellowPage();

    /**
     * Whether Icecast directory is loaded.
     *
     * @return true if Icecast directory is loaded
     */
    boolean isLoaded();

    /**
     * Get available genres.
     *
     * @return genre list
     */
    List<IcecastGenre> getGenres();

    /**
     * Get Icecast entries by genre.
     *
     * @param genre Icecast genre
     * @return Icecast entries
     */
    Collection<IcecastEntry> getEntriesByGenre(final String genre);
}
