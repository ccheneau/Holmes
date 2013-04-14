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

/**
 * Abstract node.
 */
public abstract class AbstractNode implements Comparable<AbstractNode> {
    protected final String id;
    protected final String parentId;
    protected final String name;
    protected final NodeType type;
    protected Long modifedDate;
    protected String iconUrl;

    /**
     * Constructor.
     *
     * @param type 
     *      node type
     * @param id 
     *      node id
     * @param parentId 
     *      node  parent id
     * @param name 
     *      node name
     */
    public AbstractNode(final NodeType type, final String id, final String parentId, final String name) {
        this.type = type;
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }

    public Long getModifedDate() {
        return modifedDate;
    }

    public void setModifedDate(final Long modifedDate) {
        this.modifedDate = modifedDate;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(final String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public int compareTo(final AbstractNode o) {
        if (this.getType() == o.getType()) return this.name.compareTo(o.name);
        else if (this.getType() == NodeType.TYPE_FOLDER) return -1;
        else return 1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((iconUrl == null) ? 0 : iconUrl.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((modifedDate == null) ? 0 : modifedDate.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AbstractNode other = (AbstractNode) obj;
        if (iconUrl == null) {
            if (other.iconUrl != null) return false;
        } else if (!iconUrl.equals(other.iconUrl)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (modifedDate == null) {
            if (other.modifedDate != null) return false;
        } else if (!modifedDate.equals(other.modifedDate)) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (parentId == null) {
            if (other.parentId != null) return false;
        } else if (!parentId.equals(other.parentId)) return false;
        if (type != other.type) return false;
        return true;
    }

    /**
     * Node type.
     */
    public enum NodeType {
        TYPE_FOLDER, //
        TYPE_CONTENT, //
        TYPE_PODCAST, //
        TYPE_PODCAST_ENTRY, //
        TYPE_PLAYLIST;
    }
}
