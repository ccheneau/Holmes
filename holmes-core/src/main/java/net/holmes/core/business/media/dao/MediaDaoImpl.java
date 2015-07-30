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

package net.holmes.core.business.media.dao;

import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.configuration.model.ConfigurationNode;
import net.holmes.core.business.media.dao.index.MediaIndexDao;
import net.holmes.core.business.media.dao.index.MediaIndexElement;
import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.media.model.FolderNode;
import net.holmes.core.business.media.model.MediaNode;
import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.common.MediaType;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.holmes.core.business.media.dao.index.MediaIndexElementFactory.buildConfigMediaIndexElement;
import static net.holmes.core.common.FileUtils.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Media dao implementation.
 */
@Singleton
public class MediaDaoImpl implements MediaDao {
    private static final Logger LOGGER = getLogger(MediaDaoImpl.class);

    private final ConfigurationManager configurationManager;
    private final MimeTypeManager mimeTypeManager;
    private final MediaIndexDao mediaIndexDao;

    /**
     * Instantiates a new media dao implementation.
     *
     * @param configurationManager configuration dao
     * @param mimeTypeManager      mime type manager
     * @param mediaIndexDao        media index dao
     */
    @Inject
    public MediaDaoImpl(final ConfigurationManager configurationManager, final MimeTypeManager mimeTypeManager, final MediaIndexDao mediaIndexDao) {
        this.configurationManager = configurationManager;
        this.mimeTypeManager = mimeTypeManager;
        this.mediaIndexDao = mediaIndexDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MediaNode> getNode(final String nodeId) {
        Optional<MediaNode> node;
        // Get node in mediaIndex
        MediaIndexElement indexElement = mediaIndexDao.get(nodeId);
        if (indexElement != null) {
            MediaType mediaType = MediaType.getByValue(indexElement.getMediaType());
            // File node
            node = getFileNode(nodeId, indexElement, mediaType);
        } else {
            LOGGER.warn("[getNode] {} not found in media index", nodeId);
            node = Optional.empty();
        }
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MediaNode> getChildNodes(final String parentNodeId) {
        List<MediaNode> childNodes;

        // Get node in mediaIndex
        MediaIndexElement indexElement = mediaIndexDao.get(parentNodeId);
        if (indexElement != null) {
            // Get media type
            MediaType mediaType = MediaType.getByValue(indexElement.getMediaType());
            // Get folder child nodes
            childNodes = getFolderChildNodes(parentNodeId, indexElement.getPath(), mediaType);
        } else {
            childNodes = new ArrayList<>(0);
            LOGGER.error("[getChildNodes] {} node not found in media index", parentNodeId);
        }

        return childNodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MediaNode> getRootNodeChildren(final RootNode rootNode) {
        // Add nodes defined in configuration
        List<ConfigurationNode> configNodes = configurationManager.getNodes(rootNode);
        List<MediaNode> nodes = new ArrayList<>(configNodes.size());
        for (ConfigurationNode configNode : configNodes) {
            // Add node to mediaIndex
            mediaIndexDao.put(configNode.getId(), buildConfigMediaIndexElement(rootNode, configNode));
            nodes.add(new FolderNode(configNode.getId(), rootNode.getId(), configNode.getLabel(), new File(configNode.getPath())));
        }
        return nodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanUpCache() {
        mediaIndexDao.clean();
    }

    /**
     * Get file or folder node
     *
     * @param nodeId       node id
     * @param indexElement index element
     * @param mediaType    media type
     * @return file or folder node
     */
    private Optional<MediaNode> getFileNode(final String nodeId, final MediaIndexElement indexElement, final MediaType mediaType) {
        File nodeFile = new File(indexElement.getPath());
        if (isValidFile(nodeFile)) {
            // Content node
            MimeType mimeType = mimeTypeManager.getMimeType(nodeFile.getName());
            if (mimeType != null) {
                return buildContentNode(nodeId, indexElement.getParentId(), nodeFile, mediaType, mimeType);
            }
        } else if (isValidDirectory(nodeFile)) {
            // Folder node
            String nodeName = indexElement.getName() != null ? indexElement.getName() : nodeFile.getName();
            return Optional.of(new FolderNode(nodeId, indexElement.getParentId(), nodeName, nodeFile));
        }
        return Optional.empty();
    }

    /**
     * Get children of a folder node.
     *
     * @param folderNodeId folder node id
     * @param folderPath   folder path
     * @param mediaType    media type
     * @return folder child nodes matching media type
     */
    private List<MediaNode> getFolderChildNodes(final String folderNodeId, final String folderPath, final MediaType mediaType) {
        List<File> children = listChildren(folderPath, true);
        List<MediaNode> nodes = new ArrayList<>(children.size());
        for (File child : children) {
            // Add node to mediaIndex
            if (child.isDirectory()) {
                // Add folder node
                String nodeId = mediaIndexDao.add(new MediaIndexElement(folderNodeId, mediaType.getValue(), null, child.getAbsolutePath(), null, true, false));
                nodes.add(new FolderNode(nodeId, folderNodeId, child.getName(), child));
            } else {
                // Add content node
                addContentNode(nodes, folderNodeId, child, mediaType);
            }
        }
        return nodes;
    }

    /**
     * Add content node to node list.
     *
     * @param nodes     node list
     * @param parentId  parent id
     * @param file      file
     * @param mediaType media type
     */
    private void addContentNode(final List<MediaNode> nodes, final String parentId, final File file, final MediaType mediaType) {
        MimeType mimeType = mimeTypeManager.getMimeType(file.getName());
        if (mimeType != null) {
            // Add file node
            String nodeId = mediaIndexDao.add(new MediaIndexElement(parentId, mediaType.getValue(), mimeType.getMimeType(), file.getAbsolutePath(), null, true, false));
            buildContentNode(nodeId, parentId, file, mediaType, mimeType).ifPresent(nodes::add);
        }
    }

    /**
     * Build content node.
     *
     * @param nodeId    node id
     * @param parentId  parent id
     * @param file      file
     * @param mediaType media type
     * @return optional content node
     */
    private Optional<MediaNode> buildContentNode(final String nodeId, final String parentId, final File file, final MediaType mediaType, final MimeType mimeType) {
        // Check mime type
        return Optional.ofNullable(mimeType.getType() == mediaType || mimeType.isSubTitle() ? new ContentNode(nodeId, parentId, file.getName(), file, mimeType) : null);
    }
}
