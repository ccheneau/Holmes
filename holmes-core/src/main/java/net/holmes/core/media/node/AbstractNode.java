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

import java.io.Serializable;

public abstract class AbstractNode implements Comparable<AbstractNode>, Serializable {
    private static final long serialVersionUID = 5909549322056486631L;

    protected String id;
    protected String parentId;
    protected String name;
    protected String modifedDate;
    protected NodeType type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getType() {
        return type;
    }

    public String getModifedDate() {
        return modifedDate;
    }

    public void setModifedDate(String modifedDate) {
        this.modifedDate = modifedDate;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(AbstractNode o) {
        if (this.getType() == o.getType()) return this.name.compareTo(o.name);
        else if (this.getType() == NodeType.TYPE_FOLDER) return -1;
        else return 1;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AbstractNode [id=");
        builder.append(id);
        builder.append(", parentId=");
        builder.append(parentId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", modifedDate=");
        builder.append(modifedDate);
        builder.append(", type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }
}
