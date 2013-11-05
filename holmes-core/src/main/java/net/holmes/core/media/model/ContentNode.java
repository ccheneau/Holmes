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

import java.io.File;

/**
 * Content node.
 */
public final class ContentNode extends MimeTypeNode {

    private final Long size;
    private final String path;

    /**
     * Instantiates a new content node.
     *
     * @param id       node id
     * @param parentId parent node id
     * @param name     node name
     * @param file     node file
     * @param mimeType mime type
     */
    public ContentNode(final String id, final String parentId, final String name, final File file, final MimeType mimeType) {
        super(NodeType.TYPE_CONTENT, id, parentId, name, mimeType);
        this.path = file.getAbsolutePath();
        this.size = file.length();
        this.modifiedDate = file.lastModified();
    }

    /**
     * Gets the content node size.
     *
     * @return the content node size
     */
    public Long getSize() {
        return size;
    }

    /**
     * Gets the content node path.
     *
     * @return the content node path
     */
    public String getPath() {
        return path;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, parentId, name, type, modifiedDate, iconUrl, mimeType, size, path);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;

        final ContentNode other = (ContentNode) obj;
        return Objects.equal(this.mimeType, other.mimeType) && Objects.equal(this.size, other.size) && Objects.equal(this.path, other.path);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(mimeType)
                .addValue(size)
                .addValue(path)
                .addValue(id)
                .addValue(type)
                .addValue(parentId)
                .addValue(name)
                .addValue(modifiedDate)
                .addValue(iconUrl)
                .toString();
    }
}
