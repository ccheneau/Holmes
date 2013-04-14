/**
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

import java.io.Serializable;

/**
 * Media index element.
 * @author Cedric
 *
 */
public class MediaIndexElement implements Serializable {
    private static final long serialVersionUID = -7736893745535506209L;

    private final String parentId;
    private final String mediaType;
    private final String name;
    private final String path;
    private final boolean localPath;

    /**
     * Constructor.
     *
     * @param parentId 
     *      parent id
     * @param mediaType 
     *      media type
     * @param path 
     *      path
     * @param name 
     *      name
     * @param localPath 
     *      local path
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
        final int prime = 31;
        int result = 1;
        result = prime * result + (localPath ? 1231 : 1237);
        result = prime * result + ((mediaType == null) ? 0 : mediaType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MediaIndexElement other = (MediaIndexElement) obj;
        if (localPath != other.localPath) return false;
        if (mediaType == null) {
            if (other.mediaType != null) return false;
        } else if (!mediaType.equals(other.mediaType)) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (parentId == null) {
            if (other.parentId != null) return false;
        } else if (!parentId.equals(other.parentId)) return false;
        if (path == null) {
            if (other.path != null) return false;
        } else if (!path.equals(other.path)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MediaIndexElement [parentId=");
        builder.append(parentId);
        builder.append(", mediaType=");
        builder.append(mediaType);
        builder.append(", name=");
        builder.append(name);
        builder.append(", path=");
        builder.append(path);
        builder.append(", localPath=");
        builder.append(localPath);
        builder.append("]");
        return builder.toString();
    }
}
