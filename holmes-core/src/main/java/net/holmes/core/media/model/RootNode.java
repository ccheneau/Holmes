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

package net.holmes.core.media.model;

import net.holmes.core.common.MediaType;

import static net.holmes.core.common.MediaType.*;

/**
 * Root node.
 */
public enum RootNode {
    ROOT("0", "-1", false, null), //
    VIDEO("1_VIDEOS", ROOT.getId(), true, TYPE_VIDEO), //
    PICTURE("2_PICTURES", ROOT.getId(), true, TYPE_IMAGE), //
    AUDIO("3_AUDIOS", ROOT.getId(), true, TYPE_AUDIO), //
    PODCAST("4_PODCASTS", ROOT.getId(), false, TYPE_PODCAST), //
    ICECAST("5_ICECAST", ROOT.getId(), false, TYPE_ICECAST_GENRE), //
    NONE("ROOT_NODE_NONE", "", false, TYPE_NONE);
    private final String id;
    private final String parentId;
    private final MediaType mediaType;
    private final boolean localPath;

    /**
     * Instantiates a new root node.
     *
     * @param id        node id
     * @param parentId  parent node id
     * @param localPath whether to look for child elements in local path. False for Podcast and Icecast entries
     * @param mediaType media type
     */
    private RootNode(final String id, final String parentId, final boolean localPath, final MediaType mediaType) {
        this.id = id;
        this.parentId = parentId;
        this.localPath = localPath;
        this.mediaType = mediaType;
    }

    /**
     * Gets root node by id.
     *
     * @param id node id
     * @return the root node or RootNode.NONE
     */
    public static RootNode getById(final String id) {
        for (RootNode rootNode : RootNode.values())
            if (rootNode.id.equals(id)) return rootNode;

        return NONE;
    }

    /**
     * Gets the root node id.
     *
     * @return the root node id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the root node parent id.
     *
     * @return the root node parent id
     */
    public String getParentId() {
        return this.parentId;
    }

    /**
     * Gets the root node media type.
     *
     * @return the root node media type
     */
    public MediaType getMediaType() {
        return this.mediaType;
    }

    /**
     * Whether child elements are stored in local file system.
     *
     * @return true if child elements are stored on file system
     */
    public boolean isLocalPath() {
        return this.localPath;
    }

    /**
     * Gets the bundle key.
     *
     * @return the bundle key
     */
    public String getBundleKey() {
        return "rootNode." + id;
    }
}
