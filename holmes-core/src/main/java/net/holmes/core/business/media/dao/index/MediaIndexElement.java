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

package net.holmes.core.business.media.dao.index;

import java.io.Serializable;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

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

    /**
     * Get parent id.
     *
     * @return parent id
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Get media type.
     *
     * @return media type
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Ge path.
     *
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Get name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Check if element is a local path.
     *
     * @return true if element is a local path
     */
    public boolean isLocalPath() {
        return localPath;
    }

    /**
     * Get mime type.
     *
     * @return mime type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Check if element is locked.
     *
     * @return true if if element is locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(parentId, mediaType, mimeType, name, path, localPath, locked);
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

        MediaIndexElement other = (MediaIndexElement) obj;
        return Objects.equals(this.parentId, other.parentId)
                && Objects.equals(this.mediaType, other.mediaType)
                && Objects.equals(this.mimeType, other.mimeType)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.path, other.path)
                && Objects.equals(this.localPath, other.localPath)
                && Objects.equals(this.locked, other.locked);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this)
                .add("parentId", parentId)
                .add("mediaType", mediaType)
                .add("mimeType", mimeType)
                .add("name", name)
                .add("path", path)
                .add("localPath", localPath)
                .toString();
    }
}
