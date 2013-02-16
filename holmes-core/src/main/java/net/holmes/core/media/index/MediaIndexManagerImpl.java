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
package net.holmes.core.media.index;

import java.util.UUID;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

public class MediaIndexManagerImpl implements MediaIndexManager {

    private BiMap<String, MediaIndexElement> elements;

    public MediaIndexManagerImpl() {
        elements = Maps.synchronizedBiMap(HashBiMap.<String, MediaIndexElement> create());
        //elements = HashBiMap.create();
    }

    @Override
    public MediaIndexElement get(String uuid) {
        return elements.get(uuid);
    }

    @Override
    public String add(String parentId, String mediaType, String path, String name) {
        MediaIndexElement element = new MediaIndexElement(parentId, mediaType, path, name);
        String uuid = elements.inverse().get(element);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            elements.put(uuid, element);
        }
        return uuid;
    }

    @Override
    public void put(String uuid, String parentId, String mediaType, String path, String name) {
        if (elements.get(uuid) == null) {
            elements.put(uuid, new MediaIndexElement(parentId, mediaType, path, name));
        }
    }
}
