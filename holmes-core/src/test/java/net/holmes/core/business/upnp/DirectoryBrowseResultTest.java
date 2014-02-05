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

package net.holmes.core.business.upnp;

import net.holmes.core.business.media.model.ContentNode;
import net.holmes.core.business.media.model.FolderNode;
import net.holmes.core.business.media.model.RawUrlNode;
import net.holmes.core.common.MimeType;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;

import static net.holmes.core.business.media.model.AbstractNode.NodeType.TYPE_PODCAST_ENTRY;
import static org.junit.Assert.*;

public class DirectoryBrowseResultTest {

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testDirectoryBrowseResult() {
        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        assertEquals(directoryBrowseResult.getFirstResult(), 0);
        assertEquals(directoryBrowseResult.getTotalCount(), 0);
        assertEquals(directoryBrowseResult.getItemCount(), 0);
        assertNotNull(directoryBrowseResult.getDidl());
    }

    @Test
    public void testAddVideoItem() throws IOException, ContentDirectoryException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        MimeType mimeType = MimeType.valueOf("video/x-msvideo");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddAudioItem() throws IOException, ContentDirectoryException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        MimeType mimeType = MimeType.valueOf("audio/mpeg");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType);
        node.setIconUrl("http://google.com");

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddImageItem() throws IOException, ContentDirectoryException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        MimeType mimeType = MimeType.valueOf("image/jpeg");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddSubtitleItem() throws IOException, ContentDirectoryException {
        File file = File.createTempFile(testName.getMethodName(), "srt");
        file.deleteOnExit();
        MimeType mimeType = MimeType.valueOf("application/x-subrip");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddBadApplicationItem() throws IOException, ContentDirectoryException {
        File file = File.createTempFile(testName.getMethodName(), "srt");
        file.deleteOnExit();
        MimeType mimeType = MimeType.valueOf("application/bad-subrip");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 0);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 0);
    }

    @Test
    public void testAddOggItem() throws IOException, ContentDirectoryException {
        File file = File.createTempFile(testName.getMethodName(), "ogg");
        file.deleteOnExit();
        MimeType mimeType = MimeType.valueOf("application/ogg");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddBadItem() throws IOException, ContentDirectoryException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        MimeType mimeType = MimeType.valueOf("bad-type/bad-subtype");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType);

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 0);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 0);
    }

    @Test(expected = ContentDirectoryException.class)
    public void testBadAddItemIconUrl() throws IOException, ContentDirectoryException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        MimeType mimeType = MimeType.valueOf("audio/mpeg");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType);
        node.setIconUrl("\\bad_url");

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
    }

    @Test
    public void testAddContainer() throws ContentDirectoryException {
        FolderNode node = new FolderNode("id", "parentId", "name");
        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addContainer("1", node, 1);
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testAddPodcastItem() throws ContentDirectoryException {
        MimeType mimeType = MimeType.valueOf("video/x-msvideo");
        RawUrlNode node = new RawUrlNode(TYPE_PODCAST_ENTRY, "id", "parentId", "name", mimeType, "url", "duration");
        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        directoryBrowseResult.addUrlItem("1", node, "name");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);
    }

    @Test
    public void testFilterResult() throws IOException, ContentDirectoryException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        MimeType mimeType = MimeType.valueOf("audio/mpeg");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType);
        node.setIconUrl("http://google.com");

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 1);
        assertTrue(directoryBrowseResult.acceptNode());
        assertEquals(directoryBrowseResult.getTotalCount(), 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);

        assertFalse(directoryBrowseResult.acceptNode());
        assertEquals(directoryBrowseResult.getTotalCount(), 2);
    }

    @Test
    public void testFilterResultNoMaxResult() throws IOException, ContentDirectoryException {
        File file = File.createTempFile(testName.getMethodName(), "avi");
        file.deleteOnExit();
        MimeType mimeType = MimeType.valueOf("audio/mpeg");
        ContentNode node = new ContentNode("id", "1", "name", file, mimeType);
        node.setIconUrl("http://google.com");

        DirectoryBrowseResult directoryBrowseResult = new DirectoryBrowseResult(0, 0);
        assertTrue(directoryBrowseResult.acceptNode());
        assertEquals(directoryBrowseResult.getTotalCount(), 1);
        directoryBrowseResult.addItem("1", node, "http://google.com");
        assertEquals(directoryBrowseResult.getItemCount(), 1);
        assertEquals(directoryBrowseResult.getDidl().getCount(), 1);

        assertTrue(directoryBrowseResult.acceptNode());
        assertEquals(directoryBrowseResult.getTotalCount(), 2);
    }
}
