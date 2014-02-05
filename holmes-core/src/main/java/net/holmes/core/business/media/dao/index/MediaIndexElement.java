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

package net.holmes.core.business.media.dao.index;

import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * Media index element.
 */
public class MediaIndexElement implements Serializable {
    private final String parentId;
    private final String mediaType;
    private final String mimeType;
    private final String name;
    private final String path;
    private final boolean localPath;
    private final boolean locked;

    /**
     * Instantiates a new media index element.
     *
     * @param parentId  parent id
     * @param mediaType media type
     * @param mimeType  mime type
     * @param path      path
     * @param name      name
     * @param localPath whether element is stored on local file system
     * @param locked    locked element, cannot be removed from media index
     */
    public MediaIndexElement(final String parentId, final String mediaType, final String mimeType, final String path, final String name, final boolean localPath, final boolean locked) {
        this.parentId = parentId;
        this.mediaType = mediaType;
        this.mimeType = mimeType;
        this.path = path;
        this.name = name;
        this.localPath = localPath;
        this.locked = locked;
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

    public String getMimeType() {
        return mimeType;
    }

    public boolean isLocked() {
        return locked;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(parentId, mediaType, mimeType, name, path, localPath, locked);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final MediaIndexElement other = (MediaIndexElement) obj;
        return Objects.equal(this.parentId, other.parentId)
                && Objects.equal(this.mediaType, other.mediaType)
                && Objects.equal(this.mimeType, other.mimeType)
                && Objects.equal(this.name, other.name)
                && Objects.equal(this.path, other.path)
                && Objects.equal(this.localPath, other.localPath)
                && Objects.equal(this.locked, other.locked);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("parentId", parentId)
                .add("mediaType", mediaType)
                .add("mimeType", mimeType)
                .add("name", name)
                .add("path", path)
                .add("localPath", localPath)
                .toString();
    }
}
