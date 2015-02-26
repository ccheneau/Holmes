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

package net.holmes.core.business.media.model;

import java.util.Objects;

/**
 * Abstract node.
 */
public abstract class AbstractNode implements MediaNode {

    final String id;
    final String parentId;
    final String name;
    final NodeType type;
    Long modifiedDate;
    String iconUrl;

    /**
     * Instantiates a new abstract node.
     *
     * @param type     node type
     * @param id       node id
     * @param parentId parent node id
     * @param name     node name
     */
    AbstractNode(final NodeType type, final String id, final String parentId, final String name) {
        this.type = type;
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParentId() {
        return parentId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getModifiedDate() {
        return modifiedDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModifiedDate(final Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIconUrl() {
        return iconUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIconUrl(final String iconUrl) {
        this.iconUrl = iconUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(final MediaNode o) {
        if (this.getType() == o.getType()) {
            return this.name.compareTo(o.getName());
        } else if (this.getType() == NodeType.TYPE_FOLDER) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, name, type, modifiedDate, iconUrl);
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

        final AbstractNode other = (AbstractNode) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.parentId, other.parentId)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.modifiedDate, other.modifiedDate);
    }
}
