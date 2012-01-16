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
package net.holmes.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import net.holmes.core.configuration.ContentFolder;
import net.holmes.core.configuration.IConfiguration;
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
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The Class MediaServiceImpl.
 */
public final class MediaServiceImpl implements IMediaService
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(MediaServiceImpl.class);

    /** The Constant MEDIA_FILE_NAME. */
    private static final String MEDIA_FILE_NAME = "media.xml";

    /** The node map. */
    private Map<String, AbstractNode> nodeMap;

    /** The configuration. */
    @Inject
    private IConfiguration configuration;

    /** The content type factory. */
    @Inject
    private IContentTypeFactory contentTypeFactory;

    /**
     * Instantiates a new media service impl.
     */
    public MediaServiceImpl()
    {
        initMap();
    }

    /**
     * Inits the map.
     */
    private void initMap()
    {
        nodeMap = new HashMap<String, AbstractNode>();

        ResourceBundle bundle = ResourceBundle.getBundle("message");

        // Create root node
        ContainerNode rootNode = new ContainerNode();
        rootNode.setId(ContentFolder.ROOT_NODE_ID);
        rootNode.setName(bundle.getString("node.rootNode"));
        rootNode.setChildNodeIds(new ArrayList<String>());
        rootNode.getChildNodeIds().add(ContentFolder.ROOT_VIDEO_NODE_ID);
        rootNode.getChildNodeIds().add(ContentFolder.ROOT_AUDIO_NODE_ID);
        rootNode.getChildNodeIds().add(ContentFolder.ROOT_PICTURE_NODE_ID);
        rootNode.getChildNodeIds().add(ContentFolder.ROOT_PODCAST_NODE_ID);
        nodeMap.put(ContentFolder.ROOT_NODE_ID, rootNode);

        // Create root video node
        ContainerNode videoNode = new ContainerNode();
        videoNode.setId(ContentFolder.ROOT_VIDEO_NODE_ID);
        videoNode.setName(bundle.getString("node.video"));
        videoNode.setChildNodeIds(new ArrayList<String>());
        videoNode.setParentNodeId(ContentFolder.ROOT_NODE_ID);
        nodeMap.put(ContentFolder.ROOT_VIDEO_NODE_ID, videoNode);

        // Create root audio node
        ContainerNode audioNode = new ContainerNode();
        audioNode.setId(ContentFolder.ROOT_AUDIO_NODE_ID);
        audioNode.setName(bundle.getString("node.audio"));
        audioNode.setChildNodeIds(new ArrayList<String>());
        audioNode.setParentNodeId(ContentFolder.ROOT_NODE_ID);
        nodeMap.put(ContentFolder.ROOT_AUDIO_NODE_ID, audioNode);

        // Create root picture node
        ContainerNode pictureNode = new ContainerNode();
        pictureNode.setId(ContentFolder.ROOT_PICTURE_NODE_ID);
        pictureNode.setName(bundle.getString("node.picture"));
        pictureNode.setChildNodeIds(new ArrayList<String>());
        pictureNode.setParentNodeId(ContentFolder.ROOT_NODE_ID);
        nodeMap.put(ContentFolder.ROOT_PICTURE_NODE_ID, pictureNode);

        // Create root podcast node
        ContainerNode podcastNode = new ContainerNode();
        podcastNode.setId(ContentFolder.ROOT_PODCAST_NODE_ID);
        podcastNode.setName(bundle.getString("node.podcast"));
        podcastNode.setChildNodeIds(new ArrayList<String>());
        podcastNode.setParentNodeId(ContentFolder.ROOT_NODE_ID);
        nodeMap.put(ContentFolder.ROOT_PODCAST_NODE_ID, podcastNode);

    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.IMediaService#getNode(java.lang.String)
     */
    @Override
    public AbstractNode getNode(String nodeId)
    {
        return nodeMap.get(nodeId);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.IMediaService#scanAll(boolean)
     */
    @Override
    public synchronized void scanAll(boolean autosave)
    {
        scanRootNode(ContentFolder.ROOT_VIDEO_NODE_ID, configuration.getConfig().getVideoFolders(), false, ContentType.TYPE_VIDEO);
        scanRootNode(ContentFolder.ROOT_AUDIO_NODE_ID, configuration.getConfig().getAudioFolders(), false, ContentType.TYPE_AUDIO);
        scanRootNode(ContentFolder.ROOT_PICTURE_NODE_ID, configuration.getConfig().getPictureFolders(), false, ContentType.TYPE_IMAGE);
        scanRootNode(ContentFolder.ROOT_PODCAST_NODE_ID, configuration.getConfig().getPodcasts(), true, null);

        if (autosave) save();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.IMediaService#scanVideos(boolean)
     */
    @Override
    public void scanVideos(boolean autosave)
    {
        scanRootNode(ContentFolder.ROOT_VIDEO_NODE_ID, configuration.getConfig().getVideoFolders(), false, ContentType.TYPE_VIDEO);
        if (autosave) save();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.IMediaService#scanAudios(boolean)
     */
    @Override
    public void scanAudios(boolean autosave)
    {
        scanRootNode(ContentFolder.ROOT_AUDIO_NODE_ID, configuration.getConfig().getAudioFolders(), false, ContentType.TYPE_AUDIO);
        if (autosave) save();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.IMediaService#scanPictures(boolean)
     */
    @Override
    public void scanPictures(boolean autosave)
    {
        scanRootNode(ContentFolder.ROOT_PICTURE_NODE_ID, configuration.getConfig().getPictureFolders(), false, ContentType.TYPE_IMAGE);
        if (autosave) save();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.IMediaService#scanPodcasts(boolean)
     */
    @Override
    public void scanPodcasts(boolean autosave)
    {
        scanRootNode(ContentFolder.ROOT_PODCAST_NODE_ID, configuration.getConfig().getPodcasts(), true, null);
        if (autosave) save();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.IMediaService#getNodes()
     */
    @Override
    public Map<String, AbstractNode> getNodes()
    {
        return nodeMap;
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
        AbstractNode rootNode = nodeMap.get(rootNodeId);
        if (rootNode instanceof ContainerNode)
        {
            ContainerNode rootContainerNode = (ContainerNode) rootNode;
            List<String> nodesToRemove = new ArrayList<String>();
            boolean nodeUpdated = false;

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
                            rootContainerNode.getChildNodeIds().add(addedNode.getId());
                            nodeUpdated = true;
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
                                rootContainerNode.getChildNodeIds().add(addedNode.getId());
                                nodeUpdated = true;
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
                nodeUpdated = true;
                for (String nodeId : nodesToRemove)
                {
                    removeNode(nodeId);
                    rootContainerNode.getChildNodeIds().remove(nodeId);
                }
            }

            // Update parent node version
            if (nodeUpdated) updateNodeVersion(rootNodeId);
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
            boolean nodeUpdated = false;
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
                        parentNode.getChildNodeIds().add(addedNode.getId());
                        nodeUpdated = true;
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
                nodeUpdated = true;
                for (String nodeId : nodesToRemove)
                {
                    removeNode(nodeId);
                    parentNode.getChildNodeIds().remove(nodeId);
                }
            }

            // Update parent node version
            if (nodeUpdated) updateNodeVersion(parentNode.getId());

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
            currentNode = nodeMap.get(nodeId);
            if (currentNode != null && currentNode instanceof ContainerNode && name.equals(currentNode.getName()))
            {
                currentNode.setModifedDate(DateFormat.formatUpnpDate(folder.lastModified()));
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
        node.setChildNodeIds(new ArrayList<String>());
        node.setModifedDate(DateFormat.formatUpnpDate(folder.lastModified()));

        // Add to node map
        nodeMap.put(node.getId(), node);

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
            currentNode = nodeMap.get(nodeId);
            if (currentNode != null && currentNode instanceof ContentNode && name.equals(currentNode.getName()))
            {
                currentNode.setModifedDate(DateFormat.formatUpnpDate(file.lastModified()));
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

            // Add to node map
            nodeMap.put(node.getId(), node);
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
            currentNode = nodeMap.get(nodeId);
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

        // Add to node map
        nodeMap.put(node.getId(), node);

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
        AbstractNode node = nodeMap.get(nodeId);
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
            nodeMap.remove(nodeId);
        }
        if (logger.isDebugEnabled()) logger.debug("[END] remove node");
    }

    /**
     * Update node version and parent node version.
     *
     * @param nodeId the node id
     */
    private void updateNodeVersion(String nodeId)
    {
        AbstractNode node = nodeMap.get(nodeId);
        if (node != null)
        {
            node.incrementVersion();
            if (node.getParentNodeId() != null)
            {
                updateNodeVersion(node.getParentNodeId());
            }
        }
    }

    /**
     * Gets the x stream.
     *
     * @return the x stream
     */
    private XStream getXStream()
    {
        XStream xs = new XStream(new DomDriver("UTF-8"));
        xs.alias("container", ContainerNode.class);
        xs.alias("content", ContentNode.class);
        xs.alias("contentType", ContentType.class);
        xs.alias("podcast", PodcastContainerNode.class);
        return xs;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.MediaService#save()
     */
    @Override
    public void save()
    {
        XStream xs = getXStream();

        String mediaPath = configuration.getHomeMediaDirectory();
        if (mediaPath != null)
        {
            String filePath = mediaPath + File.separator + MEDIA_FILE_NAME;
            File confFile = new File(filePath);

            OutputStream out = null;
            try
            {
                out = new FileOutputStream(confFile);
                xs.toXML(nodeMap, out);
            }
            catch (FileNotFoundException e)
            {
                logger.error(e.getMessage(), e);
            }
            finally
            {
                try
                {
                    if (out != null) out.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.IMediaService#load()
     */
    @Override
    @SuppressWarnings("unchecked")
    public void load()
    {
        XStream xs = getXStream();

        String mediaPath = configuration.getHomeMediaDirectory();
        if (mediaPath != null)
        {
            String filePath = mediaPath + File.separator + MEDIA_FILE_NAME;
            File confFile = new File(filePath);
            if (confFile.exists() && confFile.canRead())
            {
                InputStream in = null;
                try
                {
                    in = new FileInputStream(confFile);
                    nodeMap = (Map<String, AbstractNode>) xs.fromXML(in);
                }
                catch (FileNotFoundException e)
                {
                    logger.error(e.getMessage(), e);
                }
                finally
                {
                    try
                    {
                        if (in != null) in.close();
                    }
                    catch (IOException e)
                    {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
