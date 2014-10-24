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

package net.holmes.core.business.configuration;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Configuration node.
 */
public final class ConfigurationNode {

    private final String id;
    private String label;
    private String path;

    /**
     * Instantiates a new configuration node.
     *
     * @param id    node id
     * @param label node label
     * @param path  node path
     */
    public ConfigurationNode(final String id, final String label, final String path) {
        this.id = id;
        this.label = label;
        this.path = path;
    }

    /**
     * Get configuration node id.
     *
     * @return configuration node id
     */
    public String getId() {
        return id;
    }

    /**
     * Get configuration node name.
     *
     * @return configuration node name
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set configuration node label.
     *
     * @param label configuration node label
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * Get configuration node path.
     *
     * @return configuration node path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set configuration node path.
     *
     * @param path configuration node path
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, label, path);
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

        final ConfigurationNode other = (ConfigurationNode) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.label, other.label)
                && Objects.equals(this.path, other.path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("label", label)
                .add("path", path)
                .toString();
    }
}
