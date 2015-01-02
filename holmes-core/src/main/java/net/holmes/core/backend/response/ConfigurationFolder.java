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

package net.holmes.core.backend.response;


import java.util.Objects;

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

    /**
     * Get folder id.
     *
     * @return folder id
     */
    public String getId() {
        return id;
    }

    /**
     * Set folder id.
     *
     * @param id new folder id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Get folder name.
     *
     * @return folder name
     */
    public String getName() {
        return name;
    }

    /**
     * Set folder name.
     *
     * @param name folder name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get folder path.
     *
     * @return folder path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set folder path.
     *
     * @param path new folder path
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, path);
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

        final ConfigurationFolder other = (ConfigurationFolder) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.path, other.path);
    }
}
