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

package net.holmes.core.backend.response;

public class IndexElement {

    private final String id;
    private final String parentId;
    private final String mediaType;
    private final String name;
    private final String path;

    public IndexElement(String id, String parentId, String mediaType, String name, String path) {
        this.id = id;
        this.parentId = parentId;
        this.mediaType = mediaType;
        this.name = name;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
