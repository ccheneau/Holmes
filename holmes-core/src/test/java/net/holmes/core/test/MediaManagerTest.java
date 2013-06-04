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

package net.holmes.core.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.TestCase;
import net.holmes.common.media.AbstractNode;
import net.holmes.common.media.RootNode;
import net.holmes.core.TestModule;
import net.holmes.core.media.MediaManager;
import org.junit.Before;

import javax.inject.Inject;
import java.util.List;

public class MediaManagerTest extends TestCase {

    @Inject
    private MediaManager mediaManager;

    @Override
    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    public void testGetRootNode() {
        AbstractNode node = mediaManager.getNode(RootNode.ROOT.getId());
        assertNotNull(node);

        List<AbstractNode> childNodes = mediaManager.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 4);
    }

    public void testGetRootVideoNode() {
        AbstractNode node = mediaManager.getNode(RootNode.VIDEO.getId());
        assertNotNull(node);

        List<AbstractNode> childNodes = mediaManager.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 1);

        List<AbstractNode> nodes = mediaManager.getChildNodes(childNodes.iterator().next());
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 1);
        assertEquals(nodes.iterator().next().getName(), "video.avi");

    }

    public void testGetRootAudioNode() {
        AbstractNode node = mediaManager.getNode(RootNode.AUDIO.getId());
        assertNotNull(node);

        List<AbstractNode> childNodes = mediaManager.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 1);

        List<AbstractNode> nodes = mediaManager.getChildNodes(childNodes.iterator().next());
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 1);
        assertEquals(nodes.iterator().next().getName(), "audio.mp3");
    }

    public void testGetRootPictureNode() {
        AbstractNode node = mediaManager.getNode(RootNode.PICTURE.getId());
        assertNotNull(node);

        List<AbstractNode> childNodes = mediaManager.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 1);

        List<AbstractNode> nodes = mediaManager.getChildNodes(childNodes.iterator().next());
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 1);
        assertEquals(nodes.iterator().next().getName(), "image.jpg");
    }

    public void testGetRootPodcastNode() {
        AbstractNode node = mediaManager.getNode(RootNode.PODCAST.getId());
        assertNotNull(node);

        List<AbstractNode> childNodes = mediaManager.getChildNodes(node);
        assertNotNull(childNodes);
        assertFalse(childNodes.isEmpty());
        assertEquals(childNodes.size(), 1);
    }
}
