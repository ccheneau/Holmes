/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

import net.holmes.core.media.model.AbstractNode;

import java.util.Collection;
import java.util.List;

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
    AbstractNode getNode(String nodeId);

    /**
     * Get child nodes.
     *
     * @param request child node request
     * @return child node result
     */
    ChildNodeResult getChildNodes(ChildNodeRequest request);

    /**
     * Child node request.
     */
    final class ChildNodeRequest {

        private final AbstractNode parentNode;
        private final List<String> availableMimeTypes;

        /**
         * Instantiates a new child node request.
         *
         * @param parentNode parent node
         */
        public ChildNodeRequest(final AbstractNode parentNode) {
            this(parentNode, null);
        }

        /**
         * Instantiates a new child node request.
         *
         * @param parentNode         parent node
         * @param availableMimeTypes available mime types.
         */
        public ChildNodeRequest(final AbstractNode parentNode, final List<String> availableMimeTypes) {
            this.parentNode = parentNode;
            this.availableMimeTypes = availableMimeTypes;
        }

        /**
         * Get parent node.
         *
         * @return parent node
         */
        public AbstractNode getParentNode() {
            return parentNode;
        }

        /**
         * Get available mime types.
         *
         * @return available mime types
         */
        public List<String> getAvailableMimeTypes() {
            return availableMimeTypes;
        }
    }

    /**
     * Child node result
     */
    final class ChildNodeResult {
        private final Collection<AbstractNode> childNodes;
        private final int totalCount;

        /**
         * Instantiates a new child node result.
         *
         * @param childNodes child nodes
         * @param totalCount total count
         */
        public ChildNodeResult(Collection<AbstractNode> childNodes, int totalCount) {
            this.childNodes = childNodes;
            this.totalCount = totalCount;
        }

        /**
         * Get child nodes.
         *
         * @return child nodes
         */
        public Collection<AbstractNode> getChildNodes() {
            return childNodes;
        }

        /**
         * Get total count.
         *
         * @return total count
         */
        public int getTotalCount() {
            return totalCount;
        }
    }
}
