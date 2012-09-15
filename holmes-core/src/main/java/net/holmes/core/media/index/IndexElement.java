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
package net.holmes.core.media.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class IndexElement implements Serializable {
    private static final long serialVersionUID = -4133480765408326085L;

    private String parentId;
    private String mediaType;
    private String name;
    private String path;
    private Map<String, String> metadata;

    public IndexElement(String parentId, String mediaType, String path, String name) {
        this.parentId = parentId;
        this.mediaType = mediaType;
        this.path = path;
        this.name = name;
        this.metadata = new HashMap<String, String>();
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

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mediaType == null) ? 0 : mediaType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IndexElement other = (IndexElement) obj;
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
}
