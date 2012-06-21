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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.holmes.core.configuration.ContentFolder;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.media.dao.IMediaDao;
import net.holmes.core.model.AbstractNode;
import net.holmes.core.model.ContainerNode;
import net.holmes.core.model.ContentNode;
import net.holmes.core.model.ContentType;
import net.holmes.core.model.IContentTypeFactory;
import net.holmes.core.model.PodcastContainerNode;
import net.holmes.core.util.DateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * The Class MediaService.
 */
public final class MediaService implements IMediaService
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(MediaService.class);

    /** The media DAO. */
    @Inject
    private IMediaDao mediaDao;

    /** The configuration. */
    @Inject
    private IConfiguration configuration;

    /** The content type factory. */
    @Inject
    private IContentTypeFactory contentTypeFactory;

    /**
     * Instantiates a new media service impl.
     */
    public MediaService()
    {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#getNode(java.lang.String)
     */
    @Override
    public AbstractNode getNode(String nodeId)
    {
        return mediaDao.getNode(nodeId);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#scanAll()
     */
    @Override
    public synchronized void scanAll()
    {
        scanRootNode(ContentFolder.ROOT_VIDEO_NODE_ID, configuration.getConfig().getVideoFolders(), false, ContentType.TYPE_VIDEO);
        scanRootNode(ContentFolder.ROOT_AUDIO_NODE_ID, configuration.getConfig().getAudioFolders(), false, ContentType.TYPE_AUDIO);
        scanRootNode(ContentFolder.ROOT_PICTURE_NODE_ID, configuration.getConfig().getPictureFolders(), false, ContentType.TYPE_IMAGE);
        scanRootNode(ContentFolder.ROOT_PODCAST_NODE_ID, configuration.getConfig().getPodcasts(), true, null);

        mediaDao.flush();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#scanVideos()
     */
    @Override
    public void scanVideos()
    {
        scanRootNode(ContentFolder.ROOT_VIDEO_NODE_ID, configuration.getConfig().getVideoFolders(), false, ContentType.TYPE_VIDEO);
        mediaDao.flush();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#scanAudios()
     */
    @Override
    public void scanAudios()
    {
        scanRootNode(ContentFolder.ROOT_AUDIO_NODE_ID, configuration.getConfig().getAudioFolders(), false, ContentType.TYPE_AUDIO);
        mediaDao.flush();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#scanPictures()
     */
    @Override
    public void scanPictures()
    {
        scanRootNode(ContentFolder.ROOT_PICTURE_NODE_ID, configuration.getConfig().getPictureFolders(), false, ContentType.TYPE_IMAGE);
        mediaDao.flush();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#scanPodcasts()
     */
    @Override
    public void scanPodcasts()
    {
        scanRootNode(ContentFolder.ROOT_PODCAST_NODE_ID, configuration.getConfig().getPodcasts(), true, null);
        mediaDao.flush();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.IMediaService#getNodeIds()
     */
    @Override
    public Set<String> getNodeIds()
    {
        return mediaDao.getNodeIds();
    }

    /**
      * Scan a root node.
      *
      * @param rootNodeId the root node id
      * @param contentFolders the content folders
      * @param podcast the podcast
      * @param mediaType the media type
      */
    private void scanRootNode(String rootNodeId, List<ContentFolder> contentFolders, boolean podcast, String mediaType)
    {
        if (logger.isDebugEnabled()) logger.debug("[START] scanRootNode rootNodeId:" + rootNodeId);

        // Get root node
        AbstractNode rootNode = mediaDao.getNode(rootNodeId);
        if (rootNode instanceof ContainerNode)
        {
            ContainerNode rootContainerNode = (ContainerNode) rootNode;
            List<String> nodesToRemove = new ArrayList<String>();

            // Initialize nodes to remove
            if (rootContainerNode.getChildNodeIds() != null)
            {
                for (String nodeId : rootContainerNode.getChildNodeIds())
                    nodesToRemove.add(nodeId);
            }

            if (contentFolders != null && !contentFolders.isEmpty())
            {
                if (podcast)
                {
                    PodcastContainerNode addedNode = null;
                    // Add podcast nodes
                    for (ContentFolder contentFolder : contentFolders)
                    {
                        addedNode = addPodcastNode(contentFolder.getLabel(), contentFolder.getPath(), rootContainerNode);
                        if (rootContainerNode.getChildNodeIds().contains(addedNode.getId()))
                        {
                            // Node already exist => do not remove it
                            nodesToRemove.remove(addedNode.getId());
                        }
                        else
                        {
                            // Add to child nodes
                            mediaDao.addChildNode(rootContainerNode.getId(), addedNode.getId());
                        }
                    }
                }
                else
                {
                    ContainerNode addedNode = null;
                    // Add container nodes
                    for (ContentFolder contentFolder : contentFolders)
                    {
                        addedNode = null;
                        File file = new File(contentFolder.getPath());
                        if (file.exists() && file.isDirectory() && file.canRead())
                        {
                            // Add container node
                            addedNode = addContainerNode(contentFolder.getLabel(), file, rootContainerNode);
                            if (rootContainerNode.getChildNodeIds().contains(addedNode.getId()))
                            {
                                // Node already exist => do not remove it
                                nodesToRemove.remove(addedNode.getId());
                            }
                            else
                            {
                                // Add to child nodes
                                mediaDao.addChildNode(rootContainerNode.getId(), addedNode.getId());
                            }

                            // Scan child folder
                            scanFolder(file, addedNode, true, mediaType);
                        }
                    }
                }

            }
            // Remove obsolete nodes
            if (!nodesToRemove.isEmpty())
            {
                for (String nodeId : nodesToRemove)
                {
                    removeNode(nodeId);
                    mediaDao.removeChildNode(rootContainerNode.getId(), nodeId);
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("[END] scanRootNode");

    }

    /**
     * Scan a specific folder.
     *
     * @param folder the folder
     * @param parentNode the parent node
     * @param recursive the recursive
     * @param mediaType the media type
     */
    private void scanFolder(File folder, ContainerNode parentNode, boolean recursive, String mediaType)
    {
        if (logger.isDebugEnabled()) logger.debug("[START] scanFolder folder:" + folder + " parentNodeId:" + parentNode.getId());

        File[] files = folder.listFiles();

        if (files != null)
        {
            AbstractNode addedNode = null;
            List<String> nodesToRemove = new ArrayList<String>();

            // Initialize nodes to remove
            if (parentNode.getChildNodeIds() != null)
            {
                for (String nodeId : parentNode.getChildNodeIds())
                    nodesToRemove.add(nodeId);
            }

            for (File file : files)
            {
                addedNode = null;
                if (file.canRead() && !file.isHidden())
                {
                    if (file.isDirectory())
                    {
                        // Add container node
                        addedNode = addContainerNode(file.getName(), file, parentNode);
                    }
                    else
                    {
                        // Add content node
                        addedNode = addContentNode(file, parentNode, mediaType);
                    }
                }

                if (addedNode != null)
                {
                    if (parentNode.getChildNodeIds().contains(addedNode.getId()))
                    {
                        // Node already exist => do not remove it
                        nodesToRemove.remove(addedNode.getId());
                    }
                    else
                    {
                        // Adds to child nodes
                        mediaDao.addChildNode(parentNode.getId(), addedNode.getId());
                    }

                    // Scan child folder
                    if (addedNode != null && addedNode instanceof ContainerNode && recursive)
                    {
                        scanFolder(file, (ContainerNode) addedNode, true, mediaType);
                    }
                }
            }

            // Remove obsolete nodes
            if (!nodesToRemove.isEmpty())
            {
                for (String nodeId : nodesToRemove)
                {
                    removeNode(nodeId);
                    mediaDao.removeChildNode(parentNode.getId(), nodeId);
                }
            }

        }
        if (logger.isDebugEnabled()) logger.debug("[END] scanFolder");
    }

    /**
     * Add a container node. If node already exists, returns existing one
     *
     * @param name the name
     * @param folder the folder
     * @param parentNode the parent node
     * @return the container node
     */
    private ContainerNode addContainerNode(String name, File folder, ContainerNode parentNode)
    {
        if (logger.isDebugEnabled()) logger.debug("[START] addContainerNode folder:" + folder + " parentNodeId:" + parentNode.getId());

        AbstractNode currentNode = null;
        for (String nodeId : parentNode.getChildNodeIds())
        {
            // Check if node already exists in container child nodes
            currentNode = mediaDao.getNode(nodeId);
            if (currentNode != null && currentNode instanceof ContainerNode && name.equals(currentNode.getName()))
            {
                mediaDao.updateNodeModifiedDate(currentNode.getId(), DateFormat.formatUpnpDate(folder.lastModified()));
                if (logger.isDebugEnabled()) logger.debug("[END] addContainerNode existing node:" + currentNode);
                return (ContainerNode) currentNode;
            }
        }

        // Node not found, create a new one
        ContainerNode node = new ContainerNode();
        node.setName(name);
        node.setPath(folder.getAbsolutePath());
        node.setParentNodeId(parentNode.getId());
        node.setId(UUID.randomUUID().toString());
        node.setChildNodeIds(new LinkedList<String>());
        node.setModifedDate(DateFormat.formatUpnpDate(folder.lastModified()));

        // Add the node
        mediaDao.addNode(node);

        if (logger.isDebugEnabled()) logger.debug("[END] addContainerNode added node:" + node);
        return node;
    }

    /**
     * Add a content node. If node already exists, returns existing one
     *
     * @param file the file
     * @param parentNode the parent node
     * @param mediaType the media type (video, audio, image)
     * @return the content node
     */
    private ContentNode addContentNode(File file, ContainerNode parentNode, String mediaType)
    {
        if (logger.isDebugEnabled()) logger.debug("[START] addContentNode file:" + file + " parentNodeId:" + parentNode.getId());

        String name = file.getName();
        AbstractNode currentNode = null;

        // Check if node already exists in container child nodes
        for (String nodeId : parentNode.getChildNodeIds())
        {
            currentNode = mediaDao.getNode(nodeId);
            if (currentNode != null && currentNode instanceof ContentNode && name.equals(currentNode.getName()))
            {
                mediaDao.updateNodeModifiedDate(currentNode.getId(), DateFormat.formatUpnpDate(file.lastModified()));
                if (logger.isDebugEnabled()) logger.debug("[END] addContentNode existing node:" + currentNode);
                return (ContentNode) currentNode;
            }
        }

        // Node not found, create a new one
        ContentNode node = null;

        // Check content type
        ContentType contentType = contentTypeFactory.getContentType(file.getName());
        if (contentType != null && contentType.getType().equals(mediaType))
        {
            node = new ContentNode();
            node.setName(file.getName());
            node.setPath(file.getAbsolutePath());
            node.setParentNodeId(parentNode.getId());
            node.setContentType(contentType);
            node.setSize(file.length());
            node.setId(UUID.randomUUID().toString());
            node.setModifedDate(DateFormat.formatUpnpDate(file.lastModified()));

            // Add the node
            mediaDao.addNode(node);
        }
        if (logger.isDebugEnabled()) logger.debug("[END] addContentNode added node:" + node);

        return node;
    }

    /**
     * Add a pod-cast node. If node already exists, returns existing one
     *
     * @param name the name
     * @param url the url
     * @param parentNode the parent node
     * @return the podcast container node
     */
    private PodcastContainerNode addPodcastNode(String name, String url, ContainerNode parentNode)
    {
        if (logger.isDebugEnabled()) logger.debug("[START] addPodcastNode url:" + url + " parentNodeId:" + parentNode.getId());

        AbstractNode currentNode = null;

        // Check if node already exists in container child nodes
        for (String nodeId : parentNode.getChildNodeIds())
        {
            currentNode = mediaDao.getNode(nodeId);
            if (currentNode != null && currentNode instanceof PodcastContainerNode && name.equals(currentNode.getName()))
            {
                if (logger.isDebugEnabled()) logger.debug("[END] addPodcastNode existing node:" + currentNode);
                return (PodcastContainerNode) currentNode;
            }
        }

        // Node not found, create a new one
        PodcastContainerNode node = new PodcastContainerNode();
        node.setName(name);
        node.setUrl(url);
        node.setParentNodeId(parentNode.getId());
        node.setId(UUID.randomUUID().toString());

        // Add the node
        mediaDao.addNode(node);

        if (logger.isDebugEnabled()) logger.debug("[END] addPodcastNode added node:" + node);

        return node;
    }

    /**
     * Remove node and its child nodes.
     *
     * @param nodeId the node id
     */
    private void removeNode(String nodeId)
    {
        if (logger.isDebugEnabled()) logger.debug("[START] remove node:" + nodeId);
        AbstractNode node = mediaDao.getNode(nodeId);
        if (node != null)
        {
            if (node instanceof ContainerNode)
            {
                // Remove child nodes
                if (((ContainerNode) node).getChildNodeIds() != null)
                {
                    for (String childNodeId : ((ContainerNode) node).getChildNodeIds())
                    {
                        removeNode(childNodeId);
                    }
                }
            }

            // Remove node
            mediaDao.removeNode(nodeId);
        }
        if (logger.isDebugEnabled()) logger.debug("[END] remove node");
    }
}
