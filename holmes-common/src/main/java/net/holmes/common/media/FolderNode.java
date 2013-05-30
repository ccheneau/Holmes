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

package net.holmes.common.media;

import java.io.File;

/**
 * Folder node.
 */
public final class FolderNode extends AbstractNode {

    private final String path;

    /**
     * Instantiates a new folder node.
     *
     * @param id       node id
     * @param parentId parent node id
     * @param name     node name
     * @param folder   node folder
     */
    public FolderNode(final String id, final String parentId, final String name, final File folder) {
        super(NodeType.TYPE_FOLDER, id, parentId, name);
        this.path = folder.getAbsolutePath();
        this.modifiedDate = folder.lastModified();
    }

    /**
     * Instantiates a new folder node.
     *
     * @param id       node id
     * @param parentId parent node id
     * @param name     node name
     */
    public FolderNode(final String id, final String parentId, final String name) {
        super(NodeType.TYPE_FOLDER, id, parentId, name);
        this.path = null;

    }

    public String getPath() {
        return path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        FolderNode other = (FolderNode) obj;
        if (path == null) {
            if (other.path != null) return false;
        } else if (!path.equals(other.path)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FolderNode [path=");
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
