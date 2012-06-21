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
package net.holmes.core.media.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import net.holmes.core.configuration.ContentFolder;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.model.AbstractNode;
import net.holmes.core.model.ContainerNode;
import net.holmes.core.model.ContentNode;
import net.holmes.core.model.ContentType;
import net.holmes.core.model.PodcastContainerNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The Class XmlMediaDaoImpl.
 */
public class XmlMediaDao implements IMediaDao
{
    private static Logger logger = LoggerFactory.getLogger(XmlMediaDao.class);

    /** The Constant MEDIA_FILE_NAME. */
    private static final String MEDIA_FILE_NAME = "media.xml";

    /** The node map. */
    private Map<String, AbstractNode> nodeMap;

    /** The configuration. */
    @Inject
    private IConfiguration configuration;

    /**
     * Initialize.
     */
    @Inject
    @SuppressWarnings("unchecked")
    public void initialize()
    {
        if (logger.isDebugEnabled()) logger.debug("[START] init()");

        // Load data from XML file
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

        // Checks default nodes
        if (nodeMap == null) nodeMap = new HashMap<String, AbstractNode>();
        ResourceBundle bundle = ResourceBundle.getBundle("message");

        if (getNode(ContentFolder.ROOT_NODE_ID) == null)
        {
            // Create root node
            ContainerNode rootNode = new ContainerNode();
            rootNode.setId(ContentFolder.ROOT_NODE_ID);
            rootNode.setName(bundle.getString("node.rootNode"));
            rootNode.setChildNodeIds(new LinkedList<String>());
            rootNode.getChildNodeIds().add(ContentFolder.ROOT_VIDEO_NODE_ID);
            rootNode.getChildNodeIds().add(ContentFolder.ROOT_AUDIO_NODE_ID);
            rootNode.getChildNodeIds().add(ContentFolder.ROOT_PICTURE_NODE_ID);
            rootNode.getChildNodeIds().add(ContentFolder.ROOT_PODCAST_NODE_ID);

            addNode(rootNode);
        }

        if (getNode(ContentFolder.ROOT_VIDEO_NODE_ID) == null)
        {
            // Create root video node
            ContainerNode videoNode = new ContainerNode();
            videoNode.setId(ContentFolder.ROOT_VIDEO_NODE_ID);
            videoNode.setName(bundle.getString("node.video"));
            videoNode.setChildNodeIds(new LinkedList<String>());
            videoNode.setParentNodeId(ContentFolder.ROOT_NODE_ID);

            addNode(videoNode);
        }

        if (getNode(ContentFolder.ROOT_AUDIO_NODE_ID) == null)
        {
            // Create root audio node
            ContainerNode audioNode = new ContainerNode();
            audioNode.setId(ContentFolder.ROOT_AUDIO_NODE_ID);
            audioNode.setName(bundle.getString("node.audio"));
            audioNode.setChildNodeIds(new LinkedList<String>());
            audioNode.setParentNodeId(ContentFolder.ROOT_NODE_ID);

            addNode(audioNode);
        }

        if (getNode(ContentFolder.ROOT_PICTURE_NODE_ID) == null)
        {
            // Create root picture node
            ContainerNode pictureNode = new ContainerNode();
            pictureNode.setId(ContentFolder.ROOT_PICTURE_NODE_ID);
            pictureNode.setName(bundle.getString("node.picture"));
            pictureNode.setChildNodeIds(new LinkedList<String>());
            pictureNode.setParentNodeId(ContentFolder.ROOT_NODE_ID);

            addNode(pictureNode);
        }

        if (getNode(ContentFolder.ROOT_PODCAST_NODE_ID) == null)
        {
            // Create root podcast node
            ContainerNode podcastNode = new ContainerNode();
            podcastNode.setId(ContentFolder.ROOT_PODCAST_NODE_ID);
            podcastNode.setName(bundle.getString("node.podcast"));
            podcastNode.setChildNodeIds(new LinkedList<String>());
            podcastNode.setParentNodeId(ContentFolder.ROOT_NODE_ID);

            addNode(podcastNode);
        }

        if (logger.isDebugEnabled()) logger.debug("[END] init()");
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.dao.IMediaDao#getNode(java.lang.String)
     */
    @Override
    public AbstractNode getNode(String nodeId)
    {
        return nodeMap.get(nodeId);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.dao.IMediaDao#addNode(net.holmes.core.model.AbstractNode)
     */
    @Override
    public void addNode(AbstractNode node)
    {
        nodeMap.put(node.getId(), node);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.dao.IMediaDao#removeNode(java.lang.String)
     */
    @Override
    public void removeNode(String nodeId)
    {
        nodeMap.remove(nodeId);
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.dao.IMediaDao#getNodeIds()
     */
    @Override
    public Set<String> getNodeIds()
    {
        return nodeMap.keySet();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.dao.IMediaDao#flush()
     */
    @Override
    public void flush()
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
     * @see net.holmes.core.service.dao.IMediaDao#addChildNode(java.lang.String, java.lang.String)
     */
    @Override
    public void addChildNode(String containerNodeId, String childNodeId)
    {
        if (nodeMap.get(containerNodeId) != null && nodeMap.get(containerNodeId) instanceof ContainerNode)
        {
            ((ContainerNode) nodeMap.get(containerNodeId)).getChildNodeIds().add(childNodeId);
        }
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.dao.IMediaDao#removeChildNode(java.lang.String, java.lang.String)
     */
    @Override
    public void removeChildNode(String containerNodeId, String childNodeId)
    {
        if (nodeMap.get(containerNodeId) != null && nodeMap.get(containerNodeId) instanceof ContainerNode)
        {
            ((ContainerNode) nodeMap.get(containerNodeId)).getChildNodeIds().remove(childNodeId);
        }
    }

    /* (non-Javadoc)
     * @see net.holmes.core.service.dao.IMediaDao#updateNodeModifiedDate(java.lang.String, java.lang.String)
     */
    @Override
    public void updateNodeModifiedDate(String nodeId, String date)
    {
        if (nodeMap.get(nodeId) != null)
        {
            nodeMap.get(nodeId).setModifedDate(date);
        }
    }

}
