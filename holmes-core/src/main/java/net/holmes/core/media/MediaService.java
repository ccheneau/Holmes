/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
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
            // Get node id
            NodeId id = new NodeId(nodeId);
            if (id.isValid()) {
                if (MimeType.TYPE_PODCAST.equals(id.getType())) {
                    // podcast node
                    node = buildPodcastNode("", id.getPath());
                }
                else {
                    File nodeFile = new File(id.getPath());
                    if (nodeFile.exists() && nodeFile.canRead() && !nodeFile.isHidden()) {
                        if (nodeFile.isFile()) {
                            // content node
                            node = buildContentNode(nodeId, nodeFile, id.getType());
                        }
                        else if (nodeFile.isDirectory()) {
                            // folder node
                            node = buildFolderNode(nodeId, nodeFile.getName(), nodeFile);
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
            childNodes = getRootChildNodes(configuration.getAudioFolders(), false, MimeType.TYPE_AUDIO);
        }
        else if (ConfigurationNode.ROOT_VIDEO_NODE_ID.equals(parentNode.getId())) {
            // Child nodes of ROOT_VIDEO_NODE_ID are video folders stored in configuration
            childNodes = getRootChildNodes(configuration.getVideoFolders(), false, MimeType.TYPE_VIDEO);
        }
        else if (ConfigurationNode.ROOT_PICTURE_NODE_ID.equals(parentNode.getId())) {
            // Child nodes of ROOT_PICTURE_NODE_ID are picture folders stored in configuration
            childNodes = getRootChildNodes(configuration.getPictureFolders(), false, MimeType.TYPE_IMAGE);
        }
        else if (ConfigurationNode.ROOT_PODCAST_NODE_ID.equals(parentNode.getId())) {
            // Child nodes of ROOT_PODCAST_NODE_ID are pod-cast URLs stored in configuration
            childNodes = getRootChildNodes(configuration.getPodcasts(), true, null);
        }
        else if (parentNode.getId() != null) {
            // Get node id
            NodeId id = new NodeId(parentNode.getId());
            if (id.isValid()) {
                if (MimeType.TYPE_PODCAST.equals(id.getType())) {
                    // get podcast child nodes
                    childNodes = getPodcastChildNodes(id.getPath());
                }
                else {
                    File node = new File(id.getPath());
                    if (node.exists() && node.isDirectory() && node.canRead() && !node.isHidden()) {
                        // get folder child nodes
                        childNodes = getFolderChildNodes(node, id.getType());
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) logger.debug("[END] getChildNodes :" + childNodes);
        return childNodes;
    }

    /**
     * Get childs of a root node
     */
    private List<AbstractNode> getRootChildNodes(List<ConfigurationNode> contentFolders, boolean podcast, String mediaType) {
        List<AbstractNode> nodes = new ArrayList<AbstractNode>();
        if (contentFolders != null && !contentFolders.isEmpty()) {
            if (podcast) {
                // Add podcast nodes
                for (ConfigurationNode contentFolder : contentFolders) {
                    nodes.add(buildPodcastNode(contentFolder.getLabel(), contentFolder.getPath()));
                }
            }
            else {
                // Add folder nodes
                for (ConfigurationNode contentFolder : contentFolders) {
                    File file = new File(contentFolder.getPath());
                    if (file.exists() && file.isDirectory() && file.canRead()) {
                        NodeId id = new NodeId(mediaType, file.getAbsolutePath());
                        nodes.add(buildFolderNode(id.getNodeId(), contentFolder.getLabel(), file));
                    }
                }
            }
        }
        return nodes;
    }

    /**
     * Get childs of a folder node
     */
    private List<AbstractNode> getFolderChildNodes(File folder, String mediaType) {
        List<AbstractNode> nodes = new ArrayList<AbstractNode>();
        File[] files = folder.listFiles();
        if (files != null) {
            AbstractNode node = null;
            for (File file : files) {
                node = null;
                if (file.canRead() && !file.isHidden()) {
                    StringBuilder nodeId = new StringBuilder();
                    nodeId.append(mediaType).append("|").append(file.getAbsolutePath());
                    if (file.isDirectory()) {
                        // Add folder node
                        node = buildFolderNode(nodeId.toString(), file.getName(), file);
                    }
                    else {
                        // Add content node
                        node = buildContentNode(nodeId.toString(), file, mediaType);
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
    private List<AbstractNode> getPodcastChildNodes(String url) {
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
        return node;
    }

    private FolderNode buildFolderNode(String nodeId, String name, File folder) {
        FolderNode node = new FolderNode();
        node.setId(nodeId);
        node.setName(name);
        node.setPath(folder.getAbsolutePath());
        node.setModifedDate(formatUpnpDate(folder.lastModified()));
        return node;
    }

    private ContentNode buildContentNode(String nodeId, File file, String mediaType) {
        ContentNode node = null;

        // Check mime type
        MimeType mimeType = mimeTypeFactory.getMimeType(file.getName());
        if (mimeType != null && mimeType.getType().equals(mediaType)) {
            node = new ContentNode();
            node.setId(nodeId);
            node.setName(file.getName());
            node.setPath(file.getAbsolutePath());
            node.setMimeType(mimeType);
            node.setSize(file.length());
            node.setModifedDate(formatUpnpDate(file.lastModified()));
        }
        return node;
    }

    private PodcastNode buildPodcastNode(String name, String url) {
        PodcastNode node = new PodcastNode();
        NodeId id = new NodeId(MimeType.TYPE_PODCAST, url);
        node.setId(id.getNodeId());
        node.setName(name);
        node.setUrl(url);
        return node;
    }

    private String formatUpnpDate(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return new SimpleDateFormat(UPNP_DATE_FORMAT).format(cal.getTime());
    }

    /**
     * Node id parser for nodes having id with format "nodeType|Path or Url" 
     */
    private class NodeId {
        private String nodeType = null;;
        private String nodePath = null;

        private static final String SEPARATOR = "|";

        public NodeId(String nodeId) {
            String[] nodeParams = nodeId.split("\\" + SEPARATOR);
            if (nodeParams != null && nodeParams.length == 2) {
                this.nodeType = nodeParams[0];
                this.nodePath = nodeParams[1];
            }
        }

        public NodeId(String nodeType, String nodePath) {
            this.nodeType = nodeType;
            this.nodePath = nodePath;
        }

        public boolean isValid() {
            return nodeType != null && nodePath != null;
        }

        public String getType() {
            return this.nodeType;
        }

        public String getPath() {
            return this.nodePath;
        }

        public String getNodeId() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.nodeType).append(SEPARATOR).append(this.nodePath);
            return sb.toString();
        }
    }
}
