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

import java.util.UUID;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class MediaIndex implements IMediaIndex {

    private BiMap<String, IndexElement> elements;

    public MediaIndex() {
        elements = HashBiMap.create();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.index.IMediaIndex#getElement(java.lang.String)
     */
    @Override
    public IndexElement getElement(String uuid) {
        return elements.get(uuid);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.index.IMediaIndex#add(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String add(String parentId, String mediaType, String path, String name) {
        IndexElement element = new IndexElement(parentId, mediaType, path, name);
        String uuid = elements.inverse().get(element);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            elements.put(uuid, element);
        }
        return uuid;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.index.IMediaIndex#put(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void put(String uuid, String parentId, String mediaType, String path, String name) {
        if (elements.get(uuid) == null) {
            elements.put(uuid, new IndexElement(parentId, mediaType, path, name));
        }
    }
}
