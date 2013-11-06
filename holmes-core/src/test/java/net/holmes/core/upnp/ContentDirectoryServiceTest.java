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

package net.holmes.core.upnp;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.test.TestModule;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.model.BrowseResult;
import org.junit.Before;
import org.junit.Test;

import static net.holmes.core.media.model.RootNode.*;
import static org.fourthline.cling.support.model.BrowseFlag.DIRECT_CHILDREN;
import static org.fourthline.cling.support.model.BrowseFlag.METADATA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ContentDirectoryServiceTest {

    private ContentDirectoryService contentDirectoryService;
    private MediaManager mediaManager;

    @Before
    public void setUp() {
        contentDirectoryService = new ContentDirectoryService();
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(contentDirectoryService);
        mediaManager = injector.getInstance(MediaManager.class);
    }

    @Test
    public void testBrowseRootDC() throws ContentDirectoryException {
        BrowseResult result = contentDirectoryService.browse(ROOT.getId(), DIRECT_CHILDREN, null, 0, 10, null, new RemoteClientInfo());
        assertNotNull(result);

    }

    @Test
    public void testBrowseVideoDC() throws ContentDirectoryException {
        // Get video root node
        AbstractNode videoNode = mediaManager.getNode(VIDEO.getId());
        assertNotNull(videoNode);

        // Get children of video root node
        MediaManager.ChildNodeResult result = mediaManager.getChildNodes(new MediaManager.ChildNodeRequest(videoNode));
        assertNotNull(result.getChildNodes());
        assertTrue(result.getTotalCount() > 0);

        // Browse video root node
        BrowseResult browseResult = contentDirectoryService.browse(VIDEO.getId(), DIRECT_CHILDREN, null, 0, 10, null, new RemoteClientInfo());
        assertNotNull(browseResult);

        // Browse first child of video root node
        browseResult = contentDirectoryService.browse(result.getChildNodes().iterator().next().getId(), DIRECT_CHILDREN, null, 0, 10, null, new RemoteClientInfo());
        assertNotNull(browseResult);
    }

    @Test
    public void testBrowsePodcastDC() throws ContentDirectoryException {
        // Get podcast root node
        AbstractNode videoNode = mediaManager.getNode(PODCAST.getId());
        assertNotNull(videoNode);

        // Get children of podcast root node
        MediaManager.ChildNodeResult result = mediaManager.getChildNodes(new MediaManager.ChildNodeRequest(videoNode));
        assertNotNull(result.getChildNodes());
        assertTrue(result.getTotalCount() > 0);

        // Browse podcast root node
        BrowseResult browseResult = contentDirectoryService.browse(PODCAST.getId(), DIRECT_CHILDREN, null, 0, 10, null, new RemoteClientInfo());
        assertNotNull(browseResult);

        // Browse first child of podcast root node
        browseResult = contentDirectoryService.browse(result.getChildNodes().iterator().next().getId(), DIRECT_CHILDREN, null, 0, 10, null, new RemoteClientInfo());
        assertNotNull(browseResult);
    }

    @Test
    public void testBrowseRootMetadata() throws ContentDirectoryException {
        BrowseResult result = contentDirectoryService.browse(ROOT.getId(), METADATA, null, 0, 10, null, new RemoteClientInfo());
        assertNotNull(result);

    }

    @Test
    public void testBrowseRootNullFlag() throws ContentDirectoryException {
        BrowseResult result = contentDirectoryService.browse(ROOT.getId(), null, null, 0, 10, null, new RemoteClientInfo());
        assertNotNull(result);

    }

    @Test(expected = ContentDirectoryException.class)
    public void testBrowseNull() throws ContentDirectoryException {
        contentDirectoryService.browse(null, METADATA, null, 0, 10, null, new RemoteClientInfo());
    }
}
