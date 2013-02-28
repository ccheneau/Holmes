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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.media.index.MediaIndexElement;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.media.node.ContentNode;
import net.holmes.core.media.node.FolderNode;
import net.holmes.core.media.node.PlaylistNode;
import net.holmes.core.media.node.PodcastEntryNode;
import net.holmes.core.media.node.PodcastNode;
import net.holmes.core.media.node.RootNode;
import net.holmes.core.media.playlist.M3uParser;
import net.holmes.core.media.playlist.PlaylistItem;
import net.holmes.core.util.bundle.Bundle;
import net.holmes.core.util.inject.Loggable;
import net.holmes.core.util.mimetype.MimeType;
import net.holmes.core.util.mimetype.MimeTypeFactory;

import org.slf4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
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
    private final MimeTypeFactory mimeTypeFactory;
    private final Bundle bundle;
    private final MediaIndexManager mediaIndexManager;
    private final Cache<String, List<AbstractNode>> podcastCache;

    @Inject
    public MediaManagerImpl(Configuration configuration, MimeTypeFactory mimeTypeFactory, Bundle bundle, MediaIndexManager mediaIndexManager) {
        this.configuration = configuration;
        this.mimeTypeFactory = mimeTypeFactory;
        this.bundle = bundle;
        this.mediaIndexManager = mediaIndexManager;

        // Initialize podcast cache
        this.podcastCache = CacheBuilder.newBuilder() //
                .maximumSize(50) //
                .expireAfterWrite(2, TimeUnit.HOURS) //
                .build();
    }

    @Override
    public AbstractNode getNode(String nodeId) {
        AbstractNode node = null;
        if (logger.isDebugEnabled()) logger.debug("[START] getNode nodeId:{}", nodeId);

        RootNode rootNode = RootNode.getById(nodeId);
        if (rootNode != null) {
            // Root node
            node = new FolderNode(rootNode.getId(), rootNode.getParentId(), bundle.getString("rootNode." + rootNode.getId()));
        } else if (nodeId != null) {
            // Get node in mediaIndex
            MediaIndexElement indexElement = mediaIndexManager.get(nodeId);
            if (indexElement != null) {
                if (MediaType.TYPE_PODCAST.getValue().equals(indexElement.getMediaType())) {
                    // Podcast node
                    node = new PodcastNode(nodeId, RootNode.PODCAST.getId(), indexElement.getName(), indexElement.getPath());
                } else if (MediaType.TYPE_PLAYLIST.getValue().equals(indexElement.getMediaType())) {
                    // Playlist node
                    node = new PlaylistNode(nodeId, indexElement.getParentId(), indexElement.getName(), indexElement.getPath());
                } else {
                    File nodeFile = new File(indexElement.getPath());
                    if (nodeFile.exists() && nodeFile.canRead() && !nodeFile.isHidden()) {
                        if (nodeFile.isFile()) {
                            // Content node
                            node = getFileNode(nodeId, indexElement.getParentId(), nodeFile, indexElement.getMediaType());
                        } else if (nodeFile.isDirectory()) {
                            // Folder node
                            String nodeName = indexElement.getName() != null ? indexElement.getName() : nodeFile.getName();
                            node = new FolderNode(nodeId, indexElement.getParentId(), nodeName, nodeFile);
                        }
                    }
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("[END] getNode node:{}", node);
        return node;
    }

    @Override
    public List<AbstractNode> getChildNodes(AbstractNode parentNode) {
        if (logger.isDebugEnabled()) logger.debug("[START] getChildNodes nodeId:{}", parentNode.getId());

        List<AbstractNode> childNodes = null;
        if (RootNode.ROOT.getId().equals(parentNode.getId())) {
            // Child nodes of root are audio, video, picture and pod-cast root nodes
            childNodes = Lists.newArrayList();
            childNodes.add(new FolderNode(RootNode.AUDIO.getId(), RootNode.AUDIO.getParentId(), bundle.getString("rootNode." + RootNode.AUDIO.getId())));
            childNodes.add(new FolderNode(RootNode.VIDEO.getId(), RootNode.VIDEO.getParentId(), bundle.getString("rootNode." + RootNode.VIDEO.getId())));
            childNodes.add(new FolderNode(RootNode.PICTURE.getId(), RootNode.PICTURE.getParentId(), bundle.getString("rootNode." + RootNode.PICTURE.getId())));
            childNodes.add(new FolderNode(RootNode.PODCAST.getId(), RootNode.PODCAST.getParentId(), bundle.getString("rootNode." + RootNode.PODCAST.getId())));
        } else if (RootNode.AUDIO.getId().equals(parentNode.getId())) {
            // Child nodes of audio root are audio folders stored in configuration
            childNodes = getConfigurationChildNodes(parentNode.getId(), configuration.getAudioFolders(), false, MediaType.TYPE_AUDIO);
        } else if (RootNode.VIDEO.getId().equals(parentNode.getId())) {
            // Child nodes of video root are video folders stored in configuration
            childNodes = getConfigurationChildNodes(parentNode.getId(), configuration.getVideoFolders(), false, MediaType.TYPE_VIDEO);
        } else if (RootNode.PICTURE.getId().equals(parentNode.getId())) {
            // Child nodes of pitcure root are picture folders stored in configuration
            childNodes = getConfigurationChildNodes(parentNode.getId(), configuration.getPictureFolders(), false, MediaType.TYPE_IMAGE);
        } else if (RootNode.PODCAST.getId().equals(parentNode.getId())) {
            // Child nodes of podcast root are pod-cast URLs stored in configuration
            childNodes = getConfigurationChildNodes(parentNode.getId(), configuration.getPodcasts(), true, MediaType.TYPE_PODCAST);
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
                    File node = new File(indexElement.getPath());
                    if (node.exists() && node.isDirectory() && node.canRead() && !node.isHidden()) {
                        // Get folder child nodes
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

    /**
     * Get childs of a configuration node (child nodes are stored in configuration)
     */
    private List<AbstractNode> getConfigurationChildNodes(String rootNodeId, List<ConfigurationNode> contentFolders, boolean podcast, MediaType mediaType) {
        List<AbstractNode> nodes = Lists.newArrayList();
        if (contentFolders != null && !contentFolders.isEmpty()) {
            if (podcast) {
                // Add podcast nodes
                for (ConfigurationNode contentFolder : contentFolders) {
                    // Add node to mediaIndex
                    mediaIndexManager.put(contentFolder.getId(), rootNodeId, mediaType.getValue(), contentFolder.getPath(), contentFolder.getLabel(), false);
                    // Add child node
                    nodes.add(new PodcastNode(contentFolder.getId(), RootNode.PODCAST.getId(), contentFolder.getLabel(), contentFolder.getPath()));
                }
            } else {
                // Add folder nodes
                for (ConfigurationNode contentFolder : contentFolders) {
                    File file = new File(contentFolder.getPath());
                    if (file.exists() && file.isDirectory() && file.canRead()) {
                        // Add node to mediaIndex
                        mediaIndexManager.put(contentFolder.getId(), rootNodeId, mediaType.getValue(), contentFolder.getPath(), contentFolder.getLabel(), true);
                        // Add child node
                        nodes.add(new FolderNode(contentFolder.getId(), rootNodeId, contentFolder.getLabel(), file));
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
                    String nodeId = mediaIndexManager.add(parentId, mediaType, file.getAbsolutePath(), null, true);
                    if (file.isDirectory()) {
                        // Add folder node
                        node = new FolderNode(nodeId, parentId, file.getName(), file);
                    } else {
                        // Add file node
                        node = getFileNode(nodeId, parentId, file, mediaType);
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
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
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

    private List<AbstractNode> getPlaylistChildNodes(String parentId, String path) {
        List<AbstractNode> nodes = Lists.newArrayList();
        List<PlaylistItem> items = new M3uParser(new File(path)).parse();
        if (items != null) {
            for (PlaylistItem item : items) {
                MimeType mimeType = mimeTypeFactory.getMimeType(item.getPath());
                if (mimeType.isMedia()) {
                    String nodeId = mediaIndexManager.add(parentId, mimeType.getType(), item.getPath(), item.getLabel(), true);
                    nodes.add(new ContentNode(nodeId, parentId, item.getLabel(), new File(item.getPath()), mimeType));
                }
            }
        }
        return nodes;
    }

    private AbstractNode getFileNode(String nodeId, String parentId, File file, String mediaType) {
        AbstractNode node = null;

        // Check mime type
        MimeType mimeType = mimeTypeFactory.getMimeType(file.getName());
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
}
