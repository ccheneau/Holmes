/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

package net.holmes.core.business.media.dao.index;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.Subscribe;
import net.holmes.core.business.configuration.model.ConfigurationNode;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.common.event.ConfigurationEvent;
import org.slf4j.Logger;

import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import static com.google.common.collect.Maps.synchronizedBiMap;
import static net.holmes.core.business.media.dao.index.MediaIndexElementFactory.buildConfigMediaIndexElement;
import static net.holmes.core.business.media.model.RootNode.PODCAST;
import static net.holmes.core.common.UniqueIdGenerator.newUniqueId;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Media index dao implementation.
 */
@Singleton
public class MediaIndexDaoImpl implements MediaIndexDao {
    private static final Logger LOGGER = getLogger(MediaIndexDaoImpl.class);

    private final BiMap<String, MediaIndexElement> elements;

    /**
     * Instantiates a new media index dao implementation.
     */
    public MediaIndexDaoImpl() {
        this.elements = synchronizedBiMap(HashBiMap.<String, MediaIndexElement>create());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MediaIndexElement get(final String uuid) {
        return elements.get(uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String add(final MediaIndexElement element) {
        String uuid = elements.inverse().get(element);
        if (uuid == null) {
            uuid = newUniqueId();
            elements.put(uuid, element);
        }
        return uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String uuid, final MediaIndexElement element) {
        if (elements.get(uuid) == null) {
            elements.put(uuid, element);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeChildren(final String uuid) {
        MediaIndexElement elValue;
        Collection<String> toRemove = new ArrayList<>();

        // Search elements to remove
        for (Entry<String, MediaIndexElement> indexEntry : elements.entrySet()) {
            elValue = indexEntry.getValue();
            // Check parent id
            if (elValue.getParentId().equals(uuid) || toRemove.contains(elValue.getParentId())) {
                toRemove.add(indexEntry.getKey());
                LOGGER.debug("Remove child entry {} from media index", elValue.toString());
            }
        }

        // Remove elements
        toRemove.forEach(elements::remove);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void clean() {
        String elId;
        MediaIndexElement elValue;

        // Search elements to remove
        Collection<String> toRemove = new ArrayList<>();
        for (Entry<String, MediaIndexElement> indexEntry : elements.entrySet()) {
            elId = indexEntry.getKey();
            elValue = indexEntry.getValue();
            if (!elValue.isLocked()) {
                // Check parent id is still in index
                if (elements.get(elValue.getParentId()) == null || toRemove.contains(elValue.getParentId())) {
                    toRemove.add(elId);
                    LOGGER.debug("Remove entry {} from media index (invalid parent id)", elValue.toString());
                }
                // Check element is still on file system
                if (!toRemove.contains(elId) && elValue.isLocalPath() && !new File(elValue.getPath()).exists()) {
                    toRemove.add(elId);
                    LOGGER.debug("Remove entry {} from media index (path does not exist)", elValue.toString());
                }
            }
        }

        // Remove elements
        toRemove.forEach(elements::remove);
    }

    /**
     * Configuration has changed, update media index.
     *
     * @param configurationEvent configuration event
     */
    @Subscribe
    public void handleConfigEvent(final ConfigurationEvent configurationEvent) {
        ConfigurationNode configNode = configurationEvent.getNode();
        RootNode rootNode = configurationEvent.getRootNode();
        switch (configurationEvent.getType()) {
            case ADD_FOLDER:
                // Add node to mediaIndex
                put(configNode.getId(), buildConfigMediaIndexElement(rootNode, configNode));
                break;
            case UPDATE_FOLDER:
                // Remove node and child nodes from mediaIndex and add node to mediaIndex
                remove(configNode.getId(), rootNode, configNode);
                put(configNode.getId(), buildConfigMediaIndexElement(rootNode, configNode));
                break;
            case DELETE_FOLDER:
                // Remove node and child nodes from mediaIndex
                remove(configNode.getId(), rootNode, configNode);
                break;
            default:
                break;
        }
    }

    /**
     * Remove media index element.
     *
     * @param uuid       element uuid
     * @param rootNode   root node
     * @param configNode configuration node
     */
    private void remove(final String uuid, final RootNode rootNode, final ConfigurationNode configNode) {
        if (elements.get(uuid) != null) {
            elements.remove(uuid);
        }
        // Remove children for non Podcast elements
        if (rootNode != PODCAST) {
            removeChildren(configNode.getId());
        }
    }
}
