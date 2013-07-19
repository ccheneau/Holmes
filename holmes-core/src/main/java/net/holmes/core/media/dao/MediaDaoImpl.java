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

package net.holmes.core.media.dao;

import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.sun.syndication.io.FeedException;
import net.holmes.core.common.MediaType;
import net.holmes.core.common.NodeFile;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.ConfigurationNode;
import net.holmes.core.common.configuration.Parameter;
import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.common.mimetype.MimeTypeManager;
import net.holmes.core.media.index.MediaIndexElement;
import net.holmes.core.media.index.MediaIndexElementFactory;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static net.holmes.core.media.model.RootNode.PODCAST;

/**
 * MediaManager node factory.
 */
public class MediaDaoImpl implements MediaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaDaoImpl.class);
    private final Configuration configuration;
    private final MimeTypeManager mimeTypeManager;
    private final MediaIndexManager mediaIndexManager;
    private final Cache<String, String> imageCache;
    private final Cache<String, List<AbstractNode>> podcastCache;

    /**
     * Instantiates a new media DAO implementation.
     *
     * @param configuration     configuration
     * @param mimeTypeManager   mime type manager
     * @param mediaIndexManager media index manager
     * @param podcastCache      podcast cache
     * @param imageCache        image cache
     */
    @Inject
    public MediaDaoImpl(final Configuration configuration, final MimeTypeManager mimeTypeManager, final MediaIndexManager mediaIndexManager,
                        @Named("podcastCache") final Cache<String, List<AbstractNode>> podcastCache,
                        @Named("imageCache") final Cache<String, String> imageCache) {
        this.configuration = configuration;
        this.mimeTypeManager = mimeTypeManager;
        this.mediaIndexManager = mediaIndexManager;
        this.podcastCache = podcastCache;
        this.imageCache = imageCache;
    }

    @Override
    public AbstractNode getNode(String nodeId) {
        AbstractNode node = null;
        // Get node in mediaIndex
        MediaIndexElement indexElement = mediaIndexManager.get(nodeId);
        if (indexElement != null) {
            MediaType mediaType = MediaType.getByValue(indexElement.getMediaType());
            if (mediaType == MediaType.TYPE_PODCAST)
                // Podcast node
                node = new PodcastNode(nodeId, PODCAST.getId(), indexElement.getName(), indexElement.getPath());
            else
                // File node
                node = getFileNode(nodeId, indexElement, mediaType);
        } else LOGGER.warn("{} not found in media index", nodeId);

        return node;
    }

    @Override
    public List<AbstractNode> getChildNodes(String parentNodeId) {
        List<AbstractNode> childNodes = null;
        if (parentNodeId != null) {
            // Get node in mediaIndex
            MediaIndexElement indexElement = mediaIndexManager.get(parentNodeId);
            if (indexElement != null) {
                MediaType mediaType = MediaType.getByValue(indexElement.getMediaType());
                if (mediaType == MediaType.TYPE_PODCAST) {
                    // Get podcast entries
                    try {
                        childNodes = getPodcastEntries(parentNodeId, indexElement.getPath());
                    } catch (ExecutionException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                } else {
                    // Get folder child nodes
                    NodeFile node = new NodeFile(indexElement.getPath());
                    if (node.isValidDirectory())
                        childNodes = getFolderChildNodes(parentNodeId, node, mediaType);
                }
            } else LOGGER.error("{} node not found in index", parentNodeId);
        }
        return childNodes;
    }

    @Override
    public List<AbstractNode> getConfigurationChildNodes(final RootNode rootNode) {
        List<AbstractNode> nodes = Lists.newArrayList();
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        if (configNodes != null && !configNodes.isEmpty()) {
            if (rootNode == PODCAST)
                // Add podcast nodes
                for (ConfigurationNode configNode : configNodes) {
                    // Add node to mediaIndex
                    mediaIndexManager.put(configNode.getId(), MediaIndexElementFactory.buildMediaIndexElement(rootNode, configNode));
                    // Add child node
                    nodes.add(new PodcastNode(configNode.getId(), rootNode.getId(), configNode.getLabel(), configNode.getPath()));
                }
            else
                // Add folder nodes
                for (ConfigurationNode configNode : configNodes) {
                    NodeFile file = new NodeFile(configNode.getPath());
                    if (file.isValidDirectory()) {
                        // Add node to mediaIndex
                        mediaIndexManager.put(configNode.getId(), MediaIndexElementFactory.buildMediaIndexElement(rootNode, configNode));
                        // Add child node
                        nodes.add(new FolderNode(configNode.getId(), rootNode.getId(), configNode.getLabel(), file));
                    }
                }
        }
        return nodes;
    }

    /**
     * Get file or folder node
     *
     * @param nodeId       node id
     * @param indexElement index element
     * @param mediaType    media type
     * @return file or folder node
     */
    private AbstractNode getFileNode(String nodeId, MediaIndexElement indexElement, MediaType mediaType) {
        AbstractNode node = null;
        NodeFile nodeFile = new NodeFile(indexElement.getPath());
        if (nodeFile.isValidFile())
            // Content node
            node = buildContentNode(nodeId, indexElement.getParentId(), nodeFile, mediaType, mimeTypeManager.getMimeType(nodeFile.getName()));
        else if (nodeFile.isValidDirectory()) {
            // Folder node
            String nodeName = indexElement.getName() != null ? indexElement.getName() : nodeFile.getName();
            node = new FolderNode(nodeId, indexElement.getParentId(), nodeName, nodeFile);
        }
        return node;
    }

    /**
     * Get children of a folder node.
     *
     * @param parentId  parent node id
     * @param folder    folder
     * @param mediaType media type
     * @return folder child nodes matching media type
     */
    private List<AbstractNode> getFolderChildNodes(final String parentId, final NodeFile folder, final MediaType mediaType) {
        List<AbstractNode> nodes = Lists.newArrayList();
        for (File file : folder.listValidFiles(true, true)) {
            // Add node to mediaIndex
            String nodeId = mediaIndexManager.add(new MediaIndexElement(parentId, mediaType.getValue(), file.getAbsolutePath(), null, true));
            if (file.isDirectory())
                // Add folder node
                nodes.add(new FolderNode(nodeId, parentId, file.getName(), file));
            else {
                // Add file node
                AbstractNode node = buildContentNode(nodeId, parentId, file, mediaType, mimeTypeManager.getMimeType(file.getName()));
                if (node != null)
                    nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * Gets pod-cast entries. A pod-cast is a RSS.
     *
     * @param podcastId  podcast id
     * @param podcastUrl podcast url
     * @return entries parsed from pod-cast RSS feed
     */
    @SuppressWarnings("unchecked")
    private List<AbstractNode> getPodcastEntries(final String podcastId, final String podcastUrl) throws ExecutionException {
        return podcastCache.get(podcastUrl, new Callable<List<AbstractNode>>() {
            @Override
            public List<AbstractNode> call() throws IOException, FeedException {
                // No entries in cache, read them from RSS feed
                final List<AbstractNode> podcastEntryNodes = Lists.newArrayList();
                new PodcastParser() {
                    @Override
                    public void newPodcastEntryNode(PodcastEntryNode podcastEntryNode) {
                        // Add podcast entry node
                        podcastEntryNodes.add(podcastEntryNode);
                    }
                }.parse(podcastUrl, podcastId);
                return podcastEntryNodes;
            }
        });
    }

    /**
     * Gets the content resolution. Only available for image.
     *
     * @param fileName the file name
     * @param mimeType the mime type
     * @return the content resolution
     */
    private String getContentResolution(final String fileName, final MimeType mimeType) {
        if (configuration.getParameter(Parameter.ENABLE_CONTENT_RESOLUTION) && mimeType.getType() == MediaType.TYPE_IMAGE)
            try {
                return imageCache.get(fileName, new Callable<String>() {
                    @Override
                    public String call() throws IOException {
                        BufferedImage bufferedImage = ImageIO.read(new File(fileName).toURI().toURL());
                        return String.format("%dx%d", bufferedImage.getWidth(), bufferedImage.getHeight());
                    }
                });
            } catch (ExecutionException | UncheckedExecutionException | ExecutionError e) {
                LOGGER.error(e.getMessage(), e);
            }
        return "0x0";
    }

    /**
     * Build content node.
     *
     * @param nodeId    node id
     * @param parentId  parent id
     * @param file      file
     * @param mediaType media type
     * @return content node
     */
    private ContentNode buildContentNode(final String nodeId, final String parentId, final File file, final MediaType mediaType, final MimeType mimeType) {
        if (mimeType != null) {
            // Check mime type
            if (mimeType.getType() == mediaType)
                // build content node
                return new ContentNode(nodeId, parentId, file.getName(), file, mimeType, getContentResolution(file.getAbsolutePath(), mimeType));
            else if (configuration.getParameter(Parameter.ENABLE_EXTERNAL_SUBTITLES) && mimeType.isSubTitle())
                // build subtitle node
                return new ContentNode(nodeId, parentId, file.getName(), file, mimeType, null);
        }
        return null;
    }
}
