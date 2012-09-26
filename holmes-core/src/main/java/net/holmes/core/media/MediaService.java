/**
* Copyright (C) 2012  Cedric Cheneau
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.media.index.IMediaIndex;
import net.holmes.core.media.index.IndexElement;
import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.media.node.ContentNode;
import net.holmes.core.media.node.FolderNode;
import net.holmes.core.media.node.PlaylistNode;
import net.holmes.core.media.node.PodcastEntryNode;
import net.holmes.core.media.node.PodcastNode;
import net.holmes.core.util.MediaType;
import net.holmes.core.util.bundle.IBundle;
import net.holmes.core.util.mimetype.IMimeTypeFactory;
import net.holmes.core.util.mimetype.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.ITunes;
import com.sun.syndication.feed.module.mediarss.MediaEntryModule;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public final class MediaService implements IMediaService {
    private static Logger logger = LoggerFactory.getLogger(MediaService.class);

    @Inject
    private IConfiguration configuration;

    @Inject
    private IMimeTypeFactory mimeTypeFactory;

    @Inject
    private IBundle bundle;

    @Inject
    private IMediaIndex mediaIndex;

    private Cache<String, List<AbstractNode>> podcastCache;

    public MediaService() {
        podcastCache = CacheBuilder.newBuilder() //
                .maximumSize(50) //
                .expireAfterWrite(2, TimeUnit.HOURS) //
                .build();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#getNode(java.lang.String)
     */
    @Override
    public AbstractNode getNode(String nodeId) {
        AbstractNode node = null;
        if (logger.isDebugEnabled()) logger.debug("[START] getNode nodeId:" + nodeId);

        RootNode rootNode = RootNode.getById(nodeId);
        if (rootNode != null) {
            // Root node
            node = buildRootNode(rootNode);
        } else if (nodeId != null) {
            // Get node in mediaIndex
            IndexElement indexElement = mediaIndex.getElement(nodeId);
            if (indexElement != null) {
                if (MediaType.TYPE_PODCAST.getValue().equals(indexElement.getMediaType())) {
                    // Podcast node
                    node = buildPodcastNode(nodeId, indexElement.getName(), indexElement.getPath());
                } else if (MediaType.TYPE_PLAYLIST.getValue().equals(indexElement.getMediaType())) {
                    // Playlist node
                    node = buildPlaylistNode(nodeId, indexElement.getParentId(), indexElement.getName(), indexElement.getPath());
                } else {
                    File nodeFile = new File(indexElement.getPath());
                    if (nodeFile.exists() && nodeFile.canRead() && !nodeFile.isHidden()) {
                        if (nodeFile.isFile()) {
                            // Content node
                            node = buildFileNode(nodeId, indexElement.getParentId(), nodeFile, indexElement.getMediaType());
                        } else if (nodeFile.isDirectory()) {
                            // Folder node
                            String nodeName = indexElement.getName() != null ? indexElement.getName() : nodeFile.getName();
                            node = buildFolderNode(nodeId, indexElement.getParentId(), nodeName, nodeFile);
                        }
                    }
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("[END] getNode node:" + node);
        return node;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#getChildNodes(net.holmes.core.media.node.AbstractNode)
     */
    @Override
    public List<AbstractNode> getChildNodes(AbstractNode parentNode) {
        if (logger.isDebugEnabled()) logger.debug("[START] getChildNodes nodeId:" + parentNode.getId());

        List<AbstractNode> childNodes = null;
        if (RootNode.ROOT.getId().equals(parentNode.getId())) {
            // Child nodes of root are audio, video, picture and pod-cast root nodes
            childNodes = Lists.newArrayList();
            childNodes.add(buildRootNode(RootNode.AUDIO));
            childNodes.add(buildRootNode(RootNode.VIDEO));
            childNodes.add(buildRootNode(RootNode.PICTURE));
            childNodes.add(buildRootNode(RootNode.PODCAST));
        } else if (RootNode.AUDIO.getId().equals(parentNode.getId())) {
            // Child nodes of audio are audio folders stored in configuration
            childNodes = getConfigurationChildNodes(parentNode.getId(), configuration.getAudioFolders(), false, MediaType.TYPE_AUDIO);
        } else if (RootNode.VIDEO.getId().equals(parentNode.getId())) {
            // Child nodes of video are video folders stored in configuration
            childNodes = getConfigurationChildNodes(parentNode.getId(), configuration.getVideoFolders(), false, MediaType.TYPE_VIDEO);
        } else if (RootNode.PICTURE.getId().equals(parentNode.getId())) {
            // Child nodes of pitcure are picture folders stored in configuration
            childNodes = getConfigurationChildNodes(parentNode.getId(), configuration.getPictureFolders(), false, MediaType.TYPE_IMAGE);
        } else if (RootNode.PODCAST.getId().equals(parentNode.getId())) {
            // Child nodes of podcast are pod-cast URLs stored in configuration
            childNodes = getConfigurationChildNodes(parentNode.getId(), configuration.getPodcasts(), true, MediaType.TYPE_PODCAST);
        } else if (parentNode.getId() != null) {
            // Get node in mediaIndex
            IndexElement indexElement = mediaIndex.getElement(parentNode.getId());
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

        if (logger.isDebugEnabled()) logger.debug("[END] getChildNodes :" + childNodes);
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
                    mediaIndex.put(contentFolder.getId(), rootNodeId, mediaType.getValue(), contentFolder.getPath(), contentFolder.getLabel());
                    // Add child node
                    nodes.add(buildPodcastNode(contentFolder.getId(), contentFolder.getLabel(), contentFolder.getPath()));
                }
            } else {
                // Add folder nodes
                for (ConfigurationNode contentFolder : contentFolders) {
                    File file = new File(contentFolder.getPath());
                    if (file.exists() && file.isDirectory() && file.canRead()) {
                        // Add node to mediaIndex
                        mediaIndex.put(contentFolder.getId(), rootNodeId, mediaType.getValue(), contentFolder.getPath(), contentFolder.getLabel());
                        // Add child node
                        nodes.add(buildFolderNode(contentFolder.getId(), rootNodeId, contentFolder.getLabel(), file));
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
                    String nodeId = mediaIndex.add(parentId, mediaType, file.getAbsolutePath(), null);
                    if (file.isDirectory()) {
                        // Add folder node
                        node = buildFolderNode(nodeId, parentId, file.getName(), file);
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
                            for (SyndEntry rssEntry : rssEntries) {
                                // Add node for each feed entries
                                if (rssEntry.getEnclosures() != null && !rssEntry.getEnclosures().isEmpty()) {
                                    String duration = null;
                                    String iconUrl = null;
                                    EntryInformation itunesInfo = (EntryInformation) (rssEntry.getModule(ITunes.URI));
                                    if (itunesInfo != null && itunesInfo.getDuration() != null) {
                                        duration = itunesInfo.getDuration().toString();
                                    }
                                    MediaEntryModule mediaInfo = (MediaEntryModule) (rssEntry.getModule(MediaModule.URI));
                                    if (mediaInfo != null && mediaInfo.getMetadata() != null && mediaInfo.getMetadata().getThumbnail() != null) {
                                        iconUrl = mediaInfo.getMetadata().getThumbnail()[0].getUrl().toString();
                                    }
                                    for (SyndEnclosure enclosure : (List<SyndEnclosure>) rssEntry.getEnclosures()) {
                                        PodcastEntryNode podcastEntryNode = new PodcastEntryNode();
                                        podcastEntryNode.setId(UUID.randomUUID().toString());
                                        podcastEntryNode.setParentId(parentId);
                                        podcastEntryNode.setName(rssEntry.getTitle().trim());
                                        if (rssEntry.getPublishedDate() != null) {
                                            podcastEntryNode.setModifedDate(rssEntry.getPublishedDate());
                                        }
                                        if (enclosure.getType() != null) {
                                            podcastEntryNode.setMimeType(new MimeType(enclosure.getType()));
                                        }
                                        podcastEntryNode.setSize(enclosure.getLength());
                                        podcastEntryNode.setUrl(enclosure.getUrl());
                                        podcastEntryNode.setDuration(duration);
                                        podcastEntryNode.setIconUrl(iconUrl);

                                        podcastEntryNodes.add(podcastEntryNode);
                                    }
                                }
                            }
                        }
                    } catch (MalformedURLException e) {
                        logger.error(e.getMessage(), e);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    } catch (IllegalArgumentException e) {
                        logger.error(e.getMessage(), e);
                    } catch (FeedException e) {
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
        // TODO
        return null;
    }

    private FolderNode buildRootNode(RootNode rootNode) {
        FolderNode node = new FolderNode();
        node.setId(rootNode.getId());
        node.setName(bundle.getString("rootNode." + rootNode.getId()));
        node.setParentId(rootNode.getParentId());
        return node;
    }

    private FolderNode buildFolderNode(String nodeId, String parentId, String name, File folder) {
        FolderNode node = new FolderNode();
        node.setId(nodeId);
        node.setParentId(parentId);
        node.setName(name);
        node.setPath(folder.getAbsolutePath());
        node.setModifedDate(new Date(folder.lastModified()));
        return node;
    }

    private AbstractNode buildFileNode(String nodeId, String parentId, File file, String mediaType) {
        AbstractNode node = null;

        // Check mime type
        MimeType mimeType = mimeTypeFactory.getMimeType(file.getName());
        if (mimeType != null) {
            if (mimeType.getType().equals(MediaType.TYPE_PLAYLIST.getValue())) {
                node = buildPlaylistNode(nodeId, parentId, file.getName(), file.getAbsolutePath());
            } else if (mimeType.getType().equals(mediaType)) {
                node = buildContentNode(nodeId, parentId, file, mimeType);
            }
        }
        return node;
    }

    private ContentNode buildContentNode(String nodeId, String parentId, File file, MimeType mimeType) {
        ContentNode node = new ContentNode();
        node.setId(nodeId);
        node.setParentId(parentId);
        node.setName(file.getName());
        node.setPath(file.getAbsolutePath());
        node.setMimeType(mimeType);
        node.setSize(file.length());
        node.setModifedDate(new Date(file.lastModified()));
        return node;
    }

    private PodcastNode buildPodcastNode(String nodeId, String name, String url) {
        PodcastNode node = new PodcastNode();
        node.setId(nodeId);
        node.setParentId(RootNode.PODCAST.getId());
        node.setName(name);
        node.setUrl(url);
        return node;
    }

    private PlaylistNode buildPlaylistNode(String nodeId, String parentId, String name, String path) {
        PlaylistNode node = new PlaylistNode();
        node.setId(nodeId);
        node.setParentId(parentId);
        node.setName(name);
        node.setPath(path);
        return node;
    }

}
