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

package net.holmes.core.media;

import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.ITunes;
import com.sun.syndication.feed.module.mediarss.MediaEntryModule;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import net.holmes.common.MediaType;
import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.ConfigurationNode;
import net.holmes.common.configuration.Parameter;
import net.holmes.common.event.ConfigurationEvent;
import net.holmes.common.event.MediaEvent;
import net.holmes.common.media.*;
import net.holmes.common.mimetype.MimeType;
import net.holmes.common.mimetype.MimeTypeManager;
import net.holmes.core.inject.InjectLogger;
import net.holmes.core.media.index.MediaIndexElement;
import net.holmes.core.media.index.MediaIndexElementFactory;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.playlist.M3uParser;
import net.holmes.core.media.playlist.PlaylistItem;
import net.holmes.core.media.playlist.PlaylistParserException;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static net.holmes.common.media.RootNode.*;

/**
 * Media manager implementation.
 */
public final class MediaManagerImpl implements MediaManager {

    private final Configuration configuration;
    private final MimeTypeManager mimeTypeManager;
    private final ResourceBundle resourceBundle;
    private final MediaIndexManager mediaIndexManager;
    private final Cache<String, List<AbstractNode>> podcastCache;
    private final Cache<File, String> imageCache;
    @InjectLogger
    private Logger logger;

    /**
     * Instantiates a new media manager implementation.
     *
     * @param configuration     configuration
     * @param mimeTypeManager   mime type manager
     * @param resourceBundle    resource bundle
     * @param mediaIndexManager media index manager
     * @param podcastCache      podcast cache
     */
    @Inject
    public MediaManagerImpl(final Configuration configuration, final MimeTypeManager mimeTypeManager, final ResourceBundle resourceBundle,
                            final MediaIndexManager mediaIndexManager, @Named("podcastCache") final Cache<String, List<AbstractNode>> podcastCache,
                            @Named("imageCache") final Cache<File, String> imageCache) {
        this.configuration = configuration;
        this.mimeTypeManager = mimeTypeManager;
        this.resourceBundle = resourceBundle;
        this.mediaIndexManager = mediaIndexManager;
        this.podcastCache = podcastCache;
        this.imageCache = imageCache;
    }

    @Override
    public AbstractNode getNode(final String nodeId) {
        AbstractNode node = null;
        if (logger.isDebugEnabled()) logger.debug("[START] getNode nodeId:{}", nodeId);

        RootNode rootNode = RootNode.getById(nodeId);
        if (rootNode != null) {
            // Root node
            node = new FolderNode(rootNode.getId(), rootNode.getParentId(), resourceBundle.getString("rootNode." + rootNode.getId()));
        } else if (nodeId != null) {
            // Get node in mediaIndex
            MediaIndexElement indexElement = mediaIndexManager.get(nodeId);
            if (indexElement != null) {
                MediaType mediaType = MediaType.getByValue(indexElement.getMediaType());
                switch (mediaType) {
                    case TYPE_PODCAST:
                        // Pod-cast node
                        node = new PodcastNode(nodeId, PODCAST.getId(), indexElement.getName(), indexElement.getPath());
                        break;
                    case TYPE_PLAYLIST:
                        // Playlist node
                        node = new PlaylistNode(nodeId, indexElement.getParentId(), indexElement.getName(), indexElement.getPath());
                        break;
                    default:
                        // File or folder node
                        File nodeFile = new File(indexElement.getPath());
                        if (nodeFile.exists() && nodeFile.canRead() && !nodeFile.isHidden()) {
                            if (nodeFile.isFile()) {
                                // Content node
                                node = buildFileNode(nodeId, indexElement.getParentId(), nodeFile, mediaType);
                            } else if (nodeFile.isDirectory()) {
                                // Folder node
                                String nodeName = indexElement.getName() != null ? indexElement.getName() : nodeFile.getName();
                                node = new FolderNode(nodeId, indexElement.getParentId(), nodeName, nodeFile);
                            }
                        }

                }
            } else if (logger.isWarnEnabled()) logger.warn("{} not found in media index", nodeId);
        }
        if (logger.isDebugEnabled()) logger.debug("[END] getNode node:{}", node);
        return node;
    }

    @Override
    public List<AbstractNode> getChildNodes(final AbstractNode parentNode) {
        if (logger.isDebugEnabled()) logger.debug("[START] getChildNodes nodeId:{}", parentNode.getId());

        List<AbstractNode> childNodes = null;
        RootNode rootNode = RootNode.getById(parentNode.getId());
        if (rootNode != null) {
            switch (rootNode) {
                case ROOT:
                    // Child nodes of root are audio, video, picture and pod-cast root nodes
                    childNodes = Lists.newArrayList();
                    boolean hideEmpty = configuration.getParameter(Parameter.HIDE_EMPTY_ROOT_NODES);
                    addRootNode(childNodes, hideEmpty, AUDIO);
                    addRootNode(childNodes, hideEmpty, VIDEO);
                    addRootNode(childNodes, hideEmpty, PICTURE);
                    addRootNode(childNodes, hideEmpty, PODCAST);
                    break;
                case AUDIO:
                case VIDEO:
                case PICTURE:
                case PODCAST:
                    // Child nodes are stored in configuration
                    childNodes = getConfigurationChildNodes(rootNode);
                    break;
                default:
                    break;
            }
        } else if (parentNode.getId() != null) {
            // Get node in mediaIndex
            MediaIndexElement indexElement = mediaIndexManager.get(parentNode.getId());
            if (indexElement != null) {
                MediaType mediaType = MediaType.getByValue(indexElement.getMediaType());
                switch (mediaType) {
                    case TYPE_PODCAST:
                        // Get pod-cast entries
                        childNodes = getPodcastEntries(parentNode.getId(), indexElement.getPath());
                        break;
                    case TYPE_PLAYLIST:
                        // Get playlist entries
                        childNodes = getPlaylistEntries(parentNode.getId(), indexElement.getPath());
                        break;
                    default:
                        // Get folder child nodes
                        File node = new File(indexElement.getPath());
                        if (node.exists() && node.isDirectory() && node.canRead() && !node.isHidden()) {
                            childNodes = getFolderChildNodes(parentNode.getId(), node, mediaType);
                        }
                }
            } else {
                logger.error("{} node not found in index", parentNode.getId());
            }
        }
        if (logger.isDebugEnabled()) logger.debug("[END] getChildNodes :{}", childNodes);
        return childNodes;
    }

    @Override
    public void scanAll() {
        AbstractNode rootNode = getNode(RootNode.ROOT.getId());
        scanNode(rootNode, true);
    }

    private void scanNode(final AbstractNode parentNode, final boolean recursive) {
        if (parentNode instanceof FolderNode) {
            List<AbstractNode> childNodes = getChildNodes(parentNode);
            if (recursive && childNodes != null) {
                for (AbstractNode childNode : childNodes) {
                    scanNode(childNode, recursive);
                }
            }
        }
    }

    /**
     * Adds the root node.
     *
     * @param childNodes the child nodes
     * @param hideEmpty  hide if empty
     * @param rootNode   the root node
     */
    private void addRootNode(List<AbstractNode> childNodes, final boolean hideEmpty, final RootNode rootNode) {
        if (!hideEmpty || !configuration.getFolders(rootNode).isEmpty()) {
            childNodes.add(new FolderNode(rootNode.getId(), rootNode.getParentId(), resourceBundle.getString(rootNode.getBundleKey())));
        }
    }

    /**
     * Get child nodes of a configuration node (child nodes are stored in configuration).
     *
     * @param rootNode root node from configuration
     * @return configuration child nodes
     */
    private List<AbstractNode> getConfigurationChildNodes(final RootNode rootNode) {
        List<AbstractNode> nodes = Lists.newArrayList();
        List<ConfigurationNode> configNodes = configuration.getFolders(rootNode);
        if (configNodes != null && !configNodes.isEmpty()) {
            if (rootNode == PODCAST) {
                // Add podcast nodes
                for (ConfigurationNode configNode : configNodes) {
                    // Add node to mediaIndex
                    mediaIndexManager.put(configNode.getId(), MediaIndexElementFactory.get(rootNode, configNode));
                    // Add child node
                    nodes.add(new PodcastNode(configNode.getId(), rootNode.getId(), configNode.getLabel(), configNode.getPath()));
                }
            } else {
                // Add folder nodes
                for (ConfigurationNode configNode : configNodes) {
                    File file = new File(configNode.getPath());
                    if (file.exists() && file.isDirectory() && file.canRead()) {
                        // Add node to mediaIndex
                        mediaIndexManager.put(configNode.getId(), MediaIndexElementFactory.get(rootNode, configNode));
                        // Add child node
                        nodes.add(new FolderNode(configNode.getId(), rootNode.getId(), configNode.getLabel(), file));
                    }
                }
            }
        }
        return nodes;
    }

    /**
     * Get children of a folder node.
     *
     * @param parentId  parent node id
     * @param folder    folder
     * @param mediaType media type
     * @return folder child nodes matching media type
     */
    private List<AbstractNode> getFolderChildNodes(final String parentId, final File folder, final MediaType mediaType) {
        List<AbstractNode> nodes = Lists.newArrayList();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.canRead() && !file.isHidden()) {
                    // Add node to mediaIndex
                    String nodeId = mediaIndexManager.add(new MediaIndexElement(parentId, mediaType.getValue(), file.getAbsolutePath(), null, true));
                    if (file.isDirectory()) {
                        // Add folder node
                        nodes.add(new FolderNode(nodeId, parentId, file.getName(), file));
                    } else {
                        // Add file node
                        nodes.add(buildFileNode(nodeId, parentId, file, mediaType));
                    }
                }
            }
        }
        return nodes;
    }

    /**
     * Gets pod-cast entries. A pod-cast is a RSS.
     *
     * @param podCastId pod-cast id
     * @param url       pod-cast url
     * @return entries parsed from pod-cast RSS feed
     */
    @SuppressWarnings("unchecked")
    private List<AbstractNode> getPodcastEntries(final String podCastId, final String url) {

        try {
            return podcastCache.get(url, new Callable<List<AbstractNode>>() {
                @Override
                public List<AbstractNode> call() throws IOException, FeedException {
                    // No entries in cache, read them from RSS feed
                    List<AbstractNode> podcastEntryNodes = Lists.newArrayList();
                    XmlReader reader = null;
                    try {
                        // Get RSS feed entries
                        URL feedSource = new URL(url);
                        reader = new XmlReader(feedSource);
                        List<SyndEntry> rssEntries = new SyndFeedInput().build(reader).getEntries();
                        if (rssEntries != null && !rssEntries.isEmpty()) {
                            MimeType mimeType;
                            for (SyndEntry rssEntry : rssEntries) {
                                // Add podcast entry node for each feed entry
                                if (rssEntry.getEnclosures() != null && !rssEntry.getEnclosures().isEmpty()) {
                                    String duration = null;
                                    String iconUrl = null;
                                    EntryInformation itunesInfo = (EntryInformation) (rssEntry.getModule(ITunes.URI));
                                    if (itunesInfo != null && itunesInfo.getDuration() != null) {
                                        duration = itunesInfo.getDuration().toString();
                                    }
                                    MediaEntryModule mediaInfo = (MediaEntryModule) (rssEntry.getModule(MediaModule.URI));
                                    if (mediaInfo != null && mediaInfo.getMetadata() != null && mediaInfo.getMetadata().getThumbnail() != null
                                            && mediaInfo.getMetadata().getThumbnail().length > 0) {
                                        iconUrl = mediaInfo.getMetadata().getThumbnail()[0].getUrl().toString();
                                    }
                                    for (SyndEnclosure enclosure : (List<SyndEnclosure>) rssEntry.getEnclosures()) {
                                        mimeType = enclosure.getType() != null ? new MimeType(enclosure.getType()) : null;
                                        if (mimeType != null && mimeType.isMedia()) {
                                            PodcastEntryNode podcastEntryNode = new PodcastEntryNode(UUID.randomUUID().toString(), //
                                                    podCastId, rssEntry.getTitle().trim(), mimeType, enclosure.getUrl(), duration);
                                            podcastEntryNode.setIconUrl(iconUrl);
                                            if (rssEntry.getPublishedDate() != null)
                                                podcastEntryNode.setModifiedDate(rssEntry.getPublishedDate().getTime());

                                            podcastEntryNodes.add(podcastEntryNode);
                                        }
                                    }
                                }
                            }
                        }
                    } finally {
                        // Close the reader
                        try {
                            if (reader != null) reader.close();
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    return podcastEntryNodes;
                }
            });
        } catch (ExecutionException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Get playlist entries.
     *
     * @param playlistId parent id
     * @param path       path
     * @return playlist child nodes
     */
    private List<AbstractNode> getPlaylistEntries(final String playlistId, final String path) {
        List<AbstractNode> nodes = Lists.newArrayList();

        try {
            List<PlaylistItem> items = new M3uParser(new File(path)).parse();
            if (items != null) {
                for (PlaylistItem item : items) {
                    MimeType mimeType = mimeTypeManager.getMimeType(item.getPath());
                    if (mimeType.isMedia()) {
                        String nodeId = mediaIndexManager.add(new MediaIndexElement(playlistId, mimeType.getType(), item.getPath(), item.getLabel(), true));
                        nodes.add(new ContentNode(nodeId, playlistId, item.getLabel(), new File(item.getPath()), mimeType, null));
                    }
                }
            }
        } catch (PlaylistParserException e) {
            logger.error(e.getMessage(), e);
        }

        return nodes;
    }

    /**
     * Build file node.
     *
     * @param nodeId    node id
     * @param parentId  parent id
     * @param file      file
     * @param mediaType media type
     * @return built node
     */
    private AbstractNode buildFileNode(final String nodeId, final String parentId, final File file, final MediaType mediaType) {
        AbstractNode node = null;

        // Check mime type
        MimeType mimeType = mimeTypeManager.getMimeType(file.getName());
        if (mimeType != null) {
            MediaType mimeMediaType = MediaType.getByValue(mimeType.getType());
            if (mimeMediaType == MediaType.TYPE_PLAYLIST) {
                node = new PlaylistNode(nodeId, parentId, file.getName(), file.getAbsolutePath());
            } else if (mimeMediaType.equals(mediaType)) {
                String resolution = getContentResolution(file, mimeType);
                node = new ContentNode(nodeId, parentId, file.getName(), file, mimeType, resolution);
            } else if (mimeType.isSubtitle() && configuration.getParameter(Parameter.ENABLE_EXTERNAL_SUBTITLES))
                node = new ContentNode(nodeId, parentId, file.getName(), file, mimeType, null);

        }
        return node;
    }

    /**
     * Gets the content resolution. Only available for image.
     *
     * @param file     the file
     * @param mimeType the mime type
     * @return the content resolution
     */
    private String getContentResolution(final File file, final MimeType mimeType) {
        if (mimeType.isImage()) {
            try {
                return imageCache.get(file, new Callable<String>() {
                    @Override
                    public String call() throws IOException {
                        String resolution = null;
                        BufferedImage bimg = ImageIO.read(file);
                        if (bimg != null) resolution = String.format("%dx%d", bimg.getWidth(), bimg.getHeight());
                        return resolution != null ? resolution : "0x0";
                    }
                });
            } catch (ExecutionException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Configuration has changed, update media index.
     *
     * @param configurationEvent configuration event
     */
    @Subscribe
    public void handleConfigEvent(final ConfigurationEvent configurationEvent) {
        if (logger.isDebugEnabled()) logger.debug("Configuration event received: {}", configurationEvent.toString());

        ConfigurationNode configNode = configurationEvent.getNode();
        RootNode rootNode = configurationEvent.getRootNode();
        switch (configurationEvent.getType()) {
            case ADD:
                // Add node to mediaIndex
                mediaIndexManager.put(configNode.getId(), MediaIndexElementFactory.get(rootNode, configNode));
                break;
            case UPDATE:
                // Remove node and child nodes from mediaIndex
                mediaIndexManager.remove(configNode.getId());
                if (rootNode != PODCAST) mediaIndexManager.removeChildren(configNode.getId());
                // Add node to mediaIndex
                mediaIndexManager.put(configNode.getId(), MediaIndexElementFactory.get(rootNode, configNode));
                break;
            case DELETE:
                // Remove node and child nodes from mediaIndex
                mediaIndexManager.remove(configNode.getId());
                if (rootNode != PODCAST) mediaIndexManager.removeChildren(configNode.getId());
                break;
            default:
                logger.error("Unknown event");
                break;
        }
    }

    /**
     * Handle media event.
     *
     * @param mediaEvent media event
     */
    @Subscribe
    public void handleMediaEvent(final MediaEvent mediaEvent) {
        if (logger.isDebugEnabled()) logger.debug("Media event received: {}", mediaEvent.toString());

        switch (mediaEvent.getType()) {
            case SCAN_ALL:
                scanAll();
                break;
            case SCAN_NODE:
                AbstractNode node = getNode(mediaEvent.getParameter());
                if (node != null) scanNode(node, true);
                break;
            default:
                logger.error("Unknown event");
                break;
        }
    }
}
