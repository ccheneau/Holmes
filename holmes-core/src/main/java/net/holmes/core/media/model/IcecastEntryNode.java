/*
 * Copyright (C) 2012-2013  Cedric Cheneau
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

package net.holmes.core.media.model;

import com.google.common.base.Objects;
import net.holmes.core.common.mimetype.MimeType;

/**
 * Icecast entry node.
 */
public class IcecastEntryNode extends AbstractNode {

    private final String url;
    private final MimeType mimeType;

    /**
     * Instantiates a new Icecast entry node.
     *
     * @param id       node id
     * @param parentId parent node id
     * @param name     node name
     * @param mimeType mime type
     * @param url      url
     */
    public IcecastEntryNode(final String id, final String parentId, final String name, final MimeType mimeType, final String url) {
        super(NodeType.TYPE_ICECAST_ENTRY, id, parentId, name);
        this.url = url;
        this.mimeType = mimeType;
    }

    /**
     * Gets the Icecast entry mime type.
     *
     * @return the Icecast entry mime type
     */
    public MimeType getMimeType() {
        return this.mimeType;
    }

    /**
     * Gets the Icecast entry url.
     *
     * @return the Icecast entry url
     */
    public String getUrl() {
        return url;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, parentId, name, type, modifiedDate, iconUrl, mimeType, url);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;

        final IcecastEntryNode other = (IcecastEntryNode) obj;
        return Objects.equal(this.type, other.type) && Objects.equal(this.url, other.url);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(type)
                .addValue(url)
                .addValue(id)
                .addValue(mimeType)
                .addValue(parentId)
                .addValue(name)
                .addValue(modifiedDate)
                .addValue(iconUrl)
                .toString();
    }
}
