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

import net.holmes.core.business.mimetype.model.MimeType;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Raw Url node represents a link to external Url.
 */
public final class RawUrlNode extends MimeTypeNode {

    private final String url;
    private final String duration;

    /**
     * Instantiates a new raw Url node.
     *
     * @param type     type
     * @param id       node id
     * @param parentId parent node id
     * @param name     node name
     * @param mimeType mime type
     * @param url      url
     * @param duration duration
     */
    public RawUrlNode(final NodeType type, final String id, final String parentId, final String name, final MimeType mimeType, final String url,
                      final String duration) {
        super(type, id, parentId, name, mimeType);
        this.url = url;
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public String getDuration() {
        return duration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, name, type, modifiedDate, iconUrl, mimeType, url, duration);
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

        final RawUrlNode other = (RawUrlNode) obj;
        return Objects.equals(this.mimeType, other.mimeType)
                && Objects.equals(this.url, other.url)
                && Objects.equals(this.duration, other.duration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("parentId", parentId)
                .add("name", name)
                .add("modifiedDate", modifiedDate)
                .add("iconUrl", iconUrl)
                .add("mimeType", mimeType)
                .add("url", url)
                .add("duration", duration)
                .toString();
    }
}
