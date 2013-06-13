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
        if (folder != null) {
            this.path = folder.getAbsolutePath();
            this.modifiedDate = folder.lastModified();
        } else {
            this.path = null;
        }
    }

    /**
     * Instantiates a new folder node.
     *
     * @param id       node id
     * @param parentId parent node id
     * @param name     node name
     */
    public FolderNode(final String id, final String parentId, final String name) {
        this(id, parentId, name, null);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, parentId, name, type, modifiedDate, iconUrl, path);
    }

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
        final FolderNode other = (FolderNode) obj;
        return Objects.equal(this.path, other.path);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
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
