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

package net.holmes.core.business.media;

import net.holmes.core.business.media.model.AbstractNode;

import java.util.Collection;
import java.util.Optional;

/**
 * Media manager.
 */
public interface MediaManager {
    /**
     * Get node.
     *
     * @param nodeId node id
     * @return node
     */
    Optional<AbstractNode> getNode(String nodeId);

    /**
     * Get node URL.
     *
     * @param node node
     * @return node URL
     */
    String getNodeUrl(AbstractNode node);

    /**
     * Search child nodes.
     *
     * @param request media search request
     * @return media search result
     */
    Collection<AbstractNode> searchChildNodes(MediaSearchRequest request);

    /**
     * Clean up cache
     */
    void cleanUpCache();
}
