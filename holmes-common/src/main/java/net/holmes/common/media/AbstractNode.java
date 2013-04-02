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


public abstract class AbstractNode implements Comparable<AbstractNode> {
    protected final String id;
    protected final String parentId;
    protected final String name;
    protected final NodeType type;
    protected Long modifedDate;
    protected String iconUrl;

    public AbstractNode(NodeType type, String id, String parentId, String name) {
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

    public void setModifedDate(Long modifedDate) {
        this.modifedDate = modifedDate;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public int compareTo(AbstractNode o) {
        if (this.getType() == o.getType()) return this.name.compareTo(o.name);
        else if (this.getType() == NodeType.TYPE_FOLDER) return -1;
        else return 1;
    }

    public enum NodeType {
        TYPE_FOLDER, //
        TYPE_CONTENT, //
        TYPE_PODCAST, //
        TYPE_PODCAST_ENTRY, //
        TYPE_PLAYLIST;
    }

}
