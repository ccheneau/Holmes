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

package net.holmes.core.backend.response;

import com.google.common.base.Objects;

/**
 * Configuration folder.
 */
public final class ConfigurationFolder {

    private String id;
    private String name;
    private String path;

    /**
     * Instantiates a new configuration folder.
     */
    public ConfigurationFolder() {
    }

    /**
     * Instantiates a new configuration folder.
     *
     * @param id   folder id
     * @param name folder name
     * @param path folder path
     */
    public ConfigurationFolder(final String id, final String name, final String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, path);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final ConfigurationFolder other = (ConfigurationFolder) obj;
        return Objects.equal(this.id, other.id) && Objects.equal(this.name, other.name) && Objects.equal(this.path, other.path);
    }
}
