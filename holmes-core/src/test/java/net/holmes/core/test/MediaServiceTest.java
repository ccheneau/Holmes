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
package net.holmes.core.test;

import java.util.List;

import junit.framework.TestCase;
import net.holmes.core.TestModule;
import net.holmes.core.configuration.ContentFolder;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.media.IMediaService;
import net.holmes.core.model.AbstractNode;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class MediaServiceTest extends TestCase {
    private static Logger logger = LoggerFactory.getLogger(MediaServiceTest.class);

    @Inject
    private IMediaService mediaService;

    @Inject
    private IConfiguration configuration;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testMediaService() {
        logger.debug(configuration.getConfig().toString());
    }

    @Test
    public void testGetRootNode() {
        AbstractNode node = mediaService.getNode(ContentFolder.ROOT_NODE_ID);
        assertNotNull(node);
        logger.debug(node.toString());

        List<AbstractNode> childNodes = mediaService.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        logger.debug(childNodes.toString());
    }

    @Test
    public void testGetRootVideoNode() {
        AbstractNode node = mediaService.getNode(ContentFolder.ROOT_VIDEO_NODE_ID);
        assertNotNull(node);
        logger.debug(node.toString());

        List<AbstractNode> childNodes = mediaService.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        logger.debug(childNodes.toString());

        for (AbstractNode childNode : childNodes) {
            List<AbstractNode> nodes = mediaService.getChildNodes(childNode);
            assertNotNull(nodes);
            assertFalse(nodes.isEmpty());
            for (AbstractNode iNode : nodes) {
                logger.debug(iNode.toString());
            }
        }

    }

    @Test
    public void testGetRootAudioNode() {
        AbstractNode node = mediaService.getNode(ContentFolder.ROOT_AUDIO_NODE_ID);
        assertNotNull(node);
        logger.debug(node.toString());

        List<AbstractNode> childNodes = mediaService.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        logger.debug(childNodes.toString());

        for (AbstractNode childNode : childNodes) {
            List<AbstractNode> nodes = mediaService.getChildNodes(childNode);
            assertNotNull(nodes);
            assertFalse(nodes.isEmpty());
            for (AbstractNode iNode : nodes) {
                logger.debug(iNode.toString());
            }
        }
    }

    @Test
    public void testGetRootPictureNode() {
        AbstractNode node = mediaService.getNode(ContentFolder.ROOT_PICTURE_NODE_ID);
        assertNotNull(node);
        logger.debug(node.toString());

        List<AbstractNode> childNodes = mediaService.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        logger.debug(childNodes.toString());

        for (AbstractNode childNode : childNodes) {
            List<AbstractNode> nodes = mediaService.getChildNodes(childNode);
            assertNotNull(nodes);
            assertFalse(nodes.isEmpty());
            for (AbstractNode iNode : nodes) {
                logger.debug(iNode.toString());
            }
        }
    }

    @Test
    public void testGetRootPodcastNode() {
        AbstractNode node = mediaService.getNode(ContentFolder.ROOT_PODCAST_NODE_ID);
        assertNotNull(node);
        logger.debug(node.toString());

        List<AbstractNode> childNodes = mediaService.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        logger.debug(childNodes.toString());

        for (AbstractNode childNode : childNodes) {
            List<AbstractNode> nodes = mediaService.getChildNodes(childNode);
            assertNotNull(nodes);
            assertFalse(nodes.isEmpty());
            for (AbstractNode iNode : nodes) {
                logger.debug(iNode.toString());
            }
        }
    }
}
