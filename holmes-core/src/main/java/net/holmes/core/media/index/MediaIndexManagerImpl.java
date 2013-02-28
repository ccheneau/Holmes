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

import java.io.File;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;

import net.holmes.core.media.node.RootNode;
import net.holmes.core.util.inject.Loggable;

import org.slf4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Loggable
public class MediaIndexManagerImpl implements MediaIndexManager {
    private Logger logger;

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
    public String add(String parentId, String mediaType, String path, String name, boolean localPath) {
        MediaIndexElement element = new MediaIndexElement(parentId, mediaType, path, name, localPath);
        String uuid = elements.inverse().get(element);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            elements.put(uuid, element);
        }
        return uuid;
    }

    @Override
    public void put(String uuid, String parentId, String mediaType, String path, String name, boolean localPath) {
        if (elements.get(uuid) == null) {
            elements.put(uuid, new MediaIndexElement(parentId, mediaType, path, name, localPath));
        }
    }

    @Override
    public void remove(String uuid) {
        if (elements.get(uuid) != null) {
            elements.remove(uuid);
        }
    }

    @Override
    public void clean() {
        String elId = null;
        MediaIndexElement elValue = null;
        Collection<String> toRemove = Lists.newArrayList();

        // Search elements to remove
        for (Entry<String, MediaIndexElement> indexEntry : elements.entrySet()) {
            elId = indexEntry.getKey();
            elValue = indexEntry.getValue();
            if (RootNode.getById(elId) == null) {
                // Check parent id is still in index (only for non root nodes)
                if (elements.get(elValue.getParentId()) == null || toRemove.contains(elValue.getParentId())) {
                    toRemove.add(elId);
                    if (logger.isDebugEnabled()) logger.debug("Remove entry {} from media index (invalid parent id)", elValue.toString());
                }
            }
            // Check element is still on file system
            else if (elValue.isLocalPath()) {
                if (!new File(elValue.getPath()).exists()) {
                    toRemove.add(elId);
                    if (logger.isDebugEnabled()) logger.debug("Remove entry {} from media index (path does not exist)", elValue.toString());
                }
            }
        }

        // Remove elements
        for (String id : toRemove) {
            elements.remove(id);
        }
    }
}
