/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

public class MediaServiceTest {

    @Inject
    private MediaService mediaService;

    @Inject
    private Configuration configuration;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testGetRootNode() {
        AbstractNode node = mediaService.getNode(RootNode.ROOT.getId());
        assertNotNull(node);

        MediaService.ChildNodeResult result = mediaService.getChildNodes(new MediaService.ChildNodeRequest(node));
        assertNotNull(result.getChildNodes());
        assertFalse(result.getChildNodes().isEmpty());
        assertEquals(result.getTotalCount(), 4);
    }

    @Test
    public void testVideoNodes() {
        AbstractNode node = mediaService.getNode(RootNode.VIDEO.getId());
        assertNotNull(node);

        MediaService.ChildNodeResult result = mediaService.getChildNodes(new MediaService.ChildNodeRequest(node));
        assertNotNull(result.getChildNodes());
        assertFalse(result.getChildNodes().isEmpty());
        assertEquals(1, result.getTotalCount());
        assertNotNull(mediaService.getNode(result.getChildNodes().iterator().next().getId()));

        result = mediaService.getChildNodes(new MediaService.ChildNodeRequest(result.getChildNodes().iterator().next()));
        assertNotNull(result.getChildNodes());
        assertFalse(result.getChildNodes().isEmpty());
        assertEquals(3, result.getTotalCount());
        assertConfigNodes(result.getChildNodes(), "video.avi", "video.srt");
    }

    @Test
    public void testAudioNodes() {
        AbstractNode node = mediaService.getNode(RootNode.AUDIO.getId());
        assertNotNull(node);

        MediaService.ChildNodeResult result = mediaService.getChildNodes(new MediaService.ChildNodeRequest(node));
        assertNotNull(result.getChildNodes());
        assertFalse(result.getChildNodes().isEmpty());
        assertEquals(1, result.getTotalCount());
        assertNotNull(mediaService.getNode(result.getChildNodes().iterator().next().getId()));

        result = mediaService.getChildNodes(new MediaService.ChildNodeRequest(result.getChildNodes().iterator().next()));
        assertNotNull(result.getChildNodes());
        assertFalse(result.getChildNodes().isEmpty());
        assertEquals(2, result.getTotalCount());
        assertConfigNodes(result.getChildNodes(), "audio.mp3", "");
    }

    @Test
    public void testPictureNodes() {
        AbstractNode node = mediaService.getNode(RootNode.PICTURE.getId());
        assertNotNull(node);

        MediaService.ChildNodeResult result = mediaService.getChildNodes(new MediaService.ChildNodeRequest(node));
        assertNotNull(result.getChildNodes());
        assertFalse(result.getChildNodes().isEmpty());
        assertEquals(1, result.getTotalCount());
        assertNotNull(mediaService.getNode(result.getChildNodes().iterator().next().getId()));

        result = mediaService.getChildNodes(new MediaService.ChildNodeRequest(result.getChildNodes().iterator().next()));
        assertNotNull(result.getChildNodes());
        assertFalse(result.getChildNodes().isEmpty());
        assertEquals(2, result.getTotalCount());
        assertConfigNodes(result.getChildNodes(), "image.jpg");
    }

    private void assertConfigNodes(Collection<AbstractNode> nodes, String... fileNodeNames) {
        List<String> fileNodeNameList = Arrays.asList(fileNodeNames);
        for (AbstractNode abstractNode : nodes) {
            if (abstractNode instanceof FolderNode) {
                assertEquals("subFolder", abstractNode.getName());
                AbstractNode node1 = mediaService.getNode(abstractNode.getId());
                assertNotNull(node1);
                assertEquals("subFolder", node1.getName());
                MediaService.ChildNodeResult result = mediaService.getChildNodes(new MediaService.ChildNodeRequest(node1));
                assertNotNull(result.getChildNodes());
                assertFalse(result.getChildNodes().isEmpty());
                assertEquals(1, result.getTotalCount());
                assertTrue(fileNodeNameList.contains(result.getChildNodes().iterator().next().getName()));
                AbstractNode node2 = mediaService.getNode(result.getChildNodes().iterator().next().getId());
                assertNotNull(node2);
                assertTrue(fileNodeNameList.contains(node2.getName()));
            } else if (abstractNode != null) {
                assertTrue(fileNodeNameList.contains(abstractNode.getName()));
                AbstractNode node1 = mediaService.getNode(abstractNode.getId());
                assertNotNull(node1);
                assertTrue(fileNodeNameList.contains(node1.getName()));
            }
        }
    }

    @Test
    public void testPodcastNodes() {
        AbstractNode node = mediaService.getNode(RootNode.PODCAST.getId());
        assertNotNull(node);

        MediaService.ChildNodeResult result = mediaService.getChildNodes(new MediaService.ChildNodeRequest(node));
        assertNotNull(result.getChildNodes());
        assertFalse(result.getChildNodes().isEmpty());
        assertEquals(result.getTotalCount(), 1);
        assertNotNull(mediaService.getNode(result.getChildNodes().iterator().next().getId()));

        result = mediaService.getChildNodes(new MediaService.ChildNodeRequest(result.getChildNodes().iterator().next()));
        assertNotNull(result.getChildNodes());
        assertFalse(result.getChildNodes().isEmpty());
    }

    @Test
    public void testGetNodeUrl() {
        AbstractNode node = mediaService.getNode(RootNode.PICTURE.getId());
        String url = mediaService.getNodeUrl(node);
        System.out.println(url);
        assertNotNull(url);
    }

    @Test
    public void testHandleMediaEventScanNode() {
        if (mediaService instanceof MediaServiceImpl) {
            MediaServiceImpl mediaManagerImpl = (MediaServiceImpl) mediaService;
            mediaManagerImpl.handleMediaEvent(new MediaEvent(MediaEvent.MediaEventType.SCAN_NODE, "audiosTest"));
        }
    }

    @Test
    public void testHandleMediaEventUnknown() {
        if (mediaService instanceof MediaServiceImpl) {
            MediaServiceImpl mediaManagerImpl = (MediaServiceImpl) mediaService;
            mediaManagerImpl.handleMediaEvent(new MediaEvent(MediaEvent.MediaEventType.UNKNOWN, null));
        }
    }

    @Test
    public void testCacheCleanUp() {
        mediaService.cleanUpCache();
    }
}
