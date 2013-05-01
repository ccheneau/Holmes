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
package net.holmes.common.media;

import java.io.File;

import net.holmes.common.mimetype.MimeType;

/**
 * Content node.
 */
public final class ContentNode extends AbstractNode {
    private final MimeType mimeType;
    private final Long size;
    private final String path;

    /**
     * Constructor.
     *
     * @param id 
     *      node id
     * @param parentId 
     *      node parent id
     * @param name 
     *      node name
     * @param file 
     *      node file
     * @param mimeType 
     *      mime type
     */
    public ContentNode(final String id, final String parentId, final String name, final File file, final MimeType mimeType) {
        super(NodeType.TYPE_CONTENT, id, parentId, name);
        this.path = file.getAbsolutePath();
        this.mimeType = mimeType;
        this.size = file.length();
        this.modifiedDate = file.lastModified();
    }

    /**
     * Gets the content node mime type.
     *
     * @return the content node mime type
     */
    public MimeType getMimeType() {
        return this.mimeType;
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
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((size == null) ? 0 : size.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        ContentNode other = (ContentNode) obj;
        if (mimeType == null) {
            if (other.mimeType != null) return false;
        } else if (!mimeType.equals(other.mimeType)) return false;
        if (path == null) {
            if (other.path != null) return false;
        } else if (!path.equals(other.path)) return false;
        if (size == null) {
            if (other.size != null) return false;
        } else if (!size.equals(other.size)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ContentNode [mimeType=");
        builder.append(mimeType);
        builder.append(", size=");
        builder.append(size);
        builder.append(", path=");
        builder.append(path);
        builder.append(", id=");
        builder.append(id);
        builder.append(", parentId=");
        builder.append(parentId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", modifiedDate=");
        builder.append(modifiedDate);
        builder.append(", type=");
        builder.append(type);
        builder.append(", iconUrl=");
        builder.append(iconUrl);
        builder.append("]");
        return builder.toString();
    }
}
