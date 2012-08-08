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

    private static String SEPARATOR = "|";

    private BiMap<String, String> nodeUUID;

    public MediaIndex() {
        nodeUUID = HashBiMap.create();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.index.IMediaIndex#getValue(java.lang.String)
     */
    @Override
    public IndexNode getValue(String uuid) {
        IndexNode nodeValue = null;
        String value = nodeUUID.get(uuid);
        if (value != null) {
            String[] nodeParams = value.split("\\" + SEPARATOR);
            if (nodeParams != null) {
                if (nodeParams.length == 3) {
                    nodeValue = new IndexNode(nodeParams[0], nodeParams[1], nodeParams[2], null);
                }
                else if (nodeParams.length == 4) {
                    nodeValue = new IndexNode(nodeParams[0], nodeParams[1], nodeParams[2], nodeParams[3]);
                }
            }
        }
        return nodeValue;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.index.IMediaIndex#add(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String add(String parentId, String mediaType, String path, String name) {
        StringBuilder nodeValue = new StringBuilder();
        nodeValue.append(parentId).append(SEPARATOR).append(mediaType).append(SEPARATOR).append(path);
        if (name != null) nodeValue.append(SEPARATOR).append(name);
        String uuid = nodeUUID.inverse().get(nodeValue.toString());
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            nodeUUID.put(uuid, nodeValue.toString());
        }
        return uuid;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.index.IMediaIndex#put(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String put(String uuid, String parentId, String mediaType, String path, String name) {
        if (nodeUUID.get(uuid) == null) {
            StringBuilder nodeValue = new StringBuilder();
            nodeValue.append(parentId).append(SEPARATOR).append(mediaType).append(SEPARATOR).append(path);
            if (name != null) nodeValue.append(SEPARATOR).append(name);
            nodeUUID.put(uuid, nodeValue.toString());
        }
        return uuid;
    }
}
