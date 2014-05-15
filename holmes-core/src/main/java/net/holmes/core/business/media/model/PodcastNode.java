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

package net.holmes.core.business.media.model;

import com.google.common.base.Objects;

/**
 * Podcast node.
 */
public final class PodcastNode extends AbstractNode {

    private final String url;

    /**
     * Instantiates a new podcast node.
     *
     * @param id       node id
     * @param parentId parent node id
     * @param name     node name
     * @param url      node url
     */
    public PodcastNode(final String id, final String parentId, final String name, final String url) {
        super(NodeType.TYPE_PODCAST, id, parentId, name);
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id, parentId, name, type, modifiedDate, iconUrl, url);
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
        if (!super.equals(obj)) {
            return false;
        }

        final PodcastNode other = (PodcastNode) obj;
        return Objects.equal(this.url, other.url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("parentId", parentId)
                .add("name", name)
                .add("modifiedDate", modifiedDate)
                .add("iconUrl", iconUrl)
                .add("url", url)
                .toString();
    }
}
