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

package net.holmes.core.backend.response;

import java.util.ArrayList;
import java.util.List;


/**
 * Device browse result
 */
public class DeviceBrowseResult {
    private String parentNodeId;
    private String errorMessage;
    private final List<BrowseFolder> folders = new ArrayList<>();
    private final List<BrowseContent> contents = new ArrayList<>();

    /**
     * Get parent node id.
     *
     * @return parent node id
     */
    public String getParentNodeId() {
        return parentNodeId;
    }

    /**
     * Set parent node id.
     *
     * @param parentNodeId new parent node id
     */
    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    /**
     * Get error message.
     *
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set error message.
     *
     * @param errorMessage new error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get folders.
     *
     * @return folders
     */
    public List<BrowseFolder> getFolders() {
        return folders;
    }

    /**
     * Get contents.
     *
     * @return contents
     */
    public List<BrowseContent> getContents() {
        return contents;
    }

    /**
     * Browse folder.
     */
    public static class BrowseFolder {
        private String nodeId;
        private String folderName;

        /**
         * Get node id.
         *
         * @return node id
         */
        public String getNodeId() {
            return nodeId;
        }

        /**
         * Set node id.
         *
         * @param nodeId new node id
         */
        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        /**
         * Get folder name.
         *
         * @return folder name
         */
        public String getFolderName() {
            return folderName;
        }

        /**
         * Set folder name.
         *
         * @param folderName new folder name
         */
        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }
    }

    /**
     * Browse content.
     */
    public static class BrowseContent {
        private String nodeId;
        private String contentName;
        private String contentUrl;

        /**
         * Get node id.
         *
         * @return node id
         */
        public String getNodeId() {
            return nodeId;
        }

        /**
         * Set node id.
         *
         * @param nodeId new node id
         */
        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        /**
         * Get content name.
         *
         * @return content namme
         */
        public String getContentName() {
            return contentName;
        }

        /**
         * Set content name
         *
         * @param contentName new content name
         */
        public void setContentName(String contentName) {
            this.contentName = contentName;
        }

        /**
         * Get content URL.
         *
         * @return content URL
         */
        public String getContentUrl() {
            return contentUrl;
        }

        /**
         * Set Content URL.
         *
         * @param contentUrl new content URL
         */
        public void setContentUrl(String contentUrl) {
            this.contentUrl = contentUrl;
        }
    }
}
