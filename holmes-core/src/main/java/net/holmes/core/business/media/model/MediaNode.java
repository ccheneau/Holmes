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

package net.holmes.core.business.media.model;

/**
 * Media node
 */
public interface MediaNode extends Comparable<MediaNode> {
    /**
     * Gets the node id.
     *
     * @return the node id
     */
    String getId();

    /**
     * Gets the parent node id.
     *
     * @return the parent node id
     */
    String getParentId();

    /**
     * Gets the node name.
     *
     * @return the node name
     */
    String getName();

    /**
     * Gets the node type.
     *
     * @return the node type
     */
    AbstractNode.NodeType getType();

    /**
     * Gets the node modified date.
     *
     * @return the node modified date
     */
    Long getModifiedDate();

    /**
     * Sets the node modified date.
     *
     * @param modifiedDate the new node modified date
     */
    void setModifiedDate(Long modifiedDate);

    /**
     * Gets the node icon url.
     *
     * @return the node icon url
     */
    String getIconUrl();

    /**
     * Sets the node icon url.
     *
     * @param iconUrl the new node icon url
     */
    void setIconUrl(String iconUrl);


    /**
     * Node type.
     */
    public enum NodeType {
        TYPE_FOLDER,
        TYPE_CONTENT,
        TYPE_PODCAST,
        TYPE_PODCAST_ENTRY,
        TYPE_UNKNOWN
    }
}
