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

package net.holmes.core.business.media.dao.icecast;

import com.google.common.base.Objects;

/**
 * Icecast entry.
 */
public class IcecastEntry {
    private final String name;
    private final String url;
    private final String type;
    private final String genre;

    /**
     * Instantiates a new Icecast entry.
     *
     * @param name  entry name
     * @param url   entry url
     * @param type  entry type
     * @param genre entry genre
     */
    public IcecastEntry(String name, String url, String type, String genre) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.genre = genre;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getGenre() {
        return genre;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(name, type, genre);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final IcecastEntry other = (IcecastEntry) obj;
        return Objects.equal(this.name, other.name) && Objects.equal(this.type, other.type) && Objects.equal(this.genre, other.genre);
    }
}
