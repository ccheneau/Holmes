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
package net.holmes.core.test;

import java.util.List;

import javax.inject.Inject;

import junit.framework.TestCase;
import net.holmes.core.TestModule;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.RootNode;
import net.holmes.core.media.node.AbstractNode;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MediaServiceTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(MediaServiceTest.class);

    @Inject
    private MediaService mediaService;

    @Inject
    private Configuration configuration;

    @Override
    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testMediaService() {
        logger.debug(configuration.toString());
    }

    @Test
    public void testGetRootNode() {
        AbstractNode node = mediaService.getNode(RootNode.ROOT.getId());
        assertNotNull(node);
        logger.debug(node.toString());

        List<AbstractNode> childNodes = mediaService.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 4);
        logger.debug(childNodes.toString());
    }

    @Test
    public void testGetRootVideoNode() {
        AbstractNode node = mediaService.getNode(RootNode.VIDEO.getId());
        assertNotNull(node);
        logger.debug(node.toString());

        List<AbstractNode> childNodes = mediaService.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 1);
        logger.debug(childNodes.toString());

        List<AbstractNode> nodes = mediaService.getChildNodes(childNodes.iterator().next());
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 1);
        assertEquals(nodes.iterator().next().getName(), "video.avi");

    }

    @Test
    public void testGetRootAudioNode() {
        AbstractNode node = mediaService.getNode(RootNode.AUDIO.getId());
        assertNotNull(node);
        logger.debug(node.toString());

        List<AbstractNode> childNodes = mediaService.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 1);
        logger.debug(childNodes.toString());

        List<AbstractNode> nodes = mediaService.getChildNodes(childNodes.iterator().next());
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 1);
        assertEquals(nodes.iterator().next().getName(), "audio.mp3");
    }

    @Test
    public void testGetRootPictureNode() {
        AbstractNode node = mediaService.getNode(RootNode.PICTURE.getId());
        assertNotNull(node);
        logger.debug(node.toString());

        List<AbstractNode> childNodes = mediaService.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 1);
        logger.debug(childNodes.toString());

        List<AbstractNode> nodes = mediaService.getChildNodes(childNodes.iterator().next());
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 1);
        assertEquals(nodes.iterator().next().getName(), "image.jpg");
    }

    @Test
    public void testGetRootPodcastNode() {
        AbstractNode node = mediaService.getNode(RootNode.PODCAST.getId());
        assertNotNull(node);
        logger.debug(node.toString());

        List<AbstractNode> childNodes = mediaService.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 1);
        logger.debug(childNodes.toString());
    }
}
