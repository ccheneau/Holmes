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

package net.holmes.core.media.index;

import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * Media index element.
 */
public class MediaIndexElement implements Serializable {
    private static final long serialVersionUID = -7736893745535506209L;

    private final String parentId;
    private final String mediaType;
    private final String name;
    private final String path;
    private final boolean localPath;

    /**
     * Instantiates a new media index element.
     *
     * @param parentId  parent id
     * @param mediaType media type
     * @param path      path
     * @param name      name
     * @param localPath local path
     */
    public MediaIndexElement(final String parentId, final String mediaType, final String path, final String name, final boolean localPath) {
        this.parentId = parentId;
        this.mediaType = mediaType;
        this.path = path;
        this.name = name;
        this.localPath = localPath;
    }

    public String getParentId() {
        return parentId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public boolean isLocalPath() {
        return localPath;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parentId, mediaType, name, path, localPath);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final MediaIndexElement other = (MediaIndexElement) obj;
        return Objects.equal(this.parentId, other.parentId) && Objects.equal(this.mediaType, other.mediaType) && Objects.equal(this.name, other.name) && Objects.equal(this.path, other.path) && Objects.equal(this.localPath, other.localPath);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(parentId)
                .addValue(mediaType)
                .addValue(name)
                .addValue(path)
                .addValue(localPath)
                .toString();
    }
}
