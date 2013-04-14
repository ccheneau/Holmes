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
package net.holmes.core.media;

import java.util.List;

import net.holmes.common.media.AbstractNode;

/**
 * Media manager.
 */
public interface MediaManager {
    /**
     * Get node.
     * 
     * @param nodeId
     *      node id
     * @return node
     */
    AbstractNode getNode(String nodeId);

    /**
     * Get child nodes.
     * 
     * @param parentNode
     *      parent node
     * @return child nodes
     */
    List<AbstractNode> getChildNodes(AbstractNode parentNode);

    /**
     * Perform a full scan.
     */
    void scanAll();

    /**
     * Scan a specific node.
     * 
     * @param parentNode
     *      parent node to scan
     * @param recursive
     *      whether to scan child nodes
     */
    void scanNode(AbstractNode parentNode, boolean recursive);
}
