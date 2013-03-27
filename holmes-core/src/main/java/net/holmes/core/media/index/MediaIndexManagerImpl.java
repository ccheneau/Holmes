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
import java.util.Set;
import java.util.UUID;

import net.holmes.core.inject.Loggable;
import net.holmes.core.media.node.RootNode;

import org.slf4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Loggable
public class MediaIndexManagerImpl implements MediaIndexManager {
    private Logger logger;

    private final BiMap<String, MediaIndexElement> elements;

    public MediaIndexManagerImpl() {
        this.elements = Maps.synchronizedBiMap(HashBiMap.<String, MediaIndexElement> create());
        //this.elements = HashBiMap.create();
    }

    @Override
    public MediaIndexElement get(String uuid) {
        return elements.get(uuid);
    }

    @Override
    public String add(MediaIndexElement element) {
        String uuid = elements.inverse().get(element);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            elements.put(uuid, element);
        }
        return uuid;
    }

    @Override
    public void put(String uuid, MediaIndexElement element) {
        if (elements.get(uuid) == null) elements.put(uuid, element);
    }

    @Override
    public void remove(String uuid) {
        if (elements.get(uuid) != null) elements.remove(uuid);
    }

    @Override
    public synchronized void removeChilds(String uuid) {
        MediaIndexElement elValue = null;
        Collection<String> toRemove = Lists.newArrayList();

        // Search elements to remove
        for (Entry<String, MediaIndexElement> indexEntry : elements.entrySet()) {
            elValue = indexEntry.getValue();
            // Check parent id
            if (elValue.getParentId().equals(uuid) || toRemove.contains(elValue.getParentId())) {
                toRemove.add(indexEntry.getKey());
                if (logger.isDebugEnabled()) logger.debug("Remove child entry {} from media index", elValue.toString());
            }
        }

        // Remove elements
        for (String id : toRemove) {
            elements.remove(id);
        }
    }

    @Override
    public synchronized void clean() {
        String elId = null;
        MediaIndexElement elValue = null;
        Collection<String> toRemove = Lists.newArrayList();

        // Search elements to remove
        for (Entry<String, MediaIndexElement> indexEntry : elements.entrySet()) {
            elId = indexEntry.getKey();
            elValue = indexEntry.getValue();

            // Check parent id is still in index (only for non root nodes and direct childs)
            if (RootNode.getById(elId) == null && RootNode.getById(elValue.getParentId()) == null
                    && (elements.get(elValue.getParentId()) == null || toRemove.contains(elValue.getParentId()))) {
                toRemove.add(elId);
                if (logger.isDebugEnabled()) logger.debug("Remove entry {} from media index (invalid parent id)", elValue.toString());
            }
            // Check element is still on file system
            if (!toRemove.contains(elId) && elValue.isLocalPath() && !new File(elValue.getPath()).exists()) {
                toRemove.add(elId);
                if (logger.isDebugEnabled()) logger.debug("Remove entry {} from media index (path does not exist)", elValue.toString());
            }
        }
        // Remove elements
        for (String id : toRemove) {
            elements.remove(id);
        }
    }

    @Override
    public Set<Entry<String, MediaIndexElement>> getElements() {
        return elements.entrySet();
    }
}
