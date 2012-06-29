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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import net.holmes.core.configuration.ContentFolder;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.model.AbstractNode;
import net.holmes.core.model.ContentNode;
import net.holmes.core.model.ContentType;
import net.holmes.core.model.FolderNode;
import net.holmes.core.model.IContentTypeFactory;
import net.holmes.core.model.PodcastItemNode;
import net.holmes.core.model.PodcastNode;
import net.holmes.core.util.DateFormat;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * The Class MediaService.
 */
public final class MediaService implements IMediaService
{
    private static Logger logger = LoggerFactory.getLogger(MediaService.class);

    /** The configuration. */
    @Inject
    private IConfiguration configuration;

    /** The content type factory. */
    @Inject
    private IContentTypeFactory contentTypeFactory;

    /** The cache manager. */
    @Inject
    private CacheManager cacheManager;

    /** The bundle. */
    private ResourceBundle bundle;

    /** The root nodes. */
    private Map<String, String> rootNodes;

    /**
     * Instantiates a new media service.
     */
    public MediaService()
    {
        bundle = ResourceBundle.getBundle("message");
        rootNodes = new HashMap<String, String>();
        rootNodes.put(ContentFolder.ROOT_NODE_ID, "node.rootNode");
        rootNodes.put(ContentFolder.ROOT_AUDIO_NODE_ID, "node.audio");
        rootNodes.put(ContentFolder.ROOT_VIDEO_NODE_ID, "node.video");
        rootNodes.put(ContentFolder.ROOT_PICTURE_NODE_ID, "node.picture");
        rootNodes.put(ContentFolder.ROOT_PODCAST_NODE_ID, "node.podcast");
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#getNode(java.lang.String)
     */
    @Override
    public AbstractNode getNode(String nodeId)
    {
        AbstractNode node = null;
        if (logger.isDebugEnabled()) logger.debug("[START] getNode nodeId:" + nodeId);

        if (rootNodes.get(nodeId) != null)
        {
            // root node
            node = getRootNode(nodeId, rootNodes.get(nodeId));
        }
        else if (nodeId != null)
        {
            String[] nodeParams = nodeId.split("|");
            if (nodeParams != null && nodeParams.length == 2)
            {
                if (ContentType.TYPE_PODCAST.equals(nodeParams[0]))
                {
                    // podcast node
                    node = getPodcastNode("", nodeParams[1]);
                }
                else
                {
                    File nodeFile = new File(nodeParams[1]);
                    if (nodeFile.exists() && nodeFile.canRead() && !nodeFile.isHidden())
                    {
                        if (nodeFile.isFile())
                        {
                            // content node
                            node = getContentNode(nodeId, nodeFile, nodeParams[0]);
                        }
                        else if (nodeFile.isDirectory())
                        {
                            // folder node
                            node = getFolderNode(nodeId, nodeFile.getName(), nodeFile);
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) logger.debug("[END] getNode node:" + node);
        return node;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#getChildNodes(net.holmes.core.model.AbstractNode)
     */
    @Override
    public List<AbstractNode> getChildNodes(AbstractNode parentNode)
    {
        if (logger.isDebugEnabled()) logger.debug("[START] getChildNodes nodeId:" + parentNode.getId());

        List<AbstractNode> childNodes = null;
        if (ContentFolder.ROOT_NODE_ID.equals(parentNode.getId()))
        {
            childNodes = new ArrayList<AbstractNode>();
            childNodes.add(getRootNode(ContentFolder.ROOT_AUDIO_NODE_ID, rootNodes.get(ContentFolder.ROOT_AUDIO_NODE_ID)));
            childNodes.add(getRootNode(ContentFolder.ROOT_VIDEO_NODE_ID, rootNodes.get(ContentFolder.ROOT_VIDEO_NODE_ID)));
            childNodes.add(getRootNode(ContentFolder.ROOT_PICTURE_NODE_ID, rootNodes.get(ContentFolder.ROOT_PICTURE_NODE_ID)));
            childNodes.add(getRootNode(ContentFolder.ROOT_PODCAST_NODE_ID, rootNodes.get(ContentFolder.ROOT_PODCAST_NODE_ID)));
        }
        else if (ContentFolder.ROOT_AUDIO_NODE_ID.equals(parentNode.getId()))
        {
            childNodes = getChildRootNodes(configuration.getConfig().getAudioFolders(), false, ContentType.TYPE_AUDIO);
        }
        else if (ContentFolder.ROOT_VIDEO_NODE_ID.equals(parentNode.getId()))
        {
            childNodes = getChildRootNodes(configuration.getConfig().getVideoFolders(), false, ContentType.TYPE_VIDEO);
        }
        else if (ContentFolder.ROOT_PICTURE_NODE_ID.equals(parentNode.getId()))
        {
            childNodes = getChildRootNodes(configuration.getConfig().getPictureFolders(), false, ContentType.TYPE_IMAGE);
        }
        else if (ContentFolder.ROOT_AUDIO_NODE_ID.equals(parentNode.getId()))
        {
            childNodes = getChildRootNodes(configuration.getConfig().getPodcasts(), true, null);
        }
        else if (parentNode.getId() != null)
        {
            String[] nodeParams = parentNode.getId().split("|");
            if (nodeParams != null && nodeParams.length == 2)
            {
                if (ContentType.TYPE_PODCAST.equals(nodeParams[0]))
                {
                    // podcast items
                    childNodes = getPodcastItems(nodeParams[1]);
                }
                else
                {
                    File node = new File(nodeParams[1]);
                    if (node.exists() && node.isDirectory() && node.canRead() && !node.isHidden())
                    {
                        // folder items
                        childNodes = getFolderItems(node, nodeParams[0]);
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) logger.debug("[END] getChildNodes :" + childNodes);
        return childNodes;
    }

    /**
     * Gets childs of a root node.
     *
     * @param contentFolders the content folders
     * @param podcast the podcast
     * @param mediaType the media type
     * @return the child root nodes
     */
    private List<AbstractNode> getChildRootNodes(List<ContentFolder> contentFolders, boolean podcast, String mediaType)
    {
        List<AbstractNode> nodes = new ArrayList<AbstractNode>();
        if (contentFolders != null && !contentFolders.isEmpty())
        {
            if (podcast)
            {
                // Add podcast nodes
                for (ContentFolder contentFolder : contentFolders)
                {
                    nodes.add(getPodcastNode(contentFolder.getLabel(), contentFolder.getPath()));
                }
            }
            else
            {
                // Add folder nodes
                for (ContentFolder contentFolder : contentFolders)
                {
                    File file = new File(contentFolder.getPath());
                    if (file.exists() && file.isDirectory() && file.canRead())
                    {
                        StringBuilder nodeId = new StringBuilder();
                        nodeId.append(mediaType).append("|").append(file.getAbsolutePath());
                        nodes.add(getFolderNode(nodeId.toString(), contentFolder.getLabel(), file));
                    }
                }
            }
        }
        return nodes;
    }

    /**
     * Gets folder items.
     *
     * @param folder the folder
     * @param mediaType the media type
     * @return the list
     */
    private List<AbstractNode> getFolderItems(File folder, String mediaType)
    {
        List<AbstractNode> nodes = new ArrayList<AbstractNode>();
        File[] files = folder.listFiles();
        if (files != null)
        {
            AbstractNode node = null;
            for (File file : files)
            {
                node = null;
                if (file.canRead() && !file.isHidden())
                {
                    StringBuilder nodeId = new StringBuilder();
                    nodeId.append(mediaType).append("|").append(file.getAbsolutePath());
                    if (file.isDirectory())
                    {
                        // Add folder node
                        node = getFolderNode(nodeId.toString(), file.getName(), file);
                    }
                    else
                    {
                        // Add content node
                        node = getContentNode(nodeId.toString(), file, mediaType);
                    }
                }
                if (node != null) nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * Gets the podcast items.
     *
     * @param url the url
     * @return the podcast items
     */
    @SuppressWarnings("unchecked")
    private List<AbstractNode> getPodcastItems(String url)
    {
        List<AbstractNode> podcastItemNodes = null;
        Cache podcastItemsCache = cacheManager.getCache("podcastItems");

        // Try to read items from cache
        if (podcastItemsCache.get(url) == null)
        {
            // No items in cache, read them from RSS feed
            XmlReader reader = null;
            URL feedSource;
            try
            {
                // Get RSS feed entries
                feedSource = new URL(url);
                reader = new XmlReader(feedSource);
                SyndFeed feed = new SyndFeedInput().build(reader);
                List<SyndEntry> rssEntries = feed.getEntries();
                if (rssEntries != null && !rssEntries.isEmpty())
                {
                    podcastItemNodes = new ArrayList<AbstractNode>();
                    for (SyndEntry rssEntry : rssEntries)
                    {
                        if (rssEntry.getEnclosures() != null && !rssEntry.getEnclosures().isEmpty())
                        {
                            for (SyndEnclosure enclosure : (List<SyndEnclosure>) rssEntry.getEnclosures())
                            {
                                PodcastItemNode podcastItemNode = new PodcastItemNode();
                                podcastItemNode.setId(UUID.randomUUID().toString());
                                podcastItemNode.setName(rssEntry.getTitle());
                                if (rssEntry.getPublishedDate() != null)
                                {
                                    podcastItemNode.setModifedDate(DateFormat.formatUpnpDate(rssEntry.getPublishedDate().getTime()));
                                }
                                if (enclosure.getType() != null)
                                {
                                    podcastItemNode.setContentType(new ContentType(enclosure.getType()));
                                }
                                podcastItemNode.setSize(enclosure.getLength());
                                podcastItemNode.setUrl(enclosure.getUrl());

                                podcastItemNodes.add(podcastItemNode);
                            }
                        }
                    }
                }
            }
            catch (MalformedURLException e)
            {
                logger.error(e.getMessage(), e);
            }
            catch (IOException e)
            {
                logger.error(e.getMessage(), e);
            }
            catch (IllegalArgumentException e)
            {
                logger.error(e.getMessage(), e);
            }
            catch (FeedException e)
            {
                logger.error(e.getMessage(), e);
            }
            finally
            {
                // Close reader
                try
                {
                    if (reader != null) reader.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }

            // Add items to cache
            podcastItemsCache.put(new Element(url, podcastItemNodes));
        }
        else
        {
            // Get items from cache
            podcastItemNodes = (List<AbstractNode>) (podcastItemsCache.get(url).getValue());
        }
        return podcastItemNodes;
    }

    /**
     * Gets the root node.
     *
     * @param nodeId the node id
     * @param bundleKey the bundle key
     * @return the root node
     */
    private FolderNode getRootNode(String nodeId, String bundleKey)
    {
        FolderNode node = new FolderNode();
        node.setId(nodeId);
        node.setName(bundle.getString(bundleKey));
        return node;
    }

    /**
     * Gets the folder node.
     *
     * @param nodeId the node id
     * @param name the name
     * @param folder the folder
     * @return the folder node
     */
    private FolderNode getFolderNode(String nodeId, String name, File folder)
    {
        FolderNode node = new FolderNode();
        node.setId(nodeId);
        node.setName(name);
        node.setPath(folder.getAbsolutePath());
        node.setModifedDate(DateFormat.formatUpnpDate(folder.lastModified()));
        return node;
    }

    /**
     * Get a content node.
     *
     * @param nodeId the node id
     * @param file the file
     * @param mediaType the media type (video, audio, image)
     * @return the content node
     */
    private ContentNode getContentNode(String nodeId, File file, String mediaType)
    {
        ContentNode node = null;

        // Check content type
        ContentType contentType = contentTypeFactory.getContentType(file.getName());
        if (contentType != null && contentType.getType().equals(mediaType))
        {
            node = new ContentNode();
            node.setId(nodeId);
            node.setName(file.getName());
            node.setPath(file.getAbsolutePath());
            node.setContentType(contentType);
            node.setSize(file.length());
            node.setModifedDate(DateFormat.formatUpnpDate(file.lastModified()));
        }
        return node;
    }

    /**
     * Get a pod-cast node.
     *
     * @param name the name
     * @param url the url
     * @return the podcast node
     */
    private PodcastNode getPodcastNode(String name, String url)
    {
        PodcastNode node = new PodcastNode();
        StringBuilder nodeId = new StringBuilder();
        nodeId.append(ContentType.TYPE_PODCAST).append("|").append(url);
        node.setId(nodeId.toString());
        node.setName(name);
        node.setUrl(url);
        return node;
    }

}
