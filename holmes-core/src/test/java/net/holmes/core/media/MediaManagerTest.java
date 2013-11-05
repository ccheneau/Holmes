/*
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

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.Parameter;
import net.holmes.core.common.event.MediaEvent;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.FolderNode;
import net.holmes.core.media.model.RootNode;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class MediaManagerTest {

    @Inject
    private MediaManager mediaManager;

    @Inject
    private Configuration configuration;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testGetRootNode() {
        AbstractNode node = mediaManager.getNode(RootNode.ROOT.getId());
        assertNotNull(node);

        Collection<AbstractNode> childNodes = mediaManager.getChildNodes(node, null);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 4);
    }

    @Test
    public void testVideoNodes() {
        AbstractNode node = mediaManager.getNode(RootNode.VIDEO.getId());
        assertNotNull(node);

        Collection<AbstractNode> childNodes = mediaManager.getChildNodes(node, null);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(1, childNodes.size());
        assertNotNull(mediaManager.getNode(childNodes.iterator().next().getId()));

        Collection<AbstractNode> nodes = mediaManager.getChildNodes(childNodes.iterator().next(), null);
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(3, nodes.size());
        assertConfigNodes(nodes, "video.avi", "video.srt");
    }

    @Test
    public void testAudioNodes() {
        AbstractNode node = mediaManager.getNode(RootNode.AUDIO.getId());
        assertNotNull(node);

        Collection<AbstractNode> childNodes = mediaManager.getChildNodes(node, null);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(1, childNodes.size());
        assertNotNull(mediaManager.getNode(childNodes.iterator().next().getId()));

        Collection<AbstractNode> nodes = mediaManager.getChildNodes(childNodes.iterator().next(), null);
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(2, nodes.size());
        assertConfigNodes(nodes, "audio.mp3", "");
    }

    @Test
    public void testPictureNodes() {
        AbstractNode node = mediaManager.getNode(RootNode.PICTURE.getId());
        assertNotNull(node);

        Collection<AbstractNode> childNodes = mediaManager.getChildNodes(node, null);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(1, childNodes.size());
        assertNotNull(mediaManager.getNode(childNodes.iterator().next().getId()));

        Collection<AbstractNode> nodes = mediaManager.getChildNodes(childNodes.iterator().next(), null);
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(2, nodes.size());
        assertConfigNodes(nodes, "image.jpg");
    }

    @Test
    public void testPictureNodesWithoutContentResolution() {
        configuration.setBooleanParameter(Parameter.ENABLE_CONTENT_RESOLUTION, false);
        AbstractNode node = mediaManager.getNode(RootNode.PICTURE.getId());
        assertNotNull(node);

        Collection<AbstractNode> childNodes = mediaManager.getChildNodes(node, null);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(1, childNodes.size());
        assertNotNull(mediaManager.getNode(childNodes.iterator().next().getId()));

        Collection<AbstractNode> nodes = mediaManager.getChildNodes(childNodes.iterator().next(), null);
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(2, nodes.size());
        assertConfigNodes(nodes, "image.jpg");
    }

    private void assertConfigNodes(Collection<AbstractNode> nodes, String... fileNodeNames) {
        List<String> fileNodeNameList = Arrays.asList(fileNodeNames);
        for (AbstractNode abstractNode : nodes) {
            if (abstractNode instanceof FolderNode) {
                assertEquals("subFolder", abstractNode.getName());
                AbstractNode node1 = mediaManager.getNode(abstractNode.getId());
                assertNotNull(node1);
                assertEquals("subFolder", node1.getName());
                Collection<AbstractNode> nodes1 = mediaManager.getChildNodes(node1, null);
                assertNotNull(nodes1);
                assertFalse(nodes1.isEmpty());
                assertEquals(1, nodes1.size());
                assertTrue(fileNodeNameList.contains(nodes1.iterator().next().getName()));
                AbstractNode node2 = mediaManager.getNode(nodes1.iterator().next().getId());
                assertNotNull(node2);
                assertTrue(fileNodeNameList.contains(node2.getName()));
            } else if (abstractNode != null) {
                assertTrue(fileNodeNameList.contains(abstractNode.getName()));
                AbstractNode node1 = mediaManager.getNode(abstractNode.getId());
                assertNotNull(node1);
                assertTrue(fileNodeNameList.contains(node1.getName()));
            }
        }
    }

    @Test
    public void testPodcastNodes() {
        AbstractNode node = mediaManager.getNode(RootNode.PODCAST.getId());
        assertNotNull(node);

        Collection<AbstractNode> childNodes = mediaManager.getChildNodes(node, null);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 1);
        assertNotNull(mediaManager.getNode(childNodes.iterator().next().getId()));

        Collection<AbstractNode> nodes = mediaManager.getChildNodes(childNodes.iterator().next(), null);
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
    }

    @Test
    public void testHandleMediaEventScanNode() {
        if (mediaManager instanceof MediaManagerImpl) {
            MediaManagerImpl mediaManagerImpl = (MediaManagerImpl) mediaManager;
            mediaManagerImpl.handleMediaEvent(new MediaEvent(MediaEvent.MediaEventType.SCAN_NODE, "audiosTest"));
        }
    }

    @Test
    public void testHandleMediaEventUnknown() {
        if (mediaManager instanceof MediaManagerImpl) {
            MediaManagerImpl mediaManagerImpl = (MediaManagerImpl) mediaManager;
            mediaManagerImpl.handleMediaEvent(new MediaEvent(MediaEvent.MediaEventType.UNKNOWN, null));
        }
    }
}
