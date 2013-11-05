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
import com.sun.syndication.io.FeedException;
import net.holmes.core.common.MediaType;
import net.holmes.core.common.NodeFile;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.ConfigurationNode;
import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.common.mimetype.MimeTypeManager;
import net.holmes.core.media.dao.icecast.IcecastDao;
import net.holmes.core.media.dao.icecast.IcecastEntry;
import net.holmes.core.media.index.MediaIndexElement;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static net.holmes.core.common.UniqueId.newUniqueId;
import static net.holmes.core.media.index.MediaIndexElementFactory.buildMediaIndexElement;
import static net.holmes.core.media.model.AbstractNode.NodeType.TYPE_ICECAST_ENTRY;
import static net.holmes.core.media.model.RootNode.ICECAST;
import static net.holmes.core.media.model.RootNode.PODCAST;

/**
 * MediaManager node factory.
 */
public class MediaDaoImpl implements MediaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaDaoImpl.class);
    private final Configuration configuration;
    private final MimeTypeManager mimeTypeManager;
    private final MediaIndexManager mediaIndexManager;
    private final IcecastDao icecastDao;
    private final Cache<String, List<AbstractNode>> podcastCache;

    /**
     * Instantiates a new media DAO implementation.
     *
     * @param configuration     configuration
     * @param mimeTypeManager   mime type manager
     * @param mediaIndexManager media index manager
     * @param icecastDao        Icecast dao
     * @param podcastCache      podcast cache
     */
    @Inject
    public MediaDaoImpl(final Configuration configuration, final MimeTypeManager mimeTypeManager, final MediaIndexManager mediaIndexManager,
                        final IcecastDao icecastDao,
                        @Named("podcastCache") final Cache<String, List<AbstractNode>> podcastCache) {
        this.configuration = configuration;
        this.mimeTypeManager = mimeTypeManager;
        this.mediaIndexManager = mediaIndexManager;
        this.icecastDao = icecastDao;
        this.podcastCache = podcastCache;
    }

    @Override
    public AbstractNode getNode(String nodeId) {
        AbstractNode node = null;
        // Get node in mediaIndex
        MediaIndexElement indexElement = mediaIndexManager.get(nodeId);
        if (indexElement != null) {
            MediaType mediaType = MediaType.getByValue(indexElement.getMediaType());
            switch (mediaType) {
                case TYPE_PODCAST:
                    // Podcast node
                    node = new PodcastNode(nodeId, PODCAST.getId(), indexElement.getName(), indexElement.getPath());
                    break;
                case TYPE_ICECAST_GENRE:
                    // Icecast genre node
                    node = new IcecastGenreNode(nodeId, ICECAST.getId(), indexElement.getName(), indexElement.getPath());
                    break;
                default:
                    // File node
                    node = getFileNode(nodeId, indexElement, mediaType);
                    break;
            }
        } else LOGGER.warn("{} not found in media index", nodeId);
        return node;
    }

    @Override
    public List<AbstractNode> getChildNodes(String parentNodeId) {
        List<AbstractNode> childNodes = Lists.newArrayList();
        // Get node in mediaIndex
        MediaIndexElement indexElement = mediaIndexManager.get(parentNodeId);
        if (indexElement != null) {
            MediaType mediaType = MediaType.getByValue(indexElement.getMediaType());
            switch (mediaType) {
                case TYPE_PODCAST:
                    // Get podcast entries
                    try {
                        childNodes.addAll(getPodcastEntries(parentNodeId, indexElement.getPath()));
                    } catch (ExecutionException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                    break;
                case TYPE_ICECAST_GENRE:
                    // Get Icecast entries
                    childNodes.addAll(getIcecastEntries(parentNodeId, indexElement.getPath()));
                    break;
                default:
                    // Get folder child nodes
                    NodeFile node = new NodeFile(indexElement.getPath());
                    if (node.isValidDirectory())
                        childNodes.addAll(getFolderChildNodes(parentNodeId, node, mediaType));
                    break;
            }
        } else LOGGER.error("{} node not found in index", parentNodeId);

        return childNodes;
    }


    @Override
    public List<AbstractNode> getSubRootChildNodes(final RootNode rootNode) {
        List<AbstractNode> nodes = Lists.newArrayList();
        switch (rootNode) {
            case PODCAST:
                // Add podcast nodes stored in configuration
                for (ConfigurationNode configNode : configuration.getFolders(rootNode)) {
                    // Add node to mediaIndex
                    mediaIndexManager.put(configNode.getId(), buildMediaIndexElement(rootNode, configNode));
                    // Add child node
                    nodes.add(new PodcastNode(configNode.getId(), rootNode.getId(), configNode.getLabel(), configNode.getPath()));
                }
                break;
            case ICECAST:
                if (icecastDao.isLoaded()) {
                    // Add Icecast genre from Icecast dao
                    for (String genre : icecastDao.getGenres()) {
                        String id = "icecast_genre_" + genre;
                        String genreName = Character.toUpperCase(genre.charAt(0)) + genre.substring(1);
                        // Add node to media index
                        mediaIndexManager.put(id, buildMediaIndexElement(rootNode, genre, genreName));
                        // Add child node
                        nodes.add(new IcecastGenreNode(id, rootNode.getId(), genreName, genre));
                    }
                }
                break;
            default:
                // Add folder nodes stored in configuration
                for (ConfigurationNode configNode : configuration.getFolders(rootNode)) {
                    NodeFile file = new NodeFile(configNode.getPath());
                    if (file.isValidDirectory()) {
                        // Add node to mediaIndex
                        mediaIndexManager.put(configNode.getId(), buildMediaIndexElement(rootNode, configNode));
                        // Add child node
                        nodes.add(new FolderNode(configNode.getId(), rootNode.getId(), configNode.getLabel(), file));
                    }
                }
                break;
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
        for (File file : folder.listChildFiles(true)) {
            // Add node to mediaIndex
            String nodeId = mediaIndexManager.add(new MediaIndexElement(parentId, mediaType.getValue(), file.getAbsolutePath(), null, true));
            if (file.isDirectory())
                // Add folder node
                nodes.add(new FolderNode(nodeId, parentId, file.getName(), file));
            else {
                // Add file node
                AbstractNode node = buildContentNode(nodeId, parentId, file, mediaType, mimeTypeManager.getMimeType(file.getName()));
                if (node != null) nodes.add(node);
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
                    public void addPodcastEntryNode(RawUrlNode podcastEntryNode) {
                        // Add podcast entry node
                        podcastEntryNodes.add(podcastEntryNode);
                    }
                }.parse(podcastUrl, podcastId);
                return podcastEntryNodes;
            }
        });
    }

    /**
     * Gets Icecast entries by genre.
     *
     * @param genre genre
     * @return Icecast entries
     */
    private Collection<AbstractNode> getIcecastEntries(final String parentNodeId, final String genre) {
        Collection<AbstractNode> result = Lists.newArrayList();
        for (IcecastEntry entry : icecastDao.getEntriesByGenre(genre))
            result.add(new RawUrlNode(TYPE_ICECAST_ENTRY, newUniqueId(), parentNodeId, entry.getName(), new MimeType(entry.getType()), entry.getUrl(), null));

        return result;
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
        // Check mime type
        if (mimeType != null)
            if (mimeType.getType() == mediaType)
                // build content node
                return new ContentNode(nodeId, parentId, file.getName(), file, mimeType);
            else if (mimeType.isSubTitle())
                // build subtitle node
                return new ContentNode(nodeId, parentId, file.getName(), file, mimeType);
        return null;
    }
}
