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

import java.util.Map;

import com.google.common.collect.Maps;

public class Folder {
    private final String data;
    private final String state = "closed";
    private final Map<String, String> metadata;

    public Folder(String data, String path) {
        this.data = data;
        this.metadata = Maps.newHashMap();
        this.metadata.put("path", path);
    }

    public String getState() {
        return state;
    }

    public String getData() {
        return data;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
