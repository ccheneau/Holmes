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

import static net.holmes.common.media.RootNode.AUDIO;
import static net.holmes.common.media.RootNode.PICTURE;
import static net.holmes.common.media.RootNode.PODCAST;
import static net.holmes.common.media.RootNode.VIDEO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;

import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.ConfigurationNode;
import net.holmes.common.configuration.Parameter;
import net.holmes.common.event.ConfigurationEvent;
import net.holmes.common.event.MediaEvent;
import net.holmes.common.media.AbstractNode;
import net.holmes.common.media.ContentNode;
import net.holmes.common.media.FolderNode;
import net.holmes.common.media.MediaType;
import net.holmes.common.media.PlaylistNode;
import net.holmes.common.media.PodcastEntryNode;
import net.holmes.common.media.PodcastNode;
import net.holmes.common.media.RootNode;
import net.holmes.common.mimetype.MimeType;
import net.holmes.common.mimetype.MimeTypeManager;
import net.holmes.core.inject.Loggable;
import net.holmes.core.media.index.MediaIndexElement;
import net.holmes.core.media.index.MediaIndexElementFactory;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.playlist.M3uParser;
import net.holmes.core.media.playlist.PlaylistItem;
import net.holmes.core.media.playlist.PlaylistParserException;

import org.slf4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.ITunes;
import com.sun.syndication.feed.module.mediarss.MediaEntryModule;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@Loggable
public final class MediaManagerImpl implements MediaManager {
    private Logger logger;

    private final Configuration configuration;
    private final MimeTypeManager mimeTypeManager;
    private final ResourceBundle resourceBundle;
    private final MediaIndexManager mediaIndexManager;
    private final Cache<String, List<AbstractNode>> podcastCache;

    @Inject
    public MediaManagerImpl(Configuration configuration, MimeTypeManager mimeTypeManager, ResourceBundle resourceBundle, MediaIndexManager mediaIndexManager,
            @Named("podcastCache") Cache<String, List<AbstractNode>> podcastCache) {
        this.configuration = configuration;
        this.mimeTypeManager = mimeTypeManager;
        this.resourceBundle = resourceBundle;
        this.mediaIndexManager = mediaIndexManager;
        this.podcastCache = podcastCache;
    }

    @Override
    public AbstractNode getNode(String nodeId) {
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
                if (MediaType.TYPE_PODCAST.getValue().equals(indexElement.getMediaType())) {
                    // Podcast node
                    node = new PodcastNode(nodeId, PODCAST.getId(), indexElement.getName(), indexElement.getPath());
                } else if (MediaType.TYPE_PLAYLIST.getValue().equals(indexElement.getMediaType())) {
                    // Playlist node
                    node = new PlaylistNode(nodeId, indexElement.getParentId(), indexElement.getName(), indexElement.getPath());
                } else {
                    File nodeFile = new File(indexElement.getPath());
                    if (nodeFile.exists() && nodeFile.canRead() && !nodeFile.isHidden()) {
                        if (nodeFile.isFile()) {
                            // Content node
                            node = buildFileNode(nodeId, indexElement.getParentId(), nodeFile, indexElement.getMediaType());
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
    public List<AbstractNode> getChildNodes(AbstractNode parentNode) {
        if (logger.isDebugEnabled()) logger.debug("[START] getChildNodes nodeId:{}", parentNode.getId());

        List<AbstractNode> childNodes = null;
        RootNode rootNode = RootNode.getById(parentNode.getId());
        if (rootNode != null) {
            switch (rootNode) {
            case ROOT:
                // Child nodes of root are audio, video, picture and pod-cast root nodes
                childNodes = Lists.newArrayList();
                childNodes.add(new FolderNode(AUDIO.getId(), AUDIO.getParentId(), resourceBundle.getString("rootNode." + AUDIO.getId())));
                childNodes.add(new FolderNode(VIDEO.getId(), VIDEO.getParentId(), resourceBundle.getString("rootNode." + VIDEO.getId())));
                childNodes.add(new FolderNode(PICTURE.getId(), PICTURE.getParentId(), resourceBundle.getString("rootNode." + PICTURE.getId())));
                childNodes.add(new FolderNode(PODCAST.getId(), PODCAST.getParentId(), resourceBundle.getString("rootNode." + PODCAST.getId())));
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
                if (MediaType.TYPE_PODCAST.getValue().equals(indexElement.getMediaType())) {
                    // Get podcast child nodes
                    childNodes = getPodcastChildNodes(parentNode.getId(), indexElement.getPath());
                } else if (MediaType.TYPE_PLAYLIST.getValue().equals(indexElement.getMediaType())) {
                    // Get playlist child nodes
                    childNodes = getPlaylistChildNodes(parentNode.getId(), indexElement.getPath());
                } else {
                    // Get folder child nodes
                    File node = new File(indexElement.getPath());
                    if (node.exists() && node.isDirectory() && node.canRead() && !node.isHidden()) {
                        childNodes = getFolderChildNodes(parentNode.getId(), node, indexElement.getMediaType());
                    }
                }
            } else {
                logger.error(parentNode.getId() + " node not found in index");
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

    @Override
    public void scanNode(AbstractNode node, boolean recursive) {
        if (node instanceof FolderNode) {
            List<AbstractNode> childNodes = getChildNodes(node);
            if (recursive && childNodes != null) {
                for (AbstractNode childNode : childNodes)
                    scanNode(childNode, recursive);
            }
        }
    }

    /**
     * Get childs of a configuration node (child nodes are stored in configuration)
     */
    private List<AbstractNode> getConfigurationChildNodes(RootNode rootNode) {
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
     * Get childs of a folder node
     */
    private List<AbstractNode> getFolderChildNodes(String parentId, File folder, String mediaType) {
        List<AbstractNode> nodes = Lists.newArrayList();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                AbstractNode node = null;
                if (file.canRead() && !file.isHidden()) {
                    // Add node to mediaIndex
                    String nodeId = mediaIndexManager.add(new MediaIndexElement(parentId, mediaType, file.getAbsolutePath(), null, true));
                    if (file.isDirectory()) {
                        // Add folder node
                        node = new FolderNode(nodeId, parentId, file.getName(), file);
                    } else {
                        // Add file node
                        node = buildFileNode(nodeId, parentId, file, mediaType);
                    }
                }
                if (node != null) nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * A pod-cast is a RSS feed URL
     * @return entries parsed from pod-cast RSS feed
     */
    @SuppressWarnings("unchecked")
    private List<AbstractNode> getPodcastChildNodes(final String parentId, final String url) {

        try {
            return podcastCache.get(url, new Callable<List<AbstractNode>>() {
                @Override
                public List<AbstractNode> call() throws Exception {
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
                                                    parentId, rssEntry.getTitle().trim(), mimeType, //
                                                    enclosure.getLength(), enclosure.getUrl(), duration);
                                            podcastEntryNode.setIconUrl(iconUrl);
                                            if (rssEntry.getPublishedDate() != null) podcastEntryNode.setModifedDate(rssEntry.getPublishedDate().getTime());

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
     * Get childs of playlist node
     * @param parentId
     * @param path
     * @return
     */
    private List<AbstractNode> getPlaylistChildNodes(String parentId, String path) {
        List<AbstractNode> nodes = Lists.newArrayList();

        try {
            List<PlaylistItem> items = new M3uParser(new File(path)).parse();
            if (items != null) {
                for (PlaylistItem item : items) {
                    MimeType mimeType = mimeTypeManager.getMimeType(item.getPath());
                    if (mimeType.isMedia()) {
                        String nodeId = mediaIndexManager.add(new MediaIndexElement(parentId, mimeType.getType(), item.getPath(), item.getLabel(), true));
                        nodes.add(new ContentNode(nodeId, parentId, item.getLabel(), new File(item.getPath()), mimeType));
                    }
                }
            }
        } catch (PlaylistParserException e) {
            logger.error(e.getMessage(), e);
        }

        return nodes;
    }

    /**
     * Build file node
     * 
     * @param nodeId
     * @param parentId
     * @param file
     * @param mediaType
     * @return
     */
    private AbstractNode buildFileNode(String nodeId, String parentId, File file, String mediaType) {
        AbstractNode node = null;

        // Check mime type
        MimeType mimeType = mimeTypeManager.getMimeType(file.getName());
        if (mimeType != null) {
            if (mimeType.getType().equals(MediaType.TYPE_PLAYLIST.getValue())) {
                node = new PlaylistNode(nodeId, parentId, file.getName(), file.getAbsolutePath());
            } else if (mimeType.getType().equals(mediaType)) {
                node = new ContentNode(nodeId, parentId, file.getName(), file, mimeType);
            } else if (mimeType.isSubTitle() && configuration.getParameter(Parameter.ENABLE_EXTERNAL_SUBTITLES))
                node = new ContentNode(nodeId, parentId, file.getName(), file, mimeType);

        }
        return node;
    }

    /**
     * Configuration has changed, update media index
     * @param event
     */
    @Subscribe
    public void handleConfigEvent(ConfigurationEvent configurationEvent) {
        if (logger.isDebugEnabled()) logger.debug("Configuration event received: {}", configurationEvent.toString());

        ConfigurationNode configNode = configurationEvent.getNode();
        RootNode rootNode = configurationEvent.getRootNode();
        switch (configurationEvent.getType()) {
        case ADD:
            // Add node to mediaIndex
            mediaIndexManager.put(configNode.getId(), MediaIndexElementFactory.get(rootNode, configNode));
            break;
        case UPDATE:
            // Remove node and childs from mediaIndex
            mediaIndexManager.remove(configNode.getId());
            if (rootNode != PODCAST) mediaIndexManager.removeChilds(configNode.getId());
            // Add node to mediaIndex
            mediaIndexManager.put(configNode.getId(), MediaIndexElementFactory.get(rootNode, configNode));
            break;
        case DELETE:
            // Remove node and childs from mediaIndex
            mediaIndexManager.remove(configNode.getId());
            if (rootNode != PODCAST) mediaIndexManager.removeChilds(configNode.getId());
            break;
        default:
            logger.error("Unknown event");
            break;
        }
    }

    @Subscribe
    public void handleMediaEvent(MediaEvent mediaEvent) {
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
