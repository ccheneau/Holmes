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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.media.index.IMediaIndex;
import net.holmes.core.media.index.IndexNode;
import net.holmes.core.media.node.AbstractNode;
import net.holmes.core.media.node.ContentNode;
import net.holmes.core.media.node.FolderNode;
import net.holmes.core.media.node.PodcastEntryNode;
import net.holmes.core.media.node.PodcastNode;
import net.holmes.core.util.mimetype.IMimeTypeFactory;
import net.holmes.core.util.mimetype.MimeType;
import net.holmes.core.util.resource.IResource;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public final class MediaService implements IMediaService {
    private static Logger logger = LoggerFactory.getLogger(MediaService.class);

    private static final String UPNP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Inject
    private IConfiguration configuration;

    @Inject
    private IMimeTypeFactory mimeTypeFactory;

    @Inject
    IResource resource;

    @Inject
    IMediaIndex mediaIndex;

    private CacheManager cacheManager;

    private Map<String, String> rootNodes;

    public MediaService() {
        cacheManager = CacheManager.create();
        rootNodes = new HashMap<String, String>();
        rootNodes.put(ConfigurationNode.ROOT_NODE_ID, "node.rootNode");
        rootNodes.put(ConfigurationNode.ROOT_AUDIO_NODE_ID, "node.audio");
        rootNodes.put(ConfigurationNode.ROOT_VIDEO_NODE_ID, "node.video");
        rootNodes.put(ConfigurationNode.ROOT_PICTURE_NODE_ID, "node.picture");
        rootNodes.put(ConfigurationNode.ROOT_PODCAST_NODE_ID, "node.podcast");
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#getNode(java.lang.String)
     */
    @Override
    public AbstractNode getNode(String nodeId) {
        AbstractNode node = null;
        if (logger.isDebugEnabled()) logger.debug("[START] getNode nodeId:" + nodeId);

        if (rootNodes.get(nodeId) != null) {
            // root node
            node = buildRootNode(nodeId);
        }
        else if (nodeId != null) {
            // Get node in mediaIndex
            IndexNode indexNode = mediaIndex.getValue(nodeId);
            if (indexNode != null) {
                if (MimeType.TYPE_PODCAST.equals(indexNode.getMediaType())) {
                    // podcast node
                    node = buildPodcastNode(nodeId, indexNode.getName(), indexNode.getPath());
                }
                else {
                    File nodeFile = new File(indexNode.getPath());
                    if (nodeFile.exists() && nodeFile.canRead() && !nodeFile.isHidden()) {
                        if (nodeFile.isFile()) {
                            // content node
                            node = buildContentNode(nodeId, indexNode.getParentId(), nodeFile, indexNode.getMediaType());
                        }
                        else if (nodeFile.isDirectory()) {
                            // folder node
                            String nodeName = indexNode.getName() != null ? indexNode.getName() : nodeFile.getName();
                            node = buildFolderNode(nodeId, indexNode.getParentId(), nodeName, nodeFile);
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
        if (ConfigurationNode.ROOT_NODE_ID.equals(parentNode.getId())) {
            // Child nodes of ROOT_NODE_ID are audio, video, picture and pod-cast root nodes
            childNodes = new ArrayList<AbstractNode>();
            childNodes.add(buildRootNode(ConfigurationNode.ROOT_AUDIO_NODE_ID));
            childNodes.add(buildRootNode(ConfigurationNode.ROOT_VIDEO_NODE_ID));
            childNodes.add(buildRootNode(ConfigurationNode.ROOT_PICTURE_NODE_ID));
            childNodes.add(buildRootNode(ConfigurationNode.ROOT_PODCAST_NODE_ID));
        }
        else if (ConfigurationNode.ROOT_AUDIO_NODE_ID.equals(parentNode.getId())) {
            // Child nodes of ROOT_AUDIO_NODE_ID are audio folders stored in configuration
            childNodes = getRootChildNodes(parentNode.getId(), configuration.getAudioFolders(), false, MimeType.TYPE_AUDIO);
        }
        else if (ConfigurationNode.ROOT_VIDEO_NODE_ID.equals(parentNode.getId())) {
            // Child nodes of ROOT_VIDEO_NODE_ID are video folders stored in configuration
            childNodes = getRootChildNodes(parentNode.getId(), configuration.getVideoFolders(), false, MimeType.TYPE_VIDEO);
        }
        else if (ConfigurationNode.ROOT_PICTURE_NODE_ID.equals(parentNode.getId())) {
            // Child nodes of ROOT_PICTURE_NODE_ID are picture folders stored in configuration
            childNodes = getRootChildNodes(parentNode.getId(), configuration.getPictureFolders(), false, MimeType.TYPE_IMAGE);
        }
        else if (ConfigurationNode.ROOT_PODCAST_NODE_ID.equals(parentNode.getId())) {
            // Child nodes of ROOT_PODCAST_NODE_ID are pod-cast URLs stored in configuration
            childNodes = getRootChildNodes(parentNode.getId(), configuration.getPodcasts(), true, MimeType.TYPE_PODCAST);
        }
        else if (parentNode.getId() != null) {
            // Get node in mediaIndex
            IndexNode indexNode = mediaIndex.getValue(parentNode.getId());
            if (indexNode != null) {
                if (MimeType.TYPE_PODCAST.equals(indexNode.getMediaType())) {
                    // get podcast child nodes
                    childNodes = getPodcastChildNodes(parentNode.getId(), indexNode.getPath());
                }
                else {
                    File node = new File(indexNode.getPath());
                    if (node.exists() && node.isDirectory() && node.canRead() && !node.isHidden()) {
                        // get folder child nodes
                        childNodes = getFolderChildNodes(parentNode.getId(), node, indexNode.getMediaType());
                    }
                }
            }
            else {
                logger.error(parentNode.getId() + " node not found in index");
            }
        }

        if (logger.isDebugEnabled()) logger.debug("[END] getChildNodes :" + childNodes);
        return childNodes;
    }

    /**
     * Get childs of a root node (child nodes are stored in configuration)
     */
    private List<AbstractNode> getRootChildNodes(String rootNodeId, List<ConfigurationNode> contentFolders, boolean podcast, String mediaType) {
        List<AbstractNode> nodes = new ArrayList<AbstractNode>();
        if (contentFolders != null && !contentFolders.isEmpty()) {
            if (podcast) {
                // Add podcast nodes
                for (ConfigurationNode contentFolder : contentFolders) {
                    // Add node to mediaIndex
                    mediaIndex.put(contentFolder.getId(), rootNodeId, mediaType, contentFolder.getPath(), contentFolder.getLabel());
                    // Add child node
                    nodes.add(buildPodcastNode(contentFolder.getId(), contentFolder.getLabel(), contentFolder.getPath()));
                }
            }
            else {
                // Add folder nodes
                for (ConfigurationNode contentFolder : contentFolders) {
                    File file = new File(contentFolder.getPath());
                    if (file.exists() && file.isDirectory() && file.canRead()) {
                        // Add node to mediaIndex
                        mediaIndex.put(contentFolder.getId(), rootNodeId, mediaType, contentFolder.getPath(), contentFolder.getLabel());
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
        List<AbstractNode> nodes = new ArrayList<AbstractNode>();
        File[] files = folder.listFiles();
        if (files != null) {
            AbstractNode node = null;
            for (File file : files) {
                node = null;
                if (file.canRead() && !file.isHidden()) {
                    // Add node to mediaIndex
                    String nodeId = mediaIndex.add(parentId, mediaType, file.getAbsolutePath(), null);
                    if (file.isDirectory()) {
                        // Add folder node
                        node = buildFolderNode(nodeId, parentId, file.getName(), file);
                    }
                    else {
                        // Add content node
                        node = buildContentNode(nodeId, parentId, file, mediaType);
                    }
                }
                if (node != null) nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * Get childs of a pod-cast node
     * A pod-cast is a RSS feed URL
     * @return entries parsed from RSS feed
     */
    @SuppressWarnings("unchecked")
    private List<AbstractNode> getPodcastChildNodes(String parentId, String url) {
        List<AbstractNode> podcastEntryNodes = null;
        Cache podcastEntriesCache = cacheManager.getCache("podcastEntries");

        // Try to read entries from cache
        if (podcastEntriesCache == null || podcastEntriesCache.get(url) == null) {
            // No entries in cache, read them from RSS feed
            XmlReader reader = null;
            try {
                // Get RSS feed entries
                URL feedSource = new URL(url);
                reader = new XmlReader(feedSource);
                List<SyndEntry> rssEntries = new SyndFeedInput().build(reader).getEntries();
                if (rssEntries != null && !rssEntries.isEmpty()) {
                    podcastEntryNodes = new ArrayList<AbstractNode>();
                    for (SyndEntry rssEntry : rssEntries) {
                        // Add node for each feed entries
                        if (rssEntry.getEnclosures() != null && !rssEntry.getEnclosures().isEmpty()) {
                            for (SyndEnclosure enclosure : (List<SyndEnclosure>) rssEntry.getEnclosures()) {
                                PodcastEntryNode podcastEntryNode = new PodcastEntryNode();
                                podcastEntryNode.setId(UUID.randomUUID().toString());
                                podcastEntryNode.setParentId(parentId);
                                podcastEntryNode.setName(rssEntry.getTitle());
                                if (rssEntry.getPublishedDate() != null) {
                                    podcastEntryNode.setModifedDate(formatUpnpDate(rssEntry.getPublishedDate().getTime()));
                                }
                                if (enclosure.getType() != null) {
                                    podcastEntryNode.setMimeType(new MimeType(enclosure.getType()));
                                }
                                podcastEntryNode.setSize(enclosure.getLength());
                                podcastEntryNode.setUrl(enclosure.getUrl());

                                podcastEntryNodes.add(podcastEntryNode);
                            }
                        }
                    }
                }
            }
            catch (MalformedURLException e) {
                logger.error(e.getMessage(), e);
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
            }
            catch (FeedException e) {
                logger.error(e.getMessage(), e);
            }
            finally {
                // Close the reader
                try {
                    if (reader != null) reader.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            // Add entries to cache
            if (podcastEntriesCache != null) podcastEntriesCache.put(new Element(url, podcastEntryNodes));
        }
        else {
            // Get entries from cache
            podcastEntryNodes = (List<AbstractNode>) (podcastEntriesCache.get(url).getValue());
        }
        return podcastEntryNodes;
    }

    private FolderNode buildRootNode(String nodeId) {
        FolderNode node = new FolderNode();
        node.setId(nodeId);
        node.setName(resource.getString(rootNodes.get(nodeId)));
        if (ConfigurationNode.ROOT_NODE_ID.equals(nodeId)) node.setParentId("-1");
        else node.setParentId(ConfigurationNode.ROOT_NODE_ID);
        return node;
    }

    private FolderNode buildFolderNode(String nodeId, String parentId, String name, File folder) {
        FolderNode node = new FolderNode();
        node.setId(nodeId);
        node.setParentId(parentId);
        node.setName(name);
        node.setPath(folder.getAbsolutePath());
        node.setModifedDate(formatUpnpDate(folder.lastModified()));
        return node;
    }

    private ContentNode buildContentNode(String nodeId, String parentId, File file, String mediaType) {
        ContentNode node = null;

        // Check mime type
        MimeType mimeType = mimeTypeFactory.getMimeType(file.getName());
        if (mimeType != null && mimeType.getType().equals(mediaType)) {
            node = new ContentNode();
            node.setId(nodeId);
            node.setParentId(parentId);
            node.setName(file.getName());
            node.setPath(file.getAbsolutePath());
            node.setMimeType(mimeType);
            node.setSize(file.length());
            node.setModifedDate(formatUpnpDate(file.lastModified()));
        }
        return node;
    }

    private PodcastNode buildPodcastNode(String nodeId, String name, String url) {
        PodcastNode node = new PodcastNode();
        node.setId(nodeId);
        node.setParentId(ConfigurationNode.ROOT_PODCAST_NODE_ID);
        node.setName(name);
        node.setUrl(url);
        return node;
    }

    private String formatUpnpDate(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return new SimpleDateFormat(UPNP_DATE_FORMAT).format(cal.getTime());
    }
}
