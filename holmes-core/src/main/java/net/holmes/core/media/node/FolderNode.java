/**
* Copyright (C) 2012  Cedric Cheneau
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
package net.holmes.core.media.node;

public final class FolderNode extends AbstractNode {
    private static final long serialVersionUID = 8129761596085917631L;

    public FolderNode() {
        type = NodeType.TYPE_FOLDER;
    }

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.node.AbstractNode#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FolderNode [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", path=");
        builder.append(path);
        builder.append(", modifedDate=");
        builder.append(modifedDate);
        builder.append(", type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }
}
