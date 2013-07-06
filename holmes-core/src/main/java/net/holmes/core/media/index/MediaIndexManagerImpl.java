/*
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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.holmes.core.inject.InjectLogger;
import net.holmes.core.media.model.RootNode;
import org.slf4j.Logger;

import java.io.File;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * Media index manager implementation.
 */
public class MediaIndexManagerImpl implements MediaIndexManager {
    private final BiMap<String, MediaIndexElement> elements;
    @InjectLogger
    private Logger logger;

    /**
     * Instantiates a new media index manager implementation.
     */
    public MediaIndexManagerImpl() {
        this.elements = Maps.synchronizedBiMap(HashBiMap.<String, MediaIndexElement>create());
    }

    @Override
    public MediaIndexElement get(final String uuid) {
        return elements.get(uuid);
    }

    @Override
    public String add(final MediaIndexElement element) {
        String uuid = elements.inverse().get(element);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            elements.put(uuid, element);
        }
        return uuid;
    }

    @Override
    public void put(final String uuid, final MediaIndexElement element) {
        if (elements.get(uuid) == null) elements.put(uuid, element);
    }

    @Override
    public void remove(final String uuid) {
        if (elements.get(uuid) != null) elements.remove(uuid);
    }

    @Override
    public synchronized void removeChildren(final String uuid) {
        MediaIndexElement elValue;
        Collection<String> toRemove = Lists.newArrayList();

        // Search elements to remove
        for (Entry<String, MediaIndexElement> indexEntry : elements.entrySet()) {
            elValue = indexEntry.getValue();
            // Check parent id
            if (elValue.getParentId().equals(uuid) || toRemove.contains(elValue.getParentId())) {
                toRemove.add(indexEntry.getKey());
                logger.debug("Remove child entry {} from media index", elValue.toString());
            }
        }

        // Remove elements
        for (String id : toRemove)
            elements.remove(id);
    }

    @Override
    public synchronized void clean() {
        String elId;
        MediaIndexElement elValue;
        Collection<String> toRemove = Lists.newArrayList();

        // Search elements to remove
        for (Entry<String, MediaIndexElement> indexEntry : elements.entrySet()) {
            elId = indexEntry.getKey();
            elValue = indexEntry.getValue();

            // Check parent id is still in index (only for non root nodes and direct children)
            if (RootNode.getById(elId) == RootNode.NONE && RootNode.getById(elValue.getParentId()) == RootNode.NONE
                    && (elements.get(elValue.getParentId()) == null || toRemove.contains(elValue.getParentId()))) {
                toRemove.add(elId);
                logger.debug("Remove entry {} from media index (invalid parent id)", elValue.toString());
            }
            // Check element is still on file system
            if (!toRemove.contains(elId) && elValue.isLocalPath() && !new File(elValue.getPath()).exists()) {
                toRemove.add(elId);
                logger.debug("Remove entry {} from media index (path does not exist)", elValue.toString());
            }
        }
        // Remove elements
        for (String id : toRemove) {
            elements.remove(id);
        }
    }
}
